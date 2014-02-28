
public class ExecutorProcessBuilder extends aExecutor {


	@Override
	public void exec(String argsPar[], boolean isShellCommand) {
		if (argsPar.length < 1) {
			System.err.println("!!!!");
			System.exit(1);
		}

		String[] args = isShellCommand ? buildCmdForPlatformShell(argsPar) : argsPar; 		
		
		try {

			System.out.print("PB excecuting: ");
			for (int i = 0; i< args.length; i++)
				System.out.print(" "+args[i]);
			System.out.println("");
			
			Process process = new ProcessBuilder(args).start();
			// any error message?
			StreamGobbler errorGobbler = new
				StreamGobbler(process.getErrorStream(), "ERROR");
			// any output?
			StreamGobbler outputGobbler = new
				StreamGobbler(process.getInputStream(), "OUTPUT");

			// kick them off
			errorGobbler.start(); outputGobbler.start();

			// any error???
			int exitVal = process.waitFor();
			System.out.println("ExitValue: " + exitVal);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	ProcessBuilder pb;
}
