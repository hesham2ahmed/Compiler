package Parser;

import Scanner.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

public class Parser {
    private final Scanner scanner;
    private Stack<String> stack;
    private Token cur_token;

    // constructor
    public Parser(Scanner scanner){
        this.scanner = scanner;
        this.stack = new Stack<String>();
    }

    /**
     * function to start parsing the code
     */
    public void start() {
        // check if the code is empty or not
        if(!scanner.hasNestToken())
            return;
        // get the first token
        cur_token = scanner.nextToken();
        stack.push("$");
        // program is the start point
        stack.push("program");
        while(!stack.empty()){
            match();
        }
    }

    /**
     * function to decide what next or what non terminal is gonna to release
     */
    private void match(){
        // this is correct case where stack has only $ and last token is $
        if(stack.peek().equals("$") && cur_token.getToken().equals("$")){
            System.out.println("CORRECT");
            stack.pop();
            return;
        }
        // cases
        else if (cur_token.getToken().equals(stack.peek()))
        {
            cur_token = scanner.nextToken();
            stack.pop();
        } else if (cur_token.getType() == TokenType.IDENTIFIER && stack.peek().equals("id"))
        {
            cur_token = scanner.nextToken();
            stack.pop();
        } else if ((cur_token.getType() == TokenType.INTEGER || cur_token.getType() == TokenType.CHAR || cur_token.getType() == TokenType.FLOAT)
                && stack.peek().equals("value"))
        {
            cur_token = scanner.nextToken();
            stack.pop();
        }else if (cur_token.getType() == TokenType.DATATYPE && (stack.peek().equals("int") || stack.peek().equals("char") || stack.peek().equals("float")))
        {
            cur_token = scanner.nextToken();
            stack.pop();
        }
        else {
            try {
                // use the name of expression to call the appropriate function
                Parser.class.getMethod(stack.pop()).invoke(this);
            } catch (Exception e) {
                // catch error in the code
                detectError(cur_token.getLine(), cur_token.getToken());
            }
        }
    }

    /**
     * these all the non terminals in the grammar and each non terminal has release of terminal or not
     */
    //1-
    public void program(){
        if(cur_token.getToken().equals("if") || cur_token.getType() == TokenType.IDENTIFIER ||
        cur_token.getType() == TokenType.DATATYPE)
            stack.push("stmt_seq");
        else
            detectError(cur_token.getLine(), cur_token.getToken());
    }
    //2-
    public void stmt_seq(){
        if(cur_token.getToken().equals("if") || cur_token.getType() == TokenType.IDENTIFIER ||
                cur_token.getType() == TokenType.DATATYPE) {
            stack.push("stmt_seq2");
            stack.push("stmt");
        }
        else
            detectError(cur_token.getLine(), cur_token.getToken());
    }
    //3-
    public void stmt_seq2(){
        if(!cur_token.getToken().equals("if") && cur_token.getType() != TokenType.IDENTIFIER &&
                cur_token.getType() != TokenType.DATATYPE && !cur_token.getToken().equals("}") && !cur_token.getToken().equals("$"))
            detectError(cur_token.getLine(), cur_token.getToken());
        else if(cur_token.getToken().equals("}") || cur_token.getToken().equals("$"))
            Epsilon();
        else
            stack.push("stmt_seq");
    }
    //4-
    public void stmt(){
        if(cur_token.getToken().equals("if"))
            stack.push("if_stmt");
        else if(cur_token.getType() == TokenType.IDENTIFIER)
            stack.push("assign_stmt");
        else if (cur_token.getType() == TokenType.DATATYPE)
            stack.push("declare_stmt");
        else
            detectError(cur_token.getLine(), cur_token.getToken());
    }
    //5-
    public void if_stmt(){
        if(cur_token.getToken().equals("if")) {
            stack.push("else_part");
            stack.push("}");
            stack.push("stmt_seq2");
            stack.push("{");
            stack.push(")");
            stack.push("condition");
            stack.push("(");
            stack.push("if");
        }
        else
            detectError(cur_token.getLine(),cur_token.getToken());
    }
    //6-
    public void else_part(){
        if(cur_token.getToken().equals("else")){
            stack.push("}");
            stack.push("stmt_seq2");
            stack.push("{");
            stack.push("else");
        }
        else if (cur_token.getToken().equals("if") || cur_token.getType() == TokenType.IDENTIFIER
        || cur_token.getType() == TokenType.DATATYPE || cur_token.getToken().equals("$"))
            Epsilon();
        else
            detectError(cur_token.getLine(),cur_token.getToken());
    }
    //7-
    public void condition(){
        if(cur_token.getType() == TokenType.IDENTIFIER ||
                cur_token.getType() == TokenType.INTEGER || cur_token.getType() == TokenType.FLOAT
        || cur_token.getType() == TokenType.CHAR || cur_token.getToken().equals("(") ) {
            stack.push("condition2");
            stack.push("exp");
        }
        else
            detectError(cur_token.getLine(),cur_token.getToken());
    }
    //8-
    public void condition2(){
        if(cur_token.getToken().equals(")"))
            Epsilon();
        else if(cur_token .getToken().equals("<")  ||
                cur_token .getToken().equals(">")  ||
                cur_token .getToken().equals("=")  ||
                cur_token .getToken().equals("<=") ||
                cur_token .getToken().equals(">=") ||
                cur_token .getToken().equals("==") )
        {
            stack.push("exp");
            stack.push("comp_sign");
        }
        else
            detectError(cur_token.getLine(),cur_token.getToken());
    }
    //9-
    public void comp_sign(){
        if(cur_token.getToken().equals("<"))
            stack.push("<");
        else if (cur_token.getToken().equals(">"))
            stack.push(">");
        else if (cur_token.getToken().equals("="))
            stack.push("=");
        else if (cur_token.getToken().equals("<="))
            stack.push("<=");
        else if (cur_token.getToken().equals(">="))
            stack.push(">=");
        else if (cur_token.getToken().equals("=="))
            stack.push("==");
        else
            detectError(cur_token.getLine(),cur_token.getToken());
    }
    //10-
    public void exp() {
        if (cur_token.getType() == TokenType.IDENTIFIER
                || cur_token.getToken().equals(")")
                || cur_token.getType() == TokenType.INTEGER
                || cur_token.getType() == TokenType.FLOAT
                || cur_token.getType() == TokenType.CHAR) {
            stack.push("exp2");
            stack.push("term");
        }
        else
            detectError(cur_token.getLine(),cur_token.getToken());
    }
    //11-
    public void exp2(){
        if(cur_token.getToken().equals("+") | cur_token.getToken().equals("-")) {
            stack.push("exp2");
            stack.push("term");
            stack.push("add_op");
        }
        else if(cur_token.getToken().equals(")") ||
                cur_token.getToken().equals("<") ||
                cur_token.getToken().equals(">") ||
                cur_token.getToken().equals("=") ||
                cur_token.getToken().equals("<=")||
                cur_token.getToken().equals(">=")||
                cur_token.getToken().equals("==")||
                cur_token.getToken().equals(";")) {
            Epsilon();
        }
        else
            detectError(cur_token.getLine(),cur_token.getToken());
    }
    //12-
    public void add_op(){
        if(cur_token.getToken().equals("+"))
            stack.push("+");
        else if(cur_token.getToken().equals("-"))
            stack.push("-");
        else
            detectError(cur_token.getLine(),cur_token.getToken());
    }
    //13-
    public void term(){
        if(cur_token.getToken().equals("(") ||
           cur_token.getType() == TokenType.INTEGER ||
           cur_token.getType() == TokenType.FLOAT ||
           cur_token.getType() == TokenType.CHAR ||
           cur_token.getType() == TokenType.IDENTIFIER) {
            stack.push("term2");
            stack.push("factor");
        }
        else
            detectError(cur_token.getLine(),cur_token.getToken());
    }
    //14-
    public void term2(){
        if(cur_token.getToken().equals("*") || cur_token.getToken().equals("/")){
            stack.push("term2");
            stack.push("factor");
            stack.push("mul_op");
        } else if(cur_token.getToken().equals("<")||
                cur_token.getToken().equals(">")||
                cur_token.getToken().equals("=")||
                cur_token.getToken().equals("<=")||
                cur_token.getToken().equals(">=")||
                cur_token.getToken().equals("==")||
                cur_token.getToken().equals(";")||
                cur_token.getToken().equals("+")||
                cur_token.getToken().equals(")")||
                cur_token.getToken().equals("-"))
        {
            Epsilon();
        }
        else
            detectError(cur_token.getLine(), cur_token.getToken());
    }
    //15-
    public void mul_op(){
        if(cur_token.getToken().equals("*"))
            stack.push("*");
        else if(cur_token.getToken().equals("/"))
            stack.push("/");
        else
            detectError(cur_token.getLine(),cur_token.getToken());
    }
    //16-
    public void factor(){
        if (cur_token.getToken().equals("(")){
            stack.push(")");
            stack.push("exp");
            stack.push("(");
        }
        else if(cur_token.getType() == TokenType.IDENTIFIER)
            stack.push("id");
        else if(cur_token.getType() == TokenType.CHAR ||
                cur_token.getType() == TokenType.INTEGER ||
                cur_token.getType() == TokenType.FLOAT) {
            stack.push("value");
        }
        else
            detectError(cur_token.getLine(), cur_token.getToken());
    }
    //17-
    public void declare_stmt(){
        if(cur_token.getType() == TokenType.DATATYPE) {
            stack.push(";");
            stack.push("x_stmt");
            stack.push("id");
            stack.push("datatype");
        }
        else
            detectError(cur_token.getLine(), cur_token.getToken());
    }
    //18-
    public void x_stmt(){
        if(cur_token.getToken().equals("=")){
            stack.push("exp");
            stack.push("=");
        }
        else if(cur_token.getToken().equals(";"))
            Epsilon();
        else
            detectError(cur_token.getLine(), cur_token.getToken());
    }
    //19-
    public void assign_stmt(){
        if(cur_token.getType() == TokenType.IDENTIFIER) {
            stack.push(";");
            stack.push("exp");
            stack.push("=");
            stack.push("id");
        }
        else
            detectError(cur_token.getLine(), cur_token.getToken() );
    }
    //20-
    public void datatype(){
        if(cur_token.getToken().equals("int"))
            stack.push("int");
        else if(cur_token.getToken() == "float")
            stack.push("float");
        else if(cur_token.getType().equals("char"))
            stack.push("char");
        else
            detectError(cur_token.getLine(), cur_token.getToken());
    }

    /**
     * print the error with line and token
     * @param line as string
     * @param token as object
     */
    private void detectError(int line, String token){
        throw new IllegalStateException("Could not parse line " + line + " for " + token);
    }

    /**
     * just function to do nothing
     */
    public void Epsilon(){
    }
}
