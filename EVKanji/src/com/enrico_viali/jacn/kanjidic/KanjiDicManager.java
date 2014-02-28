package com.enrico_viali.jacn.kanjidic;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.*;
import com.enrico_viali.utils.*;

public class KanjiDicManager extends EVManager {

	public KanjiDicManager() {
		super();
		this.entriesByKanji = new HashMap<String, KanjidicEntry>();

		// workaround bug jdk su ArrayList
		entriesByHNr = new KanjidicEntry[3010];

		String dbpath = Cfg.PATH_JPCN_DATA + Cfg.FNAME_KANJIDIC_DB;
		try {
			_dmdb = new RDBManagerKanjiDic(dbpath);
		} catch (Exception e) {
			log.warn("not found kanjidic db at: " + dbpath);
			e.printStackTrace();
		}
	}

	public boolean joyoStats() {
		boolean ret = true;
		int[] counts = new int[11];
		int uninitializedInt = 0;
		for (int i = 0; i < counts.length; i++) {
			counts[i] = 0;
		}
		Collection<KanjidicEntry> entries = entriesByKanji.values();
		for (KanjidicEntry k : entries) {
			if (k.getJouyou() >= 0 && k.getJouyou() <= 10) {
				counts[k.getJouyou()]++;
			} else if (k.getJouyou() == Utl.NOT_INITIALIZED_INT) {
				uninitializedInt++;
			} else {
				log.error("wrong value for joyo: " + k.getJouyou());
			}
		}

		int sumDoubleCheck = uninitializedInt;
		String s = "tot kanjis: " + entriesByKanji.values().size();
		s += " - joyo:";
		s += "\n" + Utl.NOT_INITIALIZED_INT + ": " + uninitializedInt;
		for (int i = 0; i < counts.length; i++) {
			s += "\nlevel " + i + ": " + counts[i];
			sumDoubleCheck += counts[i];
		}
		s += "\ndouble check on sum: " + sumDoubleCheck;
		log.info(s);

		return ret;
	}

	public boolean jlptStats() {
		boolean ret = true;
		int[] counts = new int[6];
		int uninitializedInt = 0;
		for (int i = 0; i < counts.length; i++) {
			counts[i] = 0;
		}
		Collection<KanjidicEntry> entries = entriesByKanji.values();
		for (KanjidicEntry k : entries) {
			if (k.getOldJlpt() >=1  && k.getOldJlpt() <= 4) {
				counts[k.getOldJlpt()]++;
			} else if (k.getOldJlpt() == Utl.NOT_INITIALIZED_INT) {
				uninitializedInt++;
			} else {
				log.error("wrong value for JLPT: " + k.getOldJlpt());
			}
		}

		int sumDoubleCheck = uninitializedInt;
		String s = "tot kanjis: " + entriesByKanji.values().size();
		s += " - jlpt:";
		s += "\n" + Utl.NOT_INITIALIZED_INT + ": " + uninitializedInt;
		for (int i = 0; i < counts.length; i++) {
			s += "\nlevel " + i + ": " + counts[i];
			sumDoubleCheck += counts[i];
		}
		s += "\ndouble check on sum: " + sumDoubleCheck;
		log.info(s);

		return ret;
	}

	/**
	 * @param entry
	 * @return
	 */
	boolean addEntry(KanjidicEntry entry) {
		entriesByKanji.put(entry.kanji, entry);
		if (1 <= entry.heisigNr && entry.heisigNr <= 3007) {
			entriesByHNr[entry.heisigNr] = entry;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean add(EVJPCNEntry e) {
		if (!(e instanceof KanjidicEntry)) {
			log.error("found unexpected type: " + e.getClass().getName());
			System.exit(1);
		}
		return add(e);
	}

	public KanjidicEntry find(String key) {
		return this.entriesByKanji.get(key);
	}

	public KanjidicEntry findByHNr(int nr) {
		KanjidicEntry e = entriesByHNr[nr];
		if (e == null) {
			log.warn("kanjidic manager[" + nr + "] null");
		}
		return e;
	}

	public boolean load() throws Exception  {
		return load(StringUtils.adjustPath(Cfg.PATHNAME_KANJDIC),
				Cfg.ENCODING_EUC_JP);
	}

	public boolean load(String filepath, String encoding) throws Exception {
		InputStreamReader isr;
		BufferedReader bufferedReader;

		if (isLoaded()) {
			log.error("attempt to load in memory evakanji already loaded");
			return false;
		}

		log.trace("Dir di lavoro: " + System.getProperty("user.dir"));
		log.trace("opening: " + filepath);
		isr = new InputStreamReader(new FileInputStream(filepath), encoding);
		bufferedReader = new BufferedReader(isr);
		log.trace("Encoding: in lettura " + isr.getEncoding());

		int lineNr = 0;
		String kanjdicLine;
		kanjdicLine = bufferedReader.readLine(); // skip first line
		int kanjiWithHeisigNr = 0;
		for (; (kanjdicLine = bufferedReader.readLine()) != null
				&& (lineNr < 99999); lineNr++) {
			KanjidicEntry kanjidicEntry = new KanjidicEntry("");
			if (!KanjidicEntry.buildFromLine(kanjdicLine, Cfg.SEP_KANJIDIC,
					kanjidicEntry)) {
				log.error("read from kanjidic file failed, exiting\nline nr "
						+ lineNr + " was" + kanjdicLine);
				System.exit(1);
				// break;
			}
			if (addEntry(kanjidicEntry))
				kanjiWithHeisigNr++;
			if (!kanjidicEntry.hasOnReading()) {
				// log.warn("not found \"on\"-reading for kanji: "+kanjidicEntry.getKanji());
			}
			log.debug("input line: " + lineNr + ": " + kanjdicLine);
			if (lineNr % 100 == 0)
				log.debug("output line: " + lineNr + " "
						+ kanjidicEntry.getCSVLine(Cfg.SEP_EVKANJI_OUT, "sep"));
		}
		if (lineNr < 6355)
			log.warn("in kanjidic less than 6355 lines, found : " + lineNr);
		if (kanjiWithHeisigNr != 3007)
			log.warn("kanjis without heisig nr: " + kanjiWithHeisigNr
					+ " with H nr: " + (lineNr - kanjiWithHeisigNr));

		joyoStats();
		jlptStats();

		return true;
	}

	@Override
	public boolean writeToCSV(String pathname, boolean ovwFile, boolean ignErr) {
		return writeToCSV(pathname, Utl.ENCODING_UTF8, Cfg.SEP_EVKANJI_OUT,
				Cfg.SEP_EVKANJI_OUT_L2);
	}

	public boolean writeToCSV(String filePath, String encoding,
			String separator, String replSep) {

		if (!isLoaded()) {
			log.error("attempt to write evakanji not loaded");
			return false;
		}

		FileOutputStream fos;
		OutputStreamWriter outwriter;
		try {
			fos = new FileOutputStream(filePath);
			log.info("output: file" + filePath);
			outwriter = new OutputStreamWriter(fos, encoding);
			log.info("opening: " + Cfg.KANJIDIC_FULL_PATH);
			log.info("Encoding: in scrittura " + outwriter.getEncoding());

			int lineNr = 0;
			for (KanjidicEntry entry : this.entriesByHNr) {
				String line = entry.getCSVLine(separator, replSep);
				log.debug("writing line: " + lineNr + " " + line);
				outwriter.write(line + Cfg.NEWLINE);
			}
			log.info("nr linee input: " + lineNr);
			return true;
		} catch (IOException e) {
			log.error("", e);
		}

		return false;
	}

	public boolean writeToDB() {
		log.error("non implementata: public boolean writeToDB()");
		return false;
	}

	public void dumpByHNr() {
		for (int i = 1; i < entriesByHNr.length; i++) {
			KanjidicEntry cursor = entriesByHNr[i];
			if (cursor != null) {
				log.info("" + i + ": " + cursor.toString(" : "));
			} else {
				log.info("KanjidicEntry " + i + ": is null");
			}
		}
	}

	@Override
	public int size() {
		int nr = entriesByKanji.size();
		int inArray = 0;
		for (KanjidicEntry k : entriesByHNr)
			if (k != null)
				inArray++;
		if (inArray != nr) {
			log.error("internal incoeherence, in map " + nr + " in array "
					+ inArray);
		}
		return nr;
	}

	@Override
	public TreeSet<EVJPCNEntry> getCopyOfEntries(Comparator<EVJPCNEntry> c) {
		TreeSet<EVJPCNEntry> ts = new TreeSet<EVJPCNEntry>();
		ts.addAll(entriesByKanji.values());
		return ts;
	}

	@Override
	public boolean isLoaded() {
		if (entriesByKanji == null) {
			log.error("entriesByKanji is empty, exiting");
			System.exit(1);
			return false;
		}
		return entriesByKanji.keySet().size() > 0;
	}

	/*
	 * non c'e verso di farlo funzionare con arraylist, passato a vettore
	 * ArrayList<KanjidicEntry> entriesByHNr;
	 */
	Map<String, KanjidicEntry> entriesByKanji;
	KanjidicEntry[] entriesByHNr;
	RDBManagerKanjiDic _dmdb;
	private static org.apache.log4j.Logger log = Logger
			.getLogger(KanjiDicManager.class);
}
