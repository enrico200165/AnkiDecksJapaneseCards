package com.enrico_viali.jacn.ankideck.jlpt_words;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.generic.*;
import com.enrico_viali.jacn.common.FieldUpdate;

public class AnkiFactJLPTWords extends AnkiFact {

	public AnkiFactJLPTWords(long factId, String exprPar, IAnkiDeckGeneric dm) {
		super(factId,dm);
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

	/**
	 * controlla se un elemento del deck va aggiornato con i contenuti
	 * provenienti dal file e arricchita (più aggiornati in quando io edit tale
	 * file
	 * 
	 * @param ev
	 *            entry dal file
	 * @return
	 */
	public boolean updateWithEdict(String edictString, String comment,int number,
	FieldUpdate defaultUpdate) {

		for (AnkiField f : fields.values()) {

			// per il campo expression non dovrebbe mai succedere
			if (f.getFModelName().equals("Expression")) {
//				f.updateValue(e.getHeadWord(), defaultUpdate);
				continue;
			}

			/*if (f.getFModelName().equals("Meaning")) {
				f.updateValue(e.get, defaultUpdate);
				continue;
			} */
			
			/*if (f.getFModelName().equals("Reading")) {
				f.updateValue(ev.getHanziT(), defaultUpdate);
				continue;
			}*/

			/*if (f.getFModelName().equals("Comment")) {
				f.updateValue(ev.getHNumber(), defaultUpdate);
				continue;
			}*/
			
			if (f.getFModelName().equals("Edict")) {
				f.updateValue(edictString, defaultUpdate);
				continue;
			}

			if (f.getFModelName().equals("Number")) {
				f.updateValue(""+number, defaultUpdate);
				continue;
			}
			
			//log.info("fact id:" + this.id + " field name not managed: " +
			// f.getFModelName() + " field id: " + f.getId());
		}
		// log.info(this.toString());
		return true;
	}

	private static org.apache.log4j.Logger log = Logger.getLogger(AnkiFactJLPTWords.class);
}
