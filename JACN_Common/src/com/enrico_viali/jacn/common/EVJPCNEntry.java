package com.enrico_viali.jacn.common;

public abstract class EVJPCNEntry implements IEVJPCNEntry {

	@Override
	public abstract String getKeyExpression();

	@Override
	public int compareTo(IEVJPCNEntry o) {
		return getKeyExpression().compareTo(o.getKeyExpression());
	}	
	
	@Override
	public int hashCode() {
		return getKeyExpression().hashCode();
	}
	
	@Override
	public boolean equals(Object oo) {
		EVJPCNEntry other = (EVJPCNEntry) oo; 
		return getKeyExpression().equals(other.getKeyExpression()); 
	}
}
