import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Scanner {
	
	private static final String[] KEYWORD = {"program", "begin", "end", "int", "input", "output", "if",
			"then", "else", "endif", "do", "enddo", "while", "endwhile", "case", "of", "or", "EOF"};
	private static HashMap<String,Integer> VARIABLES = new HashMap<String,Integer>();
	public static List<Integer> DATA = new LinkedList<Integer>();
	
	
	public static void startScan(String fileName) {
		Tokenizer.generateTokens(fileName);	
	}
	
	public static String currentToken() {
		String cToken = Tokenizer.currentToken();
		cToken = wrapToken(cToken);
		System.out.println(cToken);
		return cToken;
	}
	
	public static void nextToken() { 
		Tokenizer.nextToken(); 
	}
	
	//after reading failure, we should reset stream
	public static void reset() { 
		Tokenizer.reset(); 
	}

	public static void match(String keyword) {
		String token = currentToken();
		if (token.equals(keyword)) {
			nextToken();
		} else {
			System.out.println("ERROR: Wrong keyword: " + token + ", it should be "
					+ keyword + " here.");
			System.exit(1); // Failure Case;
		}
	}
	//return value of current Token
	public static int getValue() {
		int value = 0;
		String token = currentToken();
		if (!token.contains("CONST[")) {
			System.out.println("ERROR: Wrong way to get access to getValue()");
			System.exit(1);
		} else {
			value = Integer.parseInt(token.substring(token.indexOf("[") + 1, token.indexOf("]")));
		}
		return value;
	}
	
	public static String getID() {
		String id = "";
		String token = currentToken();
		if (!token.contains("ID[")) {
			System.out.println("ERROR: Wrong way to get access to getID()");
			System.exit(1);
		} else {
			id = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
		} 
		return id;
	}
	
	public static boolean isInVariableMap(String id) {
		return VARIABLES.containsKey(id);
	}
	
	public static void putNewVariable(String id) {
		VARIABLES.put(id, null);
	}
	
	public static void changeValue(String id, int value) {
		if (VARIABLES.containsKey(id)) {
			VARIABLES.put(id, value);
		} else {
			System.out.println("ERROR: ID is not declared");
			System.exit(1);
		}
	}
	
	//return the value of id, report failure if it's not initialed.
	public static int getIdValue(String id) {
		int value;
		if (VARIABLES.containsKey(id)) {
			value = VARIABLES.get(id);
			return value;
		} else {
			System.out.println("ERROR: ID is not declared");
			System.exit(1);
		}
		return -1;
	}
	/**
	 * Return a string which can be used by parser
	 * 
	 * @param cToken valid String token
	 * @return parsable token
	 */
	private static String wrapToken(String cToken) {
		if (hasNotAcceptableChar(cToken)) return "SCANNER_ERROR[" + cToken + "]";
		else if (Arrays.asList(KEYWORD).contains(cToken)) return cToken.toUpperCase();
		else if (Character.isLetter(cToken.charAt(0))) return "ID[" + cToken + "]";
		else if (Character.isDigit(cToken.charAt(0))) return "CONST[" + cToken + "]";
		else if (cToken.equals(";")) return "SEMI";
		else if (cToken.equals("+")) return "PLUS";
		else if (cToken.equals("-")) return "MINUS";
		else if (cToken.equals("*")) return "MULTI";
		else if (cToken.equals("(")) return "LEFT_P";
		else if (cToken.equals(")")) return "RIGHT_P";
		else if (cToken.equals(",")) return "COMMA";
		else if (cToken.equals("[")) return "LEFT_B";
		else if (cToken.equals("]")) return "RIGHT_B";
		else if (cToken.equals("=")) return "EQUAL";
		else if (cToken.equals(":")) return "COLON";
		else if (cToken.equals("!")) return "NOT";
		else if (cToken.equals("<")) return "LESS";
		else if (cToken.equals("|")) return "BAR";
		else if (cToken.equals(":=")) return "ASSIGN";
		else if (cToken.equals("!=")) return "NOT_EQUAL";
		else if (cToken.equals("<=")) return "LESS_EQUAL";
		else return "ERROR[" + cToken + "]";
	}

	
	private static boolean hasNotAcceptableChar(String cToken) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void readInput() {
		java.util.Scanner in = new java.util.Scanner(System.in);
		while (in.hasNextInt()) {
			int data = in.nextInt();
			DATA.add(data);
		}
	}
	

}
