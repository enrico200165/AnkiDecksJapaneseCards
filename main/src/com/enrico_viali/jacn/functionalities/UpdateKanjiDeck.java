package com.enrico_viali.jacn.functionalities;

import org.apache.log4j.Logger;


import com.enrico_viali.jacn.ankideck.kanji_enrico.AnkiDeckMgrKanjiEnrico;
import com.enrico_viali.jacn.common.*;
import com.enrico_viali.jacn.main.GlobalManager;

public class UpdateKanjiDeck implements Executable {
	public UpdateKanjiDeck(GlobalManager par, boolean ignErr) {
		_kMgr = par;
		_ignErr = ignErr;
	}

	public boolean perform(String workFileName) {
		if (_ignErr) {
			log.warn("updateAnkiJPCNDeck() with ignErr=" + _ignErr);
		}
		if (!_kMgr.loadFromVKanjiCSV(Cfg.getDirJPCNData() + Cfg.FNAME_EVKANJI, true, _ignErr, true)) {
			log.error("loadJPCN()  had errors, cannot use it to update anki deck");
			return false;
		}AnkiDeckMgrKanjiEnrico
		 evKanjiAnkiMgr;
		try {
			evKanjiAnkiMgr = new AnkiDeckMgrKanjiEnrico(workFileName);
			if (!evKanjiAnkiMgr.load(Cfg.FNAME_DECK_EVKANJI_DB, "Expression")) {
				log.error("failed load of anki jpcn deck");
				return false;
			}
//			if (!evKanjiAnkiMgr.updateWithEVKanji(Cfg.getDirJPCNData() + Cfg.FSTEM_EVKANJI + ".new-from-delta.csv",
//			workFileName, _kMgr.evKanjiMgr, FieldUpdate.NOOVWR_ERROR)) {
				if (1 == 2) {
					log.error("il metodo deve essere reimplementato, ignora il messaggio seguente del codice originale");
					log.error("errori nell'aggiornare anki jpcn con dati da evkanji file");
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			evKanjiAnkiMgr = null; System.gc();
			// Sta nel workflow, ma sembra che li non funzioni
		}
		// questa la vera operazione
		return true;
	}

	private static org.apache.log4j.Logger log = Logger.getLogger(UpdateKanjiDeck.class);
	GlobalManager _kMgr;
	boolean _ignErr;
}

