package com.enrico_viali.jacn.actions;

import com.enrico_viali.jacn.main.TestDriver;

public class Exit extends Action {

	public Exit(TestDriver tdr) {
		super("Exit",tdr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean perform() throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
