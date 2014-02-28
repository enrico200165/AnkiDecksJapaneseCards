package com.enrico_viali.jacn.actions;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.jacn.edict.Edict_2_Manager;
import com.enrico_viali.jacn.main.TestDriver;
import com.enrico_viali.jacn.words.WordList;

public class GenNJLPTWordsList extends Action {

    public GenNJLPTWordsList(TestDriver tdr) {
        super("create word deck from word list", tdr);
    }

    @Override
    public boolean perform() throws Exception {
        WordList wlBldr = new WordList();
        wlBldr.loadFromFile(Cfg.getDirJPCNData() + "ref_input/njlpt5_1.txt");
        wlBldr.dump();

        
        Edict_2_Manager em = new Edict_2_Manager();
        // em.loadFromFile();
        // KanjiCompositesMgr km = new KanjiCompositesMgr();
        // km.loadFromRam(em.getHeadwordsList());
        // mgr.kanjidicMeanToItalianALL();
        // mgr.enrichWithEdictComposite(km);        
        
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    private static org.apache.log4j.Logger log = Logger.getLogger(GenNJLPTWordsList.class);
}
