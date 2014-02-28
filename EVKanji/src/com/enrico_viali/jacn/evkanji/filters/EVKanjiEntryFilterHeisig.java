package com.enrico_viali.jacn.evkanji.filters;

//import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.utils.*;


public class EVKanjiEntryFilterHeisig implements IEVRenderableFilter {
	
	public EVKanjiEntryFilterHeisig(int min,int max) {
		first = min;
		last = max;		
	}
	
	public void setMin(int min) {
		first = min;
	}
	public void setMax(int max) {
		last = max;
	}

	
	@Override
	public boolean includeIt(IRenderableAsTextLine ePar) {
		if (!(ePar instanceof EVKanjiEntry)) {
			log.error("tipo errato, atteso: "+EVKanjiEntry.class.getName());
			System.exit(1);
		}
		EVKanjiEntry e = (EVKanjiEntry) ePar;
		if (e.getHeisigNumber() < first)
			return false;
		if (e.getHeisigNumber() > last)
			return false;		
		return true;
	}

	int first;
	int last;

	private static org.apache.log4j.Logger log = Logger.getLogger(EVKanjiEntryFilterHeisig.class);
}
