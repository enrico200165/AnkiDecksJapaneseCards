


public class Launcher {
	
	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("USAGE: java GoodWindowsExec <cmd>");
			System.exit(1);
		}
		iExecutor e = new ExecutorProcessBuilder();
		e.exec(args, true);
	}
}
