package com.enrico_viali.jacn.actions;

import java.util.*;

import org.apache.log4j.Logger;


import com.enrico_viali.jacn.common.CJKUtils;
import com.enrico_viali.jacn.edict.*;
import com.enrico_viali.jacn.main.TestDriver;
import com.enrico_viali.jacn.words.*;
import com.enrico_viali.utils.*;

public class GenFirefoxCSVLesson extends Action {

	public static class SubstrCoord {
		public SubstrCoord(int start, int end) {
			_start = start;
			_end = end;
		}

		int _start;
		int _end;
	}

	public GenFirefoxCSVLesson(TestDriver tdr) {
		super("generate Firefox language files lesson", tdr);
		log.info("cambiare il tag hardcoded a firefox");
		tag = "firefox";
		deck = new WordOrPhraseDeck();
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	void parseTranslLine(long glLineNr, TextFileLine tfl, String sep, Map<String, String> trasls) {
		if (tfl.get_line().split(sep).length < 2) {
			// log.error("line not in format xxx = yyyy:\n"+tfl.get_line());
			return;
		}
		String title = tfl.get_line().split(sep)[0].trim();
		title = tfl.getfPathName() + ":" + title;

		String trasl = tfl.get_line().split(sep)[1].trim();
		trasls.put(title, trasl);
	}

	ArrayList<JapaneseWord> parseLine(int glbLineNr, LongWrapper itemNr, TextFileLine tfl, String sep,
	EdictManager edictMgr, String tag, Map<String, String> transls) {
		final String endEdictSep = "<br>";
		ArrayList<JapaneseWord> res = new ArrayList<JapaneseWord>();

		if (tfl.get_line().split(sep).length < 2) {
			log.error("");
			return null;
		}

		String title = tfl.get_line().split(sep)[0].trim();
		String phrase = tfl.get_line().split(sep)[1].trim();

		JapaneseWord mainEntry = new JapaneseWord();
		String mean = transls.get(tfl.getfPathName() + ":" + title);
		mean = mean != null ? mean : "title not found in transl file";
		mainEntry.setAll(phrase, title, "self", mean, "phrase, no reading now", "no comment",
		"this should be overwritten by components", tag + " phrase", 0);
		res.add(mainEntry);

		String curSubExpr = phrase;

		int right = phrase.length() - 1;
		int left = 0;
		boolean trovataCur = false;
		boolean foundAtLeastOne = false;
		ArrayList<SubstrCoord> founds = new ArrayList<SubstrCoord>();
		ArrayList<SubstrCoord> notFounds = new ArrayList<SubstrCoord>();
		while (right > left && left < phrase.length() - 1) {
			trovataCur = false;
			IEntriesList el = null;
			do {
				curSubExpr = phrase.substring(left, right);
				if ((el = edictMgr.findEntriesHeadword(curSubExpr)) != null) {
					trovataCur = true;
				} else if ((el = edictMgr.findEntriesReading(curSubExpr)) != null) {
					trovataCur = true;
					// log.info("trovato reading: "+curSubExpr);
				} else {
					right--;
				}
			} while (right > left && !trovataCur);
			if (trovataCur) {
				foundAtLeastOne = true;
				founds.add(new SubstrCoord(left, right));
				left = right;
				if (!CJKUtils.containsKanji(curSubExpr) && curSubExpr.length() < 2) {
					// Non inserire "continue" farebbe perdere aggiornamento
					// contatore
				} else {
					// la registriamo
					JapaneseWord JPCNEntry = new JapaneseWord();
					String read = "";
					String edict = "";
					if (el.getNrEntries() > 1) {
						mean = "choose manually from edict";
						read = "choose manually from edict";
					} else {
						mean = el.getEntries().get(0).getWholeDescription();
						read = el.getEntries().get(0).getReading();
					}
					edict = el.getNrEntries() > 0 ? el.getAllEdictEntriesStr(endEdictSep) : "edict not found for: "
					+ curSubExpr;
					JPCNEntry.setAll(curSubExpr, title + ", comp found", phrase, mean, read, "no comment",
					edict, tag + " word found", itemNr.value++);
					res.add(JPCNEntry);
					mainEntry.setEdict(el.getAllEdictEntriesStr(endEdictSep));
				}
			} else {
				// log.info("non trovata IN : " + phrase.substring(left));
				left = left + 1;
			}
			right = phrase.length() - 1;
		}

		if (!foundAtLeastOne) {
			if (mainEntry.getEdict().length() <= 0) {
				mainEntry.setEdict("ev_todo: trova edict");
			}
			log.info("non trovato nulla per:" + phrase);
		}

		// --- tentiamo di recuperare le sottostringhe non trovate
		if (founds != null) {
			int lastFound = 0;
			for (SubstrCoord coord : founds) {
				if (coord._start > phrase.length() - 1 || coord._end > phrase.length() - 1) {
					log.error("coord fuori range: " + coord._start + "," + coord._end + " len: " + phrase.length());
				}
				// log.info("found: [" + coord._start + "," + coord._end + "] "
				// + phrase.substring(coord._start, coord._end));
				if (lastFound < coord._start) {
					notFounds.add(new SubstrCoord(lastFound, coord._start));
				}
				lastFound = coord._end;
			}
			if (lastFound < phrase.length() - 1)
				notFounds.add(new SubstrCoord(lastFound, phrase.length() - 1));

			for (SubstrCoord coord : notFounds) {
				String recover = phrase.substring(coord._start, coord._end);
				if ((recover.length() >= 2 &&  CJKUtils.containsJapanese(recover)) ||
				(recover.length() >= 1 &&  CJKUtils.containsKanji(recover)) ) {
					JapaneseWord JPCNEntry = new JapaneseWord();
					final String msg = "\"not found entry\", missing";
					JPCNEntry.setAll(phrase.substring(coord._start, coord._end), title + ", comp not found",
					phrase, msg, msg, msg, msg, tag + " word notf", itemNr.value++);
					res.add(JPCNEntry);
				}
			}
		}

		mainEntry.setNumber(itemNr.value++); // deve avere il numero più alto
		return res;
	}

	ArrayList<TextFileLine> selectLinesOfInterest(ArrayList<TextFileLine> origLines) {
		ArrayList<TextFileLine> linesOfInterest = new ArrayList<TextFileLine>();
		for (int i = 0; i < origLines.size(); i++) {
			String l = origLines.get(i).get_line();
			if (l.matches("^\\s*#.*"))
				continue;
			if (l.length() <= 0)
				continue;
			if (!CJKUtils.containsHiragana(l) && !CJKUtils.containsKatakana(l)
			&& !CJKUtils.containsKanji(l)) {
				// log.info("scarto riga non Giapponese:\n" + l);
				continue;
			}
			linesOfInterest.add(origLines.get(i));
		}
		return linesOfInterest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.actions.Action#perform()
	 */
	@Override
	public boolean perform() throws Exception {

		Map<String, String> translations = new Hashtable<String, String>();
		{
			String translTree = "V:\\data\\data-pers\\enrico\\mydocs\\interessi\\lingue_jp-cn\\lang_pack_work\\transl";
			FileDirTree transl = new FileDirTree(translTree, null, ".*\\.properties", null);
			if (!transl.navigate(null, null)) {
				return false;
			}
			int i = 0;
			for (TextFileLine tfl : transl.get_globTextFileLine()) {
				parseTranslLine(i, tfl, "=", translations);
			}
		}
		log.info("trovate nr traduzioni: " + translations.size());

		// Set<WordOrPhrase> entries = new TreeSet<WordOrPhrase>();
		String targetTree = "V:\\data\\data-pers\\enrico\\mydocs\\interessi\\lingue_jp-cn\\lang_pack_work\\target";
		FileDirTree target = new FileDirTree(targetTree, null, ".*\\.properties", null);

		// --- read the files in the subtree
		if (!target.navigate(null, /* fmb */null)) {
			return false;
		}
		// --- select lines of interese ---
		ArrayList<TextFileLine> linesOfInterest = selectLinesOfInterest(target.get_globTextFileLine());

		EdictManager edictMgr = new Edict_1_Manager();
		edictMgr.loadFromFile();
		int nr = 0;
		LongWrapper itemNr = new LongWrapper(0L);
		for (TextFileLine t : linesOfInterest) {
			deck.addAll(parseLine(++nr, itemNr, t, "=", edictMgr, tag, translations));
		}

		deck.dump("\n");
		log.info("nr entries: " + deck.size());
		deck.writeToCSV("firefox.csv", true, true);

		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

	WordOrPhraseDeck deck;
	String tag;
	private static org.apache.log4j.Logger log = Logger.getLogger(GenFirefoxCSVLesson.class);
}
