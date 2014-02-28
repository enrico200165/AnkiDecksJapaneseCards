package com.enrico_viali.jacn.common;

import com.enrico_viali.utils.IRenderableAsTextLine;


public interface IEVJPCNEntry extends Comparable<IEVJPCNEntry> , IRenderableAsTextLine{
	
	@Override
	boolean equals(Object o);
	
	@Override
	int hashCode();
	
	String getKeyExpression();
	

	/**
	 * @param scanned 
	 * if called by an iteration index of examined elements
	 * @param included
	 * if called by an iteration index of included elements
	 * (some elements may be ignored)
	 * @return
	 */
	String getAsHTMLLine(long scanned,long included, int level);

	
	
	long getNumber();
}
