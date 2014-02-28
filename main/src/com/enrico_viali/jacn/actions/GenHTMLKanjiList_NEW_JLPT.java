package com.enrico_viali.jacn.actions;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.edict.Edict_2_Manager;
import com.enrico_viali.jacn.edict.KanjiCompositesMgr;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;
import com.enrico_viali.jacn.main.TestDriver;

public class GenHTMLKanjiList_NEW_JLPT extends Action {

	public GenHTMLKanjiList_NEW_JLPT(TestDriver tdr) {
		super("generates HTML fragment with New JLPT kanji list", tdr);
		levelInf = 1;
		levelSup = 5;
	}

	public GenHTMLKanjiList_NEW_JLPT setLevelInf(int l) {
		levelInf = l;
		return this;
	}

	public GenHTMLKanjiList_NEW_JLPT setLevelSup(int l) {
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
	    log.info("perform: "+getName());
		log.error("va re-implementata");
		EVKanjiManager mgr = new EVKanjiManager(0, null);
		mgr.loadFromKanjidic();
		
        Edict_2_Manager em = new Edict_2_Manager();
        em.loadFromFile();
        KanjiCompositesMgr km = new KanjiCompositesMgr();
        km.loadFromRam(em.getHeadwordsList());

        // mgr.kanjidicMeanToItalianALL();
        mgr.enrichWithEdictComposite(km);

		for (int i = levelSup; i >= levelInf; i--) {
			mgr.writeHTML_NEW_JLPTKanjiTable(i);
		}
		mgr = null;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

	
	int levelInf;
	int levelSup;
	
	private static org.apache.log4j.Logger log = Logger
			.getLogger(GenHTMLKanjiList_NEW_JLPT.class);

}
