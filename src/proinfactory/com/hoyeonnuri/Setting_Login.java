package proinfactory.com.hoyeonnuri;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.analytics.tracking.android.EasyTracker;
import com.proinlab.hoyeonnuri.functions.B;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Setting_Login extends Activity {

	private DefaultHttpClient httpclient = Loading.httpclient;

	private RelativeLayout LoginLayout, LogoutLayout;
	private Button LoginBtn, LogoutBtn;
	private CheckBox AutoLogin;
	private EditText setId, setPassword;
	private ImageButton BackBtn;

	private boolean loginFail = false;
	private boolean loginbool = false;
	private boolean autoLogin = false;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dismissDialog(0);
			if (msg.what == 0) {
				if (loginbool) {
					LogoutLayout.setVisibility(View.INVISIBLE);
					LoginLayout.setVisibility(View.VISIBLE);
				} else {
					LogoutLayout.setVisibility(View.VISIBLE);
					LoginLayout.setVisibility(View.INVISIBLE);
				}
			} else if (msg.what == 1) {
				open(0);
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putString(B.id, setId.getText().toString());
				editor.putString(B.pw, setPassword.getText().toString());
				editor.commit();
			} else if (msg.what == 2) {
				AutoLogin.setChecked(false);
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean(B.autoLogin, false);
				editor.commit();
				if (loginFail) {
					loginFail = false;
				} else {
					open(0);
				}
			}
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_login);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);

		autoLogin = pref.getBoolean(B.autoLogin, false);

		LoginLayout = (RelativeLayout) findViewById(R.id.setting_login_loginlayout);
		LogoutLayout = (RelativeLayout) findViewById(R.id.setting_login_logoutlayout);
		LogoutBtn = (Button) findViewById(R.id.setting_login_logoutbtn);
		LoginBtn = (Button) findViewById(R.id.setting_login_loginbtn);
		AutoLogin = (CheckBox) findViewById(R.id.setting_login_setauto);
		setId = (EditText) findViewById(R.id.setting_login_id);
		setPassword = (EditText) findViewById(R.id.setting_login_password);
		BackBtn = (ImageButton) findViewById(R.id.setting_navi_backbtn);
		LogoutLayout.setVisibility(View.INVISIBLE);
		LoginLayout.setVisibility(View.INVISIBLE);

		open(0);

		AutoLogin.setChecked(autoLogin);

		AutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean(B.autoLogin, isChecked);
				editor.commit();
			}

		});

		LoginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (setPassword.getText().toString().length() == 0) {
					Toast.makeText(Setting_Login.this, "비밀번호를 입력해주세요",
							Toast.LENGTH_LONG).show();
				} else if (setId.getText().toString().length() == 0) {
					Toast.makeText(Setting_Login.this, "아이디를 입력해주세요",
							Toast.LENGTH_LONG).show();
				} else {
					open(1);
				}
			}
		});

		LogoutBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				open(2);
			}
		});

		BackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	void open(int what) {
		showDialog(0);
		try {
			process(what);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void process(int what) throws IOException {
		final int send = what;
		final String id = setId.getText().toString();
		final String pw = setPassword.getText().toString();
		new Thread() {
			@Override
			public void run() {
				if (send == 0) {
					String str = HtmlToString("http://dormitel.korea.ac.kr/main.html");
					if (str.indexOf("로그인중입니다") == -1)
						loginbool = true;
					else
						loginbool = false;

				} else if (send == 1) {
					POST_LOGIN_DATA(id, pw);
				} else if (send == 2) {
					HtmlToString("http://dormitel.korea.ac.kr/pub/process/member_logout.php");
				}

				handler.post(new Runnable() {
					public void run() {
						handler.sendEmptyMessage(send);
					}
				});
			}
		}.start();
	}

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
				loginFail = true;
			}
		} catch (ClientProtocolException e) {
			loginFail = true;
		} catch (IOException e) {
			loginFail = true;
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
			loginFail = true;
		} catch (IOException e) {
			loginFail = true;
		}
	}

	private String PARSE_SOURCE_BY_TAG(String HtmlString, String start_tag,
			String end_tag) {
		String parsed_data = HtmlString;
		int start = HtmlString.indexOf(start_tag);

		if (start == -1)
			return null;

		parsed_data = get_start_location(parsed_data, start_tag);

		start = parsed_data.indexOf(start_tag);
		int end = parsed_data.indexOf(end_tag);

		parsed_data = parsed_data.substring(start + start_tag.length(), end);

		return parsed_data;
	}

	private String get_start_location(String HtmlString, String start_tag) {
		int start = HtmlString.indexOf(start_tag);
		HtmlString = HtmlString.substring(start);
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
			htmlSource = "noSource";
		}
		return htmlSource;
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			final LinearLayout linear = (LinearLayout) View.inflate(this,
					R.layout.processing_dialog, null);
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
			alt_bld.setView(linear);
			AlertDialog alert = alt_bld.create();
			alert.show();
			return alert;
		}
		return null;
	}
}
