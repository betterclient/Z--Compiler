package io.github.betterclient.compiler.util;

import io.github.betterclient.compiler.exception.CompilerException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class JavaStandardLibrariesUtil {
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
    }
}
