package Scanner;

import java.util.regex.Pattern;

public class Token {
    private String token;
    private Pattern pattern;
    private TokenType type;
    public  Token(String token,Pattern pattern,TokenType type)
    {
        this.pattern = pattern;
        this.token = token;
        this.type = type;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getToken() {
        return token;
    }

    public TokenType getType() {
        return type;
    }

}
