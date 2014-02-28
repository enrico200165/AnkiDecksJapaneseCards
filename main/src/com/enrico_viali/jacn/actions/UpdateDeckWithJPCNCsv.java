package com.enrico_viali.jacn.actions;

import com.enrico_viali.jacn.common.*;
import com.enrico_viali.jacn.main.TestDriver;

public class UpdateDeckWithJPCNCsv extends Action {

	public UpdateDeckWithJPCNCsv(TestDriver tdr) {
		super("update jpcndeck from master file", tdr);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.actions.Action#perform()
	 */
	@Override
	public boolean perform() throws Exception {
		if (_tdrv.getKanjiManager().updateAnkiJPCNDeck(Cfg.getDirAnkiData(), Cfg.getDirWork(), "jpcn", "anki",
		false /* ignErr */, FieldUpdate.OVERWRITE)) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
