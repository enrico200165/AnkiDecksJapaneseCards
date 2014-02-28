package com.enrico_viali.jacn.evkanji.renderers;

//import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.*;
import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.utils.*;

/**
 * @author enrico
 * 
 */
public class EVKanjiEntryRendererCSVRow implements ITexLineRenderer {

	public String render(IRenderableAsTextLine ePar, long scanned,
			long included, int level) {
		if (!(ePar instanceof EVKanjiEntry)) {
			log.error("tipo non atteso, trovato: " + ePar.getClass().getName());
			return "error";
		}
		EVKanjiEntry e = (EVKanjiEntry) ePar;
		return getAsCSVLine(e, scanned, included, Cfg.SEP_EVKANJI_OUT,
				Cfg.SEP_EVKANJI_OUT_L2) + "\n";
	}

	public String getAsCSVLine(EVKanjiEntry e, long scanned, long included,
			String sep, String sepReplacement) {
		return getCSVLine(e, sep, sepReplacement);
	}

	/**
	 * @param sep
	 * @return
	 */
	public String getCSVLine(EVKanjiEntry e, String sep, String replc) {
		String s = "";

		s = e.getKanji();

		/*
		 * s += sep + e.getHanziS(); s += sep + e.getHanziT(); s += sep +
		 * e.getHeisigNumber(); s += sep +
		 * StringUtils.escapeForDelimited(e.getHeisigPrimitives(), sep, replc);
		 * s += sep + StringUtils.escapeForDelimited(e.getHStory(), sep, replc);
		 * s += sep + StringUtils.escapeForDelimited(e.getHMean(), sep, replc);
		 */

		s += sep + e.getOn();
		s += sep + e.getKun();
		s += sep
				+ StringUtils.escapeForDelimited(e.getKanjidicMeaning(), sep,
						replc);

		s += sep + e.getStrokes();

		s += sep + e.getGrade();
		s += sep + e.getHeisigNumber();
		// s += sep + e.getOldJlpt();
		// s += sep + e.getNewJLPT();
		
		/*
		 * s += sep + StringUtils.escapeForDelimited(e.getPinyin(), sep, replc);
		 * s += sep + e.getCnMean();
		 */
		for (int i = 0; i < Cfg.KANJI_COMPO_NR; i++) {
			s += sep;
			if (e.getCompKanji(i) != null && e.getCompKanji(i).length() > 0) {
				s += e.getCompKanji(i)
						+ " ["
						+ e.getCompReading(i)
						+ "] "
						+ StringUtils.escapeForDelimited(e.getCompMeaning(i),
								sep, replc);
			} else {
				s += "-";
			}
		}

		s = Utl.normalizeString(s, sep);

		return s;
	}

	private static org.apache.log4j.Logger log = Logger
			.getLogger(EVKanjiEntryRendererCSVRow.class);
}
