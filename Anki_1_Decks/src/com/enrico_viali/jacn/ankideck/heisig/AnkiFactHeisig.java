package com.enrico_viali.jacn.ankideck.heisig;

import java.sql.*;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.generic.*;
import com.enrico_viali.utils.Utl;

/**
 * AnkiFactHeisig: a = Anki
 * 
 * @author enrico
 * 
 */

public class AnkiFactHeisig extends Fact {

	public AnkiFactHeisig(long factID, String s,IAnkiDeckGeneric deckMgrPar) {
		super(factID,deckMgrPar);
		this.kanji = Utl.NOT_INITIALIZED_STRING;
		this.hKeyword = Utl.NOT_INITIALIZED_STRING;
	}

	public AnkiFactHeisig(IAnkiDeckGeneric deckMgrPar) {
		super(Utl.NOT_INITIALIZED_INT, deckMgrPar);
		this.kanji = Utl.NOT_INITIALIZED_STRING;
		this.hKeyword = Utl.NOT_INITIALIZED_STRING;
	}

	public int getHeisigNumber() {
		return this.heisigNumber;
	}

	public String toString(String sep) {
		String s = "";
		s += this.kanji;
		s += sep + this.heisigNumber;
		s += sep + this.hKeyword;
		s += sep + this.hStory;
		s += sep + this.lesson;
		s += sep + this.onYomi;
		s += sep + this.strokes;

		return s;
	}

	boolean buildHDeckEntryFromFileLine(String line, String sep, AnkiFactHeisig entry) {
		return true;
	}

	public String getKanjiWord() {
		return kanji;
	}

	public int getHLesson() {
		return lesson;
	}

	public String getHKeyword() {
		return hKeyword;
	}

	public void setKanjiWord(String kanjiWord) {
		this.kanji = kanjiWord;
	}

	public String getHStory() {
		return hStory;
	}

	public void setHStory(String s) {
		this.hStory = s;
	}

	public boolean fillValuesVector(String[] v) {

		if (v.length < 30) {
			log.error("vettore valori troppo piccolo, nr elementi: " + v.length);
			return false;
		}

		int i = 0;
		if ((kanji != null) && (kanji.length() >= 0)) {
			v[i++] = kanji;
		} else {
			log.error("valore indefinito");
			v[i++] = "";
			return false;
		}
		return true;
	}

	@Override
	public boolean fillFromRS(ResultSet rs, long factIDPar) throws SQLException {
		try {
			if (! super.fillFromRS(rs, factIDPar)) {
				log.error("non posso fare fill di "+this.getClass());
				return false;
			}		

			if (this.kanji == null || this.kanji.length() <= 0) {
				log.error("kaniìji, campo chiave, è nullo o vuoto, factID: " + factID);
			}
			this.heisigNumber = getFieldIntValue("Heisig number",true);
			this.kanji = getFieldStrValue("Kanji", true);
			this.hKeyword = getFieldStrValue("Keyword",false);
			this.lesson = getFieldIntValue("Lesson number",false);
			this.onYomi = getFieldStrValue("On-yomi",false);
			this.hStory = getFieldStrValue("Story",false);
			this.strokes = getFieldIntValue("Stroke count",false);
		} catch (NumberFormatException n) {			
			log.error("error in number format",n);
			log.error("dump della superclasse di AnkiFactHeisig, AnkiFact: "+super.toString());
			return false;
		}
		catch (AnkiDeckMalformedFact e) {
			log.error("ill formed fact",e);
			return false;
		}
		return true;
	}

	String kanji; // quando non ci sono kanji duplica campo kanaWord
	int heisigNumber;
	String hKeyword;
	int lesson;
	String onYomi;
	String hStory;
	int strokes;
	long factID;

	private static org.apache.log4j.Logger log = Logger.getLogger(AnkiFactHeisig.class);
}
