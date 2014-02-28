package com.enrico_viali.jacn.edict;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;


public class POS {

    public POS() {
        partsOfSpeechMap = new Hashtable<String, String>();
        // -- POS markings
        partsOfSpeechMap.put("adj-i", "adjective (keiyoushi)");
        partsOfSpeechMap.put("adj-na", "adjectival nouns or quasi-adjectives (keiyodoshi)");
        partsOfSpeechMap.put("adj-no", "nouns which may take the genitive case particle `no'");
        partsOfSpeechMap.put("adj-pn", "pre-noun adjectival (rentaishi)");
        partsOfSpeechMap.put("adj-t", "`taru' adjective");
        partsOfSpeechMap.put("adj-f", "noun or verb acting prenominally (other than the above)");
        partsOfSpeechMap.put("adj", "former adjective classification (being removed)");
        partsOfSpeechMap.put("adv", "adverb (fukushi)");
        partsOfSpeechMap.put("adv-n", "adverbial noun");
        partsOfSpeechMap.put("adv-to", "adverb taking the `to' particle");

        partsOfSpeechMap.put("aux", "auxiliary");
        partsOfSpeechMap.put("aux-v", "auxiliary verb");
        partsOfSpeechMap.put("aux-adj", "auxiliary adjective");
        partsOfSpeechMap.put("conj", "conjunction");
        partsOfSpeechMap.put("ctr", "counter");
        partsOfSpeechMap.put("exp", "Expressions (phrases, clauses, etc.)");
        partsOfSpeechMap.put("id", "idiomatic expression"); // ???sembra mancare
                                                            // nell'originale
        partsOfSpeechMap.put("int", "interjection (kandoushi)");
        partsOfSpeechMap.put("iv", "irregular verb");
        partsOfSpeechMap.put("n", "noun (common) (futsuumeishi)");
        partsOfSpeechMap.put("n-adv", "adverbial noun (fukushitekimeishi)");

        partsOfSpeechMap.put("n-pref", "noun, used as a prefix");
        partsOfSpeechMap.put("n-suf", "noun, used as a suffix");
        partsOfSpeechMap.put("n-t", "noun (temporal) (jisoumeishi)");
        partsOfSpeechMap.put("num", "numeric");
        partsOfSpeechMap.put("pn", "pronoun");
        partsOfSpeechMap.put("pref", "prefix");
        partsOfSpeechMap.put("prt", "particle");
        partsOfSpeechMap.put("suf", "suffix");
        partsOfSpeechMap.put("v1", "Ichidan verb");
        partsOfSpeechMap.put("v2a-s", "Nidan verb with 'u' ending (archaic)");

        partsOfSpeechMap.put("v4h", "Yodan verb with `hu/fu' ending (archaic)");
        partsOfSpeechMap.put("v4r", "Yodan verb with `ru' ending (archaic)");
        partsOfSpeechMap.put("v5", "Godan verb (not completely classified)");
        partsOfSpeechMap.put("v5aru", "Godan verb - -aru special class");
        partsOfSpeechMap.put("v5b", "Godan verb with `bu' ending");
        partsOfSpeechMap.put("v5g", "Godan verb with `gu' ending");
        partsOfSpeechMap.put("v5k", "Godan verb with `ku' ending");
        partsOfSpeechMap.put("v5k-s", "Godan verb - iku/yuku special class");
        partsOfSpeechMap.put("v5m", "Godan verb with `mu' ending");
        partsOfSpeechMap.put("v5n", "Godan verb with `nu' ending");

        partsOfSpeechMap.put("v5r", "Godan verb with `ru' ending");
        partsOfSpeechMap.put("v5r-i", "Godan verb with `ru' ending (irregular verb)");
        partsOfSpeechMap.put("v5s", "Godan verb with `su' ending");
        partsOfSpeechMap.put("v5t", "Godan verb with `tsu' ending");
        partsOfSpeechMap.put("v5u", "Godan verb with `u' ending");
        partsOfSpeechMap.put("v5u-s", "Godan verb with `u' ending (special class)");
        partsOfSpeechMap.put("v5uru", "Godan verb - uru old class verb (old form");
        partsOfSpeechMap.put("v5z", "Godan verb with `zu' ending");
        partsOfSpeechMap.put("vz", "Ichidan verb - zuru verb - (alternative form of");
        partsOfSpeechMap.put("vi", "intransitive verb");

        partsOfSpeechMap.put("vk", "kuru verb - special class");
        partsOfSpeechMap.put("vn", "irregular nu verb");
        partsOfSpeechMap.put("vs", "noun or participle which takes the aux. verb suru");
        partsOfSpeechMap.put("vs-c", "su verb - precursor to the modern suru");
        partsOfSpeechMap.put("vs-i", "suru verb - irregular");
        partsOfSpeechMap.put("vs-s", "suru verb - special class");
        partsOfSpeechMap.put("vt", "transitive verb");

        // ----- Fiels of Application ---------

        partsOfSpeechMap.put("Buddh", "Buddhist term");
        partsOfSpeechMap.put("MA", "martial arts term");
        partsOfSpeechMap.put("comp", "computer terminology");
        partsOfSpeechMap.put("food", "food term");
        partsOfSpeechMap.put("geom", "geometry term");
        partsOfSpeechMap.put("gram", "grammatical term");
        partsOfSpeechMap.put("ling", "linguistics terminology");
        partsOfSpeechMap.put("math", "mathematics");
        partsOfSpeechMap.put("mil", "military");
        partsOfSpeechMap.put("physics", "physics terminology");

        // ----- Miscellaneous Markings -----------------
        partsOfSpeechMap.put("X", "rude or X-rated term");
        partsOfSpeechMap.put("abbr", "abbreviation");
        partsOfSpeechMap.put("arch", "archaism");
        partsOfSpeechMap.put("ateji", "ateji (phonetic) reading");
        partsOfSpeechMap.put("chn", "children's language");
        partsOfSpeechMap.put("col", "colloquialism");
        partsOfSpeechMap.put("derog", "derogatory term");
        partsOfSpeechMap.put("eK", "exclusively kanji");
        partsOfSpeechMap.put("ek", "exclusively kana");
        partsOfSpeechMap.put("fam", "familiar language");

        partsOfSpeechMap.put("fem", "female term or language");
        partsOfSpeechMap.put("gikun", "gikun (meaning) reading");
        partsOfSpeechMap.put("hon", "honorific or respectful (sonkeigo) language");
        partsOfSpeechMap.put("hum", "humble (kenjougo) language");
        partsOfSpeechMap.put("ik", "word containing irregular kana usage");
        partsOfSpeechMap.put("iK", "word containing irregular kanji usage");
        partsOfSpeechMap.put("id", "idiomatic expression");
        partsOfSpeechMap.put("io", "irregular okurigana usage");
        partsOfSpeechMap.put("m-sl", "manga slang");
        partsOfSpeechMap.put("male", "male term or language");

        partsOfSpeechMap.put("male-sl", "male slang");
        partsOfSpeechMap.put("ng", "neuter gender"); // sembra in più ???
        partsOfSpeechMap.put("oK", "word containing out-dated kanji");
        partsOfSpeechMap.put("obs", "obsolete term");
        partsOfSpeechMap.put("obsc", "obscure term");
        partsOfSpeechMap.put("ok", "out-dated or obsolete kana usage");
        partsOfSpeechMap.put("on-mim", "onomatopoeic or mimetic word");
        partsOfSpeechMap.put("poet", "poetical term");
        partsOfSpeechMap.put("pol", "polite (teineigo) language");
        partsOfSpeechMap.put("rare", "rare (now replaced by \"obsc\")");
        partsOfSpeechMap.put("sens", "sensitive word");

        partsOfSpeechMap.put("sl", "slang");
        partsOfSpeechMap.put("uK", "word usually written using kanji alone");
        partsOfSpeechMap.put("uk", "word usually written using kana alone");
        partsOfSpeechMap.put("vulg", "vulgar expression or word");

        partsOfSpeechMap.put("P", "Termine di uso frequente");

        // --- Gairaigo and Regional Words ---

        partsOfSpeechMap.put("kyb:", "Kyoto-ben");
        partsOfSpeechMap.put("osb:", "Osaka-ben");
        partsOfSpeechMap.put("ksb:", "Kansai-ben");
        partsOfSpeechMap.put("ktb:", "Kantou-ben");
        partsOfSpeechMap.put("tsb:", "Tosa-ben");
        partsOfSpeechMap.put("thb:", "Touhoku-ben");
        partsOfSpeechMap.put("tsug:", "Tsugaru-ben");
        partsOfSpeechMap.put("kyu:", "Kyuushuu-ben");

    }

    /**
     * if abbreviation is not present it returns abbreviation
     * 
     * @param abbreviation
     * @return
     */
    public String expandIfPresent(String abbreviation) {
        String ret;
        ret = partsOfSpeechMap.get(abbreviation);
        if (ret == null || ret.length() <= 0) {
            log.warn("abbreviation not found: " + abbreviation);
            ret = abbreviation;
        }
        return ret;
    }

    public boolean containsPOS(String key) {
        return partsOfSpeechMap.containsKey(key);
    }

    /**
     * in a string replaces the (P) (ik) Etc. with the fulll words
     * 
     * @return
     */
    public String replaceKE_Inf(String orig) {
        String ret = "";
        int area = 0; //
        int state = 0;
        int startWord = 0;

        ArrayList<String> frags = new ArrayList<String>();

        for (int i = 0; i < orig.length(); i++) {
            char c = orig.charAt(i);
            switch (area) {
                case 0: { // fuori parentesi
                    switch (c) {
                        case '(':
                            if (i > 0) {
                                String addIt = orig.substring(startWord, i);
                                frags.add(addIt);
                                startWord = i;
                            }
                            area = 1;
                            state = 1;
                            break;
                        case ')':
                            log.error("");
                            System.exit(-1);
                            break;
                        default: { // any other character
                            break;
                        }
                    } // switch
                    break; // state 0
                }
                case 1: { // scandendo annotazione
                    switch (c) {
                        case ')': { // fine annotazione
                            assert (state == 1);
                            String temp = expandIfPresent(orig.substring(startWord + 1, i));
                            frags.add("(" + temp);
                            startWord = i;
                            area = 0;
                            state = 0;
                            break;
                        }
                        default: { // normal character
                            break;
                        }
                    } // switch
                    break; // case 2
                }
            }
        }

        frags.add(orig.substring(startWord));

        for (String t : frags) {
            ret += t;
        }

        return ret;
    }

    Hashtable<String, String>  partsOfSpeechMap;

    private static org.apache.log4j.Logger log = Logger
                                                       .getLogger(POS.class);
}
