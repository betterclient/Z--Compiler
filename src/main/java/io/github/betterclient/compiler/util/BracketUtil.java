package io.github.betterclient.compiler.util;

import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.APIMethodCode;
import io.github.betterclient.compiler.exception.WrongBracketUsageException;
import io.github.betterclient.compiler.exception.WrongIndentationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling methods/statements
 * <p>
 * eg:
 * static void test() {} //Bracket
 * if(true) {} //Bracket
 */
public class BracketUtil {
    public static String removeAllBrackets(String code) {
        code = code.replace("\n", " ").replace(";", "\n");
        code = code.replace("{", "{\n").replace("}", "\n}\n");

        int indentationCount = 0;
        StringBuilder out = new StringBuilder();
        for (String s : code.split("\n")) {
            if (s.contains("{")) indentationCount++;
            if (s.contains("}")) indentationCount--;

            if (indentationCount == 0 && !s.contains("}")) {
                out.append(s).append("\n");
            }
        }

        return out.toString();
    }

    public static BracketUtil parseBrackets(String code) {
        BracketUtil out = new BracketUtil(code, new ArrayList<>(), null, "");

        //Remove all newlines in favor of ;
        code = code.replace("\n", " ").replace(";", "\n");
        code = code.replace("{", "{\n").replace("}", "\n}\n");

        int indentationCount = 0;
        StringBuilder currentBlock = new StringBuilder();
        BracketUtil current = out;
        for (String s : code.split("\n")) {
            s = s.replaceFirst("^\\s+", ""); //Remove spaces at start
            if (s.contains("{")) {
                indentationCount++;
                current.code = currentBlock.toString();
                currentBlock = new StringBuilder();

                current = new BracketUtil(currentBlock.toString(), new ArrayList<>(), current, s.replace("{", ""));
            } else if (s.contains("}")) {
                indentationCount--;
                if (indentationCount < 0) throw new WrongIndentationException("Closed non-existent bracket");

                String code0 = current.code = currentBlock.toString();
                code0 = current.beforeBracket + "{\n" + code0 + "}";
                currentBlock = new StringBuilder();

                if (indentationCount > 0) {
                    current.owner.code += code0 + "\n";
                    currentBlock = new StringBuilder(current.owner.code);
                }
                current = current.owner;
            } else {
                if(indentationCount == 0) continue; //Not inside a block

                currentBlock.append(s).append("\n");
            }
        }

        if (indentationCount != 0) {
            throw new WrongBracketUsageException("Unclosed brackets.");
        }

        return out;
    }

    public String code, beforeBracket;
    public final List<BracketUtil> contains;
    public final BracketUtil owner;

    public List<APIMethodCode> compilation = new ArrayList<>();

    BracketUtil(String code, List<BracketUtil> contains, BracketUtil owner, String beforeBracket) {
        this.code = code;
        this.contains = contains;
        this.owner = owner;
        this.beforeBracket = beforeBracket;

        if (owner != null)
            this.owner.contains.add(this);
    }
}
