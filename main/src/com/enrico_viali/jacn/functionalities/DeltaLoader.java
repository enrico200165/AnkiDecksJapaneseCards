package com.enrico_viali.jacn.functionalities;

import java.io.File;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.heisig.ADeckHeisigMgr;
import com.enrico_viali.jacn.common.*;
import com.enrico_viali.jacn.edict.EdictManager;
import com.enrico_viali.jacn.edict.Edict_1_Manager;
import com.enrico_viali.jacn.evkanji.core.*;
import com.enrico_viali.jacn.kanjidic.KanjiDicManager;
import com.enrico_viali.jacn.main.GlobalManager;

public class DeltaLoader implements Executable {

	public DeltaLoader(EVKanjiManager evKanjiMgr, EVKanjiManager deltaMgr, GlobalManager kMgr) {
		_deltaMgr = deltaMgr;
		_kMgr = kMgr;
		_evKanjiMgr = evKanjiMgr;
	}

	
	public boolean perform(String workFileName) {
		if ((new File(Cfg.PATHNAME_DELTA_JPCN).exists())) {
			log.info("cannot import, file exists: " + Cfg.PATHNAME_DELTA_JPCN);
			return false;
		}
		if (!_deltaMgr.readFromHeisigDeltaFile()) {
			log.error("Errors in import delta file, aborting import");
			return false;
		}

		KanjiDicManager kanjiDicMgr = new KanjiDicManager();
		
		try {
			kanjiDicMgr.load();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			log.error("",e2);
			return false;
		}
		ADeckHeisigMgr aDeckHeisigMgr;
		try {
			aDeckHeisigMgr = new ADeckHeisigMgr(Cfg.PATH_DIR_DATA);
			aDeckHeisigMgr.load();
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
		EdictManager edMgr = new Edict_1_Manager();
		if (1==2 ) {
		// (!_deltaMgr.enrichEntriesBADReviewGlobally(kanjiDicMgr, aDeckHeisigMgr, edMgr, false)) {
			log.error("enrichment of delta manager failed method must be reimplemented at high level");
			return false;
		}
		if (!_deltaMgr.check(3)) {
			log.error("delta importa failed, contains invalid entries");
			return false;
		}
		if (!_evKanjiMgr.loadFromCSVFile(Cfg.getDirAnkiData() + "..\\" + Cfg.FSTEM_EVKANJI + ".csv",/* stopOnAnomaly */true)) {
			log.error("failed to load evKanjiMgrdeck, cancelling delta read");
			return false;
		}

		// --- controlliamo che tutti i dati in import siano ok ---
		int nrAlreadyPresent = 0;
		for (IEVJPCNEntry impTmp : _deltaMgr.container.entriesByNr()) {
			EVKanjiEntry imp = (EVKanjiEntry )impTmp;
			EVKanjiEntry previous = _evKanjiMgr.findByHNr(imp.getHNumber());
			if (previous == null) {
				// tutto ok, non è già presente
				continue;
			}
			// anomalia, elemento delta file gia presente nel deck
			nrAlreadyPresent++;
			log.error("elemento delta gia presente nel deck: "
			+ previous.getKanji() + " H nr: " + previous.getHNumber()
			+ " abortisco delta import");
		}
		if (nrAlreadyPresent > 0) {
			log.error("annullo, elementi del delta già presenti nel deck, conteggio: " + nrAlreadyPresent);
			return false;
		}

		// --- se arriviamo qui tutto ok ---
		// aggiungiamo a delta container int nrImported = 0;
		int nrImported = 0;
		for (IEVJPCNEntry  eTmp : _deltaMgr.container.entriesByNr()) {
			EVKanjiEntry e = (EVKanjiEntry) eTmp;
			e.enrichWithEdictComposites(edMgr);
			_evKanjiMgr.add(e);
			nrImported++;
			log.info("imported delta entry, nr "+nrImported+" :\n" + e.detailedDump("", ","));
		}

		// scriviamo in formato EVKANJI per consentire import ad anki
		_evKanjiMgr.writeToCSV(workFileName,/* ovwFile */true, /* ignErr */false, false);
		// Adesso scriviamo in formato evkanji i contenuti del delta per anki
		_deltaMgr.writeDelta2AnkiFile();

		return true;
	}

	GlobalManager _kMgr;
	EVKanjiManager _deltaMgr;
	EVKanjiManager _evKanjiMgr;
	private static org.apache.log4j.Logger log = Logger.getLogger(DeltaLoader.class);
}
