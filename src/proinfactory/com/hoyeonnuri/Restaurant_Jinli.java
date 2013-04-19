// 식단표 주소 
// 진리관 금주 : http://sejong.welstory.com/sejong/menu/menu_01.jsp?beforeWeek=&nextWeek=&beforeWeek2=&nextWeek2=&cate=b&hall_no=901905
// 호사 금주 : http://sejong.welstory.com/sejong/menu/menu_01.jsp?beforeWeek=&nextWeek=&beforeWeek2=&nextWeek2=&cate=b&hall_no=901908
// 학생회관 : http://sejong.welstory.com/sejong/menu/menu_01.jsp?beforeWeek=&nextWeek=&beforeWeek2=&nextWeek2=&cate=b&hall_no=901909

package proinfactory.com.hoyeonnuri;

// Res 1,2,3 복사시 변경내용 인덱스
/*<*변경*>*/

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;
import com.proinlab.hoyeonnuri.functions.B;

import proinfactory.com.hoyeonnuri.R;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class Restaurant_Jinli extends Activity {

	private WebView Restaurant_WebView1;
	private WebView _restaurant_bar;
	private ProgressBar mPBar;
	private String site_url = "";
	private String site_url2 = "";
	private String site_url3 = "http://mob.korea.ac.kr/hoyeonnuri/popup_notice/restaurant_timetable.html";
	private String HtmlSource;
	private View Prepage;

	private Button _res_btn1;
	private Button _res_btn2;
	private Button _res_btn3;

	private boolean popup_value;
	private Dialog dialog;

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Update();
		}
	};

	private ImageButton BackBtn;
	private TextView DropDownMenu;
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
		setContentView(R.layout.restaurant);

		BackBtn = (ImageButton) findViewById(R.id.res_navi_backbtn);
		DropDownMenu = (TextView) findViewById(R.id.res_navi_selector);
		SettingBtn = (ImageView) findViewById(R.id.res_navi_settingbtn);

		BackBtn.setOnClickListener(mOnClick);
		DropDownMenu.setOnClickListener(mOnClick);
		SettingBtn.setOnClickListener(mOnClick);

		DropDownMenu.setText(" 진리관");

		/************************* 팝업 관련 *************************/
		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		popup_value = pref.getBoolean(B.popup_bool_jinli, false);
		
		String tmp = pref.getString(B.Rest_Page_url, "zz");
		if (tmp.indexOf("ab") != -1) {
			site_url = tmp.substring(tmp.indexOf("<ab>") + 4,
					tmp.indexOf("</ab>"));
			site_url2 = tmp.substring(tmp.indexOf("<ac>") + 4,
					tmp.indexOf("</ac>"));
		}

		if (popup_value) {
			/* dialog start */
			dialog = new Dialog(this);

			dialog.setContentView(R.layout.popup_dialog);
			dialog.setTitle("공지");

			final WebView Dialog_Web = (WebView) dialog
					.findViewById(R.id.dialog_webv);
			Dialog_Web.setWebViewClient(new MyWebClient());
			WebSettings dia_set = Dialog_Web.getSettings();
			dia_set.setJavaScriptEnabled(true);
			dia_set.setBuiltInZoomControls(true);

			Dialog_Web
					.loadUrl("http://mob.korea.ac.kr/hoyeonnuri/popup_notice/notice_rest1_html.html");

			Button btnMyDlg = (Button) dialog.findViewById(R.id.dialog_btn);
			btnMyDlg.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
		}
		/************************* 팝업 관련 *************************/

		Prepage = (View) findViewById(R.id.Res_prepage);

		_res_btn1 = (Button) findViewById(R.id.res_this);
		_res_btn2 = (Button) findViewById(R.id.res_next);
		_res_btn3 = (Button) findViewById(R.id.restaurant_time);

		// WebView 세팅
		Restaurant_WebView1 = (WebView) findViewById(R.id.webviewctrl_web);
		Restaurant_WebView1.setWebViewClient(new MyWebClient());
		WebSettings set = Restaurant_WebView1.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(false);

		_restaurant_bar = (WebView) findViewById(R.id.restaurant_bar);
		_restaurant_bar.setWebViewClient(new MyWebClient());

		String res_title = "<html> <head> <meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml; charset=euc-kr\" /> <link rel=\"stylesheet\" type=\"text/css\" href=\"http://mob.korea.ac.kr/hoyeonnuri/style_title.css\" title=\"web\" /> </head> <body><div class=\"menu_box\"><p class=\"date\">날짜</p><div class=\"menu\"><p class=\"breakfast\">아침</p><p class=\"lunch\">점심</p><p class=\"dinner\">저녁</p></div></body></html>";
		_restaurant_bar.loadDataWithBaseURL(null, res_title, "text/html",
				"utf-8", null);

		// 프로그레스바 세팅
		mPBar = (ProgressBar) findViewById(R.id.webviewctrl_pBar);
		Restaurant_WebView1.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress < 100) {
					mPBar.setVisibility(ProgressBar.VISIBLE);
				} else if (progress == 100) {
					mPBar.setVisibility(ProgressBar.GONE);
				}
				mPBar.setProgress(progress);
			}
		});

		open(site_url);
	}

	private View.OnClickListener mOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.res_navi_backbtn:
				finish();
				break;
			case R.id.res_navi_selector:
				AlertDialog.Builder selectorDialog = new AlertDialog.Builder(
						Restaurant_Jinli.this);
				selectorDialog.setTitle("식당선택");
				ArrayList<String> arrStr = new ArrayList<String>();
				arrStr.add("공지사항");
				arrStr.add("호연4관");
				arrStr.add("학생회관");
				ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(
						Restaurant_Jinli.this,
						android.R.layout.select_dialog_item, arrStr);
				selectorDialog.setAdapter(arrayAdt,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent;
								switch (which) {
								case 0:
									intent = new Intent(Restaurant_Jinli.this,
											Restaurant_Notice.class);
									startActivity(intent);
									finish();
									break;
								case 1:
									intent = new Intent(Restaurant_Jinli.this,
											Restaurant_Hoyeon.class);
									startActivity(intent);
									finish();
									break;
								case 2:
									intent = new Intent(Restaurant_Jinli.this,
											Restaurant_3.class);
									startActivity(intent);
									finish();
									break;
								}
							}
						});
				selectorDialog.show();
				break;
			case R.id.res_navi_settingbtn:
				Intent intent = new Intent(Restaurant_Jinli.this, Setting.class);
				startActivity(intent);
				break;
			}
		}
	};

	// 비튼 클릭 이벤트
	public void res_btn_click(View v) {
		switch (v.getId()) {
		case R.id.res_this:
			Prepage.setVisibility(View.VISIBLE);
			open(site_url);
			_res_btn1.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.listrowbg));
			_res_btn2.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.listview_focus));
			_res_btn3.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.listview_focus));
			_restaurant_bar.setVisibility(View.VISIBLE);
			break;
		case R.id.res_next:
			Prepage.setVisibility(View.VISIBLE);
			open(site_url2);
			_res_btn1.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.listview_focus));
			_res_btn2.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.listrowbg));
			_res_btn3.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.listview_focus));
			_restaurant_bar.setVisibility(View.VISIBLE);
			break;
		case R.id.restaurant_time:
			Restaurant_WebView1.loadUrl(site_url3);
			_res_btn1.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.listview_focus));
			_res_btn2.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.listview_focus));
			_res_btn3.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.listrowbg));
			_restaurant_bar.setVisibility(View.GONE);
			break;
		}
	}

	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	private void Update() {
		Restaurant_WebView1.loadDataWithBaseURL(null, HtmlSource, "text/html",
				"utf-8", null);
		Prepage.setVisibility(View.INVISIBLE);
		Restaurant_WebView1.setVisibility(View.VISIBLE);
	}

	void open(String _url) {
		try {
			process(_url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void process(final String __url) throws IOException {
		final Handler mHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					public void run() {
					}
				});
				try {
					// 식단표 내용 파싱
					HtmlSource = HtmlToString(__url);

					if (HtmlSource.indexOf("<div class=\"menu_nodate\">") == -1) {
						HtmlSource = removeSource(HtmlSource,
								"<SCRIPT LANGUAGE=\"JavaScript\">",
								"<!-- [s]리스트 반복 부분 -->");
						HtmlSource = removeSource(HtmlSource,
								"<!-- [e]검색 결과 -->", "</form>");
						HtmlSource = HtmlSource.replace(
								"/common/css/style.css",
								"http://mob.korea.ac.kr/hoyeonnuri/style.css");
						HtmlSource = "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />"
								+ HtmlSource;
					} else {
						HtmlSource = "<br>조회하실 내용이 없습니다";
						HtmlSource = "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />"
								+ HtmlSource;
					}

				} catch (StringIndexOutOfBoundsException e) {
				}
				mHandler.post(new Runnable() {
					public void run() {
						handler.sendEmptyMessage(0);
					}
				});
			}
		}.start();
	}

	String addSource(String HtmlString, String tag, String adddata) {
		int start = HtmlString.indexOf(tag);
		int starttag_len = tag.length();
		String preString;
		String nextString;

		preString = HtmlString.substring(0, start + starttag_len);
		nextString = HtmlString.substring(start + starttag_len + 1);
		HtmlString = preString + adddata + nextString;

		return HtmlString;
	}

	String removeSource(String HtmlString, String start_tag, String end_tag) {
		int start = HtmlString.indexOf(start_tag);
		int end = HtmlString.indexOf(end_tag);
		int endtag_len = end_tag.length();
		String preString;
		String nextString;

		preString = HtmlString.substring(0, start);
		nextString = HtmlString.substring(end + endtag_len);
		HtmlString = preString + nextString;

		return HtmlString;
	}

	String HtmlToString(String addr) {
		StringBuilder sbHtml = new StringBuilder();
		try {
			URL url = new URL(addr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream(),
									"EUC-KR"));
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
		} catch (Exception e) {
		}
		return sbHtml.toString();
	}
}