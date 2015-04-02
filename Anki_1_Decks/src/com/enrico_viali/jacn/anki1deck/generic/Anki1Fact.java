package com.enrico_viali.jacn.anki1deck.generic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.generic.AnkiDeckMalformedFact;
import com.enrico_viali.jacn.ankideck.generic.AnkiField;
import com.enrico_viali.jacn.ankideck.generic.IAnkiDeckGeneric;
import com.enrico_viali.utils.Utl;

public class Anki1Fact {

	public Anki1Fact(long factId, IAnkiDeckGeneric deckMgrPar) {
		super();
		this.id = Utl.NOT_INITIALIZED_INT;
		fields = new HashMap<String, AnkiField>();
		deckMgr = deckMgrPar;
	}

	public long getID() {
		return id;
	}

		
	@Override
	public String toString() {
		String s = "";
		s += "\nfact with ID: [" + id + "]";
		for (String k : this.fields.keySet()) {
			AnkiField f = fields.get(k);
			s += "\nfieldModelName: <" + f.getFModelName() + "> key in Map: <" + k + "> value: <" + f.getValue() + ">";
		}
		return s;
	}

	public boolean mergeWithOther(Anki1Fact other) {

		try {
			if (this.getKeyFieldName() != other.getKeyFieldName()) {
				log.warn("merging facts with different expression");
			}

			for (String k : this.fields.keySet()) {
				AnkiField f = fields.get(k);
				String fName = f.getFModelName();
				String otherValue = other.getFieldStrValue(fName, true);
				f.setValue(f.getValue() + " ev_joined  + " + otherValue);
			}
		} catch (AnkiDeckMalformedFact e) {
			log.error("malformed fact");
		}
		return true;
	}

	public String getKeyFieldName() throws AnkiDeckMalformedFact {
		return getFieldStrValue(deckMgr.getExpression(), true);
	}

	public String getKeyFieldValue() throws AnkiDeckMalformedFact {
		String name = deckMgr.getExpression();
		return getFieldStrValue(name, true);
	}

	public boolean insertField(AnkiField f) {
		if (fields.get(f.getFModelName()) == null) {
			add(f);
			return true;
		}
		log.error("field " + f.getFModelName() + " already present");
		return false;
	}

	void add(AnkiField f) {
		if (fields.get(f.getFModelName()) != null) {
			log.error("adding field with fieldModelName already present, ignore");
		} else {
			fields.put(f.getFModelName(), f);
		}
	}

	public int getFieldIntValue(String fModelNamePar, boolean mandatory) {
		
		AnkiField f = fields.get(fModelNamePar);
		if (f != null && f.getValue() != null && f.getValue().length() > 0) {
			int val = Integer.parseInt(f.getValue());
			return val;
		}

		if (!deckMgr.containsFModel(fModelNamePar)) {
			log.error("reading value of field for non existing fieldModelName: "+fModelNamePar
			+ "\nfielModelNames of this deck:\n"+deckMgr.dumpFModelNames());
			return Utl.NOT_INITIALIZED_INT;
		}
		
		if (mandatory) {
			log.error("not found field: " + fModelNamePar);
			return Utl.NOT_INITIALIZED_INT;
		} else {
			return Utl.NOT_INITIALIZED_INT;
		}
	}

	public boolean setIntValue(String fModelNamePar, int val) {
		AnkiField f = fields.get(fModelNamePar);
		if (f != null) {
			f.setValue("" + val);
			return true;
		}
		return false;
	}

	public String getFieldStrValue(String fModelNamePar, boolean mandatory) throws AnkiDeckMalformedFact {
		AnkiField f = fields.get(fModelNamePar);
		if (f != null) {
			return f.getValue();
		}
		
		if (!deckMgr.containsFModel(fModelNamePar)) {
			log.error("reading value of field for non existing fieldModelName: "+fModelNamePar
			+ "\nfielModelNames of this deck:\n"+deckMgr.dumpFModelNames());
			return "";
		}
		
		if (mandatory) {
			log.error("AnkiDeck <" + deckMgr.getDeckName() + "> non trovato field <"
			+ fModelNamePar + "> in fact " + this.id + "\ndump of the fact:" + this.toString());
			AnkiDeckMalformedFact e = new AnkiDeckMalformedFact();
			throw e;
		} 
		return "";
	}

		
	public boolean setFieldStrValue(String fModelNamePar, String val, boolean updateDb) {
		AnkiField f = fields.get(fModelNamePar);
		if (f != null) {
			f.setValue(val);
			return true;
		}
		return false;
	}

	
	public boolean fillFromRS(ResultSet rs, long factIdPar) throws SQLException {
		boolean ret = true;
		id = factIdPar;

		if (rs.next()) {
			do {
				AnkiField f = new AnkiField(this);
				if (f.fillFromRS(rs, factIdPar))
					fields.put(f.getFModelName(), f);
				else {
					log.error("error filling fact with id " + factIdPar);
					return false;
				}
			} while (rs.next() && ret);
		} else {
			log.error("result set non trovato per factID esistente: " + id);
			return false;
		}

		if (!ret) {
			log.error("error in loading fields for fact id esistente: " + id);
		}

		return ret;
	}

		
	protected long id;
	protected HashMap<String, AnkiField> fields;
	IAnkiDeckGeneric deckMgr;
	private static org.apache.log4j.Logger log = Logger.getLogger(Anki1Fact.class);
}
