package com.enrico_viali.jacn.actions;

import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.jacn.main.TestDriver;

public class JPCNCsvExport extends Action {

	public JPCNCsvExport(TestDriver tdr) {
		super("write export file for anki",tdr);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.actions.Action#perform()
	 */
	@Override
	public boolean perform() throws Exception {
		return _tdrv.getKanjiManager().evKanjiMgr.writeToCSV(Cfg.getDirWork() + Cfg.FNAME_EVKANJI, Cfg.getOvwEVKanjiFile(), true); 
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
