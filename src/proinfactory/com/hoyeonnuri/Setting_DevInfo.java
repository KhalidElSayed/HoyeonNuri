package proinfactory.com.hoyeonnuri;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Setting_DevInfo extends Activity {
	private ImageButton Backbtn;

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
		setContentView(R.layout.setting_devinfo);

		Backbtn = (ImageButton) findViewById(R.id.setting_navi_backbtn);

		Backbtn.setOnClickListener(mOnClick);
	}

	private View.OnClickListener mOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.setting_navi_backbtn:
				finish();
				break;
			}
		}
	};
}
