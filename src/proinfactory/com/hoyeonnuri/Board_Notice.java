/*
 * 게시판 주소 목록  
 * Free : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=part060301&page=
 * QnA : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=part060201&page=
 * Repair : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=part060101&page=
 * Notice : http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=know050101&page=
 */

package proinfactory.com.hoyeonnuri;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public class Board_Notice extends Activity implements OnItemClickListener {

	private ImageButton BackBtn;
	private ImageView SettingBtn;
	private TextView SelectorBtn;

	private int cur_page = 1;
	private ArrayList<ArrayList<String>> arList = new ArrayList<ArrayList<String>>();
	private ListView MyList;
	private Board_CustomAdapter MyAdapter;
	private String site_URL = "http://dormitel.korea.ac.kr/pub/board/bbs_free.html?cboardID=know050101&page=";
	private View footer;
	private View footer_ready;

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1)
				return;
			listUpdate();
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
		setContentView(R.layout.board_list);

		MyAdapter = new Board_CustomAdapter(this, R.layout.listview_contents,
				arList);

		MyList = (ListView) findViewById(R.id.board_listview);
		BackBtn = (ImageButton) findViewById(R.id.board_navi_backbtn);
		SettingBtn = (ImageView) findViewById(R.id.board_navi_settingbtn);
		SelectorBtn = (TextView) findViewById(R.id.board_navi_selector);

		BackBtn.setOnClickListener(mOnClick);
		SettingBtn.setOnClickListener(mOnClick);
		SelectorBtn.setOnClickListener(mOnClick);

		SelectorBtn.setText(" 공지사항");

		footer = getLayoutInflater().inflate(R.layout.footer, null, false);
		footer_ready = getLayoutInflater().inflate(R.layout.footer_ready, null,
				false);

		MyList.addHeaderView(footer_ready);
		MyList.setAdapter(MyAdapter);
		MyList.setOnItemClickListener(this);

		Board_Parsing board_parsing = new Board_Parsing(handler, arList,
				site_URL, cur_page);
		board_parsing.open();

	}

	private View.OnClickListener mOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.board_navi_backbtn:
				finish();
				break;
			case R.id.board_navi_selector:
				AlertDialog.Builder selectorDialog = new AlertDialog.Builder(
						Board_Notice.this);
				selectorDialog.setTitle("게시판");
				ArrayList<String> arrStr = new ArrayList<String>();
				arrStr.add("QnA");
				arrStr.add("자유게시판");
				arrStr.add("수리해주세요");
				ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(
						Board_Notice.this, android.R.layout.select_dialog_item,
						arrStr);
				selectorDialog.setAdapter(arrayAdt,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent;
								switch (which) {
								case 0:
									intent = new Intent(Board_Notice.this,
											Board_QnA.class);
									startActivity(intent);
									finish();
									break;
								case 1:
									intent = new Intent(Board_Notice.this,
											Board_Free.class);
									startActivity(intent);
									finish();
									break;
								case 2:
									intent = new Intent(Board_Notice.this,
											Board_Repair.class);
									startActivity(intent);
									finish();
									break;
								}
							}
						});
				selectorDialog.show();
				break;
			case R.id.board_navi_settingbtn:
				Intent intent = new Intent(Board_Notice.this,Setting.class);
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
		showDialog(0);
		Board_Parsing board_parsing = new Board_Parsing(handler, arList,
				site_URL, cur_page);
		board_parsing.open();
	}

	// Thread 후 실행할 함수
	private void listUpdate() {
		MyAdapter.notifyDataSetChanged();
		MyList.removeHeaderView(footer_ready);
		MyList.removeFooterView(footer_ready);
		MyList.addFooterView(footer);
	}

	// 리스트뷰 아이템 클릭
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		ArrayList<String> data = (ArrayList<String>) parent
				.getItemAtPosition(position);
		Bundle extras = new Bundle();
		extras.putString("Intent_URL", data.get(A.Board_boardpage));
		extras.putString("Intent_Title", data.get(A.Board_data));
		extras.putString("Intent_Secret", data.get(A.Board_secret_board));
		extras.putString("Intent_comment", data.get(A.Board_comment));
		extras.putString("Intent_writer", data.get(A.Board_writer));
		extras.putString("Intent_date", data.get(A.Board_date));
		Intent intent = new Intent(this, Board_WebViewCtrl.class);

		intent.putExtras(extras);
		startActivity(intent);
	}

}
