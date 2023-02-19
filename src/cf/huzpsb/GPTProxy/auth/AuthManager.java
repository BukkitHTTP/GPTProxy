package cf.huzpsb.GPTProxy.auth;

import nano.http.d2.database.NanoDb;

public class AuthManager {
    public static NanoDb<String, AuthUser> db = null;

    public static void loadDb() {
        try {
            db = new NanoDb<>("auth.db", AuthManager.class.getClassLoader());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeDb() {
        try {
            db.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
