package com.enrico_viali.jacn.evkanji.actions;

import org.apache.log4j.Logger;
import com.enrico_viali.gtranslate.GTranslateDB;
import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;

public class EVKanjyEntryActionTranslateMeaning implements IEVKanjiEntryAction {

	@Override
	public boolean elabora(EVKanjiEntry k, EVKanjiManager mgr, Object o) {
		if (o instanceof GTranslateDB) {
			GTranslateDB tsl = (GTranslateDB) o;
			return mgr.kanjidicMeanToItalianSingle(k, tsl);
		} else {
			log.error("");
			return false;
		}
	}
	
	public boolean initialAction(EVKanjiEntry k, EVKanjiManager mgr, Object otherParams) { return true;}

	public boolean finalAction(EVKanjiEntry k, EVKanjiManager mgr, Object otherParams) { return true;}

	
	static org.apache.log4j.Logger log = Logger.getLogger(EVKanjyEntryActionTranslateMeaning.class);
}
