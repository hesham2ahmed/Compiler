package Scanner;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * what you will receive from this class are all tokens in the file you sent to constructor as a string
 */
public class Scanner {
    // array list contains types of tokens and their patterns
    private ArrayList<TokenInfo> tokensTypes;
    //Code of strings to scan
    private String file;
    // counter to track line for each token
    private int counter = 1;
    public Scanner(String file){
        this.tokensTypes = new ArrayList<TokenInfo>();
        this.file = file;
        prepareTokensInfo();
    }

    private void prepareTokensInfo()
    {
        // For identifiers
        tokensTypes.add(new TokenInfo(Pattern.compile("^(([a-zA-Z]([_]|[a-zA-Z0-9])*))"), TokenType.IDENTIFIER));
        // For integer value
        tokensTypes.add(new TokenInfo(Pattern.compile("^((-)?[0-9]+)"), TokenType.INTEGER));
        // For float value
        tokensTypes.add(new TokenInfo(Pattern.compile("^((-)?[0-9]+(.)[0-9]+)"), TokenType.FLOAT));
        // For Char value
        tokensTypes.add(new TokenInfo(Pattern.compile("^(\'(\\w|\\s)\')"), TokenType.CHAR));
        // For Datatype
        tokensTypes.add(new TokenInfo(Pattern.compile("^(int|float|char)"), TokenType.DATATYPE));
        //others
        for(String token : new String[] {">=", "<=", "==", "=", "<", ">", ",","\\(", "\\)", "\\{", "\\}", "if", "else", "!", "\\+", "\\-", "\\/", "\\*", "main", ";"})
            tokensTypes.add(new TokenInfo(Pattern.compile("^(" + token + ")"), TokenType.TOKEN));

    }

    public Token nextToken(){
        // if it's new line so increase counter by 1
        if(file.charAt(0) == '\n')
            this.counter +=1;

        // remove space and new line from first and end of string
        file= file.trim();

        //loop over RE to find or match current token
        for(TokenInfo tokenInfo : tokensTypes)
        {
            Matcher matcher = tokenInfo.getPattern().matcher(file);

            // if it's matches then remove this token from string file or code
            if(matcher.find()){
                String  token = matcher.group();
                file = matcher.replaceFirst("");

                //Prepare tokens to parser or next phase
               return PrepareToken(tokenInfo, token);
            }
        }

        throw new IllegalStateException("Could not parse line " + counter);
    }

    /**
     * @param tokenInfo
     * @param token
     * @return Token
     */
    private Token PrepareToken(TokenInfo tokenInfo, String token){
        //correct token type
        if(token.equals("int") || token.equals("char") ||token.equals("float"))
           return new Token(token, tokenInfo.getPattern(), TokenType.DATATYPE, counter);

        if(token.equals("if") || token.equals("else") ||token.equals("main"))
            return new Token(token, tokenInfo.getPattern(), TokenType.TOKEN, counter);

        //check if token is char then skip first and last char ''
        if(tokenInfo.getTokenType() == TokenType.CHAR)
            return  new Token(token.substring(1, token.length() - 1), tokenInfo.getPattern(), tokenInfo.getTokenType(), counter);

        return new Token(token, tokenInfo.getPattern(), tokenInfo.getTokenType(), counter);
    }

    public boolean hasNestToken(){
        return !this.file.isEmpty();
    }
}
