package com.fan.common.common.util.http;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HttpUtil {

    /**
     * 获取html页面源码
     *
     * @param urlStr
     * @return
     */
    public static String getHtml(String urlStr) {
        try {
            URL url = new URL(urlStr);
            InputStream inputStream = url.openStream();
            byte[] arr = new byte[inputStream.available()];
            inputStream.read(arr);
            inputStream.close();
            return new String(arr, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url    发送请求的 URL
     * @param params 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String params) throws IOException {
        PrintWriter out = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(params); // 输入post请求的参数
            out.flush();
            Scanner scanner = new Scanner(conn.getInputStream(), "utf-8");
            while (scanner.hasNext()) {
                result.append(scanner.nextLine());
            }
            scanner.close();
            return result.toString();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * @param url
     * @param params 请求参数应该是 name1=value1&name2=value2 的形式。 如果是复合对象就val1.val2=xx
     */
    public static String post(String url, String params) {
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3765.400 QQBrowser/10.6.4143.400");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            try (PrintWriter out = new PrintWriter(conn.getOutputStream())) {
                out.print(params); // 输入post请求的参数
                out.flush();
            }
            Scanner scanner = new Scanner(conn.getInputStream(), "utf-8");
            while (scanner.hasNext())
                result.append(scanner.nextLine());
            scanner.close();
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String postJson(String url, String jsonStr) {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        try {
            //此处应设定参数的编码格式，不然中文会变乱码
            StringEntity s = new StringEntity(jsonStr, "UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            post.setEntity(s);
            post.addHeader("content-type", "application/json");
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(res.getEntity());// 返回json格式
                System.out.println("推送成功" + result);
                return result;
            } else {
                System.out.println("推送失败");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "fail";
    }
}