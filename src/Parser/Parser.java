package Parser;

import Scanner.*;
import com.sun.org.apache.xpath.internal.objects.XString;
import javafx.util.Pair;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Dictionary;

public class Parser {
    private final Scanner scanner;
    private Stack<String> stack;
    private Token cur_token;
    private Dictionary<String, String> dictionary;
    public static String expecting;
    private boolean flag = true;

    // constructor
    public Parser(Scanner scanner){
        this.scanner = scanner;
        this.stack = new Stack<String>();
        this.dictionary = new Hashtable<>();
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
        stack.push("}");
        // program is the start point
        stack.push("program");
        stack.push("{");
        stack.push(")");
        stack.push("(");
        stack.push("main");
        stack.push("int");
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
            System.out.println("SUCCESS");
            stack.pop();
            return;
        }
        else if (cur_token.getType() == TokenType.DATATYPE && (stack.peek().equals("float") || stack.peek().equals("char") || stack.peek().equals("int")))
        {
            cur_token = scanner.nextToken();
            // add a new identifier to the dictionary
            putNewIdentifier(cur_token.getToken(), stack.peek(), cur_token);
            stack.pop();
        }
        // cases
        else if (cur_token.getToken().equals(stack.peek()))
        {
            cur_token = scanner.nextToken();
            stack.pop();
        } else if (cur_token.getType() == TokenType.IDENTIFIER && stack.peek().equals("id"))
        {
            // check if the identifier is declared or not before if not then that's an error
            if(!checkIdentifier(cur_token))
                ERROR.notDefinedError(cur_token);
            cur_token = scanner.nextToken();
            stack.pop();
        } else if ((cur_token.getType() == TokenType.INTEGER || cur_token.getType() == TokenType.CHAR || cur_token.getType() == TokenType.FLOAT)
                && stack.peek().equals("value"))
        {
            cur_token = scanner.nextToken();
            stack.pop();
        }
        else {
            //System.out.println(stack.peek() +" " +cur_token.getToken());
            try {
                // getting the top of the stack to expect the error
                expecting = stack.peek();
                // use the name of expression to call the appropriate function
                Parser.class.getMethod(stack.pop()).invoke(this);
            } catch (Exception e) {
                // catch error in the code
                ERROR.syntaxError(cur_token);
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
            ERROR.syntaxError(cur_token);
    }
    //2-
    public void stmt_seq(){
        if(cur_token.getToken().equals("if") || cur_token.getType() == TokenType.IDENTIFIER ||
                cur_token.getType() == TokenType.DATATYPE) {
            stack.push("stmt_seq2");
            stack.push("stmt");
        }
        else
            ERROR.syntaxError(cur_token);
    }
    //3-
    public void stmt_seq2(){
        if(!cur_token.getToken().equals("if") && cur_token.getType() != TokenType.IDENTIFIER &&
                cur_token.getType() != TokenType.DATATYPE && !cur_token.getToken().equals("}") && !cur_token.getToken().equals("$"))
            ERROR.syntaxError(cur_token);
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
            ERROR.syntaxError(cur_token);
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
            ERROR.syntaxError(cur_token);
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
        || cur_token.getType() == TokenType.DATATYPE || cur_token.getToken().equals("$") || cur_token.getToken().equals("}"))
            Epsilon();
        else
            ERROR.syntaxError(cur_token);
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
            ERROR.syntaxError(cur_token);
    }
    //8-
    public void condition2(){
        if(cur_token.getToken().equals(")"))
            Epsilon();
        else if(cur_token .getToken().equals("<")  ||
                cur_token .getToken().equals(">")  ||
                cur_token .getToken().equals("!=")  ||
                cur_token .getToken().equals("<=") ||
                cur_token .getToken().equals(">=") ||
                cur_token .getToken().equals("==") )
        {
            stack.push("exp");
            stack.push("comp_sign");
        }
        else
            ERROR.syntaxError(cur_token);

    }
    //9-
    public void comp_sign(){
        if(cur_token.getToken().equals("<"))
            stack.push("<");
        else if (cur_token.getToken().equals(">"))
            stack.push(">");
        else if (cur_token.getToken().equals("!="))
            stack.push("!=");
        else if (cur_token.getToken().equals("<="))
            stack.push("<=");
        else if (cur_token.getToken().equals(">="))
            stack.push(">=");
        else if (cur_token.getToken().equals("=="))
            stack.push("==");
        else
            ERROR.syntaxError(cur_token);
    }
    //10-
    public void exp() {
        if (cur_token.getType() == TokenType.IDENTIFIER
                || cur_token.getToken().equals("(")
                || cur_token.getType() == TokenType.INTEGER
                || cur_token.getType() == TokenType.FLOAT
                || cur_token.getType() == TokenType.CHAR) {
            stack.push("exp2");
            stack.push("term");
        }
        else
            ERROR.syntaxError(cur_token);
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
                cur_token.getToken().equals("!=") ||
                cur_token.getToken().equals("<=")||
                cur_token.getToken().equals(">=")||
                cur_token.getToken().equals("==")||
                cur_token.getToken().equals(";")) {
            Epsilon();
        }
        else
            ERROR.syntaxError(cur_token);
    }
    //12-
    public void add_op(){
        if(cur_token.getToken().equals("+"))
            stack.push("+");
        else if(cur_token.getToken().equals("-"))
            stack.push("-");
        else
            ERROR.syntaxError(cur_token);
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
            ERROR.syntaxError(cur_token);
    }
    //14-
    public void term2(){
        if(cur_token.getToken().equals("*") || cur_token.getToken().equals("/")){
            stack.push("term2");
            stack.push("factor");
            stack.push("mul_op");
        } else if(cur_token.getToken().equals("<")||
                cur_token.getToken().equals(">")||
                cur_token.getToken().equals("!=")||
                cur_token.getToken().equals("<=")||
                cur_token.getToken().equals(">=")||
                cur_token.getToken().equals("==")||
                cur_token.getToken().equals(";")||
                cur_token.getToken().equals("+")||
                cur_token.getToken().equals(")")||
                cur_token.getToken().equals("-"))
            Epsilon();

        else
            ERROR.syntaxError(cur_token);
    }
    //15-
    public void mul_op(){
        if(cur_token.getToken().equals("*"))
            stack.push("*");
        else if(cur_token.getToken().equals("/"))
            stack.push("/");
        else
            ERROR.syntaxError(cur_token);
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
            ERROR.syntaxError(cur_token);
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
            ERROR.syntaxError(cur_token);
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
            ERROR.syntaxError(cur_token);
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
            ERROR.syntaxError(cur_token);
    }
    //20-
    public void datatype(){
        if(cur_token.getToken().equals("int"))
            stack.push("int");
        else if(cur_token.getToken().equals("float"))
            stack.push("float");
        else if(cur_token.getToken().equals("char"))
            stack.push("char");
        else
            ERROR.syntaxError(cur_token);
    }

    /**
     * check if the identifier is exist or not
     */
    private boolean checkIdentifier(Token token){
        if(dictionary.get(token.getToken()) == null)
           return false;
        return true;
    }

    private void putNewIdentifier(String id, String datatype, Token token){
        if(!checkIdentifier(token)){
            dictionary.put(id, datatype);
        }
        else
            ERROR.definedError(token);
    }
    /**
     * just function to do nothing
     */
    public void Epsilon(){
    }
}
