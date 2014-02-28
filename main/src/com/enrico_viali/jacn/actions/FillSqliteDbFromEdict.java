package com.enrico_viali.jacn.actions;

import com.enrico_viali.jacn.edict.EdictManager;
import com.enrico_viali.jacn.edict.Edict_1_Manager;
import com.enrico_viali.jacn.main.TestDriver;

public class FillSqliteDbFromEdict extends Action {

	public FillSqliteDbFromEdict(TestDriver tdr) {
		super("fill sqlite edictByHeadWord DB from edictByHeadWord file",tdr);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.actions.Action#perform()
	 */
	@Override
	public boolean perform() throws Exception {
		EdictManager eMgr = new Edict_1_Manager();
		if (!eMgr.loadFromFile()) {
			return false;
		}
		if(!eMgr.writeToDBTable(/* Cfg.scratchEdictDB default false */true)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
