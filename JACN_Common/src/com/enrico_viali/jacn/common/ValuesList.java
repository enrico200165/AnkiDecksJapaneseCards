package com.enrico_viali.jacn.common;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author enrico
 * Semplici controlli su lista valori contenuti in una stringa
 */
public class ValuesList {

	public ValuesList(String s, boolean ignParenthesis, boolean splitComma,
	int expMaxLength, String[] tokenWhiteListPar) {
		longestElem = "";
		maxLen = expMaxLength;
		whiteList = tokenWhiteListPar;
		if (ignParenthesis) {
			s = s.replaceAll(parenRegex, "");
		}
		
		// trimming
		elems = s.split(" *, *");
		for (String t : elems)
			t = t.trim();
	}

	public boolean maxLenOK() {
		boolean ret = true;

		int len = 0;
		for (String t : elems) {
			if (t.length() > this.maxLen && whiteList != null) {
				boolean inWhiteList = false;
				for (String u : whiteList) {
					if (t.equals(u))
						inWhiteList = true;
				}
				if (!inWhiteList) {
					this.longestElem = t;
					ret = false;
					len = t.length();
				}
			}
		}

		if (!ret)
			log.error("!maxLenOK() " + len + " > " + maxLen + "\nelem: " + this.longestElem);
		return ret;
	}

	public ArrayList<String> getElements() {
		ArrayList<String> al = new ArrayList<String>();
		for (String s : elems) {
			al.add(s);
		}
		return al;
	}
	
	final String parenRegex = " *\\([^)]*\\) *";
	
	String[] elems = null;
	String longestElem;
	int maxLen;
	String[] whiteList;
	
	private static org.apache.log4j.Logger log = Logger.getLogger(ValuesList.class);
}
