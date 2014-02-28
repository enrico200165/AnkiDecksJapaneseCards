package com.enrico_viali.jacn.evkanji.actions;

import org.apache.log4j.Logger;
import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.jacn.evkanji.core.EVKanjiManager;

/**
 * @author enrico
 * 
 *         Per aiutare lo sviluppo, tipicamente per metterci controlli/brakpoint
 *         quando trovo elementi corrotti
 */
public class EVKanjyEntryActionCheckAdHoc implements IEVKanjiEntryAction {

	public EVKanjyEntryActionCheckAdHoc(String msg) {
		super();
		this.message = msg;
		this.anomaliesTrapped = 0;
		examined = 0;
		_badOldJLPT = 0;
	}

	@Override
	public boolean elabora(EVKanjiEntry k, EVKanjiManager mgr, Object o) {
		
		examined++;
		if (k.getOldJlpt() < 1 || k.getOldJlpt() > 4) {
			anomaliesTrapped++;
			_badOldJLPT++;
			log.debug("kanji " + k.toString()+" with invalid old JLP: "+k.getOldJlpt());
		}
		
		if (k.getHeisigNumber() <= 0) {
			anomaliesTrapped++;
			log.warn("kanji senza numero di Heisig: " + k.toString());
		}		
		return true;
	}

	public boolean initialAction(EVKanjiEntry k, EVKanjiManager mgr,
			Object otherParams) {
		return true;
	}

	public boolean finalAction(EVKanjiEntry k, EVKanjiManager mgr,
			Object otherParams) {

		log.info(message+"\n"+"examined: " + examined 
		        + " anomalies: " + anomaliesTrapped + " of which"
		        + "\nJLPT: "+_badOldJLPT);

		return true;
	}

	long anomaliesTrapped;
	long examined;
	long _badOldJLPT;
	String message;

	static org.apache.log4j.Logger log = Logger
			.getLogger(EVKanjyEntryActionCheckAdHoc.class);
}
