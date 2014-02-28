import java.util.ArrayList;

public abstract class  aExecutor implements iExecutor {

	public aExecutor() {
		super();
	}

	/**
	 * Quando il comando non è un file
	 * @param argc
	 * @return
	 */
	protected String[] buildCmdForPlatformShell(String[] argc) {
		
		ArrayList<String> cmd = new ArrayList<String>();
		
		String osName = System.getProperty("os.name");
		if (osName.equals("Windows NT") || osName.equals("Windows 7")) {
			cmd.add("cmd.exe");
			cmd.add("/C");
		} else if (osName.equals("Windows 95")) {
			cmd.add("command.com");
			cmd.add("/C");
		} else {
			return null;
		}
		// append the cmd line arguments to the command line
		for (int i = 0; i < argc.length; i++)  {
			cmd.add(argc[i]);
		}
		
		// --- se l'array non è tutto riempito si ottiene errore --- 
		String[] adjstLength = new String[cmd.size()];
		for (int j = 0; j< adjstLength.length;  j++  ) 
			adjstLength[j] = cmd.get(j);
		
		return adjstLength;
	}

	abstract public void exec(String argsPar[], boolean isShellCommand);
	
}