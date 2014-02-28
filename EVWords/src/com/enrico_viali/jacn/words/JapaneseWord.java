package com.enrico_viali.jacn.words;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.*;
import com.enrico_viali.utils.Utl;

public class JapaneseWord extends EVJPCNEntry {

	
	public JapaneseWord() {
		super();
		expression = "";//Utl.NOT_INITIALIZED_STRING;
		title = "";//Utl.NOT_INITIALIZED_STRING;
		context = "";
		meaning = "";//Utl.NOT_INITIALIZED_STRING;
		reading = "";//Utl.NOT_INITIALIZED_STRING;
		comment = "";//Utl.NOT_INITIALIZED_STRING;
		edict = "";//Utl.NOT_INITIALIZED_STRING;
		tag = "";
		number = Utl.NOT_INITIALIZED_INT;
	}
	
	
	
	
	public boolean setAll(String expression, String title, String context, String meaning, String reading,
	String comment, String edict, String tag, long number) {
		this.expression = expression;
		this.title = title;
		this.context = context;
		this.meaning = meaning;
		this.reading = reading;
		this.comment = comment;
		this.edict = edict;
		this.tag = tag;
		this.number = number;
		return true;
	}





	@Override
	public int compareTo(IEVJPCNEntry o) {
		
		return getKeyExpression().compareTo(o.getKeyExpression());
		/*
		if (getNumber() < o.getNumber())
			return -1;
		else if (getNumber() == o.getNumber())
			return 0;
		else return 1;*/
	}	

	
	
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String getKeyExpression() {
		return getExpression();
	}

	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @return the meaning
	 */
	public String getMeaning() {
		return meaning;
	}
	/**
	 * @return the reading
	 */
	public String getReading() {
		return reading;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @return the edict
	 */
	public String getEdict() {
		return edict;
	}
	/**
	 * @return the number
	 */
	public long getNumber() {
		return number;
	}
	/**
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @param meaning the meaning to set
	 */
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	/**
	 * @param reading the reading to set
	 */
	public void setReading(String reading) {
		this.reading = reading;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @param edictByHeadWord the edict to set
	 */
	public void setEdict(String edict) {
		this.edict = edict;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(long number) {
		this.number = number;
	}
	
	public String toString() {
		String sep = "\n";
		String s = sep + "expr="+expression;
		s+= sep + " title="+title;
		s+= sep + " context="+context;
		s+= sep + " mean=" +meaning;
		s+= sep + " read="+ reading;
		s+= sep + " comm="+comment;
		s+= sep + " tag="+tag;
		s+= sep + " nr="+number;
		s+= sep + " edict:\n "+edict;
		return s;
 	}
	
	/**
	 * @return the context
	 */
	public String getContext() {
		return context;
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(String context) {
		this.context = context;
	}

	
	public String getAsCSVLine(long scanned,long included,
	String sep, String sepReplacement) {
		log.warn("dummy/partial implementation check it!!!");
		return getCSVLine(sep,sepReplacement);
	}
	

	public String getCSVLine(String sep, String sepReplacement) {
		String s = "";
		s += getExpression().replace(Cfg.SEP_EVKANJI_OUT, " ");
		s += sep + getTitle().replace(Cfg.SEP_EVKANJI_OUT, " ");
		s += sep + getContext().replace(Cfg.SEP_EVKANJI_OUT, " ");
		s += sep + getMeaning().replace(Cfg.SEP_EVKANJI_OUT, " ");
		s += sep + getReading().replace(Cfg.SEP_EVKANJI_OUT, " ");
		s += sep + getComment().replace(Cfg.SEP_EVKANJI_OUT, " ");
		s += sep + getEdict().replace(Cfg.SEP_EVKANJI_OUT, " ");
		s += sep + getTag().replace(Cfg.SEP_EVKANJI_OUT, " ");
		s += sep + getNumber();
		String[] a = s.split("\t");
		if (a.length != 9)
			log.debug("");
		return s;
	}

	
	String expression;
	String title; // ex in Firefox language files
	String context; // ex in Firefox language files	
	String meaning;
	String reading;
	String comment;
	String edict;
	String tag;
	long number;
	
	private static org.apache.log4j.Logger log = Logger.getLogger(JapaneseWord.class);

	@Override
	public String getAsHTMLLine(long scanned, long included, int level) {
		// TODO Auto-generated method stub
		return null;
	}

}
