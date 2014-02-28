package com.enrico_viali.jacn.edict;

import java.util.ArrayList;

public interface IEdictEntry extends Comparable<IEdictEntry> {
	
	boolean fillValuesVector(String[] v);	
	
	public abstract String toString(String sep);

	public abstract String toString();	
	
	public abstract String getHeadWord();

	public abstract void setKanjiWord(String kanjiWord);

	public abstract String getReading();

	public abstract void setReading(String Reading);

	public abstract String getWholeDescription();
	
	abstract boolean buildFromEdictLine(String line, String sep, IEdictEntry entry, int lineNr);
	
	public abstract void setWholeDescription(String wholeDescription);

	public abstract String getField();

	public abstract void setField(String val);

	public abstract boolean getIsFrequent();

	public abstract void setIsFrequent(boolean p);

	public abstract long getLine();

	public abstract boolean getIsPlainNoun();

	/**
     * Plain noun = (n) ie NOT 
     * n-adv adverbial noun (fukushitekimeishi) 
     * n-pref noun, used as a prefix 
     * n-suf noun, used as a suffix 
     * n-t noun (temporal) (jisoumeishi)
     * @param v
     */
	public abstract void setIsPlainNoun(boolean v);

	public abstract boolean getIsAdjective();

	public abstract void setIsAdjective(boolean v);

	public abstract boolean getIsVerb();

	public abstract void setIsVerb(boolean v);

	public abstract ArrayList<String> getDescrizioni();

	public abstract void setDescrizioni(ArrayList<String> descrizioni);

	public abstract int getRating();

	public abstract void setRating(int v);

	public abstract int compareTo(IEdictEntry other);

	public abstract boolean equals(Object o);

	public abstract int hashCode();

}