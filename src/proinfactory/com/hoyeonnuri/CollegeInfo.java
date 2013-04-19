package proinfactory.com.hoyeonnuri;

import java.util.*;

import com.google.analytics.tracking.android.EasyTracker;

import proinfactory.com.hoyeonnuri.R;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class CollegeInfo extends Activity implements OnItemClickListener {

	private ArrayList<Info_datalist> arList = new ArrayList<Info_datalist>();
	private ListView MyList;
	private Info_CustomAdapter MyAdapter;

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
		setContentView(R.layout.info_list);

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
				Intent intent = new Intent(CollegeInfo.this, Setting.class);
				startActivity(intent);
			}
		});

		// 정보 리스트 추가
		Info_datalist businfo = new Info_datalist(
				0,
				0,
				"http://mob.korea.ac.kr/hoyeonnuri/popup_notice/bus_timetable.html",
				"셔틀버스 시간");
		arList.add(businfo);
		Info_datalist sejong_library = new Info_datalist(1, 0, null,
				"세종캠퍼스 도서관 잔여석");
		arList.add(sejong_library);
		Info_datalist library = new Info_datalist(0, 0,
				"http://library.korea.ac.kr/html/ko/readingroom.html",
				"안암캠퍼스 도서관 잔여석");
		arList.add(library);

		// 리스트뷰 적용
		MyAdapter = new Info_CustomAdapter(this,
				R.layout.info_listview_contents, arList);
		MyList = (ListView) findViewById(R.id.info_listview);

		MyList.setAdapter(MyAdapter);
		MyList.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	}

}