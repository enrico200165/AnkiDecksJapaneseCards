package com.enrico_viali.jacn.ankideck.generic;

import com.enrico_viali.jacn.common.FieldUpdate;

/**
 * @author enrico
 * 
 */
public interface IAnkiDeckGeneric {

	public abstract String getDeckName();

	public abstract boolean isOK();

	public boolean copyField(String fNameSource,String fNameTarget, FieldUpdate updTime);
	
	public abstract String getKeyFieldName();

	public abstract void setExpression(String s);

	public abstract Fact buildFact(long factID, String expressionName);

	public abstract boolean addCard(IAnkiCard c);

	public abstract boolean addCardModel(AnkiCardModel e);

	public abstract IAnkiCard getCardModelByID(long id);

	public abstract boolean addFact(Fact e);

	public abstract boolean addFact(Fact e, boolean allowDuplicates);

	public abstract boolean load();

	public abstract void dump(String sep);

	public abstract boolean readFromFile(String filepath, String encoding);

	public abstract Fact getFactByExp(String kanjiPar);

	public abstract Fact getFactByID(long nr);

	public abstract boolean getLoaded();

	public abstract void setLoaded(boolean v);

	public abstract boolean containsFModel(String fModName);

	public abstract String dumpFModelNames();

}