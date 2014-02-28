package com.enrico_viali.jacn.evkanji.filters;

//import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.utils.*;



public class EVKanjiEntryFilterByJoyo implements IEVRenderableFilter {
	
	public EVKanjiEntryFilterByJoyo(int min,int max) {
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
		if (e.getGrade() < minLev)
			return false;
		if (e.getGrade() > maxLev)
			return false;		
		return true;
	}

	int minLev;
	int maxLev;

	private static org.apache.log4j.Logger log = Logger.getLogger(EVKanjiEntryFilterByJoyo.class);
}
