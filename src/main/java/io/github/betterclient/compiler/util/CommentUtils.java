package io.github.betterclient.compiler.util;

public class CommentUtils {
    public static boolean IS_IN_MULTILINE_COMMENT = false; //Reset this to false after usage

    /**
     * Remove comments made with //
     * @param string input str
     * @return without comments
     */
    public static String remove2SlashComments(String string) {
        StringBuilder out = new StringBuilder();
        boolean wasLastSlash = false;
        boolean isInString = false;
        boolean wasLastBackSlash = false;
        for (char c : string.toCharArray()) {
            if (c == '"')  {
                if (wasLastBackSlash && isInString) {
                    out.append(c);
                    continue;
                }

                isInString = !isInString;
                wasLastBackSlash = false;
                out.append(c);
            } else if (c == '\\') {
                wasLastBackSlash = !wasLastBackSlash;
                out.append(c);
            } else {
                if (c != '/') {
                    out.append(c);
                } else {
                    if (isInString || wasLastBackSlash) {
                        out.append(c);
                        continue;
                    }

                    if (wasLastSlash) {
                        out = new StringBuilder(out.substring(0, out.length() - 1));
                        break;
                    } else {
                        wasLastSlash = true;
                        out.append(c);
                    }
                }
            }
        }
        return out.toString();
    }

    /**
     * Removes comments made with /*
     * @param e input str
     * @return without comments
     */
    public static String remove1SlashComments(String e) {
        StringBuilder out = new StringBuilder();

        boolean wasLastStar = false;
        boolean wasLastSlash = false;
        boolean inString = false;
        boolean wasLastBackSlash = false;
        for(char v : e.toCharArray()) {
            if(IS_IN_MULTILINE_COMMENT) {
                if(wasLastStar && v == '/') {
                    IS_IN_MULTILINE_COMMENT = false;
                    wasLastStar = false;
                    continue;
                }
            } else {
                if(!inString && wasLastSlash && v == '*') {
                    IS_IN_MULTILINE_COMMENT = true;
                    wasLastSlash = false;
                    out = new StringBuilder(out.substring(0, out.length() - 1));
                    continue;
                }

                if(!wasLastBackSlash && v == '"') {
                    inString = !inString;
                }
            }

            wasLastStar = v == '*';
            wasLastSlash = v == '/';
            wasLastBackSlash = v == '\\';

            if(!IS_IN_MULTILINE_COMMENT) out.append(v);
        }

        return out.toString();
    }
}
