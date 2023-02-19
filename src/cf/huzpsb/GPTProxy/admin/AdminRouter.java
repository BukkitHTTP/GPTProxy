package cf.huzpsb.GPTProxy.admin;

import cf.huzpsb.GPTProxy.Config;
import cf.huzpsb.GPTProxy.auth.AuthManager;
import cf.huzpsb.GPTProxy.auth.AuthUser;
import nano.http.d2.consts.Mime;
import nano.http.d2.consts.Status;
import nano.http.d2.core.Response;
import nano.http.d2.serve.ServeProvider;

import java.util.Properties;

public class AdminRouter implements ServeProvider {
    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        if (uri.equals("/" + Config.MASTER_CODE)) {
            Response r = new Response(Status.HTTP_OK, Mime.MIME_DEFAULT_BINARY, AuthManager.db.toCSV(AuthUser.class, "卡号", AdminLocalizer.l));
            r.addHeader("Content-Disposition", "attachment; filename=\"download.csv\"");
            return r;
        }
        return new Response(Status.HTTP_FORBIDDEN, Mime.MIME_DEFAULT_BINARY, "<h1>403 Forbidden</h1>");
    }
}
