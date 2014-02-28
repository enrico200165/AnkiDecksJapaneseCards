package com.enrico_viali.jacn.edict;

import java.util.ArrayList;

public interface IEntriesList {

	int getNrEntries();
	
	boolean add(IEdictEntry e);
	String getAllEdictEntriesStr(String endEntrySep);

	ArrayList<IEdictEntry> getEntries();
}
