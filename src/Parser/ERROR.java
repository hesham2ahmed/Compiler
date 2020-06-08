package Parser;
import Scanner.*;

public abstract class ERROR {
    // syntax error that's generated from the parser or bnf grammar
    public static void syntaxError(Token token){
        throw new IllegalStateException("Could not parse line " + token.getLine() + " at "+ "\"" + token.getToken() + "\"" + " syntax error");
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
