package com.enrico_viali.jacn.actions;

import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;
import com.enrico_viali.jacn.main.TestDriver;

public class ImportFileKanjiWithoutHeisig extends Action {

	public ImportFileKanjiWithoutHeisig(TestDriver tdr) {
		super("import file of kanjis without Heisig info",tdr);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.actions.Action#perform()
	 */
	@Override
	public boolean perform() throws Exception {
		EVKanjiManager evmgr = new EVKanjiManager(0,Cfg.SEP_EVKANJI_IN);
		return _tdrv.getKanjiManager().importDeltaFile(evmgr);
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}
	
	

}
