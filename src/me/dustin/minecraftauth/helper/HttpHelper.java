package me.dustin.minecraftauth.helper;

import com.google.gson.JsonObject;
import me.dustin.minecraftauth.account.MinecraftAccount;
import me.dustin.minecraftauth.proxy.LoginProxy;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpHelper {


    public static boolean login(MinecraftAccount minecraftAccount, LoginProxy proxy) throws IOException {
        if (minecraftAccount.getAccountType() == MinecraftAccount.AccountType.MOJANG) {
            return loginMojang(minecraftAccount.getEmail(), minecraftAccount.getPassword(), proxy).toLowerCase().contains("accesstoken");
        } else {
            return loginMSA(minecraftAccount.getEmail(), minecraftAccount.getPassword(), proxy) != null;
        }
    }

    private static String loginMojang(String email, String password, LoginProxy proxy) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("agent", "Minecraft");
        jsonObject.addProperty("username", email);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("requestUser", true);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        return loginRequest("https://authserver.mojang.com/authenticate", jsonObject.toString(), header, proxy);
    }

    //for some reason it doesn't seem to work even tho this is the same exact thing I do for Jex with Microsoft login
    //issue seems to be in the second HttpURLConnection being made, where it checks if it gets redirected
    //just see if we can get a login code with username and password, going through the whole process doesn't matter as long as we get that
    private static String loginMSA(String email, String password, LoginProxy loginProxy) throws IOException {
        Proxy proxy = new Proxy(loginProxy.getType(), new InetSocketAddress(loginProxy.getIp(), loginProxy.getPort()));
        String loginPPFT;
        String loginUrl;
        URL url = new URL("https://login.live.com/oauth20_authorize.srf?redirect_uri=https://login.live.com/oauth20_desktop.srf&scope=service::user.auth.xboxlive.com::MBI_SSL&display=touch&response_type=code&locale=en&client_id=00000000402b5328");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
        InputStream inputStream = httpURLConnection.getResponseCode() == 200 ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream();

        String loginCookie = httpURLConnection.getHeaderField("set-cookie");

        String responseData = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
        Matcher bodyMatcher = Pattern.compile("sFTTag:[ ]?'.*value=\"(.*)\"/>'").matcher(responseData);
        if (bodyMatcher.find()) {
            loginPPFT = bodyMatcher.group(1);
        } else {
            return null;
        }

        bodyMatcher = Pattern.compile("urlPost:[ ]?'(.+?(?='))").matcher(responseData);
        if (bodyMatcher.find()) {
            loginUrl = bodyMatcher.group(1);
        } else {
            return null;
        }

        if (loginCookie == null || loginPPFT == null || loginUrl == null) {
            return null;
        }

        Map<String, String> requestData = new HashMap<>();

        requestData.put("login", email);
        requestData.put("loginfmt", email);
        requestData.put("passwd", password);
        requestData.put("PPFT", loginPPFT);

        String postData = encodeURL(requestData);

        byte[] data = postData.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection connection = (HttpURLConnection) new URL(loginUrl).openConnection(proxy);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(data.length));
        connection.setRequestProperty("Cookie", loginCookie);
        connection.setConnectTimeout(10 * 1000);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(data);
        }

        if (connection.getResponseCode() != 200 || connection.getURL().toString().equals(loginUrl)) {
            return null;
        }

        Pattern pattern = Pattern.compile("[?|&]code=([\\w.-]+)");

        Matcher tokenMatcher = pattern.matcher(URLDecoder.decode(connection.getURL().toString(), StandardCharsets.UTF_8.name()));
        if (tokenMatcher.find()) {
            return tokenMatcher.group(1);
        }
        return null;
    }

    private static String loginRequest(String url, String jsonData, Map<String, String> headers, LoginProxy proxy) throws IOException {
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setBufferSize(4128).build();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultConnectionConfig(connectionConfig).build();

        if (proxy.getType() == Proxy.Type.SOCKS) {
            Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create().register("https", new MyConnectionSocketFactory(SSLContexts.createSystemDefault())).build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
            httpclient = HttpClients.custom().setConnectionManager(cm).build();
        }
        HttpPost post = new HttpPost(url);
        StringEntity postingString = new StringEntity(jsonData);
        post.setEntity(postingString);
        headers.forEach(post::setHeader);

        RequestConfig.Builder config = RequestConfig.custom();
        config.setConnectionRequestTimeout(10000).setConnectTimeout(10000);
        if (proxy.getType() == Proxy.Type.HTTP)
            config.setProxy(new HttpHost(proxy.getIp(), proxy.getPort()));
        post.setConfig(config.build());

        CloseableHttpResponse response;
        if (proxy.getType() == Proxy.Type.SOCKS) {
            InetSocketAddress socksaddr = new InetSocketAddress(proxy.getIp(), proxy.getPort());
            HttpClientContext context = HttpClientContext.create();
            context.setAttribute("socks.address", socksaddr);
            response = httpclient.execute(post, context);
        } else {
            response = httpclient.execute(post);
        }
        int statusCode = response.getStatusLine().getStatusCode();
        httpclient.close();
        if (statusCode >= 200 && statusCode < 300) {
            BufferedReader input = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder buffer = new StringBuilder();
            for (String line; (line = input.readLine()) != null; ) {
                buffer.append(line);
                buffer.append("\n");
            }
            input.close();
            return buffer.toString();
        }

        return "";
    }

    public static String readURL(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10 * 1000);
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder buffer = new StringBuilder();
        for (String line; (line = input.readLine()) != null; ) {
            buffer.append(line);
            buffer.append("\n");
        }
        input.close();
        return buffer.toString();
    }

    private static String encodeURL(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    URLEncoder.encode(entry.getKey(), "UTF-8"),
                    URLEncoder.encode(entry.getValue(), "UTF-8")
            ));
        }
        return sb.toString();
    }

    //for SOCKS proxies
    static class MyConnectionSocketFactory extends SSLConnectionSocketFactory {

        public MyConnectionSocketFactory(final SSLContext sslContext) {
            super(sslContext);
        }

        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }

    }
}
