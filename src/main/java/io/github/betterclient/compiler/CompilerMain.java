package io.github.betterclient.compiler;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.storage.JavaStandardLibrariesUtil;
import io.github.betterclient.compiler.web.CodeRunner;
import org.teavm.jso.JSBody;
import org.teavm.jso.dom.html.HTMLDocument;

import java.io.OutputStream;
import java.io.PrintStream;

public class CompilerMain {
    public static void main(String[] args) throws Exception {
        JavaStandardLibrariesUtil.download();

        PrintStream old = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                addToConsole(Character.toString((char) b));
                old.write(b); //Write to normal console.log
            }

            @Override
            public void write(byte[] b, int off, int len) {
                addToConsole(new String(b, off, len));
                old.write(b, off, len); //Write to normal console.log
            }
        })); //Make System.out.println write to console output

        var document = HTMLDocument.current();

        document.getElementById("SIGMA_BUTTON_RUN_CODE").addEventListener("click", event -> {
            new Thread(CompilerMain::compile).start();
        });
    }

    private static void compile() {
        String inputCode = getCodeInput();
        resetConsole();

        try {
            APIClass compiled = Compiler.compile(inputCode.trim(), "Main");

            CodeRunner.run(compiled);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + "\n error class: " + e.getClass().getName());
        }
    }

    @JSBody(script = "return document.getElementById('codeInput').value;")
    public static native String getCodeInput();

    @JSBody(params = {"line"}, script = "document.getElementById('consoleOutput').innerText += line;")
    public static native void addToConsole(String line);

    @JSBody(script = "document.getElementById('consoleOutput').innerText = \"\";")
    public static native void resetConsole();
}