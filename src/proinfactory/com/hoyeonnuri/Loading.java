package proinfactory.com.hoyeonnuri;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.proinlab.hoyeonnuri.functions.B;
import com.proinlab.hoyeonnuri.functions.DataBaseHelper;

@SuppressLint("HandlerLeak")
public class Loading extends Activity {

	private int App_Ver;
	private String Recently_Ver, App_VerName;
	private String NoticeText, pop_bool, notice_text_true;
	private boolean main, maintxt, jinli, hoyeon, rest3;
	private String Rest_URL_Info;

	private TextView Stat_Txt;

	public static DefaultHttpClient httpclient;

	private boolean AutoLogin = false;
	private String id, pw;

	private DataBaseHelper mHelper;
	private String databasestr = null;
	private String DB_VERSION = "0";

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

	private static void DeleteDir(String path) {
		File file = new File(path);
		if (!file.exists())
			return;

		File[] childFileList = file.listFiles();
		for (File childFile : childFileList) {
			if (childFile.isDirectory()) {
				DeleteDir(childFile.getAbsolutePath());
			} else {
				childFile.delete();
			}
		}
		file.delete();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);

		File FILE = new File(Environment.getExternalStorageDirectory()
				.toString() + "/.HoyeonNuri/");
		if (!FILE.exists())
			while (FILE.mkdirs())
				;

		mHelper = new DataBaseHelper(this);

		httpclient = new DefaultHttpClient();

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		id = pref.getString(B.id, "n");
		pw = pref.getString(B.pw, "n");
		AutoLogin = pref.getBoolean(B.autoLogin, false);
		DB_VERSION = pref.getString(B.DB_VERSION, "0");

		PackageManager pm = getPackageManager();
		try {
			App_Ver = pm.getPackageInfo(getPackageName(),
					PackageManager.GET_SIGNATURES).versionCode;
			App_VerName = pm.getPackageInfo(getPackageName(),
					PackageManager.GET_SIGNATURES).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		Stat_Txt = (TextView) findViewById(R.id.loading_stat_txt);

		open();

	}

	@SuppressLint("HandlerLeak")
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				Update();
			else if (msg.what == 1)
				Stat_Txt.setText("버전 정보 불러오는 중... (1/4)");
			else if (msg.what == 2)
				Stat_Txt.setText("공지글 불러오는 중... (2/4)");
			else if (msg.what == 3)
				Stat_Txt.setText("팝업 정보 불러오는 중... (3/4)");
			else if (msg.what == 4)
				Stat_Txt.setText("배달 정보 불러오는 중... (4/4)");
		}
	};

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

				mHandler.post(new Runnable() {
					public void run() {
						handler.sendEmptyMessage(1);
					}
				});

				Recently_Ver = HtmlToString(
						"https://play.google.com/store/apps/details?id=proinfactory.com.hoyeonnuri",
						"utf-8");

				String tag = "<dd itemprop=\"softwareVersion\">";
				if (Recently_Ver == null)
					;
				else if (Recently_Ver.indexOf(tag) != -1) {
					Recently_Ver = Recently_Ver.substring(Recently_Ver
							.indexOf(tag) + tag.length());
					Recently_Ver = Recently_Ver.substring(0,
							Recently_Ver.indexOf("</dd>"));
				}

				mHandler.post(new Runnable() {
					public void run() {
						handler.sendEmptyMessage(2);
					}
				});

				NoticeText = HtmlToString(
						"http://dormitel.korea.ac.kr/main.html", "EUC-KR");

				if (NoticeText.indexOf("<td align='left' valign='top'>") == -1)
					NoticeText = "네트워크 상태가 원활하지 않습니다.";
				else {
					NoticeText = get_start_location(NoticeText,
							"<td align='left' valign='top'>");
					NoticeText = getSource(NoticeText,
							"<td align='left' valign='top'>", "</td>");
					NoticeText = NoticeText.replaceAll("<br />", "");
				}

				Rest_URL_Info = HtmlToString(
						"http://mob.korea.ac.kr/hoyeonnuri/welstory.txt",
						"EUC-KR");

				mHandler.post(new Runnable() {
					public void run() {
						handler.sendEmptyMessage(3);
					}
				});

				pop_bool = HtmlToString(
						"http://mob.korea.ac.kr/hoyeonnuri/popup_notice/notice_bool.txt",
						"EUC-KR");
				notice_text_true = HtmlToString(
						"http://mob.korea.ac.kr/hoyeonnuri/popup_notice/notice_main_text.txt",
						"utf-8");

				if (AutoLogin)
					POST_LOGIN_DATA(id, pw);

				mHandler.post(new Runnable() {
					public void run() {
						handler.sendEmptyMessage(4);
					}
				});

				databasestr = HtmlToString(
						"http://mob.korea.ac.kr/hoyeonnuri/delivery/delivery_db.xml",
						"utf-8");

				mHandler.post(new Runnable() {
					public void run() {
						handler.sendEmptyMessage(0);
					}
				});
			}
		}.start();
	}

	// 스레드 후 실행
	private void Update() {

		DataBaseXMLParse();

		if (pop_bool != null) {
			if (pop_bool.indexOf("<main_pop>NO") == -1)
				main = true;
			else
				main = false;

			if (pop_bool.indexOf("<main_text>NO") == -1)
				maintxt = true;
			else
				maintxt = false;

			if (pop_bool.indexOf("<rest1>NO") == -1)
				jinli = true;
			else
				jinli = false;

			if (pop_bool.indexOf("<rest2>NO") == -1)
				hoyeon = true;
			else
				hoyeon = false;

			if (pop_bool.indexOf("<rest3>NO") == -1)
				rest3 = true;
			else
				rest3 = false;
		} else {
			main = false;
			maintxt = false;
			jinli = false;
			hoyeon = false;
			rest3 = false;
		}

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(B.AppVerCode, App_Ver);
		editor.putString(B.AppVerName, App_VerName);
		editor.putString(B.NoticeText, NoticeText);
		editor.putString(B.NoticeTextTrue, notice_text_true);
		editor.putBoolean(B.popup_bool_main_pop, main);
		editor.putBoolean(B.popup_bool_main_text, maintxt);
		editor.putBoolean(B.popup_bool_jinli, jinli);
		editor.putBoolean(B.popup_bool_hoyeon, hoyeon);
		editor.putBoolean(B.popup_bool_rest3, rest3);
		editor.putString(B.Rest_Page_url, Rest_URL_Info);
		editor.commit();

		if (!Recently_Ver.equals(App_VerName)) {
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("업데이트")
					.setMessage("업데이트가 있습니다")
					.setPositiveButton("업데이트",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent UpdateCheckURI = new Intent(
											Intent.ACTION_VIEW);
									Uri u = Uri
											.parse("market://details?id=proinfactory.com.hoyeonnuri");
									UpdateCheckURI.setData(u);
									startActivity(UpdateCheckURI);
									finish();
								}
							})
					.setNegativeButton("다음에 하기",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									ToMain();
									dialog.dismiss();
								}
							}).show();
		} else {
			finish();
			ToMain();
		}
	}

	private void ToMain() {
		Intent intent = new Intent(this, Main.class);
		startActivity(intent);
	}

	private void DataBaseXMLParse() {
		if (databasestr == null)
			return;
		if (databasestr.contains("delivery_list version = \"" + DB_VERSION))
			return;
		if (!databasestr.contains("delivery_list version = \"-1")) {
			String ver = databasestr.substring(
					databasestr.indexOf("delivery_list version = \"") + 25,
					databasestr.indexOf("\">"));
			Log.i("TAG", ver);
			SharedPreferences pref = getSharedPreferences("pref",
					Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString(B.DB_VERSION, ver);
			editor.commit();
		}

		DeleteDir(Environment.getExternalStorageDirectory().toString()
				+ "/.HoyeonNuri/");
		mHelper = new DataBaseHelper(this);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			String[] nodeNames = { "code", "name", "tel", "delivery", "time",
					"location", "category", "menu_img", "menu_txt", "banner",
					"sponser", "bestad" };

			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream istream = new ByteArrayInputStream(
					databasestr.getBytes("utf-8"));
			Document doc = builder.parse(istream);

			Element data = doc.getDocumentElement();
			NodeList nodes = data.getElementsByTagName("data");
		
			for (int i = 0; i < nodes.getLength(); i++) {
				String[] strs = new String[nodeNames.length];
				for (int j = 0; j < nodeNames.length; j++) {
					strs[j] = data.getElementsByTagName(nodeNames[j]).item(i)
							.getFirstChild().getNodeValue();
					if (strs[j].equals("NO"))
						strs[j] = "false";
				}
				DATABASE_INSERT(strs);
			}
		} catch (Exception e) {
			Log.i("TAG", "E");
		}
	}

	/**
	 * 
	 * @param addr
	 * @param incoding
	 *            : utf-8, EUC-KR
	 * @return
	 */
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

	private String getSource(String HtmlString, String start_tag, String end_tag) {
		String parsed_data;
		int start = HtmlString.indexOf(start_tag);
		int end = HtmlString.indexOf(end_tag);
		parsed_data = HtmlString.substring(start + start_tag.length(), end);
		return parsed_data;
	}

	private String get_start_location(String HtmlString, String start_tag) {
		int start = HtmlString.indexOf(start_tag);
		HtmlString = HtmlString.substring(start);
		return HtmlString;
	}

	/*---------------------------------------------------------------------------------파싱함수*>*/

	private void POST_LOGIN_DATA(final String idstr, final String passstr) {

		String loginsource = null;
		ResponseHandler<String> responsehandler = new BasicResponseHandler();
		HttpPost httpost = new HttpPost(
				"https://portal.korea.ac.kr/s_exLogin.jsp");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("username", idstr));
		nvps.add(new BasicNameValuePair("password", passstr));
		nvps.add(new BasicNameValuePair("returnURL",
				"dormitel.korea.ac.kr/pub/process/potal_login.php"));
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			loginsource = httpclient.execute(httpost, responsehandler);

			if (PARSE_SOURCE_BY_TAG(loginsource, "name=\"sID\" value=\"", "\">")
					.equals("")) {
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		httpost = new HttpPost(
				"http://dormitel.korea.ac.kr/pub/process/potal_login.php");
		nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("sID", PARSE_SOURCE_BY_TAG(loginsource,
				"name=\"sID\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sUID", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sUID\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sPW", PARSE_SOURCE_BY_TAG(loginsource,
				"name=\"sPW\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sYN", PARSE_SOURCE_BY_TAG(loginsource,
				"name=\"sYN\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sWHY", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sWHY\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sNAME", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sNAME\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sGID", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sGID\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sGIDS", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sGIDS\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sStdId", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sStdId\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sGnm", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sGnm\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sEmail", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sEmail\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sMobile", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sMobile\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sDeptCd", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sDeptCd\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("sDeptNm", PARSE_SOURCE_BY_TAG(
				loginsource, "name=\"sDeptNm\" value=\"", "\">")));
		nvps.add(new BasicNameValuePair("msg", PARSE_SOURCE_BY_TAG(loginsource,
				"name=\"msg\" value=\"", "\">")));

		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httpclient.execute(httpost, responsehandler);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
	}

	private String PARSE_SOURCE_BY_TAG(String HtmlString, String start_tag,
			String end_tag) {
		String parsed_data = HtmlString;
		if (HtmlString == null)
			return null;

		int start = HtmlString.indexOf(start_tag);

		if (start == -1)
			return null;

		parsed_data = get_start_location(parsed_data, start_tag);

		start = parsed_data.indexOf(start_tag);
		int end = parsed_data.indexOf(end_tag);

		parsed_data = parsed_data.substring(start + start_tag.length(), end);

		return parsed_data;
	}

	private boolean DATABASE_INSERT(String[] arData) {

		SQLiteDatabase db;
		ContentValues row;

		db = mHelper.getWritableDatabase();

		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW00_CODE, arData[0]);
		row.put(DataBaseHelper.DB_ROW01_NAME, arData[1]);
		row.put(DataBaseHelper.DB_ROW02_CALL, arData[2]);
		row.put(DataBaseHelper.DB_ROW03_IS_DELIVERY, arData[3]);
		row.put(DataBaseHelper.DB_ROW04_TIME, arData[4]);
		row.put(DataBaseHelper.DB_ROW05_LOCATION, arData[5]);
		row.put(DataBaseHelper.DB_ROW06_CATEGORY, arData[6]);
		row.put(DataBaseHelper.DB_ROW07_MENUIMG, arData[7]);
		row.put(DataBaseHelper.DB_ROW08_MENUTXT, arData[8]);
		row.put(DataBaseHelper.DB_ROW09_BANNER, arData[9]);
		row.put(DataBaseHelper.DB_ROW10_SPONSER, arData[10]);
		row.put(DataBaseHelper.DB_ROW11_BESTAD, arData[11]);

		db.insert(DataBaseHelper.DB_TABLE_NAME, null, row);

		mHelper.close();

		return true;
	}
}
