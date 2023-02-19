package cf.huzpsb.GPTProxy.auth;

import cf.huzpsb.GPTProxy.Config;

public class Auth {
    public static boolean doAuth(String token) {
        if (token.equals(Config.MASTER_CODE)) {
            return true;
        }
        if (!AuthManager.db.contains(token)) {
            return false;
        }
        AuthUser user = AuthManager.db.query(token);
        user.tokens--;
        if (user.tokens <= 0) {
            AuthManager.db.remove(token);
            return true;
        }
        if (System.currentTimeMillis() - user.lastUse < Config.RATE_LIMIT) {
            return false;
        }
        user.lastUse = System.currentTimeMillis();
        AuthManager.db.set(token, user);
        return true;
    }
}
