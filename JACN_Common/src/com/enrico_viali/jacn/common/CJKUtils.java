package com.enrico_viali.jacn.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class CJKUtils {

    char HIRAGANA_START = '\u3041';
    char HIRAGANA_END   = '\u3096';
    char KATAKANA_START = '\u30A1';
    char KATAKANA_END   = '\u30F6';


    public static String remParen(String s) {
        String replace = s.replaceFirst("\\([^)]*\\)", "");
        return replace;
    }

    public CJKUtils() {
        super();
        lessonsX10LastKanji = new HashMap<Integer, Integer>();

        lessonsX10LastKanji.put(10, 15);
        lessonsX10LastKanji.put(20, 34);
        lessonsX10LastKanji.put(30, 52);
        lessonsX10LastKanji.put(40, 70);
        lessonsX10LastKanji.put(50, 94);
        lessonsX10LastKanji.put(60, 104);
        lessonsX10LastKanji.put(70, 126);
        lessonsX10LastKanji.put(80, 172);
        lessonsX10LastKanji.put(90, 194);
        lessonsX10LastKanji.put(100, 234);

    }

    public int getLessonLastKanji(int lessonNr) {
        Integer ret;
        ret = lessonsX10LastKanji.get(new Integer(lessonNr));
        if (ret == null) {
            log.error("non trovata lezione Heisig nr: " + lessonNr);
            return 0;
        }
        return ret;
    }

    public static boolean ignorable(char c) {
        if (c == ' ' || c == ',' || c == '-' || c == '.'
                || c == ':' || c == ';'
                || c == '(' || c == ')') {
            return true;
        }
        return false;
    }

    public static boolean containsKana(String s) {
        return containsHiragana(s) || containsKatakana(s);
    }

    public static boolean containsHiragana(String s) {
        if (s == null) {
            log.warn("containsHiragana was passed null string");
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (Character.UnicodeBlock.of(s.charAt(i)) == Character.UnicodeBlock.HIRAGANA)
                return true;
        }
        return false;
    }

    public static boolean containsKatakana(String s) {
        if (s == null) {
            log.warn("containsKatakana was passed null string");
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            if (Character.UnicodeBlock.of(s.charAt(i)) == Character.UnicodeBlock.KATAKANA)
                return true;
        }
        return false;
    }

    public static boolean containsKanji(String s) {
        if (s == null) {
            log.warn("containsKanji was passed null string");
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (Character.UnicodeBlock.of(s.charAt(i)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                return true;
        }
        return false;
    }

    public static boolean containsJapanese(String s) {
        return containsHiragana(s) || containsKatakana(s) || containsKanji(s);
    }

    /**
     * controlla se una stringa è tutta katakana
     * 
     * @param s
     * @return
     */
    public static boolean isAllKatakana(String s, boolean ignSep) {
        for (int i = 0; i < s.length(); i++) {
            if (ignSep && ignorable(s.charAt(i)))
                continue;
            if (Character.UnicodeBlock.of(s.charAt(i)) != Character.UnicodeBlock.KATAKANA)
                return false;
        }
        return true;
    }

    public static boolean isAllHiragana(String s, boolean ignSep) {
        for (int i = 0; i < s.length(); i++) {

            if (ignSep && ignorable(s.charAt(i)) || s.charAt(i) == ' ') // probabile
                                                                        // back,
                                                                        // c'era
                                                                        // un
                                                                        // carattere
                                                                        // corrorro
                                                                        // al
                                                                        // posto
                                                                        // dello
                                                                        // spazio
                continue;
            if (Character.UnicodeBlock.of(s.charAt(i)) != Character.UnicodeBlock.HIRAGANA)
                return false;
        }
        return true;
    }

    public static boolean isAllKanaSame(String s, boolean ignSep) {
        boolean ret = false;
        ret = ret || isAllHiragana(s, ignSep);
        ret = ret || isAllKatakana(s, ignSep);
        if (!ret) {
            ret = ret || isAllHiragana(s, ignSep);
            ret = ret || isAllKatakana(s, ignSep);
        }
        return ret;
    }

    public static boolean isAllKanji(String s, boolean ignSep) {
        for (int i = 0; i < s.length(); i++) {
            if (ignSep && ignorable(s.charAt(i)))
                continue;
            if (Character.UnicodeBlock.of(s.charAt(i)) != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                return false;
        }
        return true;
    }

    public static boolean isAllWestern(String s, boolean spaceOk) {
        boolean ret = true;
        ret = ret && !containsHiragana(s);
        ret = ret && !containsKatakana(s);
        ret = ret && !containsKanji(s);
        return ret;
    }

    public static boolean isAllEastern(String s, boolean spaceOk) {
        boolean ret = true;
        for (int i = 0; i < s.length(); i++) {
            if (Character.UnicodeBlock.of(s.charAt(i)) == Character.UnicodeBlock.HIRAGANA
                    || Character.UnicodeBlock.of(s.charAt(i)) == Character.UnicodeBlock.KATAKANA
                    || Character.UnicodeBlock.of(s.charAt(i)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {

            } else {
                return false;
            }
        }
        return true;
    }


    Map<Integer, Integer>                  lessonsX10LastKanji;
    private static org.apache.log4j.Logger log = Logger.getLogger(CJKUtils.class);

}
