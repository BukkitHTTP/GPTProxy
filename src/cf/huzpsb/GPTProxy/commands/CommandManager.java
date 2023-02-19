package cf.huzpsb.GPTProxy.commands;

import cf.huzpsb.GPTProxy.auth.AuthManager;
import cf.huzpsb.GPTProxy.auth.AuthUser;
import nano.http.d2.console.Console;
import nano.http.d2.console.Logger;

import java.util.Random;

public class CommandManager {
    private static final Random rdm = new Random();

    public static void registerCommands() {
        Console.register("create", () -> {
            Logger.info("Please type the tokens of the new user");
            int i = Integer.parseInt(Console.await());
            String p = "gpt_" + (10000 + rdm.nextInt(89999));
            AuthManager.db.set(p, new AuthUser(i));
            Logger.info("Created user : " + p);
        });
        Console.register("ban", () -> {
            Logger.info("Please type the token of the user to delete");
            String p = Console.await();
            if (AuthManager.db.contains(p)) {
                AuthManager.db.remove(p);
                Logger.info("Deleted user : " + p);
            }
            Logger.warning("User not found.");
        });
    }

    public static void unregisterCommands() {
        Console.unregister("create");
        Console.unregister("ban");
    }
}
