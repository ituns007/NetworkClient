package org.ituns.network;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.ituns.network.core.NetworkCallback;
import org.ituns.network.core.NetworkMethod;
import org.ituns.network.core.NetworkRequest;
import org.ituns.network.core.NetworkResponse;
import org.ituns.network.faker.FakerAdapter;
import org.ituns.network.faker.FakerNetworkClient;
import org.ituns.network.okhttp.stable.OkHttpNetworkClient;

public class MainActivity extends AppCompatActivity {
    private static final String CONFIG = "http://testapi.qury.me/config";

    private FakerNetworkClient mFakerNetworkClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFakerNetworkClient();
    }

    @Override
    protected void onDestroy() {
        FakerNetworkClient fakerNetworkClient = mFakerNetworkClient;
        if(fakerNetworkClient != null) {
            fakerNetworkClient.release();
            mFakerNetworkClient = null;
        }
        super.onDestroy();
    }

    private void initializeFakerNetworkClient() {
        FakerAdapter.Builder builder = new FakerAdapter.Builder(this);
        builder.append(CONFIG, "config.json");
        mFakerNetworkClient = new FakerNetworkClient(builder.build());
    }

    public void clickTestFakeSync(View view) {
        FakerNetworkClient fakerNetworkClient = mFakerNetworkClient;
        if(fakerNetworkClient == null) {
            Log.e("wangxiulong", "fake network client is null.");
            return;
        }

        NetworkRequest request = new NetworkRequest.Builder(CONFIG)
                .method(NetworkMethod.GET).build();
        NetworkResponse response = fakerNetworkClient.requestSync(request);
        Log.e("wangxiulong", "Code:" + response.code() + " Message:" + response.message() + " Body:" + new String(response.body()));
    }

    public void clickTestFakeAsync(View view) {
        FakerNetworkClient fakerNetworkClient = mFakerNetworkClient;
        if(fakerNetworkClient == null) {
            Log.e("wangxiulong", "fake network client is null.");
            return;
        }

        NetworkRequest request = new NetworkRequest.Builder(CONFIG)
                .method(NetworkMethod.GET).build();
        fakerNetworkClient.requestAsync(request, new NetworkCallback() {
            @Override
            public void onSuccess(NetworkResponse response) {
                Log.e("wangxiulong", "Code:" + response.code() + " Message:" + response.message() + " Body:" + new String(response.body()));
            }

            @Override
            public void onError(NetworkResponse response) {
                Log.e("wangxiulong", "Code:" + response.code() + " Message:" + response.message());
            }
        });
    }

    public void clickTestOkhttp(View view) {
        NetworkRequest request = new NetworkRequest.Builder("http://www.baidu.com/")
                .method(NetworkMethod.GET).build();
        OkHttpNetworkClient.getInstance().requestAsync(request, new NetworkCallback() {
            @Override
            public void onSuccess(NetworkResponse response) {
                Log.e("wangxiulong", "Code:" + response.code() + " Message:" + response.message() + " Body:" + new String(response.body()));
            }

            @Override
            public void onError(NetworkResponse response) {
                Log.e("wangxiulong", "Code:" + response.code() + " Message:" + response.message());
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private class FakerTask extends Thread {

        @Override
        public void run() {
            super.run();
        }
    }
}
