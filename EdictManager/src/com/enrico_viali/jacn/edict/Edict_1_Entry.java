package com.enrico_viali.jacn.edict;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.enrico_viali.utils.Utl;


/*
 * @author enrico
 *
 */
public class Edict_1_Entry implements IEdictEntry {

	public Edict_1_Entry() {
		super();
		_lineNr = Utl.NOT_INITIALIZED_INT;
		_len = 0;
		_headWord = "undefined";
		_reading = "undefined";
		_senses = new ArrayList<String>();
		_allDescriptions = Utl.NOT_INITIALIZED_STRING;
		_field = Utl.NOT_INITIALIZED_STRING;
		_isFrequent = false;
		_isNoun = false;
		_isVerb = false;
		_isAdjective = false;
		_rating = Utl.NOT_INITIALIZED_INT;
	}

	
	/**
	 * Reconstructed, it seems that: Calculates a score of how a word is suitable as composite
	 */
	void rateAsKanjiComposite() {
		_rating = 0;

		if (this._isFrequent)
			_rating += 10000;

		_rating += (8 - getHeadWord().length()) * 1000;

		if (this.getIsPlainNoun())
			_rating += 500;

		if (this.getIsAdjective())
			_rating += 400;

		if (this.getIsVerb())
			_rating += 300;
	}

	
	public String toString(String sep) {
		return _headWord + sep + _reading + sep + _allDescriptions;
	}

	
	@Override
	public String toString() {
		return toString(" : ");
	}

	
	public boolean buildFromEdictLine(String line, String sep, IEdictEntry entry, int lineNr) {

		_lineNr = lineNr;
		// --- elaborazione parte termin, kanj e Reading
		String kanjikanaPart = line.split(descrDelim)[0].trim();
		log.debug("splitting della stringa, parte kanjiWord-Reading\n" + kanjikanaPart);
		_headWord = kanjikanaPart.split(kanjiKanaDelim)[0].trim();
		if (kanjikanaPart.split(kanjiKanaDelim).length > 1) {
			// c'è la parte Reading
			setReading(kanjikanaPart.split(kanjiKanaDelim)[1].trim());
			log.debug("presenti entrambe le parti: " + getHeadWord() + " -- " + getReading());
		} else {
			setReading(getHeadWord().trim());
			log.debug("presente solo Reading: " + getHeadWord() + " -- " + getReading());
		}
		_len = getHeadWord().length();

		setWholeDescription(line.substring(line.indexOf("/") + 1));
		parseDescriptions(getWholeDescription(), lineNr);

		return true;
	}

	
	boolean parseDescriptions(String descriptions, int lineNr)  {
		// --- elaborazione delle descrizioni ---
		/*
		 * La sintassi delle descrizioni � strana e destrutturata, sembra che
		 * quando c'è un solo elemento non c'� il numero, altrimenti il numero �
		 * riportato fra parentesi, ie (1) (2) etc sembra che le informazioni
		 * grammaticali importanti precedano il numero e che non siano replicate
		 * all'interno delle varianti di significato dello stesso numero fra
		 * parentesi sono riportate anche informazioni non puramente
		 * grammaticali,chiamiamole "altre info" quando anche le info
		 * grammaticali sono presente queste altre info seguono, ma possono
		 * essere presenti anche da sole
		 * 
		 * A fronte del fatto che non c'� una struttura gerarchica non �
		 * possibile trovare un delimitatore di livello unico, se si volesse
		 * spezzare la descrizione l'unico approccio sembra usare il carattere /
		 * come delimitatore e poi analizzare dentro ogni token se sta iniziano
		 * un nuovo significato (presenza di (1) (2) etc
		 * 
		 * A prescindere dal caos della sintassi a livello logico SEMBRA esserci
		 * la seguente struttura: una lista di gruppi significati, numerati e
		 * con informazioni grammaticali, che contentono una lista di
		 * significati di dettaglio
		 */

	
	
		String[] elements = getWholeDescription().split("/");
		POS pos = new POS();
		
		int eIdx = 0;
		int senseIdx = -1;
		for (eIdx = 0; eIdx < elements.length; eIdx++) {
			String e = elements[eIdx];
			e = e.trim();
			log.debug("edictByHeadWord element:\n" + e);

			Pattern senseMarking = Pattern.compile(senseDelim);
			Matcher senseMatcher = senseMarking.matcher(e);

			if (senseMatcher.find()) {
				// String senseNrStr = senseMatcher.group(1);
				// log.info(descriptions + "\n" + getHeadWord() + " elemento " +
				// eIdx + " trovato sense: " + senseNrStr);
				senseIdx++;
				if (senseIdx == 0) { // dummy per eliminare warning
					// per ora non si fanno azioni
				}
			}

			Pattern genericMarking = Pattern.compile(markingPtrn);
			Matcher genMatcher = genericMarking.matcher(e);
			while (genMatcher.find()) {
				log.debug("In element: <" + e + ">\nfound: <" + genMatcher.group() + "> starting at " +
				genMatcher.start() + " index and ending at " + genMatcher.end());
				String groupStr = genMatcher.group(1);
				if (groupStr != null) {
					String[] poses = groupStr.split(" *, *");
					for (int i = 0; i < poses.length; i++) {
						if (!pos.containsPOS(poses[i])) {
							// forse segnala i numeri di senso, cioè (1)
							// è una qualche descrizione, non un marker
							// log.debug("line " + lineNr + " : " + poses[i]);
						} else {
							processPOSMarker(poses[i]);
						}
					}
				}
			}
		}
		return true;
	}

	
	boolean processPOSMarker(String mark) {
		boolean found = false;
		if (mark.equals("P")) {
			setIsFrequent(true);
			found = true;
		}

		if (mark.startsWith("n")) {
			setIsPlainNoun(true);
			found = true;
		}

		if (mark.startsWith("v")) {
			setIsVerb(true);
			found = true;
		}

		if (mark.startsWith("adj")) {
			setIsAdjective(true);
			found = true;
		}

		if (mark.equals("Buddh")
		|| mark.equals("MA")
		|| mark.equals("comp")
		|| mark.equals("food")
		|| mark.equals("geom")
		|| mark.equals("gram")
		|| mark.equals("ling")
		|| mark.equals("math")
		|| mark.equals("mil")
		|| mark.equals("physics")) {
			setField(mark);
			found = true;
		}

		if (!found)
			log.debug("unknown POS marker: " + mark);
		return found;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getHeadWord()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getHeadWord()
	 */
	
	@Override
	public String getHeadWord() {
		return _headWord;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setKanjiWord(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setKanjiWord(java.lang.String)
	 */
	@Override
	
	public void setKanjiWord(String kanjiWord) {
		_headWord = kanjiWord;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getReading()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getReading()
	 */
	
	
	public String getReading() {
		return _reading;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setReading(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setReading(java.lang.String)
	 */
	
	
	public void setReading(String Reading) {
		_reading = Reading;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getWholeDescription()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getWholeDescription()
	 */
	
	
	public String getWholeDescription() {
		return _allDescriptions;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setWholeDescription(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setWholeDescription(java.lang.String)
	 */
	
	
	public void setWholeDescription(String wholeDescription) {
		_allDescriptions = wholeDescription;
		parseDescriptions(_allDescriptions, Utl.NOT_INITIALIZED_INT);
		rateAsKanjiComposite();
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getField()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getField()
	 */
	
	
	public String getField() {
		return _field;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setField(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setField(java.lang.String)
	 */
	
	
	public void setField(String val) {
		_field = val;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getIsFrequent()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getIsFrequent()
	 */
	
	
	public boolean getIsFrequent() {
		return _isFrequent;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setFrequent(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setFrequent(boolean)
	 */
	
	
	public void setIsFrequent(boolean p) {
		_isFrequent = p;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getLine()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getLine()
	 */
	
	@Override
	public long getLine() {
		return _lineNr;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getIsNoun()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getIsNoun()
	 */
	
	
	public boolean getIsPlainNoun() {
		return _isNoun;
	}

	public void setIsPlainNoun(boolean v) {
		_isNoun = v;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getIsAdjective()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getIsAdjective()
	 */
	
	@Override
	public boolean getIsAdjective() {
		return _isAdjective;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setIsAdjective(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setIsAdjective(boolean)
	 */
	
	@Override
	public void setIsAdjective(boolean v) {
		_isAdjective = v;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getIsVerb()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getIsVerb()
	 */
	
	@Override
	public boolean getIsVerb() {
		return _isVerb;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setIsVerb(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setIsVerb(boolean)
	 */
	@Override
	public void setIsVerb(boolean v) {
		_isVerb = v;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getDescrizioni()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getDescrizioni()
	 */
	@Override
	public ArrayList<String> getDescrizioni() {
		return _senses;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setDescrizioni(java.util.ArrayList)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setDescrizioni(java.util.ArrayList)
	 */
	@Override
	public void setDescrizioni(ArrayList<String> descrizioni) {
		_senses = descrizioni;
	}

	public boolean fillValuesVector(String[] v) {
		if (v.length < 30) {
			log.error("vettore valori troppo piccolo, nr elementi: " + v.length);
			return false;
		}

		int i = 0;
		if ((getHeadWord() != null) && (getHeadWord().length() >= 0)) {
			v[i++] = getHeadWord();
		} else {
			log.error("valore indefinito");
			v[i++] = "";
			return false;
		}

		if ((this._reading != null) && (_reading.length() >= 0)) {
			v[i++] = _reading;
		} else {
			log.error("valore indefinito");
			v[i++] = "";
			return false;
		}

		if ((_allDescriptions != null) && (_allDescriptions.length() >= 0)) {
			v[i++] = getWholeDescription();
		} else {
			log.error("valore indefinito");
			v[i++] = "";
			return false;
		}

		// --- lunghezza ----
		v[i++] = "" + _len;

		if (getIsFrequent()) {
			v[i++] = "1";
		} else {
			v[i++] = "0";
		}

		v[i++] = getField();

		if (getIsPlainNoun()) {
			v[i++] = "1";
		} else {
			v[i++] = "0";
		}

		if (getIsVerb()) {
			v[i++] = "1";
		} else {
			v[i++] = "0";
		}

		if (getIsVerb()) {
			v[i++] = "1";
		} else {
			v[i++] = "0";
		}

		v[i++] = "" + _jlpt;

		v[i++] = "" + _lineNr;
		return true;
	} 

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getRating()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#getRating()
	 */
	@Override
	public int getRating() {
		return _rating;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setRating(int)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#setRating(int)
	 */
	@Override
	public void setRating(int v) {
		_rating = v;
	}

	static final String descrDelim = "/\\(";
	static final String senseDelim = "\\((\\d+)\\)";
	static final String kanjiKanaDelim = "[\\[\\]]";
	static final String markingPtrn = " *\\(([^)]*)\\) *"; // "^ *( *\\(([^)]*)\\) *)*")


	private static org.apache.log4j.Logger log = Logger.getLogger(Edict_1_Entry.class);

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#compareTo(com.enrico_viali.jacn.edict.EdictSimpleEntry)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#compareTo(com.enrico_viali.jacn.edict.EdictSimpleEntry)
	 */
	@Override
	public int compareTo(IEdictEntry other) {
		if (this.getRating() > other.getRating())
			return -1;
		if (this.getRating() < other.getRating())
			return 1;

		return 0;
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#equals(java.lang.Object)
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Edict_1_Entry))
			return false;
		return this.getHeadWord().equals(((IEdictEntry) o).getHeadWord());
	}

	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#hashCode()
	 */
	/* (non-Javadoc)
	 * @see com.enrico_viali.jacn.edict.IEdictEntry#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getHeadWord().hashCode();
	}

	
	
	long _lineNr;
	private String _headWord; // quando non ci sono kanjiWord duplica campo
	// Reading
	private String _reading;
	private String _allDescriptions; // eventually is the join of several
	// descriptions
	// for duplicated entries
	ArrayList<String> _senses;
	int _len;
	boolean _isFrequent;
	String _field;
	boolean _isNoun;
	boolean _isVerb;
	boolean _isAdjective;
	int _rating;
	
	int _jlpt;
}
