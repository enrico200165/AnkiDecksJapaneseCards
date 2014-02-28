package com.enrico_viali.jacn.main;

import java.io.*;
import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.heisig.*;
import com.enrico_viali.jacn.common.*;
import com.enrico_viali.jacn.edict.EdictManager;
import com.enrico_viali.jacn.edict.Edict_1_Manager;
import com.enrico_viali.jacn.evkanji.core.*;
import com.enrico_viali.jacn.functionalities.*;
import com.enrico_viali.jacn.kanjidic.*;
import com.enrico_viali.utils.Utl;

/**
 * @author enrico
 * 
 */

public class GlobalManager {

	public GlobalManager() {
		super();
		loaded = false;
		evKanjiMgr = new EVKanjiManager(0, Cfg.SEP_EVKANJI_IN);
	}

	public boolean loadFromVKanjiCSV(String pathName, boolean enrichIt,
			boolean ignErr, boolean edictCompos) {
		if (loaded)
			return true;

		if (!(loaded = evKanjiMgr.loadFromCSVFile(pathName, false))) {
			log.error("");
			return loaded;
		}
		// evKanjiMgr.dump(" : ", true);
		if (!evKanjiMgr.check(3)) {
			log.error("errori in evkanj");
			if (!ignErr)
				return false;
		}
		loaded = true;
		if (enrichIt) {
			enrichEntries(/* enricoh edictByHeadWord compos */edictCompos);
		}

		return loaded;
	}

	public boolean updateAnkiJPCNDeck(String dataDir, String workDir,
			String fname, String ext, boolean ignErr, FieldUpdate defaultUpdat) {
		UpdateKanjiDeck updater = new UpdateKanjiDeck(this, ignErr);
		execWorkflow(dataDir, workDir, fname, ext, updater);

		return true;
	}

	/**
	 * @param ignErr
	 * @param defaultUpdate
	 * @return
	 */
	public boolean execWorkflow(String dataDir, String workDir, String fname,
			String ext, Executable exc) {

		File orig = new File(dataDir + fname + "." + ext);
		File previous = new File(dataDir + fname + ".previous." + ext);
		File work = new File(workDir + fname + ".work." + ext);
		File newFile = new File(dataDir + fname + ".new." + ext);

		{
			File dataDirF = new File(dataDir);
			File workDirF = new File(workDir);
			if (!dataDirF.exists() || !dataDirF.isDirectory()
					|| !workDirF.exists() || !workDirF.isDirectory()) {
				log.error("non existing one of the dirs: " + dataDir + " or  "
						+ workDir);
				return false;
			}
			if (!orig.exists()) {
				log.error("non existing file: " + orig.getAbsolutePath());
				return false;
			}
			if (previous.exists()) {
				log.error("previous decks exist, please process it: " + "\n"
						+ previous.getAbsolutePath());
				return false;
			}
			if (newFile.exists()) {
				log.error("new decks exist, please process it: " + "\n"
						+ newFile.getAbsoluteFile());
				return false;
			}
			if (work.exists()) {
				log.error("work decks exist, please process it: "
						+ work.getAbsolutePath());
				return false;
			}
		}

		// --- copy to .previous in data dir and to .work in work dir
		if (!Utl.CopyFile(orig, previous)) {
			log.error("aborting because cannot save original deck to: "
					+ previous.getAbsolutePath());
			return false;
		}
		if (!Utl.CopyFile(orig, work)) {
			log.error("unable to copy " + orig.getAbsolutePath() + " to "
					+ orig.getAbsolutePath());
			return false;
		}
		if (!work.exists()) {
			log.error("cannot find " + work.getAbsolutePath());
			return false;
		}

		boolean ret = true;
		try {
			ret = exc.perform(work.getAbsolutePath());
		} finally {
			// necessario per chiusura files ed evitare fallimento rename
			System.gc();
		}

		if (ret) {
			String tmpfname = orig.getAbsoluteFile() + ".delete"
					+ Utl.tStampCompact();
			if (!Utl.CopyFile(orig, (new File(tmpfname)))) {
				log.error("renaming " + orig.getAbsolutePath() + " to "
						+ tmpfname);
				ret = false;
			} else {
				if (!orig.canWrite()) {
					log.error("not deletable(writeable) "
							+ orig.getAbsolutePath());
				} else if (!orig.exists()) {
					log.error("");
				} else if (!orig.delete()) {
					log.error("failed deletion of " + orig.getAbsolutePath());
				}
			}
			if (!work.renameTo(newFile)) {
				log.error("failed rename of " + work.getAbsolutePath()
						+ "\nto: " + newFile);
				ret = false;
			}
			if (ret)
				log.info("anki jpcn aggiornato con dati da evkanji file");
			return ret;
		} else {
			log.error("workflow fallito");
			// -- problema, cancelliamo tutti i deck tranne orig
			newFile.renameTo(new File(newFile.getAbsoluteFile() + "_del+"
					+ Utl.tStamp() + ext));
			previous.renameTo(new File(previous.getAbsoluteFile() + "_del+"
					+ Utl.tStamp() + ext));
			work.renameTo(new File(work.getAbsoluteFile() + "_del+"
					+ Utl.tStamp() + ext));
			return false;
		}
	}

	/**
	 * Riempie l' EVKanjiManager passato come parametro con le entries da
	 * importare
	 * 
	 * @param deltaMgr
	 * @return
	 * @throws IOException
	 */
	public boolean importDeltaFile(EVKanjiManager deltaMgr) throws Exception {
		boolean ok = true;

		Executable ex = new DeltaLoader(this.evKanjiMgr, deltaMgr, this);
		execWorkflow(Cfg.getDirJPCNData(), Cfg.getDirWork(), "jpcn", "csv", ex);
		return ok;
	}

	/**
	 * Per un range di heisig number individua le entries che non contengono
	 * informazioni Heisig e li scrive in un file da riempire (e reimportare)
	 * 
	 * @param evkanjiPathname
	 * @param fromHNumber
	 * @param toHNumber
	 * @param nrDeltas
	 * @return
	 * @throws Exception
	 */
	public boolean generateDeltaFile(String evkanjiPathname, int fromHNumber,
			int toHNumber, int nrDeltas) throws Exception {
		return generateHDeltaFile(evkanjiPathname, Cfg.getDirWork(),
				Cfg.FNAME_DELTA_EMPTY, Utl.ENCODING_UTF8, Cfg.SEP_DELTA_OUT,
				Cfg.SEP_REPLACER, fromHNumber, toHNumber, nrDeltas);
	}

	/**
	 * Per un range di heisig number individua le entries che non contengono
	 * informazioni Heisig e li scrive in un file da riempire (e reimportare)
	 * 
	 * @param filePath
	 * @param encoding
	 * @param sep
	 * @param replSep
	 * @param fromHNumber
	 * @param toHNumber
	 * @return
	 * @throws IOException
	 */
	public boolean generateHDeltaFile(String evkanjiPathname, String deltaDir,
			String deltaFname, String encoding, String sep, String replSep,
			int fromHNumber, int toHNumber, int nrDeltas) throws Exception {

		if (deltaFname == null || deltaFname.length() <= 0 || deltaDir == null
				|| deltaDir.length() <= 0 || !(new File(deltaDir)).exists()
				|| !(new File(deltaDir)).isDirectory()) {
			log.error("");
			return false;
		}
		if ((new File(deltaDir + Cfg.FNAME_DELTA_FILLED).exists())) {
			log.warn("cannot generate delta, you must process first" + deltaDir
					+ Cfg.FNAME_DELTA_FILLED);
			return false;
		}

		String filePath = deltaDir + deltaFname;
		if ((new File(filePath).exists())) {
			log.warn("cannot (over)write delta files, already exists: "
					+ filePath);
			return false;
		}

		if (!loaded && !loadFromVKanjiCSV(evkanjiPathname, true, false, false)) {
			log.error("");
			return false;
		}

		// --- nuovi kanj vanno arricchiti ----
		KanjiDicManager kanjiDicMgr = new KanjiDicManager();
		ADeckHeisigMgr aDeckHeisigMgr = new ADeckHeisigMgr(Cfg.PATH_DIR_DATA);
		if (!aDeckHeisigMgr.load()
				|| !kanjiDicMgr.load(Cfg.PATHNAME_KANJDIC, Cfg.ENCODING_EUC_JP)) {
			return false;
		}

		FileOutputStream fos;
		OutputStreamWriter outwriter;
		try {
			fos = new FileOutputStream(filePath);
			outwriter = new OutputStreamWriter(fos, encoding);
			log.info("opening: " + filePath + " encoding: in scrittura "
					+ outwriter.getEncoding());

			EVKanjiEntry evKanji;
			int lineNr = 0;
			int nrKanjiEsaminati = 0;
			int nrSenzaHeisig = 0;
			for (lineNr = fromHNumber; lineNr <= toHNumber; lineNr++) {
				nrKanjiEsaminati++;
				if ((evKanji = evKanjiMgr.findByHNr(lineNr)) == null) {
					if (nrSenzaHeisig > nrDeltas
							&& nrDeltas != Utl.NOT_INITIALIZED_INT)
						break;
					// non presente nel mio file, devo aggiungerlo
					evKanji = new EVKanjiEntry();
					evKanji.setHeisigNumber(lineNr);
					evKanji.enrichFromAll(aDeckHeisigMgr, kanjiDicMgr);
					String line = evKanji
							.toLineForHeisigDeltaFile(sep, replSep);
					outwriter.write(line + Cfg.NEWLINE);
					nrSenzaHeisig++;
				} else {
					log.debug("trovato kanji per heisig nr: " + lineNr);
				}
			}
			log.info("writeHeisigDeltaFile in: " + filePath
					+ " \nnr kanji esaminati: " + nrKanjiEsaminati
					+ ", senza heisig e quindi scritti: " + nrSenzaHeisig);
			outwriter.flush();
			return true;
		} catch (IOException e) {
			log.error("", e);
		}

		return false;
	}

	public boolean enrichEntries(boolean edictComp) {
		KanjiDicManager kanjiDicMgr = new KanjiDicManager();
		ADeckHeisigMgr aDeckHeisigMgr = null;
		EdictManager edMgr = new Edict_1_Manager();
		try {
			aDeckHeisigMgr = new ADeckHeisigMgr(Cfg.getDirWork());
		} catch (Exception e) {
			log.error("Failed creating ADeckHeisigMgr object", e);
		}
		loaded = loaded && aDeckHeisigMgr.load(); // ADeckHeisigMgr.dump(":");

		try {
			loaded = loaded && kanjiDicMgr.load();
		} catch (Exception e2) {
			log.error("failed to load kanjidic manager", e2);
			return false;
		}

		if (loaded) {
			log.error("da implementare, codice da riutilizzare sicuramente presente altrove");
			// return evKanjiMgr.enrichEntriesBADReviewGlobally(kanjiDicMgr,
			// aDeckHeisigMgr, edMgr, edictComp);
			return false;
		} else {

			log.error("enrich preparation failed");
			return false;
		}
	}

	boolean loaded;

	public EVKanjiManager evKanjiMgr;
	private static org.apache.log4j.Logger log = Logger
			.getLogger(GlobalManager.class);
}
