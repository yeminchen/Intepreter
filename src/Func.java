import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Func {
		
		public static HashMap<String, Func> func_list = new HashMap<String,Func>();
		public List<Integer> parameter = new LinkedList<Integer>();
		public HashMap<String,Integer> VARIABLES = new HashMap<String,Integer>();
		private FuncExpr fe;
		private FuncDS ds;
		private FuncSS ss;
		private Func_ID_List idl;
		private Para pm;
		
		
		public void parse() {
			Scanner.match("LEFT_P");
			pm = new Para();
			pm.parse();
			Scanner.match("RIGHT_P");
			ds = new FuncDS();
			ds.parse();
			Scanner.match("BEGIN");
			ss = new FuncSS();
			ss.parse();
			Scanner.match("RETURN");
			fe = new FuncExpr();
			fe.parse();
			Scanner.match("SEMI");
			Scanner.match("END");
			Scanner.match("SEMI");
		}
		
		public int execute() {
			pm.execute(this);
			ds.execute(this);
			ss.execute(this);
			
			return fe.execute(this);
		}
		
		public void changeValue(String id, int value) {
			if (this.VARIABLES.containsKey(id)) {
				this.VARIABLES.put(id, value);
			} else {
				System.out.println("ERROR: ID is not declared when changeValue(func)");
				System.exit(1);
			}
		}
		
		//return the value of id, report failure if it's not initialed.
		public int getIdValue(String id) {
			int value;
			if (this.VARIABLES.containsKey(id)) {
				value = this.VARIABLES.get(id);
				return value;
			} else {
				System.out.println("ERROR: ID" + id + " is not declared when getIdValue (func)");
				System.exit(1);
			}
			return -1;
			
		}
		
		public boolean isInVariableMap(String id) {
			return this.VARIABLES.containsKey(id);
		}
		
		public void putNewVariable(String id) {
			this.VARIABLES.put(id, null);
		}
}

class Para {
	private Para pa;
	private String id;
	private int altNo;
	
	void parse() {
		id = Scanner.getID();
		Scanner.nextToken();
		if (Scanner.currentToken().equals("COMMA")) {
			altNo = 1;
			Scanner.nextToken();
			pa = new Para(); 
			pa.parse();
		}
	}
	
	
	void execute(Func fc) {
		int pv = fc.parameter.remove(0);
		fc.VARIABLES.put(id, pv);
		if (altNo == 1) {
			pa.execute(fc);
		}
	}
}

class FuncDS {
	private int altNo = 0; 
	private FuncDS declSeq;
	private FuncDecl decl;
	
	public void parse() {
		decl = new FuncDecl();
		decl.parse();
		if (!Scanner.currentToken().equals("BEGIN")) {
			altNo = 1;
			declSeq = new FuncDS();
			declSeq.parse();
		}
	}
	
	public void execute(Func fc) {
		decl.execute(fc);
		if (altNo == 1) {
			declSeq.execute(fc);
		}
	}
}

class FuncSS {
	private int altNo = 0;
	private FuncSt stmt;
	private FuncSS stmt_seq;
	
	
	public void parse() {
		stmt = new FuncSt();
		stmt.parse();
		String token = Scanner.currentToken();
		if (!token.equals("END") && !token.equals("ENDIF")
				&& !token.equals("ENDWHILE") && !token.equals("ELSE") && !token.equals("RETURN")) {
			altNo = 1;
			stmt_seq = new FuncSS();
			stmt_seq.parse();
		}
	}
	
	public void execute(Func fc) {
		stmt.execute(fc);
		if (altNo == 1) {
			stmt_seq.execute(fc);
		}
	}
}

class FuncDecl {
	private Func_ID_List idls;
	private Func func;
	private int altNo;
	
	public void parse() {
		if (Scanner.currentToken().equals("INT")){
			altNo = 1;
			Scanner.nextToken();
			idls = new Func_ID_List();
			idls.parse();
			Scanner.match("SEMI");
		} else {
			altNo = 2;
			func = new Func();
			Scanner.match("FUN");
			String id = Scanner.getID();
			Scanner.nextToken();
			Func.func_list.put(id, func);
			func.parse();
		}
	}
	
	public void execute(Func fc) {
		if (altNo == 1) {
			idls.execute(fc);
		} 
	}
}


class FuncSt {
	
	private int altNo = 0;
	private FuncAssign assign;
	private FuncIf cond;
	private FuncLoop lp;
	private In input;
	private FuncOut out;
	
	
	public void parse() {
		String token = Scanner.currentToken();
		if (token.contains("ID")) {
			altNo = 1;
			assign = new FuncAssign();
			assign.parse();
		} else if (token.equals("IF")) {
			altNo = 2;
			cond = new FuncIf();
			cond.parse();
		} else if (token.equals("WHILE")){
			altNo = 3;
			lp = new FuncLoop();
			lp.parse();
		} else if (token.equals("INPUT")) {
			altNo = 4;
			input = new In();
			input.parse();
		} else if (token.equals("OUTPUT")) {
			altNo = 5;
			out = new FuncOut();
			out.parse();
		}
	}
	
	public void execute(Func fc) {
		if (altNo == 1) {
			assign.execute(fc);
		} else if (altNo == 2) {
			cond.execute(fc);
		} else if (altNo == 3) {
			lp.execute(fc);
		} else if (altNo == 4) {
			input.execute();
		} else if (altNo == 5) {
			out.execute(fc);
		}
	}
}

class Func_ID_List {
	private int altNo = 0;
	private String id;
	private Func_ID_List idl;
	
	public void parse() {
		id = Scanner.getID();
		Scanner.nextToken();
		if (Scanner.currentToken().equals("COMMA")) {
			altNo = 1;
			Scanner.nextToken();
			idl = new Func_ID_List(); 
			idl.parse();
		}
	}
	
	public void execute(Func fc) {
		if (!fc.isInVariableMap(id)) {
			fc.putNewVariable(id);
		} else {
			System.out.println("ERROR: Variable " + id + " has already been instantiated.");
			System.exit(1);
		}
		if (altNo == 1) {
			idl.execute(fc);
		}
	}
}

class FuncAssign {
	private FuncExpr exp;
	private String id;
	
	public void parse() {
		id = Scanner.getID();
		Scanner.nextToken();
		Scanner.match("ASSIGN");
		exp = new FuncExpr();
		exp.parse();
		Scanner.match("SEMI");
	}
	
	public void execute(Func fc) {
		if (fc.isInVariableMap(id)) {
			int value = exp.execute(fc);
			fc.changeValue(id, value);
		} else {
			System.out.println("ERROR: variable id " + id + " has not been declared");
			System.exit(1);
		}
	}
}

class FuncIf {
	private int altNo = 0;
	private FuncCond cond;
	private FuncSS sts;
	private FuncSS else_sts;
	
	public void parse() {
		Scanner.match("IF");
		cond = new FuncCond(); 
		cond.parse();
		Scanner.match("THEN");
		sts = new FuncSS();
		sts.parse();
		String token = Scanner.currentToken();
		if (token.equals("ELSE")) {
			altNo = 1;
			Scanner.nextToken();
			else_sts = new FuncSS(); 
			else_sts.parse();
		}
		Scanner.match("ENDIF");
		Scanner.match("SEMI");
	}
	
	public void execute(Func fc) {
		if (cond.execute(fc)) {
			sts.execute(fc);
		} else if (altNo == 1) {
			else_sts.execute(fc);
		}
	}
}

class FuncLoop {
	private FuncSS sts;
	private FuncCond cond;
	
	public void parse() {
		Scanner.match("WHILE");
		cond = new FuncCond(); 
		cond.parse();
		Scanner.match("BEGIN");
		sts = new FuncSS();
		sts.parse();
		Scanner.match("ENDWHILE");
		Scanner.match("SEMI");
	}
	
	public void execute(Func fc) {
		while (cond.execute(fc)) {
			sts.execute(fc);
		}
	}
}

class FuncIn {
	private String id;
	
	public void parse() {
		Scanner.match("INPUT");
		id = Scanner.getID();
		Scanner.nextToken();
		Scanner.match("SEMI");
	}
	
	public void execute(Func fc) {
		List<Integer> dt = Scanner.DATA;
		if (dt.size() > 0) {
			int input = dt.remove(0);
			fc.changeValue(id, input);
		} else {
			System.out.println("no more input");
		}
		
	}
}

class FuncCond {
	private int altNo = 0;
	private FuncCmpr cmp;
	private FuncCond cond;
	
	public void parse() {
		if (Scanner.currentToken().equals("NOT")) {
			altNo = 1;
			Scanner.nextToken();
			Scanner.match("LEFT_P");
			cond = new FuncCond();
			cond.parse();
			Scanner.match("RIGHT_P");
		} else {
			altNo = 2;
			cmp = new FuncCmpr();
			cmp.parse();
			if (Scanner.currentToken().equals("OR")) {
				altNo = 3;
				Scanner.nextToken();
				cond = new FuncCond();
				cond.parse();
			}
		}
	}
	
	public boolean execute(Func fc) {
		if (altNo == 1) {
			return (!(cond.execute(fc)));
		} else if (altNo == 2) {
			return cmp.execute(fc);
		} else if (altNo == 3) {
			return (cmp.execute(fc) || cond.execute(fc));
		}
		return false;
	}
}

class FuncCmpr {
	private int altNo;
	private FuncExpr exp1;
	private FuncExpr exp2;
	private String op;
	
	public void parse() {
		exp1 = new FuncExpr();
		exp1.parse();
		String token = Scanner.currentToken();
		if (token.equals("EQUAL") || token.equals("LESS") || token.equals("LESS_EQUAL")) {
			op = token;
			Scanner.nextToken();
		} else {
			System.out.println("ERROR: Expected a comparison operator, found " + token);
			System.exit(1); // Failure Case
		}
		exp2 = new FuncExpr();
		exp2.parse();
		
		
	}
	
	public boolean execute(Func fc) {
		int val1 = exp1.execute(fc);
		int val2 = exp2.execute(fc);
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
class FuncOut {
	
	private FuncExpr exp;
	
	public void parse() {
		Scanner.match("OUTPUT");
		exp = new FuncExpr();
		exp.parse();
		Scanner.match("SEMI");
	}
	
	public void execute(Func fc) {
		int result = exp.execute(fc);
		System.out.println(result);
	}
}

/*
 * class of <expr>
 */
class FuncExpr {
	
	private int altNo;
	private FuncTerm term;
	private FuncExpr exp;
	
	public void parse() {
		term = new FuncTerm(); 
		term.parse();
		String token = Scanner.currentToken();
		if (token.equals("PLUS")) {
			altNo = 1;
			Scanner.nextToken();
			exp = new FuncExpr(); 
			exp.parse();
		} else if (token.equals("MINUS")) {
			altNo = 2;
			Scanner.nextToken();
			exp = new FuncExpr(); 
			exp.parse();
		}
		
	}
	
	public int execute(Func fc) {
		int result = term.execute(fc);
		if (altNo ==1) { //op is +;
			result = result + exp.execute(fc);
		} else if (altNo == 2) {
			result = result - exp.execute(fc);
		}
		
		return result;
	}
	
}

/*
 * class of <term>
 */
class FuncTerm {

	private int altNo;
	private FuncFactor fct; 
	private FuncTerm tm;
	
	
	public void parse() {
		fct = new FuncFactor();
		fct.parse();
		if (Scanner.currentToken().equals("MULTI")) {
			altNo = 1;
			Scanner.nextToken();
			tm = new FuncTerm();
			tm.parse();
		}
	}
	
	public int execute(Func fc) {
		int result = fct.execute(fc);
		if (altNo == 1) {
			result = result * tm.execute(fc);
		}
		return result;
	}
}

/*
 * Class of <factor>
 */
class FuncFactor {
	private int altNo;
	private int value;      // 0 ::= const;
	private String id;      // 1 ::= id;
	private FuncFactor factor;  // 2 ::= -<factor>;
	private FuncExpr exp;
	private Func_Exp_List exl;
	
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
			exp = new FuncExpr(); 
			exp.parse();
			Scanner.match("RIGHT_P");
		} else if (Scanner.currentToken().equals("CALL")){
			altNo = 3;
			Scanner.match("CALL");
			id = Scanner.getID();
			Scanner.nextToken();
			Scanner.match("LEFT_P");
			exl = new Func_Exp_List(); 
			exl.parse();
			Scanner.match("RIGHT_P");
		}
		
	}
	
	public int execute(Func fc) {
		if (altNo == 0) {
			return value;
		} else if (altNo == 1) {
			value = fc.getIdValue(id);
			return value;
		} else if (altNo == 2){
			return exp.execute(fc);
		} else {
			Func another_fc = Func.func_list.get(id);
			exl.execute(fc, another_fc);//move expr list to parameter array
			return another_fc.execute();
		}
	}

}

class Func_Exp_List {
	
	private FuncExpr exp;
	private Func_Exp_List exl;
	int altNo;
	
	void parse() {
		exp = new FuncExpr();
		exp.parse();
		if (Scanner.currentToken().equals("COMMA")) {
			altNo = 1;
			Scanner.nextToken();
			exl = new Func_Exp_List();
			exl.parse();
		}
		
	}

	void execute(Func fc, Func another_fc) {
		int res = exp.execute(fc);
		another_fc.parameter.add(res);
		if (altNo == 1) {
			exl.execute(fc, another_fc);
		}
	}
}
