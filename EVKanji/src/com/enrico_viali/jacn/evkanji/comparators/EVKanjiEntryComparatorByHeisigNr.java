package com.enrico_viali.jacn.evkanji.comparators;

import java.util.Comparator;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;

public class EVKanjiEntryComparatorByHeisigNr implements Comparator<EVKanjiEntry> {

	@Override
	public int compare(EVKanjiEntry onePar, EVKanjiEntry twoPar) {
		if (!(onePar instanceof EVKanjiEntry || 
		twoPar instanceof  EVKanjiEntry)) {
			log.error("wrong type for one of the two ");
			System.exit(1);
		}
		EVKanjiEntry one = (EVKanjiEntry) onePar;
		EVKanjiEntry two = (EVKanjiEntry) twoPar;
		
		if (one.getHeisigNumber() < two.getHeisigNumber())
			return -1;
		else if (one.getHeisigNumber() >  two.getHeisigNumber())
			return 1;
		else
			return 0;
	}
	private static org.apache.log4j.Logger log = Logger.getLogger(EVKanjiEntryComparatorByHeisigNr.class);
}
