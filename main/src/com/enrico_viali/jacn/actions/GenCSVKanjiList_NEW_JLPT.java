package com.enrico_viali.jacn.actions;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.edict.Edict_2_Manager;
import com.enrico_viali.jacn.edict.KanjiCompositesMgr;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;
import com.enrico_viali.jacn.main.TestDriver;

public class GenCSVKanjiList_NEW_JLPT extends Action {

    public GenCSVKanjiList_NEW_JLPT(TestDriver tdr) {
        super("generates CSV kanji list", tdr);
        // TODO Auto-generated constructor stub
        levelInf = 1;
        levelSup = 5;
    }

    public GenCSVKanjiList_NEW_JLPT setLevelInf(int l) {
        levelInf = l;
        return this;
    }

    public GenCSVKanjiList_NEW_JLPT setLevelSup(int l) {
        levelSup = l;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.enrico_viali.jacn.actions.Action#perform()
     */
    @Override
    public boolean perform() throws Exception {
        log.error("va re-implementata");

        EVKanjiManager mgr = new EVKanjiManager(0, null);
        mgr.loadFromKanjidic();

        Edict_2_Manager em = new Edict_2_Manager();
        em.loadFromFile();
        KanjiCompositesMgr km = new KanjiCompositesMgr();
        km.loadFromRam(em.getHeadwordsList());

        // mgr.kanjidicMeanToItalianALL();
        mgr.enrichWithEdictComposite(km);
        log.error("rimossa la traduzione in Italiano");

        for (int i = levelSup; i >= levelInf; i--) {
            mgr.writeCSV_NEW_JLPTKanjiTable(i, "# visit http://kissako.it and http://www.facebook.com/Kissako.it for jlpt info, kana tables, word lists etc"
                    , "# please 'like' http://kissako.it and http://www.facebook.com/Kissako.it if you find this content useful");
        }
        mgr = null;
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    int                                    levelInf;
    int                                    levelSup;

    private static org.apache.log4j.Logger log = Logger
                                                       .getLogger(GenCSVKanjiList_NEW_JLPT.class);

}
