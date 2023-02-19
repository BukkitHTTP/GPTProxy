package cf.huzpsb.GPTProxy;

import cf.huzpsb.GPTProxy.admin.AdminRouter;
import cf.huzpsb.GPTProxy.auth.AuthManager;
import cf.huzpsb.GPTProxy.commands.CommandManager;
import cf.huzpsb.GPTProxy.routers.GPT2;
import cf.huzpsb.GPTProxy.routers.GPT3;
import nano.http.bukkit.api.BukkitServerProvider;
import nano.http.d2.console.Logger;
import nano.http.d2.core.Response;
import nano.http.d2.utils.Router;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main extends BukkitServerProvider {
    private static String name;
    private static File dir;
    private static String preUri;
    private static Router router = null;

    @Override
    public void onEnable(String name, File dir, String uri) {
        Main.name = name;
        Main.dir = dir;
        Main.preUri = uri;
        if (router == null) {
            router = new Router(null);
            router.add("/gpt2", new GPT2());
            router.add("/gpt3", new GPT3());
            router.add("/admin", new AdminRouter());
        }
        File config = new File(dir, "config.properties");
        if (!config.exists()) {
            throw new RuntimeException("Config file not found.");
        }
        Properties pr = new Properties();
        try {
            pr.load(new FileReader(config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Config.MAX_TEXT_LENGTH = Integer.parseInt(pr.getProperty("max_text_length"));
        Config.HF_TOKEN = pr.getProperty("hf_token");
        Config.OPENAI_TOKEN = pr.getProperty("openai_token");
        Config.MASTER_CODE = pr.getProperty("master_code");
        Config.RATE_LIMIT = Integer.parseInt(pr.getProperty("rate_limit"));
        AuthManager.loadDb();
        CommandManager.registerCommands();
    }

    @Override
    public void onDisable() {
        Logger.info("GPTProxy disabled.");
        AuthManager.closeDb();
        CommandManager.unregisterCommands();
    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        return router.serve(uri, method, header, parms, files);
    }

    @Override
    public Response fallback(String uri, String method, Properties header, Properties parms, Properties files) {
        return null;
    }
}
