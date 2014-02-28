package com.enrico_viali.jacn.actions;

import java.io.*;
import com.enrico_viali.jacn.main.TestDriver;

class StreamGobbler extends Thread {
	InputStream is;
	String type;
	OutputStream os;

	StreamGobbler(InputStream is, String type) {
		this(is, type, null);
	}

	StreamGobbler(InputStream is, String type, OutputStream redirect) {
		this.is = is;
		this.type = type;
		this.os = redirect;
	}

	public void run() {
		try {
			PrintWriter pw = null;
			if (os != null)
				pw = new PrintWriter(os);

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (pw != null)
					pw.println(line);
				System.out.println(type + ">" + line);
			}
			if (pw != null)
				pw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}

class OutStreamGobbler extends Thread {
	String name;
	OutputStream os;

	OutStreamGobbler(String type, OutputStream redirect) {
		this.name = type;
		this.os = redirect;
	}

	public void run() {
		try {
			PrintWriter pw = null;
			if (os != null) {
				pw = new PrintWriter(os);
				if (pw != null) {
					pw.write(name);
					pw.flush();
					pw.close();
					System.out.println("written: "+name+ " to input");
					System.out.flush();
				}
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
}

public class MecabProcessTest extends com.enrico_viali.jacn.actions.Action {

	public MecabProcessTest(TestDriver tdr) {
		super("mecab PROCESS", tdr);
	}

	@Override
	public boolean perform() throws Exception {

		String str = "文字エンコーディング名であるべきところが '%1$S' になっています";
		String[] args = new String[1];
		// args[0] = "netstat.exe";
		args[0] = "mecab.exe";

		try {
			FileOutputStream fos = new FileOutputStream("dummy.txt");
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(args[0]);
			// any error message?

			OutStreamGobbler os = new OutStreamGobbler(str, proc.getOutputStream());			
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", fos);

			// kick them off
			os.start();
			errorGobbler.start();
			outputGobbler.start();

			int exitVal = proc.waitFor();
			System.out.println("ExitValue: " + exitVal);
			fos.flush();
			fos.close();
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
