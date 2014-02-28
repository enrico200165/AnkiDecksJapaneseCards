package com.enrico_viali.jacn.actions;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.jacn.main.TestDriver;
import com.enrico_viali.utils.Utl;

public class GenerateFileKanjiSenzaHeisig extends Action {

	public GenerateFileKanjiSenzaHeisig(TestDriver tdr) {
		super("generate Heisig \"delta\" file, ie. kanjis with Heisig info not filled",tdr);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.actions.Action#perform()
	 */
	@Override
	public boolean perform() throws Exception {
		if (!_tdrv.getKanjiManager().generateDeltaFile(Cfg.getDirJPCNData() + Cfg.FNAME_EVKANJI, 1, Cfg.NRMAX_KANJI,Utl.NOT_INITIALIZED_INT)) {
			log.info("nuovo delta file non generato");
			return false;
		}
		return true;
	}

	
	private static org.apache.log4j.Logger log = Logger.getLogger(GenerateFileKanjiSenzaHeisig.class);


	@Override
	public String toString() {
		return this.getClass().getName();
	}
	
}
