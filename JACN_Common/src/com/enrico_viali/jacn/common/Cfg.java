package com.enrico_viali.jacn.common;

import java.util.regex.*;

import org.apache.log4j.Logger;

import com.enrico_viali.utils.EnvMap;

/*
 * Cfg.java
 *
 * Created on 29 dicembre 2006, 16.51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * 
 * @author enrico
 */
public class Cfg {

	Cfg() {
		interactive = false;
		ovwEVKanjiFile = false;
	}

	public static final String NOT_AVAIL = "none";
	public static final int NRMAX_KANJI = 3007; // 3001 ma conto H nr da 1
	public static final int KANJI_LAST_HEISIG1 = 2042;
	public static final int KANJI_LAST_HEISIG_ALL = 3007;

	
	// String PATH_DECKHEISIG = "jdbc:sqlite:..\\data\\";

	// static final String PATH_ANKI_DATA =
	// "V:/data/data-pers/enrico/mydocs/interessi/lingue_jp-cn/anki/";

	public static final int KANJI_COMPO_NR = 5;
	public static final String FSTEM_EVKANJI = "jpcn";

	public static final String FNAME_EVKANJI = FSTEM_EVKANJI + ".csv";
	public static final String FNAME_DECK_HEISIG_DB = "Heisig Kanji.anki";
	public static final String FNAME_DECK_EVKANJI_DB = "kanji_enrico.anki";
	public static final String FNAME_DECK_ANNOTATIONS = "annotations.txt";
	public static final String FNAME_DELTA_FILLED = "delta_filled.csv";
	public static final String FNAME_DECK_HEISIG = "Heisigs Remember the Kanji (RTK) 13.anki";
	public static final String FNAME_DELTA_EMPTY = "senzaHeisigInfoDelta.csv";
	public static final String FNAME_KANJIDIC_DB = "kanjidic.sqlite";

	
	public static final String PATH_DIR_DATA = "../data/";	
	public static final String PATH_ANKI_DATA = "V:/data/data-pers/enrico/mydocs/interessi/lingue_jp-cn/anki/";
	public static final String PATH_JPCN_DATA = PATH_DIR_DATA;

	public static final String PATHNAME_JOUYOU = PATH_DIR_DATA+"output/jouyou";
	public static final String PATHNAME_JLPT = PATH_DIR_DATA+"output/jlpt";
	public static final String PATHNAME_HEISIG = PATH_DIR_DATA+"output/heisig";
	public static final String PATHNAME_ANKI_H_DECK = PATH_DIR_DATA;
	public static final String PATHNAME_KANJDIC = PATH_DIR_DATA + "kanjidic.euc";
	public static final String PATHNAME_EVKANJI_IN = PATH_DIR_DATA + "jpcn.csv";
	public static final String PATHNAME_EVKANJI_OUT = "../data/" + "jpcn_out.csv";
	public static final String PATHNAME_DELTAEMPTY = "../data/" + FNAME_DELTA_EMPTY;
	public static final String PATNAME_DELTAEVKANJI = "../data/" + "delta_jpcn.csv";
	public static final String PATHNAME_DELTA_FILLED = "../data/" + FNAME_DELTA_FILLED;
	public static final String PATHNAME_DELTA_JPCN = "../data/" + "delta_filled_enriched.csv";
	public static final String PATHNAME_EDICT = PATH_DIR_DATA + "edict.euc";
	public static final String PATHNAME_EDICT2 = PATH_DIR_DATA + "edict2.euc";
	public static final String FNAME_EDICT_DB = "edictByHeadWord.db";
	public static final String FNAME_EDICT2_DB = "edict2.db";

	public static final String PATTERN_DEF = "/D([^=]+)=(.+)";

	public static final String SEP_EVKANJI_OUT = "\t";
	public static final String SEP_EVKANJI_OUT_L2 = ",";
	public static final String SEP_DELTA_OUT = "|";
	public static final String SEP_DELTA_IN = "\\|";
	public static final String SEP_EVKANJI_IN = SEP_EVKANJI_OUT;
	public static final String SEP_KANJIDIC = " ";
	public static final String SEP_REPLACER = "";
	public static final String SEP_TOSTRING = " : ";

	int CR = 13;
	int LF = 10;
	public static final String ESCAPE = "\"";

	boolean dumpRead = false;
	public static final String OUT_FILE_PATH = "./";

	public static final String ENCODING_EUC_JP = "EUCJIS";
	public static final String ENCODING_KANJIDIC = ENCODING_EUC_JP;
	public static final String ENCODING_EDICT = ENCODING_EUC_JP;
	public static final String ENCODING_OUT = "UTF8";

	// public static final String ENCODING_OUT = "";
	public static final String KANJIDIC_FULL_PATH = "./resources/" + "kanjidic"; // PATH+"kanjidic";

	public static final String NEWLINE = "\n";
	public static final String OUTFILE = OUT_FILE_PATH + "kanjout.txt";

	public static final String HEISIG_TEMPLATE = " &lt;&gt; &lt;&gt; &lt;&gt; []";

	// tokens nei contenuti

	public static final String KANJ_PICT_KWORD = "pictograph";

	public static boolean cleanEmbelish() {
		return true;
	}

	public static boolean isInteractive() {
		return interactive;
	}

	public static void setInteractive(boolean val) {
		interactive = val;
	}

	
	
	public static String getDirAnkiData() {
		if (EnvMap.getEnvMap().containsKey("JPCN_ANKI_DATA_DIR"))
			return EnvMap.getEnv("JPCN_ANKI_DATA_DIR");
		else
			return PATH_ANKI_DATA;
	}

	public static String getDirJPCNData() {
		if (EnvMap.getEnvMap().containsKey("JPCN_DATA_DIR"))
			return EnvMap.getEnv("JPCN_DATA_DIR");
		else
			return PATH_JPCN_DATA;
	}

	public static String getDirWork() {
		return Cfg.PATH_DIR_DATA;
	}

	public static boolean getEnrichWithEdictComposites() {
		return enrichWithEdictComposites;
	}

	public static void setEnrichWithEdictComposites(boolean v) {
		enrichWithEdictComposites = v;
	}

	public static boolean processDef(String def) {
		String value = null;
		String name = null;
		Pattern p = Pattern.compile(Cfg.PATTERN_DEF);
		Matcher m = p.matcher(def);

		if (m.find()) {
			name = m.group(1);
			value = m.group(2);
		} else {
			log.error("invalid value in -D option: " + def);
			return false;
		}

		if (name.equals("enrich_edict_comp")) {
			if (value.equalsIgnoreCase("true"))
				Cfg.enrichWithEdictComposites = true;
			else if (value.equalsIgnoreCase("false"))
				Cfg.enrichWithEdictComposites = false;
			else {
				log.error("invalid value in -D option: " + def);
				return false;
			}
			return true;
		}

		log.error("unknown argument in -D: " + def);
		return false;
	}

	public static void setovwEVKanjiFile(boolean val) {
		ovwEVKanjiFile = val;
	}
	public static boolean getOvwEVKanjiFile() {
		return ovwEVKanjiFile;
	}
	
	public static boolean scratchEdictDB = false;
	public static boolean interactive = false;
	private static boolean enrichWithEdictComposites = true;
	private static boolean ovwEVKanjiFile;

	private static org.apache.log4j.Logger log = Logger.getLogger(Cfg.class);
}
