package com.enrico_viali.jacn.actions;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.Cfg;
import com.enrico_viali.jacn.main.TestDriver;
import com.enrico_viali.jacn.words.WordList;

public class CreateWordDeckFromWList extends Action {

	public CreateWordDeckFromWList(TestDriver tdr) {
		super("create word deck from word list", tdr);
	}

	@Override
	public boolean perform() throws Exception {
		WordList wlBldr = new WordList();
		wlBldr.loadFromFile(Cfg.getDirJPCNData() + "wlist_in.txt");
		if (true /* da riscrivere, c'era il rimosso !wlBldr.enrichAnkiDeck()*/) {
		    log.error("");
		    System.exit(0);
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

    private static org.apache.log4j.Logger log = Logger.getLogger(CreateWordDeckFromWList.class);

}
