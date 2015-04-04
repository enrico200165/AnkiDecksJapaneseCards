package com.enrico_viali.jacn.evkanji.core;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.gtranslate.*;
import com.enrico_viali.html.HTMLEVUtils;
import com.enrico_viali.jacn.ankideck.generic.Fact;
import com.enrico_viali.jacn.ankideck.heisig.ADeckHeisigMgr;
import com.enrico_viali.jacn.ankideck.kanji_enrico.*;
import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.jacn.common.KanjiContainer;
import com.enrico_viali.jacn.common.ValuesList;
import com.enrico_viali.jacn.edict.KanjiCompositesMgr;
import com.enrico_viali.jacn.evkanji.actions.EVKanjyEntryActionCheckAdHoc;
import com.enrico_viali.jacn.evkanji.actions.EVKanjyEntryActionTranslateMeaning;
import com.enrico_viali.jacn.evkanji.actions.IEVKanjiEntryAction;
import com.enrico_viali.jacn.evkanji.comparators.EVKanjiEntryComparatorByGrade;
import com.enrico_viali.jacn.evkanji.lists_writing.A_EVKanjiWriteList;
import com.enrico_viali.jacn.evkanji.lists_writing.EVKanjiCSVList_NEW_JLPT;
import com.enrico_viali.jacn.evkanji.lists_writing.EVKanjiHTMListHeisig;
import com.enrico_viali.jacn.evkanji.lists_writing.EVKanjiHTMList_NEW_JLPT;
import com.enrico_viali.jacn.evkanji.lists_writing.EVKanjiHTMList_OLD_JLPT;
import com.enrico_viali.jacn.evkanji.lists_writing.EVKanjiHTMListJoyo;
import com.enrico_viali.jacn.evkanji.renderers.EVKanjiEntryRendererCSVRow;
import com.enrico_viali.jacn.kanjidic.KanjiDicManager;
import com.enrico_viali.jacn.kanjidic.KanjidicEntry;
import com.enrico_viali.utils.*;

/**
 * @author enrico
 * 
 *         quando è carico deve essere carico al massimo, ex. NON solo da
 *         kanjidic
 * 
 *         Definire le modalità di caricamento, proposta: - vuoto - da file CSV
 *         - da Deck anki
 * 
 */
public class EVKanjiManager {

	public EVKanjiManager(int loadMode, String fname) {
		super();
		csvDelimiter = Cfg.SEP_EVKANJI_IN;
		this.container = new KanjiContainer<EVKanjiEntry>(Cfg.NRMAX_KANJI);

		switch (loadMode) {
		case 0: { // vuoto
			break;
		}
		case 1: { // standard load it from all sources available
			String pathName = StringUtils.adjustPath(Cfg.PATH_DIR_DATA
					+ "input/" + fname);
			try {
				loadFromKanjidic(); // this is the fixed reference

				// we ignore all other sources and consider only the ankideck
				// about enrico kanji
				// as now it should contain everything
				AnkiDeckMgrKanjiEnrico ankiKanjiEnricoDeckMgr = new AnkiDeckMgrKanjiEnrico(
						pathName);
				enrichFromAnkiDecKanjiEnrico(ankiKanjiEnricoDeckMgr);
			} catch (Exception e) {
				log.error("fallita costruzione", e);

			}
			break;
		}
		case 2: { // file csv
			break;
		}
		default: {
			break;
		}
		}
	}

	public boolean isLoaded() {
		return this.container != null && this.container.size() > 0;
	}

	public int size() {
		return container.size();
	}

	long countByJoyo(int joyo) {
		int count = 0;
		for (EVKanjiEntry ePar : container.getEntriesList(null)) {
			EVKanjiEntry e = (EVKanjiEntry) ePar;
			if (e.getGrade() == joyo)
				count++;
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.evkanji.EVManager#add(com.enrico_viali.jacn.ankideck
	 * .jpcnkanji.AnkiFactJPCNKanji)
	 */
	public boolean add(AnkiFactEnricoKanji af) {
		EVKanjiEntry e = new EVKanjiEntry();

		if (!e.buildFromAnkiHeisigJPCNFact(af)) {
			log.error("failed to build valid EkanjiEntry for: " + e.getKanji());
			return false;
		}
		return add(e);
	}

	public boolean add(EVKanjiEntry e) {
		return container.insert(e.getKanji(), e, e.getNumber());
	}

	public boolean kanjidicMeanToItalianSingle(EVKanjiEntry k, GTranslate tsl) {

		String expr = k.getKanjidicMeaning();
		log.debug("kanjidic meaningS to translate: " + expr);

		if (tsl == null)
			tsl = new GTranslateDB();

		// split the meanings based on comma
		ValuesList vl = new ValuesList(expr, false, true, 999, null);
		ArrayList<String> expressions = vl.getElements();
		ArrayList<String> translations = new ArrayList<String>();
		for (String e : expressions) {
			// translate each single meaning and add it to the other
			// translations
			RespTransl r = tsl.translate(e);
			if (r.found) {
				log.debug("translating: " + e + " -> " + r.translation);
				translations.add(r.translation);
			} else {
				log.info("unable to translate: " + e);
				k.setKanjidicMeaningIT("traduzione italiana non trovata");
				return false;
			}
		}

		// copy single translations in a strint
		String allTranslations = "";
		for (String s : translations) {
			allTranslations += s + ", ";
		}
		k.setKanjidicMeaningIT(allTranslations);

		return true;
	}

	public boolean kanjidicMeanToItalianALL() {
		IEVKanjiEntryAction action = new EVKanjyEntryActionTranslateMeaning();
		GTranslate trsl = new GTranslateDB();
		return forEach(action, trsl);
	}

	/*
	 * 
	 */
	public boolean loadFromCSVFile(String pathname, boolean stopOnAnomaly) {
		if (isLoaded()) {
			return true;
		}
		return loadFromCSVFile(pathname, Utl.ENCODING_UTF8, false);
	}

	/**
	 * 
	 * @param filepath
	 * @param encoding
	 * @return
	 */
	boolean loadFromCSVFile(String filepath, String encoding,
			boolean stopOnAnomaly) {
		boolean rc = true;
		InputStreamReader isr;
		BufferedReader bufferedReader;

		if (!(new File(filepath)).exists()) {
			log.error("read non existing file: " + filepath);
			return false;
		}

		try {
			log.trace("Dir di lavoro: " + System.getProperty("user.dir")
					+ "\nopening: " + filepath);
			isr = new InputStreamReader(new FileInputStream(filepath), encoding);
			bufferedReader = new BufferedReader(isr);
			log.debug("Encoding: in lettura " + isr.getEncoding());

			String jpcnLine;
			int lineNr;
			for (lineNr = 1; (jpcnLine = bufferedReader.readLine()) != null
					&& (lineNr < 99999); lineNr++) {
				EVKanjiEntry kanji = new EVKanjiEntry();
				if (!kanji.buildVKanjiFromFileLine(lineNr, jpcnLine,
						csvDelimiter, Cfg.cleanEmbelish())) {
					log.error("elaborazione riga entriesByHNr returned false, exiting\nline nr "
							+ lineNr
							+ " was \n"
							+ jpcnLine
							+ "\nEntry: "
							+ kanji.detailedDump("", " : "));
					if (stopOnAnomaly)
						return false;
				}
				kanji.setIsReadFromEVFile(true);
				this.add(kanji);
			}
			log.debug("elaborate nr linee input: " + lineNr);
		} catch (IOException e) {
			log.error("", e);
			return false;
		} catch (Exception e) {
			log.error("Exception", e);
			return false;
		}

		return rc;
	}

	public boolean readFromHeisigDeltaFile() {
		return readFromHeisigDeltaFile(Cfg.PATHNAME_DELTA_FILLED,
				Utl.ENCODING_UTF8, true);
	}

	private boolean readFromHeisigDeltaFile(String filepath, String encoding,
			boolean stopOnAnomaly) {
		boolean rc = true;
		InputStreamReader isr;
		BufferedReader bufferedReader;

		try {

			log.info("Dir di lavoro: " + System.getProperty("user.dir")
					+ "\nopening: " + filepath);
			isr = new InputStreamReader(new FileInputStream(filepath), encoding);
			bufferedReader = new BufferedReader(isr);

			String hDeltaLine;
			int lineNr;
			for (lineNr = 1; (hDeltaLine = bufferedReader.readLine()) != null; lineNr++) {

				EVKanjiEntry kanji = new EVKanjiEntry();
				if (!kanji.buildEVKanjiFromHDeltaLine(lineNr, hDeltaLine,
						Cfg.SEP_DELTA_IN)) {
					log.error("elaborazione riga entriesByHNr returned false, exiting\nline nr "
							+ lineNr
							+ " was \n"
							+ hDeltaLine
							+ "\nEntry: "
							+ kanji.detailedDump("", " : "));
					if (stopOnAnomaly)
						return false;
				}
				kanji.setIsReadFromEVFile(true);
				this.add(kanji);
			}
			log.info("delta file, lette nr linee input: " + (lineNr - 1));
		} catch (IOException e) {
			log.error("non trovato file " + filepath);
			return false;
		} catch (Exception e) {
			log.error("Exception", e);
			return false;
		}
		return rc;
	}

	public boolean writeToDBTable(EVKanji_DB_Table tabella) throws SQLException {
		boolean ok = true;
		int nrInserFalliti = 0;
		int nrNonValidi = 0;

		if (!isLoaded()) {
			log.error("attempt to write to DB when empty");
			return false;
		}

		int curElem = 0;
		for (EVKanjiEntry kTmp : container.entriesByNr()) {
			EVKanjiEntry k = (EVKanjiEntry) kTmp;
			curElem++;
			if (k.getKanji() == null) {
				log.error("evkanj object with null kanji field: \n"
						+ k.toString());
				ok = false;
				nrNonValidi++;
				continue;
			}
			if (tabella.writeToDB(k, null)) {
				log.debug("written in table evkanj entry: " + k.toString(":"));
			} else {
				log.error("failed write in table evkanj entry: "
						+ k.toString(":"));
				nrInserFalliti++;
				ok = false;
				// break; non si esce
			}
			if (curElem > 999999) // solo per uscire prima durante le
				// sperimentazioni
				break;
		}

		log.info("saving all elements to DB; total items: " + curElem
				+ " invalid items: " + nrNonValidi + " failed inserts: "
				+ nrInserFalliti);

		return ok;
	}

	public EVKanjiEntry findByKanjiChar(String key) {
		return (EVKanjiEntry) container.getByStr(key);
	}

	public EVKanjiEntry findByHNr(int nr) {
		return (EVKanjiEntry) container.getByNr(nr);
	}

	public boolean check(int strictness) {
		boolean ret = true;

		for (EVKanjiEntry eTmp : container.entriesByNr()) {
			EVKanjiEntry e = (EVKanjiEntry) eTmp;
			ret = e.checkEntry() && ret;
			if (!ret)
				log.debug("giusto per mettere il breakpoint");
		}

		return ret;
	}

	boolean enrichFromKanjidic(KanjiDicManager kjdMgr) {
		boolean ret = true;

		for (EVKanjiEntry eTmp : container.entriesByNr()) {
			EVKanjiEntry e = (EVKanjiEntry) eTmp;
			if (!(ret = e.enrichFromKanjidic(kjdMgr) && ret)) {
				log.warn("fallito arrichimento da Kanjidic di " + e.getKanji());
			}
		}
		return ret;
	}

	public boolean loadFromKanjidic() {

		if (isLoaded()) {
			log.error("already loaded");
			System.exit(1);
			return false;
		}

		KanjiDicManager kjdMgr = new KanjiDicManager();
		try {
			kjdMgr.load();
		} catch (Exception e2) {
			log.error("", e2);
			return false;
		}

		EVKanjiEntry e;
		for (int i = 1; i <= Cfg.NRMAX_KANJI; i++) {
		    KanjidicEntry ke = kjdMgr.findByHNr(i);
			if (ke != null) {
				e = new EVKanjiEntry();
				e.enrichFromKanjidic(ke);
				add(e);
			} else {
				log.error("null (not-found) in kanjidic at index: " + i);
			}
		}

		kjdMgr = null; // un po' di pulizia a fronte di strani errori

		{ // debug temporaneo
			EVKanjyEntryActionCheckAdHoc action = new EVKanjyEntryActionCheckAdHoc(
					"in loadFromKanjidic");
			forEach(action, null);
		}
		return true;
	}

	/**
	 * Da validare, scritto a posteriori: si aspetta che il container sia gia
	 * riempito in parte, arricchisce da heisig deck
	 * 
	 * @param ADeMgr
	 * @return
	 */
	boolean enrichFromHeisigDeck(ADeckHeisigMgr ADeMgr) {
		int notFound = 0;

		if (!ADeMgr.getLoaded()) {
			log.warn("trying to fill from an empty AnkiDeck manager");
			ADeMgr.load();
		}
		for (EVKanjiEntry eTmp : container.entriesByNr()) {
			EVKanjiEntry e = (EVKanjiEntry) eTmp;
			if (!e.enrichFromAnkiDeck(ADeMgr)) {
				log.warn("enrich from Heisig deck, h nr " + e.getHeisigNumber()
						+ " char " + e.getKanji());
				notFound++;
			}
		}
		if (notFound > 0)
			log.info("enrichment from heisig deck, kanji not found:" + notFound);
		return (notFound > 0);
	}

	public boolean forEach(IEVKanjiEntryAction action, Object otherParams) {
		if (container == null) {
			log.warn("enrich found an null container, is this a load? Creating it (new ...)");
			container = new KanjiContainer<EVKanjiEntry>(this.size());
		}

		action.initialAction(null, null, null);

		long elemNr = 0;
		for (EVKanjiEntry e : container.entriesByNr()) {
			elemNr++;
			if (!(e instanceof EVKanjiEntry)) {
				log.error("wrong type found: " + e.getClass());
				return false;
			}
			if (action.elabora(e, this, otherParams)) {
				log.debug("processed nr. " + elemNr + ": " + e.toString());
			} else {
				log.error("error processing element nr. " + elemNr + ": "
						+ e.toString());
			}
		}

		action.finalAction(null, null, null);

		log.info("processed, nr EVKanji: " + elemNr);

		return true;
	}

	/**
	 * For iterations where the order is not known, so the List<> in the right
	 * order is built outside
	 * 
	 * @param list
	 * @param action
	 * @param otherParams
	 * @return
	 */
	boolean forAllInList(List<EVKanjiEntry> list, IEVKanjiEntryAction action,
			Object otherParams) {
		int kanjiPerPage = 100;
		int last = 0;

		EVKanjiHTMListHeisig delete = new EVKanjiHTMListHeisig(this);
		for (int i = 0; i < Cfg.KANJI_LAST_HEISIG1; i += kanjiPerPage) {
			last = ((i + kanjiPerPage) > Cfg.KANJI_LAST_HEISIG1) ? Cfg.KANJI_LAST_HEISIG1
					: i + kanjiPerPage;

			String title = "kanji from " + i + " to " + last;
			String htmlBefore = HTMLEVUtils
					.HTMLDocStart(title, "kanjilist.css");
			String htmlAfter = HTMLEVUtils.HTMLEnd;

			String filepathname = Cfg.PATHNAME_HEISIG + "_" + i + "-" + last;

			if (!delete
					.writeKanjiList("", htmlBefore, htmlAfter, null /* ".html" */)) {
				log.error("");
				return false;
			}
		}

		return true;
	}

	/**
	 * Da validare, scritto a posteriori: si aspetta che il container sia gia
	 * riempito in parte, arricchisce da heisig deck
	 * 
	 * @param ADeMgr
	 * @return
	 */
	boolean enrichFromAnkiDecKanjiEnrico(AnkiDeckMgrKanjiEnrico ADeMgr) {
		int notFound = 0;
		if (!ADeMgr.getLoaded()) {
			log.warn("trying to fill from an empty AnkiDeck manager");
			ADeMgr.load();
		}

		if (container == null) {
			log.warn("enrich found an null container, is this a load? Creating it (new ...)");
			container = new KanjiContainer<EVKanjiEntry>(this.size());
		}

		for (EVKanjiEntry e : container.entriesByNr()) {
			if (!(e instanceof EVKanjiEntry)) {
				log.error("wrong type found: " + e.getClass());
				System.exit(1);
			}
			EVKanjiEntry toEnrich = (EVKanjiEntry) e;

			AnkiFactEnricoKanji enricher = null;
			Fact a = ADeMgr.getFactByExp(toEnrich.getKanji());
			if (a == null) {
				// log.error("non trovato kanji "+toEnrich.getKanji()+" con ID: "+toEnrich.getNumber());
				// ADeMgr.dump(" - ");
				continue;
			}
			if (!(a instanceof AnkiFactEnricoKanji)) {
				log.error("wrong type found: " + a.getClass());
				System.exit(1);
			}
			enricher = (AnkiFactEnricoKanji) a;
			toEnrich.enrichFromADeckFactEnricoKanji(enricher);
			log.error("per ora non arricchimento parziale ");
		}
		log.info("loaded, nr EVKanji: " + this.container.size());

		if (notFound > 0)
			log.info("enrichment from heisig deck, kanji not found:" + notFound);
		return (notFound > 0);
	}

	public boolean enrichWithEdictComposite(KanjiCompositesMgr kMgr) {
		if (!Cfg.getEnrichWithEdictComposites()) {
			log.info("enrich with edit composites is OFF");
			return true;
		}
		boolean ret = true;
		int j = 0;
		log.info("arricchimento con edictByHeadWord composite, takes 12 min!");
		for (EVKanjiEntry eTmp : container.entriesByNr()) {
			EVKanjiEntry e = (EVKanjiEntry) eTmp;
			if (j++ % 20 == 0)
				log.debug("arricchimenti edictByHeadWord, elem nr: " + j);
			e.enrichWithEdictComposites(kMgr.getSortedComposites(e.getKanji(),true,5));
		}
		return ret;
	}

	public boolean writeDelta2AnkiFile() {
		try {
			return writeToCSV(Cfg.PATNAME_DELTAEVKANJI, Utl.ENCODING_UTF8,
					Cfg.SEP_EVKANJI_OUT, Cfg.SEP_REPLACER, false, true, true);
		} catch (IOException e) {
			log.error("");
			return false;
		}
	}

	public boolean writeToCSV(String pathname, boolean ovwFile, boolean ignErr) {
		return writeToCSV(pathname, ovwFile, ignErr,/* onlyFromFile */false);
	}

	public boolean writeToCSV(String pathname, boolean ovwFile, boolean ignErr,
			boolean onlyFromFile) {
		boolean ret = true;

		try {
			ret = writeToCSV(pathname, Utl.ENCODING_UTF8, Cfg.SEP_EVKANJI_OUT,
					Cfg.SEP_REPLACER, ovwFile, onlyFromFile, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
		return ret;
	}

	public boolean writeToCSV(String filePath, String encoding, String sep,
			String replSep, boolean ovwFile, boolean onlyFromEVFile,
			boolean soloConHeisig) throws IOException {

		if (onlyFromEVFile || soloConHeisig) {
			log.error("viene ignorato, scrivere il filtro");
			System.exit(1);
		}
		log.info("i separatori passati come parametri vengono ignorati");

		List<IRenderableAsTextLine> coll = new ArrayList<IRenderableAsTextLine>();
		coll.addAll(container
				.getEntriesList(new EVKanjiEntryComparatorByGrade()));
		// per ora ordino per jouyou

		FileHelper.writeList(filePath, "", encoding, coll, "", "", /* filter */
				null, new EVKanjiEntryRendererCSVRow(), 1, Cfg.NRMAX_KANJI,
				Utl.NOT_INITIALIZED_INT, true);

		return true;
	}

	public boolean deltaOUTExists() {
		return (new File(Cfg.PATHNAME_DELTA_FILLED)).exists();
	}

	public boolean writeHTMLJOUYOUKanjiTable() {
		A_EVKanjiWriteList kl = new EVKanjiHTMListJoyo(this);
		for (int i = 1; i <= 6; i++)
			kl.writeKanjiList(Cfg.PATHNAME_JOUYOU + i + ".html", "" + i, "",
					".html");
		return true;
	}

	public boolean writeHTML_OLD_JLPTKanjiTable() {
		log.info("OLD JLPT aggiorna al nuovo!!!");
		A_EVKanjiWriteList kl = new EVKanjiHTMList_OLD_JLPT(this);
		for (int i = 1; i <= 4; i++)
			kl.writeKanjiList(Cfg.PATHNAME_JLPT + i + ".html", "" + i, "",
					".html");
		return true;
	}

	public boolean writeHTML_NEW_JLPTKanjiTable(int level) {
		A_EVKanjiWriteList kl = new EVKanjiHTMList_NEW_JLPT(this, level, level);

		String htmlBefore = HTMLEVUtils.HTMLDocStart("jlpt " + level,
				"kanjilist.css");
		String htmlAfter = HTMLEVUtils.HTMLEnd;

		kl.writeKanjiList(Cfg.PATHNAME_JLPT + level, htmlBefore, htmlAfter, ".html");
		return true;
	}

	public boolean writeCSV_NEW_JLPTKanjiTable(int level, String header,
			String footer) {
		A_EVKanjiWriteList kl = new EVKanjiCSVList_NEW_JLPT(this, level, level);

		kl.writeKanjiList(Cfg.PATHNAME_JLPT + level, header, footer,".csv");
		return true;
	}

	public boolean writeHTMLHeisigKanjiTable() {

		int kanjiPerPage = 100;
		int last = 0;

		// il tipo di lista caratterizza l'algoritmo creando al suo interno
		// functionoids specifici
		EVKanjiHTMListHeisig kl = new EVKanjiHTMListHeisig(this);

		// questa iterazione DI FATTO, è sul numero di pagine in cui viene
		// ripartita la pagina
		for (int i = 0; i < Cfg.KANJI_LAST_HEISIG1; i += kanjiPerPage) {
			last = ((i + kanjiPerPage) > Cfg.KANJI_LAST_HEISIG1) ? Cfg.KANJI_LAST_HEISIG1
					: i + kanjiPerPage;
			kl.setFirstIdx(i);
			kl.setLastIdx(last);
			// build header - footer Etc.
			String title = "kanji from " + i + " to " + last;
			String htmlBefore = HTMLEVUtils
					.HTMLDocStart(title, "kanjilist.css");
			String htmlAfter = HTMLEVUtils.HTMLEnd;
			// invoca stampa scrittura di una delle pagine
			String filepathname = Cfg.PATHNAME_HEISIG + "_" + i + "-" + last;
			if (!kl.writeKanjiList(filepathname, htmlBefore, htmlAfter,".html")) {
				log.error("");
				return false;
			}
		}
		return true;
	}

	public void dump() {
		container.dump(" : ");
	}

	public TreeSet<EVKanjiEntry> getCopyOfEntries(
			Comparator<EVKanjiEntry> comparator) {
		TreeSet<EVKanjiEntry> ts = new TreeSet<EVKanjiEntry>();
		ts.addAll(container.getEntriesList(comparator));
		return ts;
	}

	String csvDelimiter;
	public KanjiContainer<EVKanjiEntry> container;

	static org.apache.log4j.Logger log = Logger.getLogger(EVKanjiManager.class);
}
