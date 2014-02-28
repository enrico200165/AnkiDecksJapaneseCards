package com.enrico_viali.jacn.ankideck.kanji_enrico;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.generic.*;
import com.enrico_viali.jacn.ankideck.heisig.*;
import com.enrico_viali.libs.rdb_jdbc.*;

public class AnkiDeckMgrKanjiEnrico extends AnkiDeckGeneric {

	public AnkiDeckMgrKanjiEnrico(String deckFname) throws Exception {
		super(deckFname, new ADeckHesigDataModel(new RDBManagerSQLite(deckFname,true)),"Expression",/* allow dupl*/false);
	}

	@Override
	public boolean load() {
		if(!super.load()) {
			log.error("");
			return false;
		}
		log.error("Dovrei aggiungere qualcosa al generico caricamento di AnkiDeck");
		return true;
	}
	
	
	@Override
	public boolean addFact(AnkiFact e) {
		if (e instanceof AnkiFactEnricoKanji) {
			return super.addFact(e);
		} else {
			log.error("inserito oggetto non di classe AnkiFactJLPTWords, classe " + e.getClass().getName());
			return false;
		}
	}

	@Override
	public AnkiFact buildFact(long factID, String keyExpression) {
		return new AnkiFactEnricoKanji(factID, keyExpression, this);
	}
		
	private static org.apache.log4j.Logger log = Logger.getLogger(AnkiDeckMgrKanjiEnrico.class);
}
