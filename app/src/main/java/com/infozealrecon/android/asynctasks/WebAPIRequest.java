package com.infozealrecon.android.asynctasks;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class WebAPIRequest {

    public static String serverResponse = "";
    public static int connectionTimeOut = 10000;
    public static int socketTimeOut = 15000;

    public static String convertStreamToString(InputStream inputStream)
            throws IOException {
        if (inputStream != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                inputStream.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public static String getJsonDataStringFormat(String url, String method, String jObjStr, String tag) {
        InputStream is = null;
        String Root_Response = null;
        HttpResponse httpResponse;
        HttpParams httpParameters = new BasicHttpParams();
        DefaultHttpClient httpClient;
        HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeOut);
        HttpConnectionParams.setSoTimeout(httpParameters, socketTimeOut);
        try {
            httpClient = new DefaultHttpClient(httpParameters);
            url = url.replace(" ", "%20");
            if (method == "POST") {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new StringEntity(jObjStr));
                httpResponse = httpClient.execute(httpPost);
                is = httpResponse.getEntity().getContent();
            } else if (method == "GET") {
                HttpGet httpGet = new HttpGet(new URI(url));
                httpResponse = httpClient.execute(httpGet);
                is = httpResponse.getEntity().getContent();
            }

            Root_Response = convertStreamToString(is);
            LogUtils.Log_e(tag, Root_Response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return Root_Response;
    }

    public static String getJsonDataStringFormat(String url, String method, List<NameValuePair> params, String tag) {

        InputStream is = null;
        String Root_Response = null;
        HttpResponse httpResponse;
        HttpParams httpParameters = new BasicHttpParams();
        DefaultHttpClient httpClient;
        HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeOut);
        HttpConnectionParams.setSoTimeout(httpParameters, socketTimeOut);
        try {
            httpClient = new DefaultHttpClient(httpParameters);
            url = url.replace(" ", "%20");
            if (method == "POST") {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                httpResponse = httpClient.execute(httpPost);
                is = httpResponse.getEntity().getContent();
            } else if (method == "GET") {
                String paramString = URLEncodedUtils.format(params, "UTF-8");
                String urlWithParams = url += "" + paramString;
                HttpGet httpGet = new HttpGet(new URI(urlWithParams));
                httpResponse = httpClient.execute(httpGet);
                is = httpResponse.getEntity().getContent();
            }
            Root_Response = convertStreamToString(is);
            LogUtils.Log_e(tag, Root_Response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return Root_Response;

    }

    public static JSONObject getServerResponseJsonObject(String url, String jObjStr) {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(jObjStr, "utf-8"));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return JSON String
        return jObj;

    }
}
