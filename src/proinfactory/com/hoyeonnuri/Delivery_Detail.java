/*
 * 게시판 주소 목록  
 * Free : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=part060301&page=
 * QnA : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=part060201&page=
 * Repair : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=part060101&page=
 * Notice : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=know050101&page=
 */

package proinfactory.com.hoyeonnuri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.proinlab.hoyeonnuri.functions.DataBaseHelper;

public class Delivery_Detail extends Activity {

	private ImageButton BackBtn;
	private ImageView SettingBtn;
	private ProgressBar pBar;

	private ImageView Banner, MenuImg;
	private TextView Name, Location, Call, IsDelivery, Map, MenuTxt, Time;
	private String menustr;
	private Bitmap menubit = null, bannerbit = null;

	private String code = "";
	private ArrayList<String> data;

	private DataBaseHelper mHelper;

	private int displayWidth, displayHeight;

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
		setContentView(R.layout.delivery_detial);

		Display display = getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		if (displayWidth > displayHeight) {
			displayHeight = display.getWidth();
			displayWidth = display.getHeight();
		}

		BackBtn = (ImageButton) findViewById(R.id.delivery_navi_backbtn);
		SettingBtn = (ImageView) findViewById(R.id.delivery_navi_settingbtn);

		Banner = (ImageView) findViewById(R.id.deliverydetail_banner);
		MenuImg = (ImageView) findViewById(R.id.deliverydetail_menuimg);
		MenuTxt = (TextView) findViewById(R.id.deliverydetail_menutxt);
		Name = (TextView) findViewById(R.id.deliverydetail_name);
		Location = (TextView) findViewById(R.id.deliverydetail_location);
		Call = (TextView) findViewById(R.id.deliverydetail_call);
		IsDelivery = (TextView) findViewById(R.id.deliverydetail_ispossible);
		Map = (TextView) findViewById(R.id.deliverydetail_locationview);
		Time = (TextView) findViewById(R.id.deliverydetail_time);

		pBar = (ProgressBar) findViewById(R.id.deliverydetail_progress);

		Call.setBackgroundColor(Color.rgb(129, 81, 28));
		Map.setBackgroundColor(Color.rgb(9, 124, 37));
		
		BackBtn.setOnClickListener(mOnClick);
		SettingBtn.setOnClickListener(mOnClick);
		Call.setOnClickListener(mOnClick);
		Map.setOnClickListener(mOnClick);

		mHelper = new DataBaseHelper(this);
		code = getIntent().getStringExtra("Intent_Code");

		data = FIND_DATA_BY_CODE(code);

		Name.setText(data.get(A.Delivery_name));
		Location.setText(data.get(A.Delivery_location));

		if (data.get(A.Delivery_isPossiable).equals("배달")) {
			IsDelivery.setBackgroundColor(Color.rgb(9, 124, 37));
			IsDelivery.setText(data.get(A.Delivery_isPossiable));
		} else {
			IsDelivery.setText("배달안함");
			IsDelivery.setBackgroundColor(Color.rgb(164, 0, 0));
		}

		if (data.get(A.Delivery_time).equals("false"))
			Time.setVisibility(View.GONE);
		Time.setText(data.get(A.Delivery_time));

		open(data.get(A.Delivery_bannerurl), 2);

	}

	private View.OnClickListener mOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.deliverydetail_call:
				Uri uri = Uri.parse("tel:" + data.get(A.Delivery_call));
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				startActivity(it);
				break;
			case R.id.deliverydetail_locationview:
				it = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="
						+ data.get(A.Delivery_location)));
				startActivity(it);
				break;
			case R.id.delivery_navi_backbtn:
				finish();
				break;
			case R.id.delivery_navi_settingbtn:
				Intent intent = new Intent(Delivery_Detail.this, Setting.class);
				startActivity(intent);
				break;
			}
		}
	};

	private ArrayList<String> FIND_DATA_BY_CODE(String code) {

		SQLiteDatabase db;

		ArrayList<String> returnStr;
		String[] columns = { DataBaseHelper.DB_ROW00_CODE,
				DataBaseHelper.DB_ROW01_NAME, DataBaseHelper.DB_ROW02_CALL,
				DataBaseHelper.DB_ROW03_IS_DELIVERY,
				DataBaseHelper.DB_ROW04_TIME, DataBaseHelper.DB_ROW05_LOCATION,
				DataBaseHelper.DB_ROW06_CATEGORY,
				DataBaseHelper.DB_ROW07_MENUIMG,
				DataBaseHelper.DB_ROW08_MENUTXT,
				DataBaseHelper.DB_ROW09_BANNER, DataBaseHelper.DB_ROW10_SPONSER };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0)
			returnStr = new ArrayList<String>();
		else {
			returnStr = new ArrayList<String>();
			while (cursor.moveToNext()) {
				if (code != null) {
					if (code.equals(cursor.getString(A.Delivery_code))) {
						for (int i = 0; i < 11; i++)
							returnStr.add(cursor.getString(i));
					}
				} else {
					for (int i = 0; i < 11; i++)
						returnStr.add(cursor.getString(i));
				}

			}
		}
		cursor.close();
		mHelper.close();
		return returnStr;
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) { // menu img
				if (menubit == null)
					return;
				Drawable draw = (Drawable) new BitmapDrawable(menubit);
				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
						displayWidth, displayWidth * menubit.getHeight()
								/ menubit.getWidth());
				MenuImg.setImageBitmap(null);
				MenuImg.setBackgroundDrawable(draw);
				MenuImg.setLayoutParams(llp);
				MenuImg.setVisibility(View.VISIBLE);
				pBar.setVisibility(View.GONE);
			} else if (msg.what == 1) { // menu txt
				MenuTxt.setText(menustr);
				MenuTxt.setVisibility(View.VISIBLE);
				pBar.setVisibility(View.GONE);
			} else if (msg.what == 2) { // banner
				if (bannerbit != null)
					Banner.setImageBitmap(bannerbit);

				if (!data.get(A.Delivery_menuimg).equals("false"))
					open(data.get(A.Delivery_menuimg), 0);
				if (!data.get(A.Delivery_menutxt).equals("false"))
					open(data.get(A.Delivery_menutxt), 1);
				if (data.get(A.Delivery_menuimg).equals("false")
						&& data.get(A.Delivery_menutxt).equals("false"))
					pBar.setVisibility(View.GONE);
			}
		}
	};

	void open(String url, int work) {
		pBar.setVisibility(View.VISIBLE);
		try {
			process(url, work);
		} catch (IOException e) {
		}
	}

	void process(final String url, final int work) throws IOException {
		final Handler mHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				switch (work) {
				case 0:
					try {
						InputStream is = new URL(url).openStream();
						menubit = BitmapFactory.decodeStream(is);
						is.close();

						mHandler.post(new Runnable() {
							public void run() {
								handler.sendEmptyMessage(0);
							}
						});

					} catch (Exception e) {
					}
					break;
				case 1:
					menustr = HtmlToString(url, "euc-kr");

					mHandler.post(new Runnable() {
						public void run() {
							handler.sendEmptyMessage(1);
						}
					});
					break;
				case 2:
					if (!data.get(A.Delivery_bannerurl).equals("false")) {
						try {
							InputStream is = new URL(url).openStream();
							bannerbit = BitmapFactory.decodeStream(is);
							is.close();
						} catch (Exception e) {
						}
					}

					mHandler.post(new Runnable() {
						public void run() {
							handler.sendEmptyMessage(2);
						}
					});

					break;
				}

			}
		}.start();
	}

	private String HtmlToString(String addr, String incoding) {
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
									incoding));
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
