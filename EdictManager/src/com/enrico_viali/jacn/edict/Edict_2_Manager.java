package com.enrico_viali.jacn.edict;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.libs.rdb_jdbc.*;

/**
 * @author enrico
 * 
 */

public class Edict_2_Manager extends EdictManager {

    public Edict_2_Manager() {
        super();
        if (partsOfSpeechMap == null) {
            partsOfSpeechMap = new Hashtable<String, String>();
            initPosMap();
            _headWordsByString = new Hashtable<String, Edict2K_Ele>();
            _headwordsForEasyExport = new ArrayList<Edict2K_Ele>();
            log.debug("created edict partsOfSpeechMap map");
        }

        String db = Cfg.getDirWork() + Cfg.FNAME_EDICT2_DB;
        try {
            RDBManagerSQLite dmdb;
            dmdb = new RDBManagerSQLite(db, false);
            dmdb.open(/* createIfNOtExists */true);
            _tabella = new EdictTable(dmdb);
        } catch (Exception e) {
            log.error(e);
            log.error("failed to create db manager with: " + db
                    + "\nworking dir: " + System.getProperty("user.dir"));
        }
    }

    /*
     * public ArrayList<IEdictEntry> selectComposForKanji(String kanji) {
     * 
     * ArrayList<IEdictEntry> all = new ArrayList<IEdictEntry>();
     * 
     * // --- estrazione primitiva --- ArrayList<IEdictEntry> start =
     * _tabella.selectKanjiCompos(kanji, " LIKE '" + kanji + "%'", 2);
     * Collections.sort(start);
     * 
     * ArrayList<IEdictEntry> end = _tabella.selectKanjiCompos(kanji, " LIKE '%"
     * + kanji + "'", 2); Collections.sort(end);
     * 
     * ArrayList<IEdictEntry> middle = _tabella.selectKanjiCompos(kanji,
     * " LIKE '%" + kanji + "%'", 3); Collections.sort(middle);
     * 
     * double nrComp = 0; // int iStart = 0; int iEnd = 0; int iMid = 0; do { if
     * (start.size() > 0) { all.add(start.get(0)); nrComp++; start.remove(0); }
     * if (end.size() > 0) { all.add(end.get(0)); nrComp++; end.remove(0); } if
     * (middle.size() > 0) { all.add(middle.get(0)); nrComp++; middle.remove(0);
     * } nrComp = nrComp + 0.1; // per evitare loop infinito quando liste //
     * vuote } while (nrComp < 5);
     * 
     * return all; }
     */

    public ArrayList<IEdictEntry> selectWord(String kanji) {
        // --- estrazione primitiva ---
        ArrayList<IEdictEntry> all = _tabella.selectExpression(kanji,
                /* condition */"",/* len > */0);
        Collections.sort(all);
        return all;
    }

    public ArrayList<IEdictEntry> selectReading(String reading) {
        // --- estrazione primitiva ---
        ArrayList<IEdictEntry> all = _tabella.selectReading(reading,
                /* condition */"",/* len > */0);
        Collections.sort(all);
        return all;
    }

    /**
     * Joion the original strings of a list of Edict entries
     * 
     * @param entries
     */
    String joinEdictEntriesStr(List<IEdictEntry> entries) {
        String merged = "";
        if (entries == null || entries.size() <= 0) {
            log.warn("null or empty entries");
            return "";
        }
        merged += entries.get(0).toString();
        for (int i = 1; i < entries.size(); i++) {
            merged += "<br>\n" + entries.get(i).toString();
        }
        return merged;
    }

    boolean containsPOS(String key) {
        return partsOfSpeechMap.containsKey(key);
    }

    public EntriesForHeadword findEntriesHeadword(String headword) {
        return null;
    }

    public EntriesForReading findEntriesReading(String reading) {
        return null;
    }

    public String toString() {
        log.error("da implementare");
        System.exit(1);
        return "";
    }

    public void dumpMemory(String sep) {
        log.info("edictw manager contains entries: " + this._headWordsByString.size());
    }

    public boolean loadFromFile() {
        return loadFromFile(Cfg.PATHNAME_EDICT2, Cfg.ENCODING_EUC_JP);
    }

    /**
     * 
     * @param filepath
     * @param encoding
     * @return
     */
    public boolean loadFromFile(String filepath, String encoding) {

        InputStreamReader isr;
        BufferedReader bufferedReader;

        int processedLines = 0;
        int entriesAdded = 0;
        try {

            log.info("Dir di lavoro: " + System.getProperty("user.dir")
                    + "\nopening: " + filepath);
            isr = new InputStreamReader(new FileInputStream(filepath), encoding);
            bufferedReader = new BufferedReader(isr);
            log.info("Encoding: in lettura " + isr.getEncoding());

            int lineNr = 1;
            String edictLine;
            edictLine = bufferedReader.readLine(); // saltiamo la prima linea
            for (; (edictLine = bufferedReader.readLine()) != null
                    && (lineNr < 200000); lineNr++) {

                // System.out.println(lineNr + ": " + edictLine);
                if (lineNr % 1000 == 0)
                    System.out.println("read: " + lineNr);
                // if (edictLine.length() > 0) continue;

                if (// !edictLine.matches(".*\\[[^]]{25,}\\].*")
                    // !edictLine.matches("[^/]*/.{50,}")
                    // !edictLine.matches(".*\\(6\\).*")
                    // edictLine.matches(".*\\[.*")
                false

                ) {
                    continue;
                }

                log.debug("line: " + lineNr + " " + edictLine);
                if (false
                // lineNr == 49841
                ) {
                    log.info("line: " + lineNr + " " + edictLine);
                    System.out.print("");
                }
                processedLines++;
                IEdictEntry entry = new Edict_2_Entry();
                if (!entry.buildFromEdictLine(edictLine, ":", entry, lineNr)) {
                    log.error("elaborazione riga edictByHeadWord returned false, exiting\nline nr "
                            + lineNr + " was" + edictLine);
                    break;
                }

                Edict_2_Entry e = (Edict_2_Entry) entry;
                if (false
                // !e.seqMatchesRgx(".*2610340.*")
                )
                    continue;
                log.debug(entry.toString());
                addEntry(e);
                entriesAdded++;
            }
            log.info("\nnr linee input:                 " + lineNr
                    + "\nnr linee elaborate:            " + processedLines
                    + "\nmemorizzati elementi:          " + entriesAdded
                    + "\nentries da export collection:  " + this._headwordsForEasyExport.size()
                    + "\nentries da Stringi collection: " + this._headwordsForEasyExport.size()

                    );
            return true;
        } catch (IOException e) {
            log.error("Failed to open Edict file: " + filepath, e);
        } /*
           * catch (ArrayIndexOutOfBoundsException e) { log.error("", e); }
           */catch (Exception e) {
            log.error("Exception", e);
        }

        return false;
    }


    @Override
    boolean addEntry(IEdictEntry e) {
        if (!(e instanceof Edict_2_Entry)) {
            log.error("trying to add object of wrong class");
            System.exit(1);
        }
        Edict_2_Entry entry = (Edict_2_Entry) e;
        // add all the headwords of the entry
        for (Edict2K_Ele headword : entry.getHeadWords()) {
            _headwordsForEasyExport.add(headword);
            _headWordsByString.put(headword.getKanjiWord(), headword);
        }
        return false;
    }

    
    public ArrayList<Edict2K_Ele> getHeadwordsList() {
        return _headwordsForEasyExport;
    }

    /**
     * I dati devono essere già caricati
     * 
     * @param tabella
     * @return
     * @throws SQLException
     */
    public boolean writeToDBTable(boolean deleteRows) throws SQLException {
        boolean ok = true;
        int nrInserFalliti = 0;
        int nrNonValidi = 0;
        /*
         * if (!_tabella.existsInDB()) { log.warn("tabella " +
         * _tabella.getName() + " not existing in database " +
         * _tabella.getDB().getDBURL() + " trying to create it");
         * _tabella.create(); }
         * 
         * if (deleteRows) _tabella.deleteAllRows();
         * 
         * int curElem = 0; for (IEdictEntry entry : edictByHeadWord.values()) {
         * curElem++; if (curElem % 200 == 0) {
         * log.info("scritto in DB elemento nr: " + curElem); } if (curElem %
         * 1000 == 0) { Runtime.getRuntime().gc(); } if (entry.getHeadWord() ==
         * null) { log.error("edictEntry: " + curElem +
         * " null kanjiWord field: \n" + entry.toString()); ok = false;
         * nrNonValidi++; continue; }
         * 
         * if (_tabella.entryExists(entry.getHeadWord())) {
         * log.debug("entry esiste gia, kanjiWord: " + entry.getHeadWord()); }
         * if (_tabella.writeToDB(entry, null, true)) {
         * log.debug("written in table evkanj entry: " + entry.toString(":")); }
         * else { log.error("failed write in table " + _tabella.getName() + " "
         * + curElem + " entry: " + entry.toString(":")); nrInserFalliti++; ok =
         * false; log.info("non esiste"); // break; //non si esce } entry =
         * null; }
         * 
         * log.info("saving Edict to DB; processed items: " + curElem +
         * " invalid items: " + nrNonValidi + " failed inserts: " +
         * nrInserFalliti);
         */
        return ok;
    }

    static void initPosMap() {
        partsOfSpeechMap.put("adj-i", "adjective (keiyoushi)");
        partsOfSpeechMap.put("adj-na",
                "adjectival nouns or quasi-adjectives (keiyodoshi)");
        partsOfSpeechMap.put("adj-no",
                "nouns which may take the genitive case particle `no'");
        partsOfSpeechMap.put("adj-pn", "pre-noun adjectival (rentaishi)");
        partsOfSpeechMap.put("adj-t", "`taru' adjective");
        partsOfSpeechMap.put("adj-f",
                "noun or verb acting prenominally (other than the above)");
        partsOfSpeechMap.put("adj",
                "former adjective classification (being removed)");
        partsOfSpeechMap.put("adv", "adverb (fukushi)");
        partsOfSpeechMap.put("adv-n", "adverbial noun");
        partsOfSpeechMap.put("adv-to", "adverb taking the `to' particle");
        partsOfSpeechMap.put("aux", "auxiliary");
        partsOfSpeechMap.put("aux-v", "auxiliary verb");
        partsOfSpeechMap.put("aux-adj", "auxiliary adjective");
        partsOfSpeechMap.put("conj", "conjunction");
        partsOfSpeechMap.put("ctr", "counter");
        partsOfSpeechMap.put("exp", "Expressions (phrases, clauses, etc.)");
        partsOfSpeechMap.put("id", "idiomatic expression");
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
        partsOfSpeechMap.put("v5", "Godan verb (not completely classified)");
        partsOfSpeechMap.put("v5b", "Godan verb with `bu' ending");
        partsOfSpeechMap.put("v5g", "Godan verb with `gu' ending");
        partsOfSpeechMap.put("v5k", "Godan verb with `ku' ending");
        partsOfSpeechMap.put("v5k-s", "Godan verb - iku/yuku special class");
        partsOfSpeechMap.put("v5m", "Godan verb with `mu' ending");
        partsOfSpeechMap.put("v5n", "Godan verb with `nu' ending");
        partsOfSpeechMap.put("v5r", "Godan verb with `ru' ending");
        partsOfSpeechMap.put("v5r-i",
                "Godan verb with `ru' ending (irregular verb)");
        partsOfSpeechMap.put("v5s", "Godan verb with `su' ending");
        partsOfSpeechMap.put("v5t", "Godan verb with `tsu' ending");
        partsOfSpeechMap.put("v5u", "Godan verb with `u' ending");
        partsOfSpeechMap.put("v5u-s",
                "Godan verb with `u' ending (special class)");
        partsOfSpeechMap.put("v5uru",
                "Godan verb - uru old class verb (old form");
        partsOfSpeechMap.put("v5z", "Godan verb with `zu' ending");
        partsOfSpeechMap.put("vz",
                "Ichidan verb - zuru verb - (alternative form of");
        partsOfSpeechMap.put("vi", "intransitive verb");
        partsOfSpeechMap.put("vk", "kuru verb - special class");
        partsOfSpeechMap.put("vn", "irregular nu verb");
        partsOfSpeechMap.put("vs",
                "noun or participle which takes the aux. verb suru");
        partsOfSpeechMap.put("vs-i", "suru verb - irregular");
        partsOfSpeechMap.put("vs-s", "suru verb - special class");
        partsOfSpeechMap.put("vt", "transitive verb");

        partsOfSpeechMap.put("Field of Application", "");

        partsOfSpeechMap
                .put("A number of entries are marked with a specific field of application. Current fields and tags are:",
                        "");

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

        partsOfSpeechMap.put("Miscellaneous", "");

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
        partsOfSpeechMap.put("hon",
                "honorific or respectful (sonkeigo) language");
        partsOfSpeechMap.put("hum", "humble (kenjougo) language");
        partsOfSpeechMap.put("iK", "word containing irregular kanji usage");
        partsOfSpeechMap.put("id", "idiomatic expression");
        partsOfSpeechMap.put("io", "irregular okurigana usage");
        partsOfSpeechMap.put("m-sl", "manga slang");
        partsOfSpeechMap.put("male", "male term or language");
        partsOfSpeechMap.put("male-sl", "male slang");
        partsOfSpeechMap.put("ng", "neuter gender");
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

        partsOfSpeechMap.put("kyb:", "Kyoto-ben");
        partsOfSpeechMap.put("osb:", "Osaka-ben");
        partsOfSpeechMap.put("ksb:", "Kansai-ben");
        partsOfSpeechMap.put("ktb:", "Kantou-ben");
        partsOfSpeechMap.put("tsb:", "Tosa-ben");
        partsOfSpeechMap.put("thb:", "Touhoku-ben");
        partsOfSpeechMap.put("tsug:", "Tsugaru-ben");
        partsOfSpeechMap.put("kyu:", "Kyuushuu-ben");

    }

    EdictTable                             _tabella;

    private static Map<String, String>     partsOfSpeechMap;

    ArrayList<Edict2K_Ele>                 _headwordsForEasyExport;                      // per
    Hashtable<String, Edict2K_Ele>         _headWordsByString;

    private static org.apache.log4j.Logger log = Logger
                                                       .getLogger(Edict_2_Manager.class);

    @Override
    public ArrayList<IEdictEntry> selectComposForKanji(String kanji) {
        // TODO Auto-generated method stub
        return null;
    }

}
