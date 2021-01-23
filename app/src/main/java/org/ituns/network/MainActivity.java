package org.ituns.network;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.ituns.network.core.Callback;
import org.ituns.network.core.Logger;
import org.ituns.network.core.Request;
import org.ituns.network.core.Response;
import org.ituns.network.faker.FakerAdapter;
import org.ituns.network.faker.FakerClient;
import org.ituns.network.faker.FakerConfig;
import org.ituns.network.okhttp.compat.OkHttpClient;
import org.ituns.network.okhttp.compat.OkHttpConfig;

public class MainActivity extends AppCompatActivity {
    private static final String CONFIG = "http://testapi.qury.me/config";

    private Handler handler;
    private FakerClient mFakerClient;
    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(getMainLooper());
        initializeFakerClient();
        initializeOkhttpClient();
    }

    @Override
    protected void onDestroy() {
        FakerClient fakerClient = mFakerClient;
        if(fakerClient != null) {
            fakerClient.release();
            mFakerClient = null;
        }
        OkHttpClient okHttpClient = mOkHttpClient;
        if(okHttpClient != null) {
            okHttpClient.release();
            mOkHttpClient = null;
        }
        super.onDestroy();
    }

    private void initializeFakerClient() {
        Logger logger = new Logger() {
            @Override
            public void log(String msg) {
                handler.post(() -> {
                    Log.e("FakerClient", msg);
                });
            }

            @Override
            public void log(Throwable t) {
                handler.post(() -> {
                    Log.e("FakerClient", "", t);
                });
            }
        };
        FakerAdapter adapter = new FakerAdapter.Builder(this)
                .append(CONFIG, "config.json")
                .build();
        FakerConfig config = new FakerConfig.Builder()
                .logger(logger)
                .adapter(adapter)
                .build();
        mFakerClient = new FakerClient(config);
    }

    private void initializeOkhttpClient() {
        Logger logger = new Logger() {
            @Override
            public void log(String msg) {
                handler.post(() -> {
                    Log.e("OkHttpClient", msg);
                });
            }

            @Override
            public void log(Throwable t) {
                handler.post(() -> {
                    Log.e("OkHttpClient", "", t);
                });
            }
        };
        OkHttpConfig config = new OkHttpConfig.Builder(this)
                .logger(logger)
                .build();
        mOkHttpClient = new OkHttpClient(config);
    }

    public void clickTestFakeSync(View view) {
        new Thread(() -> {
            FakerClient fakerClient = mFakerClient;
            if(fakerClient == null) {
                Log.e("FakerClient", "fake network client is null.");
                return;
            }

            Request request = Request.get().url(CONFIG).build();
            Response response = fakerClient.requestSync(request);
            Log.e("FakerClient", "Code:" + response.code() + " Message:" + response.message());
        }).start();

    }

    public void clickTestFakeAsync(View view) {
        FakerClient fakerClient = mFakerClient;
        if(fakerClient == null) {
            Log.e("FakerClient", "fake network client is null.");
            return;
        }

        Request request = Request.get().url(CONFIG).build();
        fakerClient.requestAsync(request, new Callback() {
            @Override
            public void onResponse(Response response) {
                Log.e("FakerClient", "Code:" + response.code() + " Message:" + response.message());
            }
        });
    }

    public void clickTestOkhttpSync(View view) {
        new Thread(() -> {
            OkHttpClient okHttpClient = mOkHttpClient;
            if(okHttpClient == null) {
                Log.e("OkHttpClient", "okhttp network client is null.");
                return;
            }

            Request request = Request.get().url("https://www.baidu.com").build();
            Response response = okHttpClient.requestSync(request);
            Log.e("OkHttpClient", "Code:" + response.code() + " Message:" + response.message());
        }).start();
    }

    public void clickTestOkhttpAsync(View view) {
        OkHttpClient okHttpClient = mOkHttpClient;
        if(okHttpClient == null) {
            Log.e("OkHttpClient", "okhttp network client is null.");
            return;
        }

        Request request = Request.get().url("https://www.baidu.com").build();
        okHttpClient.requestAsync(request, new Callback() {
            @Override
            public void onResponse(Response response) {
                Log.e("OkHttpClient", "Code:" + response.code() + " Message:" + response.message());
            }
        });
    }
}
