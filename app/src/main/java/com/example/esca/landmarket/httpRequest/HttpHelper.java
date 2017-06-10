package com.example.esca.landmarket.httpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class HttpHelper {

    HashMap<String, String> headers = new HashMap<>();

    /**
     * Default constructor
     */
    public HttpHelper() {
    }

    /**
     * Make an sync Request
     *
     * @param url    Request url
     * @param method Request method
     * @param body   Request body
     * @return Http response object
     */
    public IResponse Request(final String url, final String method, final String body) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        for (String key : this.headers.keySet()) {
            connection.setRequestProperty(key, this.headers.get(key));
        }

        if (!this.headers.containsKey("Content-Type")) {
            connection.setRequestProperty("Content-Type", "application/json");
        }

        connection.setRequestMethod(method);

        connection.setDoInput(true);
//        connection.setReadTimeout(5000);
        if (!method.equals("GET") && !method.equals("DELETE")) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes(), 0, body.length());
        }

        InputStreamReader inputStreamReader;
        int responseCode = connection.getResponseCode();
        if (responseCode >= 300 && responseCode != 303) {
            inputStreamReader = new InputStreamReader(connection.getErrorStream());
        } else {
            inputStreamReader = new InputStreamReader(connection.getInputStream());
        }
        BufferedReader responseStream = new BufferedReader(inputStreamReader);

        StringBuilder responseStringBuilder = new StringBuilder();
        {
            String tmpString;
            while ((tmpString = responseStream.readLine()) != null) {
                responseStringBuilder.append(tmpString);
            }
        }

        return new Response(connection.getResponseCode(), responseStringBuilder.toString());
    }

    /**
     * Make an async Request
     *
     * @param url      Request url
     * @param method   Request method
     * @param body     Request body
     * @param callback Callback which will be called when request is finished
     */
    public void AsyncRequest(final String url, final String method, final String body, final IResponseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.handler(HttpHelper.this.Request(url, method, body));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Easy calling Get Request
     *
     * @param url Request url
     * @return Response object
     */
    public IResponse Get(final String url) throws IOException {
        return this.Request(url, "GET", null);
    }

    public IResponse Delete(final String url) throws IOException {
        return this.Request(url, "DELETE", null);
    }

    public IResponse Post(final String url, final String body) throws IOException {
        return this.Request(url, "POST", body);
    }

    public IResponse Put(final String url, final String body) throws IOException {
        return this.Request(url, "PUT", body);
    }

    public IResponse Patch(final String url, final String body) throws IOException {
        return this.Request(url, "PATCH", body);
    }

    /**
     * Call an async Get Request
     *
     * @param url      Request url
     * @param callback callback
     */
    public void AsyncGet(final String url, final IResponseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.handler(HttpHelper.this.Get(url));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void AsyncDelete(final String url, final IResponseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.handler(HttpHelper.this.Delete(url));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void AsyncPost(final String url, final String body, final IResponseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.handler(HttpHelper.this.Post(url, body));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void AsyncPut(final String url, final String body, final IResponseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.handler(HttpHelper.this.Put(url, body));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void AsyncPatch(final String url, final String body, final IResponseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.handler(HttpHelper.this.Patch(url, body));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public HttpHelper setHeaders(final HashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public String getHeader(final String key) {
        return (this.headers.containsKey(key) ? this.headers.get(key) : null);
    }

    public HttpHelper setHeader(final String key, final String value) {
        if (value == null && this.headers.containsKey(key)) {
            this.headers.remove(key);
        } else if (value != null) {
            this.headers.put(key, value);
        }
        return this;
    }
}
