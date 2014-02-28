package com.enrico_viali.jacn.main;

import java.io.*; //import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.actions.Action;

public class TestDriver {

	public TestDriver() {
		super();
		mgr = new GlobalManager();
		actionsList = new ArrayList<Action>();
		pos = 0;

	}


	public void addAction(Action action) {
	    log.debug("aggiungo azione: "+action.getName());
	    actionsList.add(pos++, action);
	}
	
	public String getActionsList() {
		String msg = "";
		for (int i = 0; i < actionsList.size(); i++) {
			msg += "\n" + i + ": " + actionsList.get(i).getMenuMsg();
		}
		return msg;		
	}
	
	public int ui() {
		int choice;
		log.info(getActionsList()+"\n--- choose operation ---"+
		"\ncan pass operation as argument with -c<operation number on this menu>");
		try {
			choice = GetUserInput();
			return choice;
		} catch (Exception e) {
			log.error("", e);
		}
		return -1;
	}


	public  boolean executeAction(int chosenActionIdx) throws Exception {

		Action chosenAction	= actionsList.get(chosenActionIdx);		
		if (chosenAction == null) 		{
			log.error("scelta azione non legale, codice scelta: " + chosenActionIdx);
			return false;
		}
		log.info("performing action: "+chosenAction.toString());			
		if( !chosenAction.perform()) {
			log.error("Action failed"+chosenAction.toString());
		}
		return true;
	}

	
	static int GetUserInput() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		int scelta = -1;
		try {
			input = br.readLine();
			scelta = Integer.parseInt(input);
		} catch (IOException e) {
			System.out.println("Error!");
			System.exit(1);
		}
		System.out.println("scelta: " + scelta);
		return scelta;
	}

	public GlobalManager getKanjiManager() {
		return mgr;
	}

	List<Action> actionsList;
	GlobalManager mgr;
	int pos;
	
	private static org.apache.log4j.Logger log = Logger
			.getLogger(TestDriver.class);

}
