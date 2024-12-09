package io.github.betterclient.compiler.storage;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APIField;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.type.AccessType;
import io.github.betterclient.compiler.api.type.VisibilityType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import org.teavm.jso.browser.Window;

import java.util.*;

//Used to generate cookies
public class JavaStandardLibrariesUtil {
    /*public static void generateCookies() {
        List<URL> java = getJava();
        Map<String, APIClass> classMap = new HashMap<>();
        for (URL url : java) {
            try {
                File jarFileAsFile = new File(url.toURI());
                JarFile jarFile = new JarFile(jarFileAsFile);
                jarFile.stream().forEach(jarEntry -> {
                    String str = jarEntry.getName().replace(".class", "");

                    ClassNode node = new ClassNode();
                    try {
                        InputStream is = jarFile.getInputStream(jarEntry);
                        byte[] reader = is.readAllBytes();
                        is.close();
                        ClassReader reader0 = new ClassReader(reader);
                        reader0.accept(node, 0);
                    } catch (Exception e) {
                        return;
                    }

                    classMap.put(str, new APIClass(node));
                });
                jarFile.close();
                cacheClassMap(classMap);
            } catch (URISyntaxException | IOException e) {
                throw new CompilerException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void cacheClassMap(Map<String, APIClass> classMap) throws Exception {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, APIClass> stringAPIClassEntry : classMap.entrySet()) {
            APIClass clazz = stringAPIClassEntry.getValue();
            String name = stringAPIClassEntry.getKey();

            if (name.startsWith("jdk/internal")) continue;

            /*
            C java/lang/String
            E java/lang/Object
            F value [C 0
            M equals (Ljava/lang/Object;)Z 0
             *//*
            str.append("C ").append(name).append("\n");
            str.append("E ").append(" ").append(clazz.extendingClass).append("\n");
            for (APIField field : clazz.fields) {
                str.append("F ").append(" ").append(field.name).append(" ").append(field.desc).append(" ").append(field.isStatic ? 1 : 0).append("\n");
            }
            for (APIMethod method : clazz.methods) {
                str.append("M ").append(" ").append(method.name).append(" ").append(method.compileDesc()).append(" ").append(method.isStatic ? 1 : 0).append("\n");
            }
        }

        File f = new File("classpath.map");
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(str.toString().getBytes());
        fos.close();
    }

    public static List<URL> getJava() {
        return ModuleLayer.boot().modules().stream()
                .filter(module -> module.getName().startsWith("java.") || module.getName().startsWith("jdk."))
                .map(Module::getName)
                .map(JavaStandardLibrariesUtil::map).toList();
    }

    private static URL map(String moduleName) {
        Map<String, byte[]> module = getModuleClasses(moduleName);

        File f;
        JarOutputStream jos;
        try {
            f = new File(System.getProperty("user.home") + File.separator + "jmodjars_jcompiler" + File.separator + moduleName + ".jar");
            if (f.exists()) {
                return f.toURI().toURL();
            } else {
                Files.createDirectories(f.getParentFile().toPath());
                f.createNewFile();
            }
            jos = new JarOutputStream(new FileOutputStream(f));
        } catch (IOException e) {
            throw new CompilerException(e);
        }

        for (Map.Entry<String, byte[]> stringEntry : module.entrySet()) {
            try {
                jos.putNextEntry(new ZipEntry(stringEntry.getKey()));
                jos.write(stringEntry.getValue());
                jos.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            jos.close();
            return f.toURI().toURL();
        } catch (IOException e) {
            throw new CompilerException(e);
        }
    }

    private static Map<String, byte[]> getModuleClasses(String moduleName) {
        HashMap<String, byte[]> out = new HashMap<>();
        File moduleFile = new File(System.getProperty("java.home") + File.separator + "jmods" + File.separator + moduleName + ".jmod");
        if (!moduleFile.exists()) return out;

        try(JarFile jf = new JarFile(moduleFile)) {
            Enumeration<JarEntry> jarEntries = jf.entries();

            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();

                if (entry.isDirectory() || !entry.getName().startsWith("classes/")) continue;
                if (entry.getName().contains("module-info.class")) continue;

                InputStream is = jf.getInputStream(entry);
                byte[] bites = is.readAllBytes();
                is.close();

                out.put(entry.getName().substring(8), bites);
            }
        } catch (IOException e) {
            throw new CompilerException(e);
        }

        return out;
    }*/

    //-------------------------------------IN-SITE-------------------------------------

    public static List<String> pregen = new ArrayList<>();

    public static List<String> download() throws Exception {
        if (!pregen.isEmpty()) return pregen;

        Window window = Window.current();

        if (window.getLocalStorage().getItem("data0").isEmpty()) {
            while (window.getLocalStorage().getItem("data0").isEmpty()) {}
        }

        List<String> strings = new ArrayList<>();

        String code = window.getLocalStorage().getItem("data0");

        APIClass currentClass = null;
        for (String s : code.split("\n")) {
            if (s.isEmpty()) continue;

            String[] data = s.split(" ");

            String name = data[1];
            if (s.startsWith("C")) {
                currentClass = new APIClass(name);
                strings.add(name);
            } else if (s.startsWith("E")) {
                assert currentClass != null;
                currentClass.extendingClass = name;
            } else if (s.startsWith("M")) {
                assert currentClass != null;
                MethodNode node = new MethodNode(
                        Opcodes.ACC_PUBLIC + (data[4].equals("1") ? Opcodes.ACC_STATIC : 0),
                        data[2],
                        data[3],
                        null,null
                );
                currentClass.methods.add(new APIMethod(node, currentClass));
            } else if (s.startsWith("F")) {
                assert currentClass != null;
                currentClass.fields.add(new APIField(
                        currentClass, data[2], data[3],
                        new AccessType(VisibilityType.PUBLIC, false),
                        data[4].equals("1")
                ));
            }
        }

        pregen.addAll(strings);
        return strings;
    }

    /*public static void main(String[] args) throws IOException {
        File f = new File("./classpath.map");
        List<String> classpathmap = Files.readAllLines(f.toPath());
        StringBuilder sb = new StringBuilder();
        String currentClass;
        boolean add = false;
        for (String s : classpathmap) {

            String[] split = s.split(" ");
            if (s.startsWith("C ")) {
                currentClass = split[1];
                add = SupportedMethodCalls.classNames.contains(currentClass) ||
                        SupportedFieldGets.classNames.contains(currentClass);
            }

            if (add) {
                sb.append(s).append("\n");
            }
        }

        Files.writeString(new File("claspath2.map").toPath(), sb.toString());
    }*/
}
