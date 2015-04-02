package com.enrico_viali.jacn.ankideck.generic;

import java.io.*;
import java.sql.*;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.anki1deck.generic.Anki1Fact;
import com.enrico_viali.jacn.common.*;
import com.enrico_viali.utils.Utl;

public class AnkiField {

	public AnkiField(Anki1Fact containerFactPar) {
		super();
		this.id = Utl.NOT_INITIALIZED_INT;
		this.factID = Utl.NOT_INITIALIZED_INT;
		this.fieldModelID = Utl.NOT_INITIALIZED_INT;
		this.ordinal = Utl.NOT_INITIALIZED_INT;
		this.value = Utl.NOT_INITIALIZED_STRING;
		this.fModelName = Utl.NOT_INITIALIZED_STRING;
		containingFact = containerFactPar;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getFactID() {
		return factID;
	}

	public void setFactID(int factID) {
		this.factID = factID;
	}

	public int getFieldModelID() {
		return fieldModelID;
	}

	public void setFieldModelID(int fieldModelID) {
		this.fieldModelID = fieldModelID;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFModelName() {
		return fModelName;
	}

	public AnkiField setFModelName(String modelName) {
		fModelName = modelName;
		return this;
	}

	public boolean updateValue(String other, FieldUpdate conflict) {
		// String fieldPreviousValue = value;

		// controllo che il field non sia invalido
		if (this.value == null) {
			log.error("anki \"field\" with id " + id + " value is null");
			return false;
		}
		// examine limit cases for other value
		if (other == null) { // null va sempre rifiutato
			log.error("\"updated\" string is null");
			return false;
		}

		// they're equal, nothing to do
		if (value.equals(other)) {
			return true;
		}

		// they're not equal (and not null)
		String warnDifference = " field " + this.id + " fmodelname \"" + this.fModelName
		+ "\" value differs:\ncurrent: " + this.value + "\nother  :" + other;
		// log.info(warnDifference);
		FieldUpdate previous;
		do {
			previous = conflict;
			switch (conflict) {
			case PROMPT: {
				conflict = GetFieldUpdate(value, other, warnDifference);
				System.out.println("chosen: " + conflict.label);
				break;
			}
			case OVERWRITE: {
				log.info("(overwrite with update)" + warnDifference);
				
		        log.error("esco, parte di codice/architettura da ridisegnare con cura");
		        System.exit(1);
		        // codice specifico e non generale, esco per non eseguirlo
                /*				
				this.containingFact.deckMgr.getDm().updateFieldValue(this.id, other);
				this.value = other;
				appendToEVkanjiToCorrect("fyi overwritten: " + warnDifference);
				*/
				break;
			}
			case NOOVWR_ERROR: {
				log.error("(leave unchanged)" + warnDifference);
				return false;
			}
			case NOOVWR_WARN: {
				log.warn("(leave unchanged)" + warnDifference);
				break;
			}
			case SYSTEM_EXIT: {
				System.out.println("exiting as requested from user");
				System.out.flush();
				System.exit(0);

				break;
			}
			case NOOVWR_QUIET: {
				break;
			}

			case NOOVWR_SAVECOORD_EVKANJI: {
				try {
					String m = containingFact.getKeyFieldName() + " " + warnDifference;
					appendToEVkanjiToCorrect(m);
				} catch (AnkiDeckMalformedFact e) {
					log.error("fatto malformato", e);
				}
				break;
			}
			default:
				log.error("illegal value");
				return false;
			}
		} while (conflict == FieldUpdate.PROMPT || previous != conflict);

		return true;
	}

	public boolean updateValue(int other, FieldUpdate conflict) {
		return updateValue("" + other, conflict);
	}

	private FieldUpdate GetFieldUpdate(String current, String other, String msg) {
		// FieldUpdate choice;

		if (msg != null && msg.length() > 0)
			System.out.println(msg);
		System.out.println("deck value    : " + current);
		System.out.println("\"update\" value: " + other);
		System.out.println(" --- choose operation ---");
		for (FieldUpdate e : FieldUpdate.values()) {
			System.out.println("" + e.getId() + " to " + e.toString());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int scelta = -1;
		try {
			String input = br.readLine();
			scelta = Integer.parseInt(input);
		} catch (IOException e) {
			System.out.println("Error!");
			scelta = -1;
		}

		return FieldUpdate.fromId(scelta);
	}

	public boolean fillFromRS(ResultSet rs, long factIdPar) throws SQLException {
		boolean ret = true;

		int pos = 1;
		id = rs.getLong(pos++);
		factID = rs.getInt(pos++);
		fieldModelID = rs.getInt(pos++);
		ordinal = rs.getInt(pos++);

		if (rs.getString(pos) == null || rs.getString(pos).length() <= 0) {
			value = "";
			log.debug("no value: field with Id: " + id + " fieldModelName not yet set, this is normal ");
		} else {
			value = rs.getString(pos);
		}
		pos++;

		if (rs.getString(pos) == null || rs.getString(pos).length() <= 0) {
			ret = false;
			fModelName = "";
			log.error("no fieldModelName for field with id: " + id + " fact id: " + factIdPar);
			return false;
		} else {
			fModelName = rs.getString(pos);
		}
		pos++;

		log.debug("filled field, id: " + id + " modelName " + fModelName);

		if (!ret) {
			log.error("error in loading fields for fact id esistente: " + factIdPar);
		}

		return ret;
	}

	public boolean appendToEVkanjiToCorrect(String line) {

		String filePath = Cfg.getDirJPCNData() + Cfg.FNAME_DECK_ANNOTATIONS;

		try {
			FileOutputStream fos;
			OutputStreamWriter outwriter;
			fos = new FileOutputStream(filePath, true);
			// log.info("output: file" + filePath);
			outwriter = new OutputStreamWriter(fos, Utl.ENCODING_UTF8);
			// log.info("opening: " + filePath);
			// log.info("Encoding: in scrittura " + outwriter.getEncoding());
			outwriter.write("" + Calendar.getInstance().getTime() + line + Cfg.NEWLINE);
			outwriter.flush();
			return true;
		} catch (IOException e) {
			// log.error("", e);
			e.printStackTrace();
		}

		return false;
	}

	Anki1Fact containingFact;
	long id;
	int factID;
	int fieldModelID;
	int ordinal;
	String value;
	String fModelName; // non appartiente alla stessa tabella

	private static org.apache.log4j.Logger log = Logger.getLogger(AnkiField.class);
}
