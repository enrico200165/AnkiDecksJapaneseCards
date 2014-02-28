package com.enrico_viali.jacn.kanjidic;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.libs.rdb_jdbc.*;

public class KanjiDicTable extends JDBCEVTable {

	public KanjiDicTable(IRDBManager dmdb) throws Exception {
		super("da specificare", dmdb);
		int posInInputCSVFile = 0;

		addField("kanji", java.sql.Types.VARCHAR, 0, TipoChiave.IDXED_NOT_UNIQUE, posInInputCSVFile++);

		addField("code_JIS", java.sql.Types.VARCHAR, 240, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("on", java.sql.Types.VARCHAR, 0, TipoChiave.IDXED_NOT_UNIQUE, posInInputCSVFile++);
		addField("kun", java.sql.Types.VARCHAR, 0, TipoChiave.IDXED_NOT_UNIQUE, posInInputCSVFile++);
		addField("kanjidic_meaning", java.sql.Types.VARCHAR, 0, TipoChiave.IDXED_NOT_UNIQUE, posInInputCSVFile++);
		addField("bushu_nr", java.sql.Types.INTEGER, 3, TipoChiave.NOIDX_NOKEY_NOUNIQUE,posInInputCSVFile++);
		addField("hist_rad_nr", java.sql.Types.INTEGER, 3, TipoChiave.NOIDX_NOKEY_NOUNIQUE,posInInputCSVFile++);

		// frequency is a number from 1 to 2,501
		addField("frequency", java.sql.Types.INTEGER, 3, TipoChiave.NOIDX_NOKEY_NOUNIQUE,posInInputCSVFile++);
		addField("grade", java.sql.Types.INTEGER, 3, TipoChiave.NOIDX_NOKEY_NOUNIQUE,posInInputCSVFile++);
		addField("jlpt_level", java.sql.Types.INTEGER, 3, TipoChiave.NOIDX_NOKEY_NOUNIQUE,posInInputCSVFile++);
		addField("strokes", java.sql.Types.INTEGER, 3, TipoChiave.NOIDX_NOKEY_NOUNIQUE,posInInputCSVFile++);
		addField("heisig", java.sql.Types.INTEGER, 3, TipoChiave.NOIDX_NOKEY_NOUNIQUE,posInInputCSVFile++);
	}

	public boolean writeToDB(KanjidicEntry KanjidicEntry, Statement st, boolean unique)
	throws SQLException {

		String[] values = new String[40];
		KanjidicEntry.fillValuesVector(values);

		return insertRow(st, values, false, -1, unique);
	}

	KanjidicEntry findEdictHeadword(String kanjiWord) {
		String s = "SELECT * FROM " + this.getName() + " WHERE " + fields.get(0).getFieldName()
		+ "= '" + kanjiWord + "'";
		log.debug("eseguo query: " + s);
		if (dmdb.getConnection() == null) {
			log.error("conn(ection) nulla)");
			return null;
		}

		try {
			Statement st = dmdb.getConnection().createStatement();
			ResultSet rs = st.executeQuery(s);
			while (rs.next()) {
				KanjidicEntry entry = new KanjidicEntry("");
				/* da copia e incolla da altra entry, sostituire con
				 * codice appropriato
				entry.setKanjiWord(rs.getString(pos++));
				entry.setReading(rs.getString(pos++));
				entry.setWholeDescription(rs.getString(pos++));
				log.trace("trovata edictByHeadWord entry per kanjiWord: " + kanjiWord);
				*/
				return entry;
			}// end while loop
		} catch (SQLException e) {
			log.error("fallita query kanjword : " + kanjiWord + "statement era:\n" + s, e);
		}

		return null;
	}

	ArrayList<KanjidicEntry> selectKanjiCompos(String kanjiOrWord, String test, int len) {
		String s = "SELECT * FROM " + this.getName()
		// + " FETCH FIRST 10 ROWS ONLY "
		+ " WHERE " + fields.get(0).getFieldName() + test
		+ " AND len >= " + len
		+ " AND " + fields.get(4).getFieldName() + " = 1"
		+ " ORDER BY " + fields.get(3).getFieldName();

		log.debug("eseguo query: " + s);
		if (dmdb.getConnection() == null) {
			log.error("conn(ection) nulla)");
			return null;
		}

		ArrayList<KanjidicEntry> compos = new ArrayList<KanjidicEntry>();
		try {
			Statement st = dmdb.getConnection().createStatement();
			ResultSet rs = st.executeQuery(s);
			while (rs.next()) {
				KanjidicEntry entry = new KanjidicEntry("");
				/*
				entry.setKanjiWord(rs.getString(pos++));
				entry.setReading(rs.getString(pos++));
				entry.setWholeDescription(rs.getString(pos++));
				log.trace("trovata edictByHeadWord entry per kanjiWord: " + kanjiOrWord);
				*/
				compos.add(entry);
			}// end while loop
			return compos;
		} catch (SQLException e) {
			log.error("fallita query kanjword : " + kanjiOrWord + "statement era:\n" + s, e);
		}

		return null;
	}

	ArrayList<KanjidicEntry> selectExpression(String kanjiOrWord, String test, int len) {
		String s = "SELECT * FROM " + this.getName();
		s+= " WHERE " + fields.get(0).getFieldName() +" = '"+kanjiOrWord+"'";
		// + " FETCH FIRST 10 ROWS ONLY ";
		if (test.length() > 0) {
			s += " AND " + fields.get(0).getFieldName() + test;
		}
		s += " ORDER BY " + fields.get(3).getFieldName();

		log.debug("eseguo query: " + s);
		if (dmdb.getConnection() == null) {
			log.error("conn(ection) nulla)");
			return null;
		}

		ArrayList<KanjidicEntry> compos = new ArrayList<KanjidicEntry>();
		try {
			Statement st = dmdb.getConnection().createStatement();
			ResultSet rs = st.executeQuery(s);
			while (rs.next()) {
				KanjidicEntry entry = new KanjidicEntry("");
				/*
				entry.setKanjiWord(rs.getString(pos++));
				entry.setReading(rs.getString(pos++));
				entry.setWholeDescription(rs.getString(pos++));
				log.trace("trovata edictByHeadWord entry per kanjiWord: " + kanjiOrWord);
				*/
				compos.add(entry);
			}// end while loop
			return compos;
		} catch (SQLException e) {
			log.error("fallita query kanjword : " + kanjiOrWord + "statement era:\n" + s, e);
		}

		return null;
	}

	
	ArrayList<KanjidicEntry> selectReading(String kanjiOrWord, String test, int len) {
		String s = "SELECT * FROM " + this.getName();
		s+= " WHERE " + fields.get(1).getFieldName() +" = '"+kanjiOrWord+"'";
		// + " FETCH FIRST 10 ROWS ONLY ";
		if (test.length() > 0) {
			s += " AND " + fields.get(0).getFieldName() + test;
		}
		s += " ORDER BY " + fields.get(3).getFieldName();

		log.debug("eseguo query: " + s);
		if (dmdb.getConnection() == null) {
			log.error("conn(ection) nulla)");
			return null;
		}

		ArrayList<KanjidicEntry> compos = new ArrayList<KanjidicEntry>();
		try {
			Statement st = dmdb.getConnection().createStatement();
			ResultSet rs = st.executeQuery(s);
			while (rs.next()) {
				KanjidicEntry entry = new KanjidicEntry("");
				/*
				entry.setKanjiWord(rs.getString(pos++));
				entry.setReading(rs.getString(pos++));
				entry.setWholeDescription(rs.getString(pos++));
				log.trace("trovata edictByHeadWord entry per kanjiWord: " + kanjiOrWord);
				*/
				compos.add(entry);
			}// end while loop
			return compos;
		} catch (SQLException e) {
			log.error("fallita query kanjword : " + kanjiOrWord + "statement era:\n" + s, e);
		}

		return null;
	}

	
	boolean entryExists(String kanjiWord) {

		String s = "SELECT * FROM " + this.getName() + " WHERE " + fields.get(0).getFieldName()
		+ "= '" + kanjiWord + "'";
		log.debug("eseguo query: " + s);
		if (dmdb.getConnection() == null) {
			log.error("conn(ection) nulla)");
			return false;
		}

		try {
			Statement st = dmdb.getConnection().createStatement();
			ResultSet rs = st.executeQuery(s);
			log.debug("esamino lo statement per capire, st.getMaxRows(): " + st.getMaxRows());
			while (rs.next()) {
				log.debug("trovata edictByHeadWord entry per kanjiWord: " + kanjiWord);
				rs = null;
				return true;
			}// end while loop
		} catch (SQLException e) {
			log.error("fallita query kanjword : " + kanjiWord + "statement era:\n" + s, e);
		}

		return false;
	}

	private static org.apache.log4j.Logger log = Logger.getLogger(KanjiDicTable.class);
}
