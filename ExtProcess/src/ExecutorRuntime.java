

public class ExecutorRuntime extends aExecutor {

	
	/* (non-Javadoc)
	 * @see iExecutor#exec(java.lang.String[], boolean)
	 */
	@Override
	public void exec(String argsPar[], boolean isShellCommand) {
		if (argsPar.length < 1) {
			System.err.println("!!!!");
			System.exit(1);
		}

		String[] args = isShellCommand ? buildCmdForPlatformShell(argsPar) : argsPar; 		
		
		try {

			System.out.print("excecuting: ");
			for (int i = 0; i< args.length; i++)
				System.out.print(" "+args[i]);
			System.out.println("");
			
			Runtime rt = Runtime.getRuntime();

			Process proc = rt.exec(args);

			// any error message?
			StreamGobbler errorGobbler = new
				StreamGobbler(proc.getErrorStream(), "ERROR");

			// any output?
			StreamGobbler outputGobbler = new
				StreamGobbler(proc.getInputStream(), "OUTPUT");

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			// any error???
			int exitVal = proc.waitFor();
			System.out.println("ExitValue: " + exitVal);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	
}
