import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ParserAndExecuter {

	private Prog prog;
	public List<Integer> DATA = new LinkedList<Integer>();
	
	public ParserAndExecuter() {
		prog = new Prog();
	}
	public void parse() {
		prog.parse();
	}
	
	public void execute() {
		prog.execute();
	}
	
}

class Prog {
	private Decl_Seq declSeq;
	private Statement_Seq stmtSeq;
	
	public void parse() {
		Scanner.match("PROGRAM");
		declSeq = new Decl_Seq();
		declSeq.parse();
		Scanner.match("BEGIN");
		stmtSeq = new Statement_Seq();
		stmtSeq.parse();
		Scanner.match("END");
		Scanner.match("EOF");
		
	}
	
	public void execute() {
		declSeq.execute();
		stmtSeq.execute();
	}
}

class Decl_Seq {
	private int altNo = 0; 
	private Decl_Seq declSeq;
	private Decl decl;
	
	public void parse() {
		decl = new Decl();
		decl.parse();
		if (!Scanner.currentToken().equals("BEGIN")) {
			altNo = 1;
			declSeq = new Decl_Seq();
			declSeq.parse();
		}
	}
	
	public void execute() {
		decl.execute();
		if (altNo == 1) {
			declSeq.execute();
		}
	}
}

class Statement_Seq {
	private int altNo = 0;
	private Statement stmt;
	private Statement_Seq stmt_seq;
	
	
	public void parse() {
		stmt = new Statement();
		stmt.parse();
		String token = Scanner.currentToken();
		if (!token.equals("END") && !token.equals("ENDIF")
				&& !token.equals("ENDWHILE") && !token.equals("ELSE")) {
			altNo = 1;
			stmt_seq = new Statement_Seq();
			stmt_seq.parse();
		}
	}
	
	public void execute() {
		stmt.execute();
		if (altNo == 1) {
			stmt_seq.execute();
		}
	}
}

class Decl {
	private ID_List idls;
	
	public void parse() {
		Scanner.match("INT");
		idls = new ID_List();
		idls.parse();
		Scanner.match("SEMI");
	}
	
	public void execute() {
		idls.execute();
	}
}

class Statement {
	
	private int altNo = 0;
	private Assign assign;
	private If cond;
	private Loop lp;
	private In input;
	private Out out;
	
	
	public void parse() {
		String token = Scanner.currentToken();
		if (token.contains("ID")) {
			altNo = 1;
			assign = new Assign();
			assign.parse();
		} else if (token.equals("IF")) {
			altNo = 2;
			cond = new If();
			cond.parse();
		} else if (token.equals("WHILE")){
			altNo = 3;
			lp = new Loop();
			lp.parse();
		} else if (token.equals("INPUT")) {
			altNo = 4;
			input = new In();
			input.parse();
		} else if (token.equals("OUTPUT")) {
			altNo = 5;
			out = new Out();
			out.parse();
		}
	}
	
	public void execute() {
		if (altNo == 1) {
			assign.execute();
		} else if (altNo == 2) {
			cond.execute();
		} else if (altNo == 3) {
			lp.execute();
		} else if (altNo == 4) {
			input.execute();
		} else if (altNo == 5) {
			out.execute();
		}
	}
}

class ID_List {
	private int altNo = 0;
	private String id;
	private ID_List idl;
	
	public void parse() {
		id = Scanner.getID();
		Scanner.nextToken();
		if (Scanner.currentToken().equals("COMMA")) {
			altNo = 1;
			Scanner.nextToken();
			idl = new ID_List(); 
			idl.parse();
		}
	}
	
	public void execute() {
		if (!Scanner.isInVariableMap(id)) {
			Scanner.putNewVariable(id);
		} else {
			System.out.println("ERROR: Variable " + id + " has already been instantiated.");
			System.exit(1);
		}
		if (altNo == 1) {
			idl.execute();;
		}
	}
}

class Assign {
	private Expr exp;
	private String id;
	
	public void parse() {
		id = Scanner.getID();
		Scanner.nextToken();
		Scanner.match("ASSIGN");
		exp = new Expr();
		exp.parse();
		Scanner.match("SEMI");
	}
	
	public void execute() {
		if (Scanner.isInVariableMap(id)) {
			int value = exp.execute();
			Scanner.changeValue(id, value);
		} else {
			System.out.println("ERROR: variable id " + id + " has not been declared");
			System.exit(1);
		}
	}
}

class If {
	private int altNo = 0;
	private Cond cond;
	private Statement_Seq sts;
	private Statement_Seq else_sts;
	
	public void parse() {
		Scanner.match("IF");
		cond = new Cond(); 
		cond.parse();
		Scanner.match("THEN");
		sts = new Statement_Seq();
		sts.parse();
		String token = Scanner.currentToken();
		if (token.equals("ELSE")) {
			altNo = 1;
			Scanner.nextToken();
			else_sts = new Statement_Seq(); 
			else_sts.parse();
		}
		Scanner.match("ENDIF");
		Scanner.match("SEMI");
	}
	
	public void execute() {
		if (cond.execute()) {
			sts.execute();
		} else if (altNo == 1) {
			else_sts.execute();
		}
	}
}

class Loop {
	private Statement_Seq sts;
	private Cond cond;
	
	public void parse() {
		Scanner.match("WHILE");
		cond = new Cond(); 
		cond.parse();
		Scanner.match("BEGIN");
		sts = new Statement_Seq();
		sts.parse();
		Scanner.match("ENDWHILE");
		Scanner.match("SEMI");
	}
	
	public void execute() {
		while (cond.execute()) {
			sts.execute();
		}
	}
}

class In {
	private String id;
	
	public void parse() {
		Scanner.match("INPUT");
		id = Scanner.getID();
		Scanner.nextToken();
		Scanner.match("SEMI");
	}
	
	public void execute() {
		List<Integer> dt = Scanner.DATA;
		int input = dt.remove(0);
		Scanner.changeValue(id, input);
		
	}
}

class Cond {
	private int altNo = 0;
	private Cmpr cmp;
	private Cond cond;
	
	public void parse() {
		if (Scanner.currentToken().equals("NOT")) {
			altNo = 1;
			Scanner.nextToken();
			Scanner.match("LEFT_P");
			cond = new Cond();
			cond.parse();
			Scanner.match("RIGHT_P");
		} else {
			altNo = 2;
			cmp = new Cmpr();
			cmp.parse();
			if (Scanner.currentToken().equals("OR")) {
				altNo = 3;
				Scanner.nextToken();
				cond = new Cond();
				cond.parse();
			}
		}
	}
	
	public boolean execute() {
		if (altNo == 1) {
			return !(cond.execute());
		} else if (altNo == 2) {
			return cmp.execute();
		} else if (altNo == 3) {
			return (cmp.execute() || cond.execute());
		}
		return false;
	}
}

class Cmpr {
	private int altNo;
	private Expr exp1;
	private Expr exp2;
	private String op;
	
	public void parse() {
		exp1 = new Expr();
		exp1.parse();
		String token = Scanner.currentToken();
		if (token.equals("EQUAL") || token.equals("LESS") || token.equals("LESS_EQUAL")) {
			op = token;
			Scanner.nextToken();
		} else {
			System.out.println("ERROR: Expected a comparison operator, found " + token);
			System.exit(1); // Failure Case
		}
		exp2 = new Expr();
		exp2.parse();
		
		
	}
	
	public boolean execute() {
		int val1 = exp1.execute();
		int val2 = exp2.execute();
		if (op.equals("EQUAL")) {
			return (val1 == val2);
		} else if (op.equals("LESS")) {
			return (val1 < val2);
		} else if (op.equals("LESS_EQUAL")) {
			return (val1 <= val2);
		}
		return false; // this line will never be reached. Because op must be one of '=' , '<' and '<='
					  // Otherwise it will report failure in parse();
	}
}
/*
 * class for out
 */
class Out {
	
	private Expr exp;
	
	public void parse() {
		Scanner.match("OUTPUT");
		exp = new Expr();
		exp.parse();
		Scanner.match("SEMI");
	}
	
	public void execute() {
		int result = exp.execute();
		System.out.println(result);
	}
}

/*
 * class of <expr>
 */
class Expr {
	
	private int altNo;
	private Term term;
	private Expr exp;
	
	public void parse() {
		term = new Term(); 
		term.parse();
		String token = Scanner.currentToken();
		if (token.equals("PLUS")) {
			altNo = 1;
			Scanner.nextToken();
			exp = new Expr(); 
			exp.parse();
		} else if (token.equals("MINUS")) {
			altNo = 2;
			Scanner.nextToken();
			exp = new Expr(); 
			exp.parse();
		}
		
	}
	
	public int execute() {
		int result = term.execute();
		if (altNo ==1) { //op is +;
			result = result + exp.execute();
		} else if (altNo == 2) {
			result = result - exp.execute();
		}
		
		return result;
	}
	
}

/*
 * class of <term>
 */
class Term {

	private int altNo;
	private Factor fct; 
	private Term tm;
	
	
	public void parse() {
		fct = new Factor();
		fct.parse();
		if (Scanner.currentToken().equals("MULTI")) {
			altNo = 1;
			Scanner.nextToken();
			tm = new Term();
			tm.parse();
		}
	}
	
	public int execute() {
		int result = fct.execute();
		if (altNo == 1) {
			result = result * tm.execute();
		}
		return result;
	}
}

/*
 * Class of <factor>
 */
class Factor {
	private int altNo;
	private int value;      // 0 ::= const;
	private String id;      // 1 ::= id;
	private Factor factor;  // 2 ::= -<factor>;
	private Expr exp; 
	
	public void parse() {
		if (Scanner.currentToken().contains("CONST")) {
			altNo = 0;
			value = Scanner.getValue();
			Scanner.nextToken();
		//for lab 1 there are only 2 ID, x represents 1, y represent 2;
		} else if (Scanner.currentToken().contains("ID")) {
			altNo = 1;
			id = Scanner.getID();
			Scanner.nextToken();
		} else if (Scanner.currentToken().contains("LEFT_P")) {
			altNo = 2;
			Scanner.match("LEFT_P");
			exp = new Expr(); 
			exp.parse();
			Scanner.match("RIGHT_P");
		}
		
	}
	
	public int execute() {
		if (altNo == 0) {
			return value;
		} else if (altNo == 1) {
			value = Scanner.getIdValue(id);
			return value;
		} else {
			return exp.execute();
		}
	}
}



