package com.test.yongheshen.httpsdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvInfo;
    private static  final String HTTPS_URL = "https://www.12306.cn/";
    private CreateHttpsConnTask httpsTask;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews(){
        Button httpsNoCer = (Button) findViewById(R.id.btn_httpsNoCer);
        httpsNoCer.setOnClickListener(this);
        Button httpsWithCer = (Button) findViewById(R.id.btn_httpsCer);
        httpsWithCer.setOnClickListener(this);
        tvInfo = (TextView) findViewById(R.id.tv_info);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //加入对HTTPS的支持，就可以有效的建立HTTPS连接了，例如“https://www.google.com.hk”了，
            // 但是访问自己基于Nginx搭建的HTTPS服务器却不行，因为它使用了不被系统承认的自定义证书，
            // 会报出如下问题：No peer certificate。
            case R.id.btn_httpsNoCer:
                System.out.println("onClick");
                runHttpsConnection();
                break;
            case R.id.btn_httpsCer:

                break;
        }
    }


    private void runHttpsConnection() {
        if (httpsTask == null || httpsTask.getStatus() == AsyncTask.Status.FINISHED) {
            httpsTask = new CreateHttpsConnTask();
            httpsTask.execute();
        }
    }

    private class CreateHttpsConnTask extends AsyncTask<Void, Void, Void> {
        private StringBuffer sBuffer = new StringBuffer();

        @Override
        protected Void doInBackground(Void... params) {
            HttpUriRequest request = new HttpPost(HTTPS_URL);
            HttpClient httpClient = HttpUtils.getHttpsClient();
            try {
                HttpResponse httpResponse = httpClient.execute(request);
                if (httpResponse != null) {
                    StatusLine statusLine = httpResponse.getStatusLine();
                    if (statusLine != null
                            && statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new InputStreamReader(
                                    httpResponse.getEntity().getContent(),
                                    "UTF-8"));
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sBuffer.append(line);
                                System.out.println(line);
                            }

                        } catch (Exception e) {
                            Log.e("https", e.getMessage());
                        } finally {
                            if (reader != null) {
                                reader.close();
                                reader = null;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                Log.e("https", e.getMessage());
            } finally {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!TextUtils.isEmpty(sBuffer.toString())) {
                tvInfo.setText(sBuffer.toString());
            }
        }

    }

}
