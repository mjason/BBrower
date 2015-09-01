package ooo.oxo.bbrower;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private XWalkView xWalkView;
    private String js;
    private MenuItem menuItem;
    private String magnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadJs();

        xWalkView = (XWalkView) findViewById(R.id.web_view);
        setCookie();
        xWalkView.addJavascriptInterface(new JsInterface(), "NativeInterface");

        xWalkView.load("你要的网站", null);

        xWalkView.setResourceClient(new XWalkResourceClient(xWalkView) {

            @Override
            public void onProgressChanged(XWalkView view, int progressInPercent) {
                super.onProgressChanged(view, progressInPercent);
                view.load("javascript:" + js, null);
            }

            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
                downloadFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void downloadFile() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(magnet));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "沒有安裝任何可以處理磁力鏈的軟件", Toast.LENGTH_SHORT).show();
        }

    }

    public class JsInterface {
        @JavascriptInterface
        public void download(String url) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    menuItem.setVisible(true);
                }
            });
            magnet = "magnet:?xt=urn:btih:" + url;

        }

        @JavascriptInterface
        public void cancel() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    menuItem.setVisible(false);
                }
            });
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_download);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private void loadJs() {
        try {
            InputStream inputStream = getAssets().open("main_bbrower.js");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            js = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCookie() {
        XWalkCookieManager cookieManager = new XWalkCookieManager();
        cookieManager.setAcceptCookie(true);
        String url = "http://t66y.com";
        String mob = "ismob=1";
        cookieManager.setCookie(url, mob);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (xWalkView != null) {
            xWalkView.pauseTimers();
            xWalkView.onHide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (xWalkView != null) {
            xWalkView.resumeTimers();
            xWalkView.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (xWalkView != null) {
            xWalkView.onDestroy();
        }
    }
}
