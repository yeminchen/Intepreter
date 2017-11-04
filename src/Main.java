
public class Main {

	public static void main(String[] args) {
		try {
			//Scanner.startScan(args[0]);
			java.util.Scanner rd = new java.util.Scanner(System.in);
			String fileName = rd.next();
			Scanner.startScan(fileName);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error: Reading File Fails");
			System.exit(1);
		}
		ParserAndExecuter pe = new ParserAndExecuter();
		pe.parse();
		Scanner.readInput();
		pe.execute();
	}

}
