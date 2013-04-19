package proinfactory.com.hoyeonnuri;

import org.apache.http.impl.client.DefaultHttpClient;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Setting extends Activity {

	public static DefaultHttpClient httpclient = Loading.httpclient;

	private ImageButton LoginBtn, DevInfoBtn, Backbtn;

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
		setContentView(R.layout.setting);

		LoginBtn = (ImageButton) findViewById(R.id.setting_loginbtn);
		DevInfoBtn = (ImageButton) findViewById(R.id.setting_devinfo);
		Backbtn = (ImageButton) findViewById(R.id.setting_navi_backbtn);

		LoginBtn.setOnClickListener(mOnClick);
		DevInfoBtn.setOnClickListener(mOnClick);
		Backbtn.setOnClickListener(mOnClick);
	}

	private View.OnClickListener mOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
			case R.id.setting_devinfo:
				intent = new Intent(Setting.this, Setting_DevInfo.class);
				startActivity(intent);
				break;
			case R.id.setting_loginbtn:
				intent = new Intent(Setting.this, Setting_Login.class);
				startActivity(intent);
				break;
			case R.id.setting_navi_backbtn:
				finish();
				break;
			}
		}
	};
}
