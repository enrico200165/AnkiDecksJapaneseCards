package com.enrico_viali.jacn.ankideck.heisig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.generic.AnkiOneDeckDataModel;
import com.enrico_viali.libs.rdb_jdbc.IRDBManager;

public class ADeckHesigDataModel extends AnkiOneDeckDataModel {

	public ADeckHesigDataModel(IRDBManager dmdb) throws Exception {
		super(dmdb);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param tabella
	 * @return
	 * @throws SQLException
	 */
	public boolean readFromHeisigDeck(ADeckHeisigMgr mgr) throws SQLException {
		Statement stFacts;

		if (dmdb == null) {
			log.error("dmdb null)");
			return false;
		}

		if (dmdb.getConnection() == null) {
			log.error("conn(ection) nulla)");
			return false;
		}

		try {
			String factsQueryString = "SELECT id FROM facts";
			log.debug("eseguo query: " + factsQueryString);
			stFacts = dmdb.getConnection().createStatement();
			ResultSet factsRs = stFacts.executeQuery(factsQueryString);
			int nrRows = 0;
			while (factsRs.next()) {
				long factID = factsRs.getLong(1);
				nrRows++;
				// AnkiFactHeisig entry = buildEntryFromRS(rsResults);
				log.debug("letta fact entry, ID: " + factID);

				String fieldsQueryString = "SELECT F2.factId, F3.name , F2.value" + " FROM facts F1,fields F2,fieldModels F3" + " WHERE F2.factId=F1.id AND F2.fieldModelId=F3.id and F2.factId="
						+ factID;
				if (factID == 1) {
					log.trace("giusto per mettere i breakpoint su entry anomale");
				}
				Statement stFields = dmdb.getConnection().createStatement();
				ResultSet fieldsRs = stFields.executeQuery(fieldsQueryString);
				AnkiFactHeisig e = new AnkiFactHeisig(mgr);
				if (e.fillFromRS(fieldsRs, factID)) {
					if (!mgr.addFact(e)) {
						log.error("load of new fact in memory hash table failed");
						return false;
					}
				} else {
					log.error("filling fact from deck failed");
					return false;
				}
			}
			log.info("portato deck in memoria, lette righe: " + nrRows);
		} catch (SQLException e) {
			log.error("fallita query sui fatti o fields ", e);
		}

		return true;
	}

	private static org.apache.log4j.Logger log = Logger.getLogger(ADeckHesigDataModel.class);
	
}
