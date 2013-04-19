package proinfactory.com.hoyeonnuri;

import java.io.*;
import java.net.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.analytics.tracking.android.EasyTracker;

import proinfactory.com.hoyeonnuri.R;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class Board_WebViewCtrl extends Activity {

	private DefaultHttpClient httpclient = Loading.httpclient;

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
	
	private ImageButton BackBtn;
	private ImageView SettingBtn;
	
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
				Intent intent = new Intent(Board_WebViewCtrl.this,Setting.class);
				startActivity(intent);
			}
		});
		
		Prepage = (View) findViewById(R.id.board_prepage);

		WebViewController = (WebView) findViewById(R.id.board_webviewctrl_web);
		WebViewController.setWebViewClient(new MyWebClient());
		WebViewController.setHorizontalScrollBarEnabled(false);
		WebViewController.setVerticalScrollBarEnabled(false);

		WebSettings set = WebViewController.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(true);
		set.setDomStorageEnabled(true);
		set.setDefaultZoom(WebSettings.ZoomDensity.FAR);

		mPBar = (ProgressBar) findViewById(R.id.webviewctrl_pBar);

		WebViewController.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress < 100) {
					mPBar.setVisibility(ProgressBar.VISIBLE);
				} else if (progress == 100) {
					mPBar.setVisibility(ProgressBar.GONE);
				}
				mPBar.setProgress(progress);
			}
		});

		String top_title = getIntent().getStringExtra("Intent_Title");
		TextView TopText = (TextView) findViewById(R.id.toptitle);
		TopText.setText(top_title);

		board_no = getIntent().getStringExtra("Intent_URL");
		board_date = getIntent().getStringExtra("Intent_date");
		TextView sub_date = (TextView) findViewById(R.id.subbar_date);
		sub_date.setText(board_date);
		board_writer = getIntent().getStringExtra("Intent_writer");
		TextView sub_writer = (TextView) findViewById(R.id.subbar_writer);
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
		if (secret_test != -1) {
			WebViewController.loadDataWithBaseURL(null, HtmlSource,
					"text/html", "utf-8", null);
			Prepage.setVisibility(View.INVISIBLE);
			WebViewController.setVisibility(View.VISIBLE);
		}
		// 일반글일경우
		else {
			WebViewController.loadDataWithBaseURL(null, HtmlSource,
					"text/html", "utf-8", null);
			Prepage.setVisibility(View.INVISIBLE);
			WebViewController.setVisibility(View.VISIBLE);
		}
	}

	void open() {
		try {
			process();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void process() throws IOException {
		final Handler mHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					public void run() {
					}
				});

				// 데이터 로딩
				HtmlSource = HtmlToString("http://dormitel.korea.ac.kr/pub/board/bbs_free_read.html?"
						+ board_no);
				String board_data = HtmlToString("http://dormitel.korea.ac.kr/pub/board/read.html?"
						+ board_no);

				secret_test = 0;
				secret_test = HtmlSource.indexOf("이글은 비밀글입니다");

				// 비밀글 필터링
				if (secret_test != -1) {
					HtmlSource = "<br><br><br>이글은 비밀글입니다.<br>작성자와 관리자만 열람가능합니다.<br>로그인을 해주세요.";
					HtmlSource = "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />"
							+ HtmlSource;
				}

				else {
					// 댓글 없을때 실행
					if (HtmlSource.indexOf("<font color=bc8d3c><b>") == -1)
						HtmlSource = board_data;
					// 댓글 있을때 실행
					else {
						String TempSource = HtmlSource;
						String comment_writer = null, comment_data = null, comment_date = null;
						// 댓글 갯수
						int comment_count = 1;
						while (TempSource.indexOf("<font color=bc8d3c><b>") < TempSource
								.lastIndexOf("<font color=bc8d3c><b>")) {
							comment_count++;
							TempSource = get_start_location(TempSource,
									"<font color=bc8d3c><b>");
						}
						// 댓글 내용 파싱
						TempSource = HtmlSource;
						HtmlSource = "<html> <head> <link rel=\"stylesheet\" type=\"text/css\" href=\"http://115.88.201.43/hoyeonnuri/style.css\" title=\"web\" /> <body> board_data <table width=\"100%c\" border=\"0\" align=\"left\" cellspacing=\"0\"> <tr> <td> <br>";
						HtmlSource = "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />"
								+ HtmlSource.replace("board_data", board_data);
						for (int i = 0; i < comment_count; i++) {
							TempSource = get_start_location(TempSource,
									"<font color=bc8d3c><b>");
							comment_writer = getSource(TempSource, "<b>",
									"</b>");
							TempSource = get_start_location(TempSource,
									"<td class=\"text_small2\"");
							comment_data = getSource(TempSource,
									"valign=\"middle\">", "</td>");
							comment_date = getSource(TempSource,
									"<font color=\"333333\">", "</font>");

							String comment_html = "<b>&nbsp;&nbsp;"
									+ comment_writer + "</b>&nbsp;&nbsp;"
									+ comment_date
									+ "<br><div class=\"comment\">"
									+ comment_data + "</div><br>";

							HtmlSource = HtmlSource + comment_html;
						}
						HtmlSource = HtmlSource
								+ "</td> <tr> </table> </body> </html>";
					}
				}

				mHandler.post(new Runnable() {
					public void run() {
						handler.sendEmptyMessage(0);
					}
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
		parsed_data = HtmlString.substring(start + start_tag.length(), end);
		return parsed_data;
	}

	String addSource(String HtmlString, String tag, String adddata) {
		int start = HtmlString.lastIndexOf(tag);
		int starttag_len = tag.length();
		String preString;
		String nextString;

		preString = HtmlString.substring(0, start + starttag_len);
		nextString = HtmlString.substring(start + starttag_len + 1);
		HtmlString = preString + adddata + nextString;

		return HtmlString;
	}

	private String HtmlToString(String addr) {
		String htmlSource;
		try {
			HttpGet request = new HttpGet();
			request.setURI(new URI(addr));
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			htmlSource = EntityUtils.toString(entity, "EUC-KR");
		} catch (Exception e) {
			htmlSource = null;
		}
		return htmlSource;
	}
}