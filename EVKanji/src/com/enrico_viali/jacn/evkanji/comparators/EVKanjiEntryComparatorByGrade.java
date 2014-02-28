package com.enrico_viali.jacn.evkanji.comparators;

import java.util.Comparator;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;

/**
 * @author enrico
 * Sorts Kanjis by
 * - school grade
 * - number of traits
 * - unicode code point
 */
public class EVKanjiEntryComparatorByGrade implements Comparator<EVKanjiEntry> {

	public int compare(EVKanjiEntry onePar, EVKanjiEntry twoPar) {
		if (!(onePar instanceof EVKanjiEntry || 
		twoPar instanceof  EVKanjiEntry)) {
			log.error("wrong type for one of the two ");
			System.exit(1);
		}
		EVKanjiEntry one = (EVKanjiEntry) onePar;
		EVKanjiEntry two = (EVKanjiEntry) twoPar;
		
		// grado scolastico
		if (one.getGrade() < two.getGrade())
			return -1;
		if (one.getGrade() > two.getGrade())
			return 1;
		
		// -- joyo --
		if (one.getStrokes() < two.getStrokes())
			return -1;
		if (one.getStrokes() > two.getStrokes())
			return 1;
		
		if (one.getKanji().codePointAt(0) < two.getKanji().codePointAt(0))
			return -1;
		if (one.getKanji().codePointAt(0) > (int)two.getKanji().codePointAt(0))
			return 1;
		
		log.error("should never get down here");
		System.exit(-1);
				
		return one.getKanji().compareTo(two.getKanji()); //dummy
	}
	private static org.apache.log4j.Logger log = Logger.getLogger(EVKanjiEntryComparatorByGrade.class);
}
