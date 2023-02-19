package cf.huzpsb.GPTProxy.auth;

import java.io.Serializable;

public class AuthUser implements Serializable {
    private static final long serialVersionUID = 123698745L;
    public int tokens;
    public long lastUse;

    public AuthUser(int tokens) {
        this.tokens = tokens;
        this.lastUse = 0;
    }
}
