package proinfactory.com.hoyeonnuri;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.proinlab.hoyeonnuri.functions.B;

public class Main extends Activity {

	private ImageView Menu1, Menu2, Menu3, Menu4;
	private ImageView Title, SettingBtn;

	private TextView Appver, Notice;
	private ScrollView NoticeScroll;

	private Dialog dialog;

	private int displayWidth, displayHeight;

	private String NoticeStr = "";
	private String NoticeCustomStr = "";
	private String AppVerStr = "";

	private boolean main_popup_bool = false;
	private boolean main_text_bool = false;

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
		setContentView(R.layout.main);

		InitUIRate();

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);

		NoticeStr = pref.getString(B.NoticeText, "표시할 내용이 없습니다.");
		AppVerStr = pref.getString(B.AppVerName, "");
		NoticeCustomStr = pref.getString(B.NoticeTextTrue, "표시할 내용이 없습니다.");
		main_popup_bool = pref.getBoolean(B.popup_bool_main_pop, false);
		main_text_bool = pref.getBoolean(B.popup_bool_main_text, false);

		Notice.setText(NoticeStr);
		Appver.setText(AppVerStr);

		Menu1.setOnClickListener(mOnClick);
		Menu2.setOnClickListener(mOnClick);
		Menu3.setOnClickListener(mOnClick);
		Menu4.setOnClickListener(mOnClick);
		
		SettingBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Main.this, Setting.class);
				startActivity(intent);
			}
		});

		if (main_popup_bool) {
			dialog = new Dialog(this);

			dialog.setContentView(R.layout.popup_dialog);
			dialog.setTitle("공지");

			final WebView Dialog_Web = (WebView) dialog
					.findViewById(R.id.dialog_webv);
			Dialog_Web.setWebViewClient(new MyWebClient());
			WebSettings dia_set = Dialog_Web.getSettings();
			dia_set.setBuiltInZoomControls(true);

			Dialog_Web
					.loadUrl("http://mob.korea.ac.kr/hoyeonnuri/popup_notice/notice_main_html.html");

			Button btnMyDlg = (Button) dialog.findViewById(R.id.dialog_btn);
			btnMyDlg.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
		}
		if (main_text_bool)
			Notice.setText(NoticeCustomStr);
		else
			Notice.setText(NoticeStr);

	}

	private View.OnClickListener mOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
			case R.id.main_menu1:
				intent = new Intent(Main.this, Board_Notice.class);
				startActivity(intent);
				break;
			case R.id.main_menu2:
				intent = new Intent(Main.this, Restaurant_Jinli.class);
				startActivity(intent);
				break;
			case R.id.main_menu3:
				intent = new Intent(Main.this, CollegeInfo.class);
				startActivity(intent);
				break;
			case R.id.main_menu4:
				intent = new Intent(Main.this, Delivery_List.class);
				startActivity(intent);
				break;
			}
		}
	};

	private void InitUIRate() {
		RelativeLayout.LayoutParams rlp;

		Display display = getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		if (displayWidth > displayHeight) {
			displayHeight = display.getWidth();
			displayWidth = display.getHeight();
		}

		Menu1 = (ImageView) findViewById(R.id.main_menu1);
		Menu2 = (ImageView) findViewById(R.id.main_menu2);
		Menu3 = (ImageView) findViewById(R.id.main_menu3);
		Menu4 = (ImageView) findViewById(R.id.main_menu4);
		Title = (ImageView) findViewById(R.id.main_titlebar_img);
		SettingBtn = (ImageView) findViewById(R.id.main_navi_settingbtn);
		Appver = (TextView) findViewById(R.id.main_appver_txt);
		Notice = (TextView) findViewById(R.id.main_notice_txt);
		NoticeScroll = (ScrollView) findViewById(R.id.main_notice_scroll);

		rlp = new RelativeLayout.LayoutParams(432 * displayWidth / 1000,
				263 * displayHeight / 1000);
		rlp.setMargins(0, 586 * displayHeight / 1000, 0, 0);
		Menu1.setLayoutParams(rlp);

		rlp = new RelativeLayout.LayoutParams(290 * displayWidth / 1000,
				323 * displayHeight / 1000);
		rlp.setMargins(341 * displayWidth / 1000, 611 * displayHeight / 1000,
				0, 0);
		Menu2.setLayoutParams(rlp);

		rlp = new RelativeLayout.LayoutParams(242 * displayWidth / 1000,
				323 * displayHeight / 1000);
		rlp.setMargins(581 * displayWidth / 1000, 609 * displayHeight / 1000,
				0, 0);
		Menu3.setLayoutParams(rlp);

		rlp = new RelativeLayout.LayoutParams(210 * displayWidth / 1000,
				323 * displayHeight / 1000);
		rlp.setMargins(790 * displayWidth / 1000, 611 * displayHeight / 1000,
				0, 0);
		Menu4.setLayoutParams(rlp);

		rlp = new RelativeLayout.LayoutParams(displayWidth,
				10 * displayHeight / 100);
		Title.setLayoutParams(rlp);

		rlp = new RelativeLayout.LayoutParams(796 * displayWidth / 1000,
				333 * displayHeight / 1000);
		rlp.setMargins(113 * displayWidth / 1000, 176 * displayHeight / 1000,
				0, 0);
		NoticeScroll.setLayoutParams(rlp);

	}

	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
