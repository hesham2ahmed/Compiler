package Parser;
import Scanner.*;
import sun.security.krb5.internal.PAData;

public abstract class ERROR {
    // syntax error that's generated from the parser or bnf grammar
    public static void syntaxError(Token token){
        if(Parser.expecting.length() > 2 )
        {
            if(Parser.expecting.equals("stmt_seq2"))
                Parser.expecting = "}";
            else if (Parser.expecting.equals("else_part"))
                Parser.expecting = "if or variable or datatype or }";
            else if (Parser.expecting.equals("condition2"))
                Parser.expecting = ")";
            else if (Parser.expecting.equals("exp2"))
                Parser.expecting = ") or comparision sign or ;";
            else if (Parser.expecting.equals("term2"))
                Parser.expecting = ") or comparision sign or ; or + or -";
            else if (Parser.expecting.equals("x_stmt"))
                Parser.expecting = ";";
        }
        throw new IllegalStateException("Could not parse line " + token.getLine() + " at "+
                "\"" + token.getToken() + "\"" + " expected " + Parser.expecting + " (syntax error)");
    }
    // if the identifier is not defined before and user is using it.
    public static void notDefinedError(Token token){
        throw new IllegalStateException("Could not parse line " + token.getLine() +" \"" + token.getToken() + "\"" + " is not defined");
    }
    // if the identifier is already exist in the memory(dictionary) and user is declaring it again.
    public static void definedError(Token token){
        throw new IllegalStateException("Could not parse line " + token.getLine() +" \"" + token.getToken() + "\"" + " is already defined");
    }

}
