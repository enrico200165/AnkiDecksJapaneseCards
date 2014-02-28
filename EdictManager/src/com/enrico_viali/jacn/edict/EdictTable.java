package com.enrico_viali.jacn.edict;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.libs.rdb_jdbc.*;

public class EdictTable extends JDBCEVTable {

	public EdictTable(IRDBManager dmdb) throws Exception {
		super("edictmain", dmdb);
		int posInInputCSVFile = 0;

		addField("kanjiword", java.sql.Types.VARCHAR, 0,
				TipoChiave.IDXED_NOT_UNIQUE, posInInputCSVFile++);
		addField("kanaword", java.sql.Types.VARCHAR, 0,
				TipoChiave.IDXED_NOT_UNIQUE, posInInputCSVFile++);
		addField("descriptions", java.sql.Types.VARCHAR, 240,
				TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("len", java.sql.Types.INTEGER, 3,
				TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("isCommon", java.sql.Types.BOOLEAN, 0,
				TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("field", java.sql.Types.VARCHAR, 24,
				TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("isNoun", java.sql.Types.BOOLEAN, 0,
				TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("isVerb", java.sql.Types.BOOLEAN, 0,
				TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("isAdjective", java.sql.Types.BOOLEAN, 0,
				TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("jlpt", java.sql.Types.INTEGER, 0,
				TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("line", java.sql.Types.BIGINT, 0,
				TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
	}

	public boolean writeToDB(IEdictEntry edictSimpleEntry, Statement st,
			boolean unique) throws SQLException {

		String[] values = new String[40];
		edictSimpleEntry.fillValuesVector(values);

		return insertRow(st, values, false, -1, unique);
	}

	IEdictEntry findEdictHeadword(String kanjiWord) {
		String s = "SELECT * FROM " + this.getName() + " WHERE "
				+ fields.get(0).getFieldName() + "= '" + kanjiWord + "'";
		log.debug("eseguo query: " + s);
		if (dmdb.getConnection() == null) {
			log.error("conn(ection) nulla)");
			return null;
		}

		try {
			Statement st = dmdb.getConnection().createStatement();
			ResultSet rs = st.executeQuery(s);
			int pos = 1;
			while (rs.next()) {
				IEdictEntry entry = new Edict_1_Entry();
				entry.setKanjiWord(rs.getString(pos++));
				entry.setReading(rs.getString(pos++));
				entry.setWholeDescription(rs.getString(pos++));
				log.trace("trovata edictByHeadWord entry per kanjiWord: "
						+ kanjiWord);
				return entry;
			}// end while loop
		} catch (SQLException e) {
			log.error("fallita query kanjword : " + kanjiWord
					+ "statement era:\n" + s, e);
		}

		return null;
	}

	ArrayList<IEdictEntry> selectKanjiCompos(String kanjiOrWord,
			String test, int len) {
		String s = "SELECT * FROM "
				+ this.getName()
				// + " FETCH FIRST 10 ROWS ONLY "
				+ " WHERE " + fields.get(0).getFieldName() + test
				+ " AND len >= " + len + " AND " + fields.get(4).getFieldName()
				+ " = 1" + " ORDER BY " + fields.get(3).getFieldName();

		log.debug("eseguo query: " + s);
		if (dmdb.getConnection() == null) {
			log.error("conn(ection) nulla)");
			return null;
		}

		ArrayList<IEdictEntry> compos = new ArrayList<IEdictEntry>();
		try {
			Statement st = dmdb.getConnection().createStatement();
			ResultSet rs = st.executeQuery(s);
			while (rs.next()) {
				int pos = 1;
				Edict_1_Entry entry = new Edict_1_Entry();
				entry.setKanjiWord(rs.getString(pos++));
				entry.setReading(rs.getString(pos++));
				entry.setWholeDescription(rs.getString(pos++));
				log.trace("trovata edictByHeadWord entry per kanjiWord: "
						+ kanjiOrWord);
				compos.add(entry);
			}// end while loop
			return compos;
		} catch (SQLException e) {
			log.error("fallita query kanjword : " + kanjiOrWord
					+ "statement era:\n" + s, e);
		}

		return null;
	}

	ArrayList<IEdictEntry> selectExpression(String kanjiOrWord,
			String test, int len) {
		String s = "SELECT * FROM " + this.getName();
		s += " WHERE " + fields.get(0).getFieldName() + " = '" + kanjiOrWord
				+ "'";
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

		ArrayList<IEdictEntry> compos = new ArrayList<IEdictEntry>();
		try {
			Statement st = dmdb.getConnection().createStatement();
			ResultSet rs = st.executeQuery(s);
			while (rs.next()) {
				int pos = 1;
				Edict_1_Entry entry = new Edict_1_Entry();
				entry.setKanjiWord(rs.getString(pos++));
				entry.setReading(rs.getString(pos++));
				entry.setWholeDescription(rs.getString(pos++));
				log.trace("trovata edictByHeadWord entry per kanjiWord: "
						+ kanjiOrWord);
				compos.add(entry);
			}// end while loop
			return compos;
		} catch (SQLException e) {
			log.error("fallita query kanjword : " + kanjiOrWord
					+ "statement era:\n" + s, e);
		}

		return null;
	}

	ArrayList<IEdictEntry> selectReading(String kanjiOrWord, String test,
			int len) {
		String s = "SELECT * FROM " + this.getName();
		s += " WHERE " + fields.get(1).getFieldName() + " = '" + kanjiOrWord
				+ "'";
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

		ArrayList<IEdictEntry> compos = new ArrayList<IEdictEntry>();
		try {
			Statement st = dmdb.getConnection().createStatement();
			ResultSet rs = st.executeQuery(s);
			while (rs.next()) {
				int pos = 1;
				Edict_1_Entry entry = new Edict_1_Entry();
				entry.setKanjiWord(rs.getString(pos++));
				entry.setReading(rs.getString(pos++));
				entry.setWholeDescription(rs.getString(pos++));
				log.trace("trovata edictByHeadWord entry per kanjiWord: "
						+ kanjiOrWord);
				compos.add(entry);
			}// end while loop
			return compos;
		} catch (SQLException e) {
			log.error("fallita query kanjword : " + kanjiOrWord
					+ "statement era:\n" + s, e);
		}

		return null;
	}

	boolean entryExists(String kanjiWord) {

		String s = "SELECT * FROM " + this.getName() + " WHERE "
				+ fields.get(0).getFieldName() + "= '" + kanjiWord + "'";
		log.debug("eseguo query: " + s);
		if (dmdb.getConnection() == null) {
			log.error("conn(ection) nulla)");
			return false;
		}

		try {
			Statement st = dmdb.getConnection().createStatement();
			ResultSet rs = st.executeQuery(s);
			log.debug("esamino lo statement per capire, st.getMaxRows(): "
					+ st.getMaxRows());
			while (rs.next()) {
				log.debug("trovata edictByHeadWord entry per kanjiWord: "
						+ kanjiWord);
				rs = null;
				return true;
			}// end while loop
		} catch (SQLException e) {
			log.error("fallita query kanjword : " + kanjiWord
					+ "statement era:\n" + s, e);
		}

		return false;
	}

	private static org.apache.log4j.Logger log = Logger
			.getLogger(EdictTable.class);
}
