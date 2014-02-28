package com.enrico_viali.jacn.ankideck.jlpt_word_phrases;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.generic.*;
import com.enrico_viali.jacn.ankideck.heisig.*;
import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.libs.rdb_jdbc.*;

public class AnkiDeckMgrJLPTWords extends AnkiDeckGeneric {

	public AnkiDeckMgrJLPTWords(String deckFname) throws Exception {
		super(deckFname,new ADeckHesigDataModel(
		new RDBManagerSQLite(Cfg.getDirJPCNData()+deckFname,true)),
		"Expression",
		/* allow duplicate expression */true);
	}

	@Override
	public boolean addFact(AnkiFact e) {
		if (e instanceof AnkiFactJLPTWords) {
			return super.addFact(e);
		} else {
			log.error("inserito oggetto non di classe AnkiFactJLPTWords, classe " + e.getClass().getName());
			return false;
		}
	}

	@Override
	public AnkiFact buildFact(long factID, String keyExpression) {
		return new AnkiFactJLPTWords(factID, keyExpression, this);
	}



	private static org.apache.log4j.Logger log = Logger.getLogger(AnkiDeckMgrJLPTWords.class);

}
