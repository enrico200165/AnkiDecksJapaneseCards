package com.enrico_viali.jacn.ankideck.kanji_enrico;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.anki1deck.generic.Anki1Fact;
import com.enrico_viali.jacn.ankideck.generic.*;

public class AnkiFactEnricoKanji extends Anki1Fact {

	public AnkiFactEnricoKanji(long factId, String exprPar, IAnkiDeckGeneric dm) {
		super(factId, dm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean fillFromRS(ResultSet rs, long factIdPar) throws SQLException {
		boolean ret = true;
		if (!super.fillFromRS(rs, factIdPar)) {
			log.error("");
			return false;
		}
		return ret;
	}

	

	
	public void createEmptyFields() {

		insertField((new AnkiField(this)).setFModelName("Expression"));
		insertField((new AnkiField(this)).setFModelName("hanzi_s"));
		insertField((new AnkiField(this)).setFModelName("hanzi_t"));
		insertField((new AnkiField(this)).setFModelName("heisigNr"));
		insertField((new AnkiField(this)).setFModelName("heisigPrim"));
		insertField((new AnkiField(this)).setFModelName("heisigStory"));
		insertField((new AnkiField(this)).setFModelName("heisig_mean"));
		insertField((new AnkiField(this)).setFModelName("on"));
		insertField((new AnkiField(this)).setFModelName("kun"));
		insertField((new AnkiField(this)).setFModelName("kanjidicMean"));
		insertField((new AnkiField(this)).setFModelName("pinyin"));
		insertField((new AnkiField(this)).setFModelName("cnMean"));
		for (int i = 1; i < 6; i++) {
			insertField((new AnkiField(this)).setFModelName("jpc"+i));
			insertField((new AnkiField(this)).setFModelName("jpc"+i+"_kana"));
			insertField((new AnkiField(this)).setFModelName("jpc"+i+"_mean"));
		}
		insertField((new AnkiField(this)).setFModelName("heisigLesson"));
		insertField((new AnkiField(this)).setFModelName("jlpt"));
		insertField((new AnkiField(this)).setFModelName("grade"));
	}

	private static org.apache.log4j.Logger log = Logger.getLogger(AnkiFactEnricoKanji.class);
}
