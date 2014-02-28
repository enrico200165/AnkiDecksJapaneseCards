package com.enrico_viali.jacn.edict;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.libs.rdb_jdbc.*;

/**
 * @author enrico
 * 
 */

public abstract class EdictManager {

    public EdictManager() {
        super();
        // fuori questa funzionalità

        edictByHeadWord = new Hashtable<String, IEdictEntry>();
        if (entriesByHeadWord == null) {
            entriesByHeadWord = new Hashtable<String, EntriesForHeadword>();
            entriesByReading = new Hashtable<String, EntriesForReading>();
            pos = new POS(); // per non sconvolgere troppo il codide ora che ho portato
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

    abstract public ArrayList<IEdictEntry> selectComposForKanji(String kanji);

    abstract public ArrayList<IEdictEntry> selectWord(String kanji);

    abstract public ArrayList<IEdictEntry> selectReading(String reading);

    abstract boolean addEntry(IEdictEntry e);

  

    boolean containsPOS(String key) {
        return pos.containsPOS(key);
    }

    abstract public EntriesForHeadword findEntriesHeadword(String headword);

    abstract public EntriesForReading findEntriesReading(String reading);

    public void dumpMemory(String sep, boolean elements) {
        log.info("edict contains entries: " + edictByHeadWord.size());
        /*
         * for (String headword: edictByHeadWord.keySet()) { log.info(headword);
         * }
         */
    }

    abstract public boolean loadFromFile();

    abstract public boolean loadFromFile(String filepath, String encoding);

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
    POS pos; // per non sconvolgere troppo il codide ora che ho portato
    // fuori questa funzionalità

    Map<String, IEdictEntry>               edictByHeadWord;
    Map<String, EntriesForHeadword>        entriesByHeadWord;
    Map<String, EntriesForReading>         entriesByReading;

    private static org.apache.log4j.Logger log = Logger
                                                       .getLogger(EdictManager.class);
}
