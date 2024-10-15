package it.mody.smarthomemanager;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity2 extends AppCompatActivity {
    private WebView myWebview;
    public WebSettings myWebSettings;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        myWebview=findViewById(R.id.web1);
        myWebview.loadUrl("https://dhf1ywa.localto.net/ui/#!/0?socketid=CranAciU3zp_tIKSAAAF");
        myWebview.setWebViewClient(new WebViewClient());
        myWebview.getSettings().setJavaScriptEnabled(true);
        myWebview.setWebChromeClient(new WebChromeClient());
        myWebview.getSettings().setDomStorageEnabled(true);

    }
}