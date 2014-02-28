package com.enrico_viali.jacn.evkanji.lists_writing;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.enrico_viali.html.*;
import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.jacn.evkanji.comparators.*;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;
import com.enrico_viali.jacn.evkanji.filters.*;
import com.enrico_viali.jacn.evkanji.renderers.*;

public class EVKanjiHTMListHeisig extends A_EVKanjiWriteList {

	public EVKanjiHTMListHeisig(EVKanjiManager mgr) {
		this.mgr = mgr;

		_renderer = new EVKanjHTMLRenderer("KanjiEntry", "KanjiID", "KanjiInfoElement");
		filter = new EVKanjiEntryFilterHeisig(1, Cfg.NRMAX_KANJI);
		filterAlias = (EVKanjiEntryFilterHeisig) filter;
		filterAlias.setMax(Cfg.KANJI_LAST_HEISIG1);
		comparator = new EVKanjiEntryComparatorByHeisigNr();		
	}
	
	public void setFirstIdx(int first) {
		filterAlias.setMin(first);
	}

	public void setLastIdx(int last) {
		filterAlias.setMax(last);		
	}
	
	
	@Override
	String kanjiListStart(String name) {
		// HTMLEVUtils hTMLEVUtils = new HTMLEVUtils();
		String s = "";
		if (name != null && name.length() > 0) {
			s += "\n" + HTMLEVUtils.div("EVSubtitle", "", "kanji by " + name + ": ");
			s += HTMLEVUtils.br;
		}
		s += HTMLEVUtils.div("", "", kanjidicCreditsHTML());
		s += HTMLEVUtils.br;
		int wu = 100;
		ArrayList<TableHeaderCell> headers = new ArrayList<TableHeaderCell>();
		headers.add((new TableHeaderCell("Kanji")).setWidthPx(wu * 0.8));
		headers.add((new TableHeaderCell("On")).setWidthPx(wu * 1));
		headers.add((new TableHeaderCell("Kun")).setWidthPx(wu * 2));
		headers.add((new TableHeaderCell("meaning(Italian)")).setWidthPx(wu * 2));
		headers.add((new TableHeaderCell("meaning")).setWidthPx(wu * 2));
		headers.add((new TableHeaderCell("str.")).setWidthPx(wu * 0.5));
		headers.add((new TableHeaderCell("comp1")).setWidthPx(wu * 4));
		headers.add((new TableHeaderCell("comp2")).setWidthPx(wu * 4));
		headers.add((new TableHeaderCell("comp3")).setWidthPx(wu * 4));
		headers.add((new TableHeaderCell("comp4")).setWidthPx(wu * 4));
		headers.add((new TableHeaderCell("comp5")).setWidthPx(wu * 4));

		s += HTMLEVUtils.tableOpen(0, "", "kanjTable","");
		s += HTMLEVUtils.tableHeader(headers);
		return s;
	}


	EVKanjiEntryFilterHeisig filterAlias;
	
	private static org.apache.log4j.Logger log = Logger.getLogger(EVKanjiHTMListHeisig.class);

	@Override
	String kanjiListEnd(String text) {
		String s = HTMLEVUtils.tableClose();
		return s;
	} 

}
