/*
 * 게시판 주소 목록  
 * Free : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=part060301&page=
 * QnA : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=part060201&page=
 * Repair : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=part060101&page=
 * Notice : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=know050101&page=
 */

package proinfactory.com.hoyeonnuri;

import java.util.*;

import com.google.analytics.tracking.android.EasyTracker;

import proinfactory.com.hoyeonnuri.R;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class Library_info_sejong extends Activity implements
		OnItemClickListener {

	private ArrayList<Function_Parsed_datalist> arList = new ArrayList<Function_Parsed_datalist>();
	private ListView MyList;
	private Library_info_CustomAdapter MyAdapter;
	private String site_URL = "http://163.152.221.20/domian5.asp";
	private View footer_ready;

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			listUpdate();
		}
	};

	private ImageButton BackBtn;
	private ImageView SettingBtn;
	private TextView Title;

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
		Title = (TextView) findViewById(R.id.info_navi_text);

		Title.setText("세종캠퍼스 도서관 잔여석");

		BackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		SettingBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Library_info_sejong.this,
						Setting.class);
				startActivity(intent);
			}
		});

		MyAdapter = new Library_info_CustomAdapter(this,
				R.layout.library_listview_contents, arList);

		MyList = (ListView) findViewById(R.id.info_listview);

		footer_ready = getLayoutInflater().inflate(R.layout.footer_ready, null,
				false);
		MyList.addHeaderView(footer_ready);

		MyList.setAdapter(MyAdapter);
		MyList.setOnItemClickListener(this);

		// Thread 시작
		Library_info_sejong_Parsing board_parsing = new Library_info_sejong_Parsing(
				handler, arList, site_URL);
		board_parsing.open();
	}

	// ProgressBar Footer 클릭 방지
	public void Ready_onClick(View arg0) {
	}

	// Thread 후 실행할 함수
	private void listUpdate() {
		MyAdapter.notifyDataSetChanged();
		MyList.removeHeaderView(footer_ready);
	}

	// 리스트뷰 아이템 클릭
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Function_Parsed_datalist data = (Function_Parsed_datalist) parent
				.getItemAtPosition(position);
		Bundle extras = new Bundle();
		String liburl = "http://163.152.221.20/" + data.getURL();
		extras.putString("Intent_URL", liburl);
		extras.putString("Intent_Title", data.getData());
		Intent intent = new Intent(this, Info_Webview.class);

		intent.putExtras(extras);
		startActivity(intent);
	}
}
