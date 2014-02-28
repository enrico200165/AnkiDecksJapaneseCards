package com.enrico_viali.jacn.ankideck.generic;

import java.sql.ResultSet;
import java.sql.SQLException;


import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.utils.Utl;

public class AnkiCardModel {

	public AnkiCardModel(long factId, String exprPar) {
		super();
		id = Utl.NOT_INITIALIZED_INT;
		aFormat = Utl.NOT_INITIALIZED_STRING;
		qFormat = Utl.NOT_INITIALIZED_STRING;
		name = Utl.NOT_INITIALIZED_STRING;
	}

	public long getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	
	public String toString() {
		String s = "";
		String sep = Cfg.SEP_TOSTRING;		
		s = "id="+this.id;
		s+=sep+"name="+name; 
		s+=sep+"qformat="+qFormat; 
		s+=sep+"aformat="+aFormat; 		
		return s;
	}


	public String getQFormat() {
		return qFormat;
	}

	public String getAFormat() {
		return qFormat;
	}


	public boolean fillEntryFromRS(ResultSet rs, long id) throws SQLException {
		boolean ret = true;

		if (rs.next()) {
			int pos = 1;
			this.id = rs.getLong(pos++);
			this.name = rs.getString(pos++);
			this.qFormat = rs.getString(pos++);
			this.aFormat = rs.getString(pos++);
		} else {
			log.error("");
			return false;
		}

		if (!ret) {
			log.error("");
		}

		return ret;
	}

	long   id;
	String name;
	String qFormat;
	String aFormat; 	

	private static org.apache.log4j.Logger log = Logger.getLogger(AnkiCardModel.class);
}
