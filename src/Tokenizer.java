import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Tokenizer {
	
	private static final String SYMBOLS = ";,()[]=+-*|";
	private static final String SPECIAL_SYMBOL = ":!<>";
	public static List<String> tokens = new LinkedList<String>();
	private static int tracer = 0;
	
	
	public static void generateTokens(String fileName) {
		BufferedReader reader = null;
		List<String> lines = new LinkedList<String>();
		try {
			reader = new BufferedReader(new FileReader(new File(fileName)));
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		tokenize(lines);
	}
	
	
	public static void tokenize(List<String> lines) {
		for (String line : lines) {
			int length = line.length();
			int i = 0; //index of string to read char of line
			int j = 0; // j helps to get token from line
			while (i < length) {
				j = i + 1;
				char c = line.charAt(i);
				if (Character.isDigit(c)) {
					//use j to read following char to check it is ID or const
					while (j < length && Character.isDigit(line.charAt(j))) {
						j++;
					}
					String cons = line.substring(i, j);
					tokens.add(cons);
				} else if (SYMBOLS.contains(String.valueOf(c))){
					tokens.add(line.substring(i,j));
				} else if (SPECIAL_SYMBOL.contains(String.valueOf(c))) {
					if (i+1 < length && line.charAt(i+1) == '=') { //the symbol is >=, <=, != or :=
						j++;
					}
					tokens.add(line.substring(i,j));
				} else if (!Character.isWhitespace(c)) {
					while (j < length && !Character.isWhitespace(line.charAt(j))
							&& !SYMBOLS.contains(String.valueOf(line.charAt(j)))
							&& !SPECIAL_SYMBOL.contains(String.valueOf(line.charAt(j)))) { 
						j++;
					}
					tokens.add(line.substring(i, j));
				}
				i = j;
			}
		}
		tokens.add("EOF");
	}
	
	public static String currentToken() {
		
		return tokens.get(tracer);
	}
	
	public static void nextToken() {
		
		tracer++;
	}
	
	public static void reset() { 
		tracer = 0; 
	}
	

	
	
	//for -test use
	public static void main(String[] args) {

	}
}
