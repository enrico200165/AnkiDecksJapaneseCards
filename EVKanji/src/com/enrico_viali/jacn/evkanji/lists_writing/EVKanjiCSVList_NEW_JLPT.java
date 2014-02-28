package com.enrico_viali.jacn.evkanji.lists_writing;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.jacn.evkanji.comparators.*;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;
import com.enrico_viali.jacn.evkanji.filters.*;
import com.enrico_viali.jacn.evkanji.renderers.*;

public class EVKanjiCSVList_NEW_JLPT extends A_EVKanjiWriteList {

	/**
	 * @param mgr
	 * @param lowerLevel
	 * @param topLevel
	 */
	public EVKanjiCSVList_NEW_JLPT(EVKanjiManager mgr, int lowerLevel,
			int topLevel) {
		this.mgr = mgr;

		_renderer = new EVKanjiEntryRendererCSVRow(); // "KanjiEntry",
														// "KanjiID","KanjiInfoElement");
		filter = new EVKanjiEntryFilterBy_NEW_JLPT(lowerLevel, topLevel, mgr);
		comparator = new EVKanjiEntryComparatorByGrade();
	}

	@Override
	String kanjiListStart(String name) {
		// HTMLEVUtils hTMLEVUtils = new HTMLEVUtils();
		String s = "";
		if (name != null && name.length() > 0) {
			s += "#kanji by " + name + ": ";
		}
		s += "\n#" + kanjidicCreditsTXT() + "\n";

		ArrayList<String> headers = new ArrayList<String>();
		headers.add("Kanji");
		headers.add("On");
		headers.add("Kun");
		// headers.add("meaning(Italian)")).setWidthPx(wu *
		// 2));
		headers.add("Meaning");
		headers.add("Strokes");
		headers.add("Grade");
		headers.add("Heisig");

		headers.add("comp1");
		headers.add("comp2");
		headers.add("comp3");
		headers.add("comp4");
		headers.add("comp5");

		for (String h : headers) {
			s += h + Cfg.SEP_EVKANJI_OUT;
		}
		log.error("correggi, copiato alla ceca");
		return s;
	}

	private static org.apache.log4j.Logger log = Logger
			.getLogger(EVKanjiCSVList_NEW_JLPT.class);

	@Override
	String kanjiListEnd(String text) {
		return text;
	}

}
