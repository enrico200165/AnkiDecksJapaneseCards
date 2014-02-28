package com.enrico_viali.jacn.edict;

import java.util.*;

import org.apache.log4j.Logger;



/**
 * @author enrico
 * It seems to be: the collection of entries  that correspond to a headword
 *
 */
class EntriesForHeadword implements IEntriesList {

	public EntriesForHeadword(String headword) {
		super();
		this.headWord = headword;
		this.entries = new ArrayList<IEdictEntry>();
	}

	@Override
	public int getNrEntries() {
		return entries.size();
	}

	public boolean add(IEdictEntry e) {
		if (!headWord.equals(e.getHeadWord())) {
			log.error("");
			return false;
		}
		return entries.add(e);
	}

	public ArrayList<IEdictEntry> getEntries() {
		return entries;
	}

	@Override
	public String getAllEdictEntriesStr(String endEntrySep) {
		String s = "";
		for (IEdictEntry e: entries) {
			s+= e.getHeadWord()+" ["+e.getReading()+"] "+e.getWholeDescription();
			if (endEntrySep != null)
				s+= endEntrySep;
		}
		return s;
	}

	String headWord;
	ArrayList<IEdictEntry> entries;

	private static org.apache.log4j.Logger log = Logger.getLogger(EntriesForHeadword.class);
}
