package com.enrico_viali.jacn.evkanji.renderers;

//import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.enrico_viali.html.*;
import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.utils.*;

/**
 * @author enrico
 * 
 */
public class EVKanjiEntryHTMLRowRenderer implements ITexLineRenderer  {

	public EVKanjiEntryHTMLRowRenderer(String cssClassKanjiPar, String cssKanjiIDPar,
	String cssClassElementsPar) {
		super();
		cssClassKanji = cssClassKanjiPar;
		cssKanjiID = cssKanjiIDPar;
		cssElementsClass = cssClassElementsPar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.evkanji.IEVKanjiItemHTMLRenderer#render(com.
	 * enrico_viali.utils.IRenderableAsTextLine, long, long)
	 */
	@Override
	public String render(IRenderableAsTextLine ePar, long scanned, long included, int level) {
		if (!(ePar instanceof EVKanjiEntry)) {
			log.error("tipo non atteso, trovato: " + ePar.getClass().getName());
			return "error";
		}

		EVKanjiEntry e = (EVKanjiEntry) ePar;
		String frag = "";
		TableDataHelper tdh = new TableDataHelper();
		tdh.setClass(cssElementsClass);
		// frag += tdh.cTD(e.getKanji()+": "+e.getKanji().codePointAt(0)).getHTMLMarkUp(0);
		
		frag += tdh.setClass("EVKanjiKanji").cTD(e.getKanji()).getHTMLMarkUp(0);
		frag += tdh.setClass("EVKanjiOn").cTD(e.getOn()).getHTMLMarkUp(0);
		frag += tdh.setClass("EVKanjiKun").cTD(e.getKun()).getHTMLMarkUp(0);
		frag += tdh.setClass("EVKanjiNormal").cTD(e.getKanjidicMeaning()).getHTMLMarkUp(0);
		frag += tdh.cTD("" + e.getStrokes()).getHTMLMarkUp(0);
		// frag += tdh.cTD("" + e.getGrade()).getHTMLMarkUp(0);
		// frag += tdh.cTD("" + e.getJlpt()).getHTMLMarkUp(0);
		frag += tdh.cTD("" + e.getHeisigNumber()).getHTMLMarkUp(0);
		
		String compos = "";
		for (int i = 0; i < Cfg.KANJI_COMPO_NR; i++) {
		    if (e.getCompHeadword(i) == null || e.getCompHeadword(i).length() == 0)
		        continue;
		    compos += e.getCompHeadword(i);
		    compos += " ["+e.getCompKana(i)+"] ";
		    compos += " "+e.getCompMeaning(i);
		    compos += "<br />";
		}
		frag += tdh.cTD("" + compos).getHTMLMarkUp(0);

		return HTMLEVUtils.tableRow(cssClassKanji, cssKanjiID, frag);
	}

	String cssClassKanji;
	String cssKanjiID;
	String cssElementsClass;

	private static org.apache.log4j.Logger log = Logger.getLogger(EVKanjiEntryHTMLRowRenderer.class);
}
