package Scanner;

import java.util.regex.Pattern;

public class TokenInfo {
    private Pattern pattern;
    private TokenType tokenType;
    public TokenInfo(Pattern pattern, TokenType tokenType){
        this.pattern = pattern;
        this.tokenType = tokenType;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
}
