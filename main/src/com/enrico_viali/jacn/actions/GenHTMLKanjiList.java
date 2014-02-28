package com.enrico_viali.jacn.actions;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;
import com.enrico_viali.jacn.main.TestDriver;

public class GenHTMLKanjiList extends Action {

	public GenHTMLKanjiList(TestDriver tdr) {
		super("generates HTML fragment with kanji list", tdr);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.actions.Action#perform()
	 */
	@Override
	public boolean perform() throws Exception {
		log.error("va re-implementata");
		EVKanjiManager mgr = new EVKanjiManager(0,null);
		mgr.loadFromKanjidic();
		mgr.kanjidicMeanToItalianALL();
		mgr.writeHTMLHeisigKanjiTable();
		return true;
	}

	private static org.apache.log4j.Logger log = Logger
			.getLogger(GenHTMLKanjiList.class);

	@Override
	public String toString() {
		return this.getClass().getName();
	}
}
