package proinfactory.com.hoyeonnuri;

import java.io.*;

import com.google.analytics.tracking.android.EasyTracker;

import proinfactory.com.hoyeonnuri.R;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.View;
import android.webkit.*;
import android.widget.*;

public class Info_Webview extends Activity {
  
	private WebView WebViewController;
    
    private ImageButton BackBtn;
	private ImageView SettingBtn;
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_webview);
        
        BackBtn = (ImageButton) findViewById(R.id.info_navi_backbtn);
		SettingBtn = (ImageView) findViewById(R.id.info_navi_settingbtn);

		BackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		SettingBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Info_Webview.this,
						Setting.class);
				startActivity(intent);
			}
		});
        
        WebViewController = (WebView)findViewById(R.id.info_webView);
        WebViewController.setWebViewClient(new MyWebClient());
        WebViewController.setHorizontalScrollBarEnabled(false);
        WebViewController.setVerticalScrollBarEnabled(false);

        WebSettings set = WebViewController.getSettings();
        set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(true);
        set.setDomStorageEnabled(true);
        set.setDefaultFontSize(12);

        String Title = getIntent().getStringExtra("Intent_Title");
        TextView TopTitle = (TextView)findViewById(R.id.info_navi_text);
        TopTitle.setText(Title);
        
        int htmlraw = getIntent().getIntExtra("Intent_URI",0);
        String htmlurl = getIntent().getStringExtra("Intent_URL");
        
        if(htmlurl!=null){
            set.setDefaultZoom(WebSettings.ZoomDensity.FAR);
            ConnectivityManager cm =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean is3g = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
            boolean isWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
            if (is3g == true || isWifi == true)
                WebViewController.loadUrl(htmlurl);
            else {
                new AlertDialog.Builder(this)
                .setTitle("네트워크 연결이 필요합니다")
                .setMessage("네트워크 상태를 체크해 주세요")
                .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
            }
        }
        else
            WebViewController.loadDataWithBaseURL(null,readTextFromResource(htmlraw)+"<br /><br /><br />", "text/html", "utf-8",null);
    }
    
    class MyWebClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {      
            if(url.startsWith("tel:")) {
                Intent call_phone = new Intent(Intent.ACTION_VIEW , Uri.parse(url)) ;
                startActivity(call_phone) ;
                return true ;
            }
            view.loadUrl(url);      
            return true;  
           } 
    }
    
    // Back Key
    @Override
    public void onBackPressed() {
        if (WebViewController.canGoBack())
            WebViewController.goBack();
        else
            finish();
    }
    
    private String readTextFromResource(int resourceID)
    {
        InputStream raw = getResources().openRawResource(resourceID);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int i;
        try
        {
            i = raw.read();
            while (i != -1)
            {
                stream.write(i);
                i = raw.read();
            }
            raw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return stream.toString();
    }

        
}
