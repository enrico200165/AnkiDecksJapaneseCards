package com.enrico_viali.jacn.words;

//import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.*;
import com.enrico_viali.jacn.evkanji.renderers.EVKanjiEntryRendererCSVRow;
import com.enrico_viali.utils.*;

public class WordOrPhraseDeck extends EVManager  {
	
	public WordOrPhraseDeck() {
		super();
		this.words = new TreeSet<JapaneseWord>();
	}

	public boolean addAll(Collection<JapaneseWord> entries) {
		if (words.addAll(entries)) {
			return true;
		}
		return false;
	}
	
	
	@Override
	public boolean add(EVJPCNEntry e) {
		if (!(e instanceof JapaneseWord)) {
			log.error("found unexpected type: "+e.getClass().getName());
			System.exit(1);
		}
		JapaneseWord w = (JapaneseWord) e;
		return	words.add(w);
	}

	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return words.size();
	}

	public boolean writeToCSV(String filePath, boolean ovwFile, boolean ignErr)
	{
		List<IRenderableAsTextLine> coll = new ArrayList<IRenderableAsTextLine>();
		coll.addAll(words);
		FileHelper.writeList(filePath, "",Cfg.ENCODING_OUT, coll, "", "", /* filter */null,
		new EVKanjiEntryRendererCSVRow(),
		1, Cfg.NRMAX_KANJI, Utl.NOT_INITIALIZED_INT, true);
		return true;
	}


	@Override
	public TreeSet<EVJPCNEntry> getCopyOfEntries(Comparator<EVJPCNEntry> c) {
		TreeSet<EVJPCNEntry> ts = new TreeSet<EVJPCNEntry>();
		ts.addAll(words);
		return ts;
	}

	@Override
	public boolean isLoaded() {
		if (words == null) {
			log.error("words is empty, exiting");
			System.exit(1);
			return false;
		}
		return words.size() > 0;  
	}
	
	TreeSet<JapaneseWord> words;
	private static org.apache.log4j.Logger log = Logger.getLogger(WordOrPhraseDeck.class);
}
