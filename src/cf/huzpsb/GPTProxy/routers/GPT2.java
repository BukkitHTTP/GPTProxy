package cf.huzpsb.GPTProxy.routers;

import cf.huzpsb.GPTProxy.Config;
import cf.huzpsb.GPTProxy.auth.Auth;
import nano.http.d2.console.Logger;
import nano.http.d2.consts.Mime;
import nano.http.d2.consts.Status;
import nano.http.d2.core.Response;
import nano.http.d2.serve.ServeProvider;
import nano.http.d2.utils.Encoding;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class GPT2 implements ServeProvider {
    public static String request(String text) throws Exception {
        URL url = new URL("https://api-inference.huggingface.co/models/uer/gpt2-chinese-cluecorpussmall");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + Config.HF_TOKEN);
        con.setDoOutput(true);
        con.getOutputStream().write(text.getBytes(StandardCharsets.UTF_8));
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sbf = new StringBuilder();
        String temp;
        while ((temp = br.readLine()) != null) {
            sbf.append(temp);
            sbf.append("\r\n");
        }
        return sbf.toString().replace(" ", "").replace("[{\"generated_text\":\"", "").replace("\"}]", "") + Config.SUFFIX;
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
        Logger.info("GPT-2 Request: " + req);
        try {
            String ret = GPT2.request(req);
            Logger.info("GPT-2 Response: " + ret);
            return new Response(Status.HTTP_OK, Mime.MIME_PLAINTEXT, Encoding.enBase64(ret));
        } catch (Throwable t) {
            String qwq = t.toString();
            if (qwq.contains("50")) {
                return new Response(Status.HTTP_INTERNALERROR, Mime.MIME_PLAINTEXT, "Error: Server overloaded!");
            }
            return new Response(Status.HTTP_INTERNALERROR, Mime.MIME_PLAINTEXT, "Error: " + t);
        }
    }
}
