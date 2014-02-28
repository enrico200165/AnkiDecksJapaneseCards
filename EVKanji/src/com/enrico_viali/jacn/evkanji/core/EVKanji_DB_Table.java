package com.enrico_viali.jacn.evkanji.core;

import java.sql.SQLException;
import java.sql.Statement;

import com.enrico_viali.libs.rdb_jdbc.*;

public class EVKanji_DB_Table extends JDBCEVTable {

	public EVKanji_DB_Table(IRDBManager dmdb) throws Exception {
		super("evkanji", dmdb);
		int posInInputCSVFile = 0;
		
		addField("kanji", java.sql.Types.VARCHAR, 0, TipoChiave.PRIMARY_KEY, posInInputCSVFile++);
		addField("HanziS", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("HanziT", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("pinyin", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("onYomi", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("kunYomi", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("heisigPrimitives", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("hMean", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("kanjidicMeaning", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp1Kanji", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp1Kana", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp1Meanings", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp2Kanji", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp2Kana", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp2Meanings", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp3Kanji", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp3Kana", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp3Meanings", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp4Kanji", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp4Kana", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp4Meanings", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp5Kanji", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp5Kana", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("comp5Meanings", java.sql.Types.VARCHAR, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);

		addField("hNumber", java.sql.Types.INTEGER, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
		addField("grade", java.sql.Types.INTEGER, 0, TipoChiave.NOIDX_NOKEY_NOUNIQUE, posInInputCSVFile++);
	}
	
	
	public boolean writeToDB(EVKanjiEntry evkanji,Statement st) throws SQLException {
		
		String[] values = new String[40];
		evkanji.fillValuesVector(values);
		
		return insertRow(st, values, false, -1,true);
	}

}
