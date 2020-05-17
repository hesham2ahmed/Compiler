package Scanner;

import java.util.regex.Pattern;

public class Token {
    private String token;
    private Pattern pattern;
    private TokenType type;
    private int line;
    public  Token(String token, Pattern pattern, TokenType type, int line)
    {
        this.pattern = pattern;
        this.token = token;
        this.type = type;
        this.line = line;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getLine() {
        return line;
    }

    public String getToken() {
        return token;
    }

    public TokenType getType() {
        return type;
    }

}
