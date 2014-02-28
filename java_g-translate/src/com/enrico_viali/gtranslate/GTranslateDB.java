package com.enrico_viali.gtranslate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.enrico_viali.libs.rdb_jdbc.*;
import com.google.api.translate.Language;

/**
 * @author enrico
 * 
 *         Uses a DB to keep memory of translations already made and avoid
 *         repeating them
 * 
 */
public class GTranslateDB extends GTranslate {

	public GTranslateDB() {
		super();
		try {

			dbMgr = new RDBManagerSQLite("../data/in_out/translations.db",true);
			dbMgr.open(true);

			if (dbMgr.getConnection() == null) {
				log.error("conn(ection) nulla)");
				return;
			}

			JDBCEVTable translations = new JDBCEVTable("translations", dbMgr);
			translations.addField("expression_en", java.sql.Types.VARCHAR, 0,
					TipoChiave.PRIMARY_KEY, 0);
			translations.addField("translation_it", java.sql.Types.VARCHAR, 0,
					TipoChiave.IDXED_NOT_UNIQUE, 0);
			translations.addField("translation_source", java.sql.Types.VARCHAR, 32,
					TipoChiave.IDXED_NOT_UNIQUE, 0);

			translations.create();

			

		} catch (Exception e) {
			log.error("non riesco ad aprire il DB", e);
			System.exit(1);
		}
	}

	RespTransl translateFromDB(String expression) {

		RespTransl resp = new RespTransl();
		Statement st;
		String queryString = "SELECT translation_it FROM translations WHERE expression_en = '"
				+ JDBCEVUtility.escapeForSQL(expression) + "'";
		// queryString = JDBCEVUtility.escapeForSQL(queryString);
		try {
			st = dbMgr.getConnection().createStatement();
			ResultSet rs = st.executeQuery(queryString);
			log.debug("esamino lo statement per capire, st.getMaxRows(): "
					+ st.getMaxRows());
			if (rs.next()) {
				resp.found = true;
				resp.translation = rs.getString("translation_it");
			}
		} catch (SQLException e) {
			log.error("failed query: "+queryString, e);
			resp.found = false;
		} finally {
		}
		return resp;
	}

	boolean saveTranslToDB(String expression, String translation) {
		boolean ret = false;

		Statement st;
		String queryString = "insert into translations values("
				+ JDBCEVUtility.wrapSingleQuotes(JDBCEVUtility.escapeForSQL(expression),true)
				+ JDBCEVUtility.wrapSingleQuotes(JDBCEVUtility.escapeForSQL(translation),true)
				+ JDBCEVUtility.wrapSingleQuotes("google",false)
				+")";
		try {
			st = dbMgr.getConnection().createStatement();			
			st.executeUpdate(queryString);		
			return true;
		} catch (SQLException e) {
			log.error("fallito inserimento traduzione: "+expression+" ->"+translation, e);
			return false;
		}
	}

	public RespTransl translate(String toTranslate) {
		return translate(toTranslate, Language.ENGLISH, Language.ITALIAN);
	}

	
	public RespTransl translate(String toTranslate, Language source, Language dest) {
		RespTransl resp = translateFromDB(toTranslate);
		if (resp.found) {
			log.debug("ok, trovanto nel db: " + toTranslate + " -> "
					+ resp.translation);
			return resp;
		} else {
			
			resp.found = true;
			resp = super.translate(toTranslate, source, dest);
			saveTranslToDB(toTranslate, resp.translation);
		}
		return resp;
	}

	IRDBManager dbMgr;
	private static org.apache.log4j.Logger log = Logger
			.getLogger(GTranslateDB.class);
}
