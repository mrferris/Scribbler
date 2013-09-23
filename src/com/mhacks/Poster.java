package com.mhacks;

import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Pat on 9/1/13.
 * Edited by Joe on 9/1/13
 */
public class Poster implements Runnable {
    private HashMap<String, Object> _map;
    private String _path;
    private JSONObject _return;
    private String _apiKey;
    private final CountDownLatch _latch;

    public Poster(HashMap<String, Object> map, String path) {
        _map = map;
        _path = path;
        _latch = new CountDownLatch(1);
        _apiKey = "";
    }

    public Poster(HashMap<String, Object> map, String path, String apiKey) {
        _map = map;
        _path = path;
        _latch = new CountDownLatch(1);
        _apiKey = apiKey;
    }

    public JSONObject get() {
        new Thread(this).start();
        try {
            _latch.await();
        } catch (InterruptedException e) {}
        return _return;
    }

    public void run() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                String urlParams = "?";
                HttpClient httpclient = new DefaultHttpClient();
                Iterator it = _map.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry pairs = (HashMap.Entry)it.next();
                    if (!it.hasNext()) {
                        urlParams += pairs.getKey() + "=" + pairs.getValue();
                    } else {
                        urlParams += pairs.getKey() + "=" + pairs.getValue() + "&";
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                }
                String url;
                if (urlParams.equals("?")) {
                    url =  _path;
                } else {
                    url =  _path;
                }
                Log.d("GETTING", url);
                HttpGet httpGet = new HttpGet(url);

                if (!_apiKey.equals("")) {
                    byte[] data = null;
                    try {
                        data = _apiKey.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    String keyBase64 = Base64.encodeToString(data, Base64.DEFAULT);
                    Log.d("YOLO", keyBase64);
                    httpGet.setHeader("Authorization", keyBase64 + ":");
                }

                try {
                    HttpResponse response = httpclient.execute(httpGet);
                    HttpEntity resp = response.getEntity();
                    String responseEntity = EntityUtils.toString(resp);
                    _return = new JSONObject(responseEntity);
                } catch (ClientProtocolException e) {
                } catch (IOException e) {} catch (JSONException e) {
                    e.printStackTrace();
                }
                _latch.countDown();
            }
        });
        thread.start();
    }
}
