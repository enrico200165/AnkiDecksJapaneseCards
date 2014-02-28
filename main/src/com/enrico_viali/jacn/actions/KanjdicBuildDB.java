package com.enrico_viali.jacn.actions;

import com.enrico_viali.jacn.kanjidic.KanjiDicManager;
import com.enrico_viali.jacn.main.TestDriver;

public class KanjdicBuildDB extends Action {

	public KanjdicBuildDB(TestDriver tdr) {
		super("Build derby db from file", tdr);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.actions.Action#perform()
	 */
	@Override
	public boolean perform() throws Exception {

			KanjiDicManager fileMgr = new KanjiDicManager();
			fileMgr.load();
		
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
