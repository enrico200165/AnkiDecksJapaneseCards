package com.enrico_viali.jacn.edict;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class EdictUtils {

    static final String DESCR_DELIM     = "/\\(";
    static final String SENSE_DELIM     = "\\((\\d+)\\)";
    static final String KANJIKANA_REGEX = "^([^/]*)";
    static final String ENTRYNUM_REGEX  = "/(EntL[^/]*)/$";
    static final String ALLINFO_REGEX   = "[ \t](/.*)";
    static final String LINE_REGEX      = KANJIKANA_REGEX + ALLINFO_REGEX + ENTRYNUM_REGEX;
    static final String GLOSS_REGEX     = " *(/[^/]*) *";
    static final String INFO_REGEX      = "\\(([^)]*)\\)";
    static final String HEADWORDS_REGEX = "([^\\[]*)(?:\\[(.*)\\])?";

    /**
     * Ricostruito a posteriori, non sicuro al 100% rimuove il marcatore (P) di
     * edict
     * 
     * @param s
     * @return
     */
    public static String noP(String s) {
        if (s == null) {
            log.error("null string, exiting");
            System.exit(1);
        }
        String r = s.replaceAll("/ *\\( *P *\\) */", "");
        r = r.replaceAll(" */ *", "<br />");
        // Pattern p = Pattern.compile("/P/");
        // Matcher senseMatcher = senseMarking.matcher(e);
        return r;
    }

    public static String commaSpace(String s) {
        String r = s.replaceAll(",", ", ");
        return r;
    }

    public static Pattern infoPattern = Pattern.compile(INFO_REGEX);
    
    private static org.apache.log4j.Logger log = Logger.getLogger(EdictUtils.class);
}
