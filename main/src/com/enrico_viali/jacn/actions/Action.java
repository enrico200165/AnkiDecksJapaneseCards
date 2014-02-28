package com.enrico_viali.jacn.actions;

import com.enrico_viali.jacn.main.TestDriver;

public abstract class Action {
	
	public Action(String menuMsg,TestDriver tdr) {
		super();
		this.menuMsg = menuMsg;
		_tdrv = tdr;
	}

	public String getMenuMsg() {
		return menuMsg;
	}


	public  String getName() {
	    return this.getClass().getName();
	}
	
	public abstract String toString();

	public void setMenuMsg(String menuMsg) {
		this.menuMsg = menuMsg;
	}


	public abstract boolean perform() throws Exception;

	TestDriver _tdrv;
	String menuMsg;
}
