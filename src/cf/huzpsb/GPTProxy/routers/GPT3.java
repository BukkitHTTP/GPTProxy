package cf.huzpsb.GPTProxy.routers;

import cf.huzpsb.GPTProxy.Config;
import cf.huzpsb.GPTProxy.auth.Auth;
import nano.http.d2.console.Logger;
import nano.http.d2.consts.Mime;
import nano.http.d2.consts.Status;
import nano.http.d2.core.Response;
import nano.http.d2.serve.ServeProvider;
import nano.http.d2.utils.Encoding;
import nano.http.d2.utils.Request;

import java.util.Properties;

public class GPT3 implements ServeProvider {
    private static String request(String prompt) throws Exception {
        String apiUrl = "https://api.openai.com/v1/engines/text-davinci-003/completions";
        String requestBody = "{\"prompt\": \"" + prompt + "\", \"max_tokens\": " + Config.MAX_TEXT_LENGTH + ", \"temperature\": 0.9, \"n\": 1}";
        Properties pr = new Properties();
        pr.setProperty("Authorization", "Bearer " + Config.OPENAI_TOKEN);
        String response = Request.jsonPost(apiUrl, requestBody, pr);
        return response.split("text\":\"")[1].split("\",\"index")[0];
    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        String[] split = uri.substring(1).split("/");
        if (split.length != 2) {
            return new Response(Status.HTTP_BADREQUEST, Mime.MIME_PLAINTEXT, "Error: Invalid request!");
        }
        if (!Auth.doAuth(split[0])) {
            return new Response(Status.HTTP_FORBIDDEN, Mime.MIME_PLAINTEXT, "Error: Invalid token!");
        }
        String req = Encoding.deBase64(Encoding.deURL(split[1]));
        Logger.info("GPT-3 Request: " + req);
        try {
            String ret = request(req);
            Logger.info("GPT-3 Response: " + ret);
            return new Response(Status.HTTP_OK, Mime.MIME_PLAINTEXT, Encoding.enBase64(ret));
        } catch (Throwable t) {
            String qwq = t.toString();
            if (qwq.contains("openai")) {
                return new Response(Status.HTTP_INTERNALERROR, Mime.MIME_PLAINTEXT, "Error: Server overloaded!");
            }
            return new Response(Status.HTTP_INTERNALERROR, Mime.MIME_PLAINTEXT, "Error: " + t);
        }
    }
}
