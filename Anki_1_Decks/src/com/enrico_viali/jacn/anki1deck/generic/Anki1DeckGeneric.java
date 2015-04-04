package com.enrico_viali.jacn.anki1deck.generic;

import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.generic.AnkiCardModel;
import com.enrico_viali.jacn.ankideck.generic.AnkiDeckMalformedFact;
import com.enrico_viali.jacn.ankideck.generic.Fact;
import com.enrico_viali.jacn.ankideck.generic.IAnkiCard;
import com.enrico_viali.jacn.ankideck.generic.IAnkiDeckDataModel;
import com.enrico_viali.jacn.ankideck.generic.IAnkiDeckGeneric;
import com.enrico_viali.jacn.common.FieldUpdate;
import com.enrico_viali.libs.rdb_jdbc.*;

public class Anki1DeckGeneric implements IAnkiDeckGeneric {

	public Anki1DeckGeneric(String dName, Anki1DeckDataModel dmPar, String expr, boolean allowDuplicate) {
		factsByExpression = new HashMap<String, Fact>();
		factsByID = new HashMap<Long, Fact>();
		cardModelsByID = new HashMap<Long, AnkiCardModel>();
		cardsByID = new HashMap<Long, IAnkiCard>();
		
		dm = dmPar;
		loaded = false;
		allowDuplicateExpressions = allowDuplicate;
		_expression = expr;
		
		
		
		log.error("esco, parte di codice/architettura da ridisegnare con cura");
		System.exit(1);
		// codice specifico e non generale, esco per non eseguirlo
		// fmodels = dm.getFielModels();
		
		
		deckName = dName;
		log.info("fieldmodels: " + fmodels.toString());
	}

	@Override
	public boolean copyField(String fNameSource, String fNameTarget, FieldUpdate updTime) {
		// TODO Auto-generated method stub
		return false;
	}

	List<Long> getFactsIDs(String ModelName) {
		List<Long> ids = new ArrayList<Long>();

		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#getDeckName()
	 */
	@Override
	public String getDeckName() {
		return deckName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#isOK()
	 */
	@Override
	public boolean isOK() {
		return dm.check();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#getExpression()
	 */
	@Override
	public String getKeyFieldName() {
		return _expression;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#setExpression(java.lang
	 * .String)
	 */
	@Override
	public void setExpression(String s) {
		_expression = s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#buildFact(long,
	 * java.lang.String)
	 */
	@Override
	public Fact buildFact(long factID, String expressionName) {
		return new Fact(factID, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#add(com.enrico_viali
	 * .jacn.ankideck.generic.AnkiCard)
	 */
	@Override
	public boolean addCard(IAnkiCard c) {
		if (c.getID() == 1) {
			log.debug("questo if serve solo a trappare un valore");
		}
		if (cardsByID.containsKey(c.getID())) {
			log.info("factID: " + c.getID() + " id gia presente: <" + c.getID() + ">");
			return false;
		}
		cardsByID.put(c.getID(), c);
		log.debug("inserita card, ID: " + c.getID());

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#add(com.enrico_viali
	 * .jacn.ankideck.generic.AnkiCardModel)
	 */
	@Override
	public boolean addCardModel(AnkiCardModel e) {
		if (e.getID() == 1) {
			log.debug("questo if serve solo a trappare un valore");
		}
		if (cardModelsByID.containsKey(e.getID())) {
			log.info("factID: " + e.getID() + " id gia presente: <" + e.getID() + ">");
			return false;
		} else {
			cardModelsByID.put(e.getID(), e);
			log.info("inserito cardMode, ID: " + e.getID() + " name " + e.getName());
		}
		cardModelsByID.put(e.getID(), e);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#getCardModelByID(long)
	 */
	@Override
	public IAnkiCard getCardModelByID(long id) {
		if (cardModelsByID.containsKey(id)) {
			return cardModelsByID.get(id);
		}
		log.error("");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#add(com.enrico_viali
	 * .jacn.ankideck.generic.AnkiFact)
	 */
	@Override
	public boolean addFact(Fact e) {
		return addFact(e, this.allowDuplicateExpressions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#add(com.enrico_viali
	 * .jacn.ankideck.generic.AnkiFact, boolean)
	 */
	@Override
	public boolean addFact(Fact e, boolean allowDuplicates) {
		if (e.getID() == 1) {
			log.debug("questo if serve solo a trappare un valore");
		}

		try {
			if (!allowDuplicates && factsByExpression.containsKey(e.getKeyFieldName())) {
				Fact preexisting = factsByExpression.get(e.getKeyFieldName());
				log.warn("factID: " + e.getID() + " key gia presente: <" + e.getKeyFieldName() + ">"
				+ "\npre-existing:                           factID: "
				+ preexisting.getID() + "                   <" + preexisting.getKeyFieldName() + ">");
				return false;
			} else {
				factsByExpression.put(e.getKeyFieldName(), e);
				log.debug("inserito factID: " + e.getID() + " key " + e.getKeyFieldName());
			}
			factsByID.put(e.getID(), e);
		} catch (AnkiDeckMalformedFact ex) {
			log.error("ill formed fact, factID: " + e.getID() + "dump:\n" + e.toString());
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#load(java.lang.String)
	 */
	@Override
	public boolean load() {
		return load(this.deckName, this._expression);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#load(java.lang.String,
	 * java.lang.String)
	 */
	public boolean load(String deckPathName, String keyExpression) {
		if (loaded) {
			log.warn("request to load deck already loaded");
			return true;
		}

		loaded = false;
		try {
			IRDBManager dmdb = new RDBManagerSQLite(deckPathName,true);
			dmdb.open(false);
			if (dm == null) {
				dm = new Anki1DeckDataModel(dmdb);
			}
			if (!dm.readFactsDeck(this, keyExpression)) {
				log.error("");
				loaded = false;
				return false;
			} else {
				loaded = true;
				ArrayList<String> duplicates = this.dm.duplicates();
				if (duplicates.size() > 0) {
					log.info("deck contains duplicates:\n" + duplicates);
					return false;
				}
				return true;
			}

		} catch (ClassNotFoundException e) {
			log.error("Problem loading jdbc driver");
			return false;
		} catch (Exception e) {
			log.error("Exception", e);
			return false;
		}
	}

	boolean updateCardsFromEVCSV() {
		int nr = 0;
		for (IAnkiCard c : this.cardsByID.values()) {
			if (++nr % 1 == 0)
				log.info("card: " + c.questionToString());
			IAnkiCard cm = getCardModelByID(c.getCardModelId());
			log.debug("card model relativo a card: " + c.getID() + "\nmodel: " + cm.toString());
			Fact f = getFactByID(c.getFactId());
			if (f != null)
				log.debug("fatto relativo alla card con id: " + c.getID() + "\nfact: " + f.toString());
			else
				log.debug("fatto relativo alla card con id: " + c.getID() + " null");
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#dump(java.lang.String)
	 */
	@Override
	public void dump(String sep) {
		log.info("ankideck nr elementi: " + this.factsByID.size());

		for (Map.Entry<String, Fact> e : this.factsByExpression.entrySet()) {
			Fact f = e.getValue();
			log.info(f.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#readFromFile(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public boolean readFromFile(String filepath, String encoding) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#getFactByExp(java.lang
	 * .String)
	 */
	@Override
	public Fact getFactByExp(String kanjiPar) {
		return factsByExpression.get(kanjiPar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#getFactByID(long)
	 */
	@Override
	public Fact getFactByID(long nr) {
		return factsByID.get(nr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#getLoaded()
	 */
	@Override
	public boolean getLoaded() {
		return loaded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#setLoaded(boolean)
	 */
	@Override
	public void setLoaded(boolean v) {
		loaded = v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#containsFModel(java.
	 * lang.String)
	 */
	@Override
	public boolean containsFModel(String fModName) {
		FieldModel fm = fmodels.get(fModName); // just to debug easily
		if (fm != null) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enrico_viali.jacn.ankideck.generic.IAnkiDeck#dumpFModelNames()
	 */
	@Override
	public String dumpFModelNames() {
		String res = "";
		for (String s : fmodels.keySet()) {
			res += s + " ";
		}
		return res.trim();
	}

	public IAnkiDeckDataModel getDm() {
		return dm;
	}

	public int size() {
		if (factsByExpression.size() != factsByID.size()) {
			log.error("different size: "+factsByExpression.size()+" vs. "+factsByID.size()+" exiting");
			System.exit(1);
		}		
		return factsByExpression.size();
	}
	protected boolean loaded;
	protected boolean allowDuplicateExpressions;
	protected String _expression;
	protected String deckName;
	
	protected Map<String, Fact> factsByExpression;
	public Map<Long, Fact> factsByID;
	protected Map<String, FieldModel> fmodels;
	Map<Long, AnkiCardModel> cardModelsByID;
	Map<Long, IAnkiCard> cardsByID;

	public IAnkiDeckDataModel dm;
	private static org.apache.log4j.Logger log = Logger.getLogger(Anki1DeckGeneric.class);
}
