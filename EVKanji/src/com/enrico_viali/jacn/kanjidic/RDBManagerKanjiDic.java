package com.enrico_viali.jacn.kanjidic;

import com.enrico_viali.libs.rdb_jdbc.RDBManagerSQLite;

public class RDBManagerKanjiDic extends RDBManagerSQLite {
	
	
	public RDBManagerKanjiDic(String dbPathNamePar) throws Exception {
		super(dbPathNamePar,true);
		_tabella = new KanjiDicTable(this);
	}

	KanjiDicTable _tabella;
}
