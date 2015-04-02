package com.enrico_viali.jacn.ankideck.generic;

import com.enrico_viali.jacn.anki1deck.generic.Anki1Fact;
import com.enrico_viali.jacn.common.FieldUpdate;

/**
 * @author enrico
 * 
 * It models a GENERIC anki deck, with generic low level operation that use the self-descriptive
 * features of the decks data structure, ie the fact that you specify the name of a field as a 
 * string etc.
 * Specific decks, will use this for their internal implementation
 */
public interface IAnkiDeckGeneric {

	public abstract String getDeckName();

	IAnkiDeckDataModel getDm();
	
	public abstract boolean isOK();

	public boolean copyField(String fNameSource,String fNameTarget, FieldUpdate updTime);
	
	public abstract String getExpression();

	public abstract void setExpression(String s);

	public abstract Anki1Fact buildFact(long factID, String expressionName);

	public abstract boolean addCard(IAnkiCard c);

	public abstract boolean addCardModel(AnkiCardModel e);

	public abstract IAnkiCard getCardModelByID(long id);

	public abstract boolean addFact(Anki1Fact e);

	public abstract boolean addFact(Anki1Fact e, boolean allowDuplicates);

	public abstract boolean load();

	public abstract void dump(String sep);

	/**
	 * 
	 * @param filepath
	 * @param encoding
	 * @return
	 */
	public abstract boolean readFromFile(String filepath, String encoding);

	public abstract Anki1Fact getFactByExp(String kanjiPar);

	public abstract Anki1Fact getFactByID(long nr);

	public abstract boolean getLoaded();

	public abstract void setLoaded(boolean v);

	public abstract boolean containsFModel(String fModName);

	public abstract String dumpFModelNames();

}