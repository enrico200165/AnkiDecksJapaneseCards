package com.enrico_viali.jacn.evkanji.lists_writing;


import org.apache.log4j.Logger;

import com.enrico_viali.html.HTMLEVUtils;
import com.enrico_viali.jacn.evkanji.comparators.*;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;
import com.enrico_viali.jacn.evkanji.filters.*;
import com.enrico_viali.jacn.evkanji.renderers.*;

public class EVKanjiHTMList_OLD_JLPT extends A_EVKanjiWriteList {

	public EVKanjiHTMList_OLD_JLPT(EVKanjiManager mgr) {
		this.mgr = mgr;

		_renderer = new EVKanjiEntryHTMLRowRenderer("KanjiEntry", "KanjiID", "KanjiInfoElement");
		filter = new EVKanjiEntryFilterByJoyo(1, 4);
		comparator = new EVKanjiEntryComparatorByGrade();
	}

	String kanjiListStart(String name) {
		log.error("metodo da implementare, copia dalla lista di hesig dove è implementato");
		System.exit(1);
		return "";
	}

	private static org.apache.log4j.Logger log = Logger.getLogger(EVKanjiHTMList_OLD_JLPT.class);

	@Override
	String kanjiListEnd(String text) {
		String s = HTMLEVUtils.tableClose();
		return s;
	} 

}
