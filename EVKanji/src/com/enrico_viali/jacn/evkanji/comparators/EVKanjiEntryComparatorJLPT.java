package com.enrico_viali.jacn.evkanji.comparators;

import java.util.Comparator;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.EVJPCNEntry;
import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;

public class EVKanjiEntryComparatorJLPT implements Comparator<EVJPCNEntry> {
	@Override
	public int compare(EVJPCNEntry onePar, EVJPCNEntry twoPar) {
		if (!(onePar instanceof EVKanjiEntry || 
		twoPar instanceof  EVKanjiEntry)) {
			log.error("wrong type for one of the two ");
			System.exit(1);
		}
		EVKanjiEntry one = (EVKanjiEntry) onePar;
		EVKanjiEntry two = (EVKanjiEntry) twoPar;
		
		if (one.getOldJlpt() < two.getOldJlpt())
			return -1;
		if (one.getOldJlpt() > two.getOldJlpt())
			return 1;
		
		// -- uguali per joyo -- numero tratti
		if (one.getStrokes() < two.getStrokes())
			return -1;
		if (one.getStrokes() > two.getStrokes())
			return 1;
		
		return 0;
	}
	private static org.apache.log4j.Logger log = Logger.getLogger(EVKanjiEntryComparatorJLPT.class);
}
