package com.enrico_viali.jacn.ankideck.heisig;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.ankideck.generic.*;
import com.enrico_viali.jacn.common.*;
import com.enrico_viali.libs.rdb_jdbc.*;

public class ADeckHeisigMgr extends AnkiDeckGeneric {

	public ADeckHeisigMgr(String dbdir) throws Exception {

		super(Cfg.FNAME_DECK_HEISIG, new ADeckHesigDataModel(
				new RDBManagerSQLite(dbdir + Cfg.FNAME_DECK_HEISIG,true)), 
				"Kanji",/* allow duplicate expression */false);
		container = new KanjiContainer<AnkiFactHeisig>(Cfg.NRMAX_KANJI);
	}

	@Override
	public AnkiFact buildFact(long factID, String fieldName) {
		return new AnkiFactHeisig(factID, fieldName, this);
	}

	public AnkiFactHeisig findByHNr(int nr) {
		if (!getLoaded()) {
			log.error("look up by number on empty deck");
			return null;
		}
		AnkiFactHeisig f = container.getByNr(nr);
		if (f == null) {
			log.warn("AnkiFactHeisig " + nr + " not found ");
		}
		return f;
	}

	@Override
	public boolean addFact(AnkiFact o) {
		if (o == null) {
			log.error("attempt to insert null element");
			return false;
		}
		if (o instanceof AnkiFactHeisig) {
			AnkiFactHeisig e = (AnkiFactHeisig) o;
			if (!super.addFact(e))
				return false;
			try {
				if (!container.insert(e.getKeyFieldName(), e,
						e.getHeisigNumber()))
					return false;
			} catch (AnkiDeckMalformedFact e1) {
				log.error("", e1);
				return false;
			}
			return true;
		} else {
			log.error("atteso oggetto di classe AnkiFactHeisig trovata classe "
					+ o.getClass().getName());
			return false;
		}
	}

	@Override
	public boolean load() {
		if (dm.readFactsDeck(this, "Kanji"))
			setLoaded(true);
		return true;
	}

	KanjiContainer<AnkiFactHeisig> container;

	private static org.apache.log4j.Logger log = Logger
			.getLogger(ADeckHeisigMgr.class);
}
