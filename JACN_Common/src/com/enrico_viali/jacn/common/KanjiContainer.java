package com.enrico_viali.jacn.common;

import java.util.*;

import org.apache.log4j.Logger;


public class KanjiContainer<Entry> {

	public KanjiContainer(int lastIdx) {
		super();
		lastElementIdx = lastIdx;
		entriesByNr = new HashMap<Long, Entry>();
		this.entriesByStr = new HashMap<String, Entry>();
	}

	public boolean insert(String kanji, Entry e, long pos) {
		if (1 <= pos && pos <= lastElementIdx) {
			entriesByNr.put(pos, e);
			entriesByStr.put(kanji, e);
			return true;
		}
		return false;
	}

	public int size() {
		if (this.entriesByNr.size() != this.entriesByStr.size()) {
			log.error("container, subcontainers with different size: "
			+ this.entriesByNr.size() + " vs " + entriesByStr.size());
		}
		return this.entriesByNr.size();
	}

	public Entry getByNr(int n) {		
		if (n < 0 || n > this.lastElementIdx) {
			log.error("invalid indice: " + n);
			return null;
		}
		if (entriesByNr.get((long)n) == null) {
			log.debug("null entry in kanji container, for idx: " + n);
		}
		return entriesByNr.get((long)n);
	}

	public Entry getByStr(String key) {
		return this.entriesByStr.get(key);
	}

	public void dump(String sep) {
		for (Entry e : entriesByNr()) {
				log.info("oggetto " + e.getClass().getName() + ":\n" + e.toString());
		}
	}

	public Collection<Entry> entriesByNr() {
		return entriesByNr.values();
	}
	
	public ArrayList<Entry> getEntriesList(Comparator<Entry> comparator) {
		ArrayList<Entry> al = new ArrayList<Entry>();
		al.addAll(entriesByNr.values());
		if (comparator != null) {
			Collections.sort(al, comparator);
		} else {
			log.warn("a NULL comparator was passed, ignoring it");
		}
		return al;
	}

	
	
	int lastElementIdx;
	Map<Long, Entry> entriesByNr;
	Map<String, Entry> entriesByStr;
	
	private static org.apache.log4j.Logger log = Logger.getLogger(KanjiContainer.class);
}
