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

public class Restaurant_Notice extends Activity implements OnItemClickListener {

	private int cur_page = 1;
	private ArrayList<Function_Parsed_datalist> arList = new ArrayList<Function_Parsed_datalist>();
	private ListView MyList;
	private Restaurant_CustomAdapter MyAdapter;
	private String site_URL = "http://sejong.welstory.com/sejong/notice/notice_list.jsp?pg=";
	private View footer;
	private View footer_ready;

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			listUpdate();
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restaurant_list);

		BackBtn = (ImageButton) findViewById(R.id.res_navi_backbtn);
		DropDownMenu = (TextView) findViewById(R.id.res_navi_selector);
		SettingBtn = (ImageView) findViewById(R.id.res_navi_settingbtn);

		BackBtn.setOnClickListener(mOnClick);
		DropDownMenu.setOnClickListener(mOnClick);
		SettingBtn.setOnClickListener(mOnClick);

		DropDownMenu.setText(" 공지사항");
		
		MyAdapter = new Restaurant_CustomAdapter(this,
				R.layout.listview_contents, arList);

		MyList = (ListView) findViewById(R.id.res_listview);

		footer = getLayoutInflater().inflate(R.layout.footer, null, false);
		footer_ready = getLayoutInflater().inflate(R.layout.footer_ready, null,
				false);

		MyList.addHeaderView(footer_ready);
		MyList.setAdapter(MyAdapter);
		MyList.setOnItemClickListener(this);

		// Thread 시작
		Restaurant_BoardParsing board_parsing = new Restaurant_BoardParsing(
				handler, arList, site_URL, cur_page);
		board_parsing.open();
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
						Restaurant_Notice.this);
				selectorDialog.setTitle("게시판");
				ArrayList<String> arrStr = new ArrayList<String>();
				arrStr.add("진리관");
				arrStr.add("호연4관");
				arrStr.add("학생회관");
				ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(
						Restaurant_Notice.this,
						android.R.layout.select_dialog_item, arrStr);
				selectorDialog.setAdapter(arrayAdt,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent;
								switch (which) {
								case 0:
									intent = new Intent(Restaurant_Notice.this,
											Restaurant_Jinli.class);
									startActivity(intent);
									finish();
									break;
								case 1:
									intent = new Intent(Restaurant_Notice.this,
											Restaurant_Hoyeon.class);
									startActivity(intent);
									finish();
									break;
								case 2:
									intent = new Intent(Restaurant_Notice.this,
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
				Intent intent = new Intent(Restaurant_Notice.this, Setting.class);
				startActivity(intent);
				break;
			}
		}
	};

	// ProgressBar Footer 클릭 방지
	public void Ready_onClick(View arg0) {
	}

	// 더보기 Footer 클릭
	public void Moreview_onClick(View arg0) {
		MyList.removeFooterView(footer);
		MyList.addFooterView(footer_ready);
		cur_page++;
		Restaurant_BoardParsing board_parsing = new Restaurant_BoardParsing(
				handler, arList, site_URL, cur_page);
		board_parsing.open();
	}

	// Thread 후 실행할 함수
	private void listUpdate() {
		MyAdapter.notifyDataSetChanged();
		MyList.removeFooterView(footer_ready);
		MyList.removeHeaderView(footer_ready);
	}

	// 리스트뷰 아이템 클릭
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Function_Parsed_datalist data = (Function_Parsed_datalist) parent
				.getItemAtPosition(position);
		Bundle extras = new Bundle();
		extras.putString("Intent_URL", data.getURL());
		extras.putString("Intent_Title", data.getData());
		extras.putString("Intent_Secret", data.getSecret());
		extras.putString("Intent_comment", data.getComment());
		extras.putString("Intent_writer", data.getWriter());
		extras.putString("Intent_date", data.getDate());
		Intent intent = new Intent(this, Restaurant_WebViewCtrl.class);

		intent.putExtras(extras);
		startActivity(intent);
	}
}