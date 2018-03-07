# Intepreter

This is a Intepreter for a simple Core Language.
The Language details follow:

<prog> ::= program <decl-seq> begin <stmt-seq> end
<decl-seq> ::= <decl> | <decl><decl-seq>
<stmt-seq> ::= <stmt> | <stmt><stmt-seq>
<decl> ::= int <id-list> ;     <id-list> ::= id | id , <id-list>
<stmt> ::= <assign> | <if> | <loop> | <in> | <out>
<assign> ::= id := <expr> ;
<in> ::= input id ;             <out> ::= output <expr> ;
<if> ::= if <cond> then <stmt-seq> endif ;
 | if <cond> then <stmt-seq> else <stmt-seq> endif ;
<loop> ::= while <cond> begin <stmt-seq> endwhile ;
<cond> ::= <cmpr> | ! ( <cond> )
                 | <cmpr> or <cond>
<cmpr> ::= <expr> = <expr> | <expr> < <expr> 
                 | <expr> <= <expr>
<expr> ::= <term> | <term> + <expr> | <term> – <expr>
<term> ::= <factor> | <factor> * <term>
<factor> ::= const | id | ( <expr> )

