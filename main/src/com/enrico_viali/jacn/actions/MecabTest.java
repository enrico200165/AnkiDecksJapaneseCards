package com.enrico_viali.jacn.actions;

import org.apache.log4j.Logger;


import com.enrico_viali.jacn.main.TestDriver;
import com.enrico_viali.utils.Utl;

public class MecabTest extends Action {
	private static org.apache.log4j.Logger log = Logger.getLogger(MecabTest.class);

	static final String mecab_dll = "libmecab.dll";
	
	public MecabTest(TestDriver tdr) {
		super("Mecab Test", tdr);
		// TODO Auto-generated constructor stub
	}

	static {
		try {
			if (!Utl.fileExistInPath(mecab_dll)) {
				System.err.print("mecab dll not in path");
				System.exit(1);
			}

			if (!Utl.fileExistInJavaProperty(mecab_dll,"java.library.path")) {
				System.err.print("mecab dll not in load ");
				System.exit(1);
			}

			System.load("v:/programs/mecab/bin/"+mecab_dll);
		} catch (UnsatisfiedLinkError e) {
			log.error("Cannot load "+mecab_dll, e);
			System.exit(1);
		}
	}

	@Override
	public boolean perform() throws Exception {

		return false;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}
}
