package com.enrico_viali.jacn.edict;

import java.util.*;

import org.apache.log4j.Logger;

public class EntriesForReading implements IEntriesList {

	public EntriesForReading(String readPar) {
		super();
		reading = readPar;
		this.entries = new ArrayList<IEdictEntry>();
	}

	@Override
	public int getNrEntries() {
		return entries.size();
	}

	@Override
	public boolean add(IEdictEntry e) {
		if (!reading.equals(e.getReading())) {
			log.error("");
			return false;
		}
		return entries.add(e);
	}

	@Override
	public ArrayList<IEdictEntry> getEntries() {
		return entries;
	}

	@Override
	public String getAllEdictEntriesStr(String endEdictSep) {
		String s = "";
		for (IEdictEntry e: entries) {
			s+= e.getHeadWord()+" ["+e.getReading()+"] "+e.getWholeDescription();
			if (endEdictSep != null)
				s+=endEdictSep;
		}
		return s;
	}

	
	String reading;
	ArrayList<IEdictEntry> entries;
	private static org.apache.log4j.Logger log = Logger.getLogger(EntriesForHeadword.class);
}
