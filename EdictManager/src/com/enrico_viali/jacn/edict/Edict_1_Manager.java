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

public class Edict_1_Manager extends EdictManager {

    public Edict_1_Manager() {
        super();
        edictByHeadWord = new Hashtable<String, IEdictEntry>();
        if (partsOfSpeechMap == null) {
            entriesByHeadWord = new Hashtable<String, EntriesForHeadword>();
            entriesByReading = new Hashtable<String, EntriesForReading>();
            partsOfSpeechMap = new POS();
            log.debug("created edict partsOfSpeechMap map");
        }

        String db = Cfg.getDirWork() + Cfg.FNAME_EDICT_DB;
        try {
            RDBManagerSQLite dmdb;
            dmdb = new RDBManagerSQLite(db, true);
            dmdb.open(/* createIfNOtExists */true);
            _tabella = new EdictTable(dmdb);
        } catch (Exception e) {
            log.error(e);
            log.error("failed to create db manager with: " + db +
                    "\nworking dir: " + System.getProperty("user.dir"));
        }
    }

    @Override
    public ArrayList<IEdictEntry> selectComposForKanji(String kanji) {

        ArrayList<IEdictEntry> all = new ArrayList<IEdictEntry>();

        // --- estrazione primitiva ---
        ArrayList<IEdictEntry> start = _tabella.selectKanjiCompos(kanji,
                " LIKE '" + kanji + "%'", 2);
        Collections.sort(start);

        ArrayList<IEdictEntry> end = _tabella.selectKanjiCompos(kanji,
                " LIKE '%" + kanji + "'", 2);
        Collections.sort(end);

        ArrayList<IEdictEntry> middle = _tabella.selectKanjiCompos(kanji,
                " LIKE '%" + kanji + "%'", 3);
        Collections.sort(middle);

        double nrComp = 0;
        // int iStart = 0; int iEnd = 0; int iMid = 0;
        do {
            if (start.size() > 0) {
                all.add(start.get(0));
                nrComp++;
                start.remove(0);
            }
            if (end.size() > 0) {
                all.add(end.get(0));
                nrComp++;
                end.remove(0);
            }
            if (middle.size() > 0) {
                all.add(middle.get(0));
                nrComp++;
                middle.remove(0);
            }
            nrComp = nrComp + 0.1; // per evitare loop infinito quando liste
            // vuote
        } while (nrComp < 5);

        return all;
    }

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
        // Collections.sort(all);
        log.error("devi implemntare il metodo, esco");
        System.exit(-11);
        return all;
    }

    boolean addEntry(IEdictEntry e) {
        edictByHeadWord.put(e.getHeadWord(), e);

        EntriesForHeadword entriesHead;
        if (!entriesByHeadWord.containsKey(e.getHeadWord())) {
            entriesHead = new EntriesForHeadword(e.getHeadWord());
            entriesByHeadWord.put(e.getHeadWord(), entriesHead);
        } else {
            entriesHead = entriesByHeadWord.get(e.getHeadWord());
        }
        entriesHead.add(e);

        EntriesForReading entriesRead;
        if (!entriesByReading.containsKey(e.getReading())) {
            entriesRead = new EntriesForReading(e.getReading());
            entriesByReading.put(e.getReading(), entriesRead);
        } else {
            entriesRead = entriesByReading.get(e.getReading());
        }
        entriesRead.add(e);

        return true;
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
        return partsOfSpeechMap.containsPOS(key);
    }

    /**
     * Ignora il fatto che un headWord può avere più entries (uno per ogni
     * reading) dovrebbe essere sostituita dalle nuove funzioni che per una data
     * headWord o reading ritornano la lista di entries corrispondenti
     * 
     * @param key
     * @return
     */
    public IEdictEntry deprecatedFindEntry(String key) {
        return edictByHeadWord.get(key);
    }

    public EntriesForHeadword findEntriesHeadword(String headword) {
        if (entriesByHeadWord == null) {
            log.error("");
            return null;
        }
        return entriesByHeadWord.get(headword);
    }

    public EntriesForReading findEntriesReading(String reading) {
        if (entriesByReading.get(reading) == null) {
            return null;
        }
        return entriesByReading.get(reading);
    }

    public String toString() {
        String s = "edict contains entries: " + edictByHeadWord.size();
        return s;
    }

    public void dumpMemory(String sep) {
        log.info("edict contains entries: " + edictByHeadWord.size());
        /*
         * for (String headword: edictByHeadWord.keySet()) { log.info(headword);
         * }
         */
    }

    public boolean loadFromFile() {
        return loadFromFile(Cfg.PATHNAME_EDICT, Cfg.ENCODING_EUC_JP);
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
                    && (lineNr < 999999); lineNr++) {
                Edict_1_Entry edictSimpleEntry = new Edict_1_Entry();
                if (!edictSimpleEntry.buildFromEdictLine(edictLine, ":",
                        edictSimpleEntry, lineNr)) {
                    log.error("elaborazione riga edictByHeadWord returned false, exiting\nline nr "
                            + lineNr + " was" + edictLine);
                    break;
                }
                this.addEntry(edictSimpleEntry);
            }
            log.info("elaborate nr linee input: " + lineNr
                    + " memorizzati elementi: " + this.edictByHeadWord.size()
                    + " dupl " + (lineNr - 1 - edictByHeadWord.size()));
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

        if (!_tabella.existsInDB()) {
            log.warn("tabella " + _tabella.getName()
                    + " not existing in database "
                    + _tabella.getDB().getDBURL() + " trying to create it");
            _tabella.create();
        }

        if (deleteRows)
            _tabella.deleteAllRows();

        int curElem = 0;
        for (IEdictEntry entry : edictByHeadWord.values()) {
            curElem++;
            if (curElem % 200 == 0) {
                log.info("scritto in DB elemento nr: " + curElem);
            }
            if (curElem % 1000 == 0) {
                Runtime.getRuntime().gc();
            }
            if (entry.getHeadWord() == null) {
                log.error("edictEntry: " + curElem
                        + " null kanjiWord field: \n" + entry.toString());
                ok = false;
                nrNonValidi++;
                continue;
            }

            if (_tabella.entryExists(entry.getHeadWord())) {
                log.debug("entry esiste gia, kanjiWord: " + entry.getHeadWord());
            }
            if (_tabella.writeToDB(entry, null, true)) {
                log.debug("written in table evkanj entry: "
                        + entry.toString(":"));
            } else {
                log.error("failed write in table " + _tabella.getName() + " "
                        + curElem + " entry: " + entry.toString(":"));
                nrInserFalliti++;
                ok = false;
                log.info("non esiste");
                // break; //non si esce
            }
            entry = null;
        }

        log.info("saving Edict to DB; processed items: " + curElem
                + " invalid items: " + nrNonValidi + " failed inserts: "
                + nrInserFalliti);

        return ok;
    }

    EdictTable                             _tabella;
    POS                                    partsOfSpeechMap;

    private static org.apache.log4j.Logger log = Logger
                                                       .getLogger(Edict_1_Manager.class);
}
