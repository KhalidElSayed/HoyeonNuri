package proinfactory.com.hoyeonnuri;

import java.io.*;
import java.net.*;

import com.google.analytics.tracking.android.EasyTracker;

import proinfactory.com.hoyeonnuri.R;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class Restaurant_WebViewCtrl extends Activity {
    private WebView WebViewController;
    private ProgressBar mPBar;
    private View Prepage;
    private String HtmlSource;
    private String board_no;
    private String board_writer;
    private String board_date;
    private int secret_test;
    
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Update();
        }
    };
    
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
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_webviewctrl);
        
        BackBtn = (ImageButton) findViewById(R.id.board_navi_backbtn);
		SettingBtn = (ImageView) findViewById(R.id.board_navi_settingbtn);

		BackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		SettingBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Restaurant_WebViewCtrl.this,Setting.class);
				startActivity(intent);
			}
		});
        
        Prepage = (View)findViewById(R.id.board_prepage);
        
        WebViewController = (WebView)findViewById(R.id.board_webviewctrl_web);
        WebViewController.setWebViewClient(new MyWebClient());
        WebViewController.setHorizontalScrollBarEnabled(false);
        WebViewController.setVerticalScrollBarEnabled(false);
        
        WebSettings set = WebViewController.getSettings();
        set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(true);
        set.setDomStorageEnabled(true);
        set.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        
        mPBar = (ProgressBar)findViewById(R.id.webviewctrl_pBar);
        
        WebViewController.setWebChromeClient(new WebChromeClient()
        { 
               public void onProgressChanged(WebView view, int progress) { 
                   if (progress<100)
                   {
                       mPBar.setVisibility(ProgressBar.VISIBLE);
                   }
                   else if (progress==100)
                   {
                       mPBar.setVisibility(ProgressBar.GONE);
                   }
                   mPBar.setProgress(progress); 
               }  
        });
        
        String top_title = getIntent().getStringExtra("Intent_Title");
        TextView TopText = (TextView)findViewById(R.id.toptitle);
        TopText.setText(top_title);
        
        board_no = getIntent().getStringExtra("Intent_URL");
        board_date = getIntent().getStringExtra("Intent_date");
        TextView sub_date = (TextView)findViewById(R.id.subbar_date);
        sub_date.setText(board_date);
        board_writer = getIntent().getStringExtra("Intent_writer");
        TextView sub_writer = (TextView)findViewById(R.id.subbar_writer);
        sub_writer.setText(board_writer);
        open();
    }

    class MyWebClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    
    
    private void Update() {
        // 비밀글일경우
        if(secret_test != -1){
            WebViewController.loadDataWithBaseURL(null,HtmlSource, "text/html", "utf-8",null);
            Prepage.setVisibility(View.INVISIBLE);
            WebViewController.setVisibility(View.VISIBLE);
        }
        // 일반글일경우
        else {
            WebViewController.loadDataWithBaseURL(null,HtmlSource, "text/html", "utf-8",null);
            Prepage.setVisibility(View.INVISIBLE);
            WebViewController.setVisibility(View.VISIBLE);
        }
    }
    
    
    
    
    void open() {
        try { process(); }
        catch(IOException e) { e.printStackTrace(); }
    }
    
    void process() throws IOException {
        final Handler mHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                    mHandler.post(new Runnable(){
                        public void run() { }
                    }); 
                    
                    // 데이터 로딩
                    HtmlSource = HtmlToString(board_no);
                    String board_data = HtmlToString(board_no);

                    HtmlSource = board_data;
                    HtmlSource = getSource(HtmlSource,"<!-- 내용 -->","<!-- [s]이전글/다음글 -->");
                    HtmlSource = getSource(HtmlSource,"<HTML>","</HTML>");
                    
                    mHandler.post(new Runnable() {
                        public void run() { handler.sendEmptyMessage(0); }
                    });
            }
        }.start();
    }
    
    

    String get_start_location(String HtmlString, String start_tag) {
        int start = HtmlString.indexOf(start_tag);
        HtmlString = HtmlString.substring(start + 3);
        return HtmlString;
    } 
    String getSource(String HtmlString, String start_tag, String end_tag) {
        String parsed_data;
        int start = HtmlString.indexOf(start_tag);
        int end = HtmlString.indexOf(end_tag);
        parsed_data = HtmlString.substring(start+start_tag.length(),end);
        return parsed_data;
    } 
    String addSource(String HtmlString, String tag, String adddata){
        int start = HtmlString.lastIndexOf(tag);
        int starttag_len = tag.length();
        String preString;
        String nextString;
        
        preString = HtmlString.substring(0,start+starttag_len);
        nextString = HtmlString.substring(start+starttag_len+1);
        HtmlString = preString + adddata + nextString;
        
        return HtmlString;
    }
    String removeSource(String HtmlString, String start_tag, String end_tag){
        int start = HtmlString.indexOf(start_tag);
        int end = HtmlString.indexOf(end_tag);
        int endtag_len = end_tag.length();
        String preString;
        String nextString;
        
        preString = HtmlString.substring(0,start);
        nextString = HtmlString.substring(end+endtag_len);
        HtmlString = preString + nextString;
        
        return HtmlString;
    }
    
    String HtmlToString(String addr) {
        StringBuilder sbHtml = new StringBuilder();
        try {
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setUseCaches(true);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
                    for (;;) {
                        String line = br.readLine();
                        if (line == null) 
                            break;
                        sbHtml.append(line + "\n");
                    }
                    br.close();
                }
                conn.disconnect();
            }
        } catch (Exception e) {}
        return sbHtml.toString();
    }
}