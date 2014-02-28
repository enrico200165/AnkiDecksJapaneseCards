package com.enrico_viali.jacn.evkanji.filters;

//import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.utils.*;


public class EVKanjiEntryFilterBy_OLD_JLPT implements IEVRenderableFilter {
	
	public EVKanjiEntryFilterBy_OLD_JLPT(int min,int max) {
		minLev = min;
		maxLev = max;		
	}
	
	@Override
	public boolean includeIt(IRenderableAsTextLine ePar) {
		if (!(ePar instanceof EVKanjiEntry)) {
			log.error("tipo errato, atteso: "+EVKanjiEntry.class.getName());
			System.exit(1);
		}
		EVKanjiEntry e = (EVKanjiEntry) ePar;
		if (e.getOldJlpt() < minLev)
			return false;
		if (e.getOldJlpt() > maxLev)
			return false;		
		return true;
	}

	int minLev;
	int maxLev;

	private static org.apache.log4j.Logger log = Logger.getLogger(EVKanjiEntryFilterBy_OLD_JLPT.class);
}
