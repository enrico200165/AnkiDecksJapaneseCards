package com.enrico_viali.jacn.evkanji.actions;

import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;

/**
 * @author enrico
 * Functionoid for Actions to be executed in a manager (currently EVKanjiManager, but probably
 * that will be generalized at a higher level)
 */
public interface IEVKanjiEntryAction {
	
	public boolean elabora(EVKanjiEntry k, EVKanjiManager mgr, Object otherParams);
	
	public boolean initialAction(EVKanjiEntry k, EVKanjiManager mgr, Object otherParams);

	public boolean finalAction(EVKanjiEntry k, EVKanjiManager mgr, Object otherParams);
	
	
}
