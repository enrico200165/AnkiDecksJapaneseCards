package com.enrico_viali.jacn.common;

//import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;


public abstract class EVManager  {

	public EVManager() {
		super();
	}

	abstract public boolean isLoaded();

	public int size() {
		return getCopyOfEntries(null).size();
	}

	abstract public boolean add(EVJPCNEntry e);

	abstract public boolean writeToCSV(String filePath, boolean ovwFile, boolean ignErr);

	public void dump(String sep) {
		int i = 0;
		Collection<EVJPCNEntry> elems = getCopyOfEntries(null);
		for (EVJPCNEntry e : elems) {
			// if (e != null)
			log.info(++i + " " + e.toString());
			if (i%25 == 0)
				log.debug("giusto per breakpoint");
		}
	}

	public abstract TreeSet<EVJPCNEntry> getCopyOfEntries(Comparator<EVJPCNEntry> c);

	private static org.apache.log4j.Logger log = Logger.getLogger(EVManager.class);
}
