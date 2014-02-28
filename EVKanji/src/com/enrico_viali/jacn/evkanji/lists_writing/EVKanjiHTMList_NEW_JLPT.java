package com.enrico_viali.jacn.evkanji.lists_writing;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.enrico_viali.html.HTMLEVUtils;
import com.enrico_viali.html.TableHeaderCell;
import com.enrico_viali.jacn.evkanji.comparators.*;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;
import com.enrico_viali.jacn.evkanji.filters.*;
import com.enrico_viali.jacn.evkanji.renderers.*;

public class EVKanjiHTMList_NEW_JLPT extends A_EVKanjiWriteList {

	/**
	 * @param mgr
	 * @param lowerLevel
	 * @param topLevel
	 */
	public EVKanjiHTMList_NEW_JLPT(EVKanjiManager mgr, int lowerLevel,
			int topLevel) {
		this.mgr = mgr;

		_renderer = new EVKanjiEntryHTMLRowRenderer("KanjiEntry", "KanjiID",
				"KanjiInfoElement");
		filter = new EVKanjiEntryFilterBy_NEW_JLPT(lowerLevel, topLevel, mgr);
		comparator = new EVKanjiEntryComparatorByGrade();
	}

		
	@Override
	String kanjiListStart(String name) {
		// HTMLEVUtils hTMLEVUtils = new HTMLEVUtils();
		String s = "";
		if (name != null && name.length() > 0) {
			s += "\n"
					+ HTMLEVUtils.div("EVSubtitle", "", "kanji by " + name
							+ ": ");
			s += HTMLEVUtils.br;
		}
		s += HTMLEVUtils.div("", "", kanjidicCreditsHTML());
		s += HTMLEVUtils.br;

		int wu = 100;
		ArrayList<TableHeaderCell> headers = new ArrayList<TableHeaderCell>();
		headers.add((new TableHeaderCell("Kanji")).setWidthPx(wu * 0.8));
		headers.add((new TableHeaderCell("On")).setWidthPx(wu * 1));
		headers.add((new TableHeaderCell("Kun")).setWidthPx(wu * 2));
		// headers.add((new TableHeaderCell("meaning(Italian)")).setWidthPx(wu *
		// 2));
		headers.add((new TableHeaderCell("Meaning")).setWidthPx(wu * 2.5));

		headers.add((new TableHeaderCell("Strokes")).setWidthPx(wu * 0.5));
		// headers.add((new TableHeaderCell("Grade")).setWidthPx(wu * 0.5));
		headers.add((new TableHeaderCell("heisig")).setWidthPx(wu * 0.5));

		headers.add((new TableHeaderCell("composites")).setWidthPx(wu * 4));
		/*
		headers.add((new TableHeaderCell("comp2")).setWidthPx(wu * 4));
		headers.add((new TableHeaderCell("comp3")).setWidthPx(wu * 4));
		headers.add((new TableHeaderCell("comp4")).setWidthPx(wu * 4));
		headers.add((new TableHeaderCell("comp5")).setWidthPx(wu * 4));
		*/
		s += HTMLEVUtils.tableOpen(0, "kanjTable", ""," style=\"width:100%;text-align:left;table-layout:fixed;\"");
		s += HTMLEVUtils.tableHeader(headers);
		log.error("correggi, copiato a Heisig senza modifiche");
		return s;
	}

	private static org.apache.log4j.Logger log = Logger
			.getLogger(EVKanjiHTMList_NEW_JLPT.class);

	@Override
	String kanjiListEnd(String text) {
		String s = HTMLEVUtils.tableClose();
		return s;
	} 

}
