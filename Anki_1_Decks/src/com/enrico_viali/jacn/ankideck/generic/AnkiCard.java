package com.enrico_viali.jacn.ankideck.generic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.enrico_viali.utils.Utl;

public class AnkiCard {

	public AnkiCard(long factId, String exprPar) {
		super();
		id = Utl.NOT_INITIALIZED_INT;
		cardModelID = Utl.NOT_INITIALIZED_INT;
		factID = Utl.NOT_INITIALIZED_INT;
		tags  = Utl.NOT_INITIALIZED_STRING;
		answer = Utl.NOT_INITIALIZED_STRING;
		question = Utl.NOT_INITIALIZED_STRING;
	}

	public long getID() {
		return id;
	}

	@Override
	public String toString() {
		String sep = "\n"; // Cfg.SEP_TOSTRING;
		String s = "\n";
		s+= "id="+ id + sep;
		s+= "cardModelId="+ this.cardModelID + sep;
		s+= "factId="+ this.factID + sep;
		s+= "tags="+ this.tags + sep;
		s+= "question="+ this.question+ sep;
		s+= "answer="+ this.answer+ sep;		
		return s;
	}

	public String questionToString() {
		return question;
	}

	
	public long getCardModelId() {
		return cardModelID;
	}
	
	public long getFactId() {
		return factID;
	}

	
	public String getQFormat() {
		return question;
	}

	public String getAFormat() {
		return question;
	}

	public boolean fillFromRS(ResultSet rs, long id) throws SQLException {
		boolean ret = true;

		if (rs.next()) {
			int pos = 1;
			this.id = rs.getLong(pos++);
			this.factID = rs.getLong(pos++);
			this.cardModelID = rs.getLong(pos++);			
			this.tags = rs.getString(pos++);
			this.question = rs.getString(pos++);
			this.answer = rs.getString(pos++);
		} else {
			log.error("");
			return false;
		}

		if (!ret) {
			log.error("");
		}

		return ret;
	}

	long id;
	long cardModelID;
	long factID;
	String tags;
	String question;
	String answer;

	private static org.apache.log4j.Logger log = Logger.getLogger(AnkiCard.class);
}
