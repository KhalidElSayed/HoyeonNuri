package proinfactory.com.hoyeonnuri;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.proinlab.hoyeonnuri.functions.DataBaseHelper;

@SuppressLint("HandlerLeak")
public class Delivery_List extends Activity implements OnItemClickListener {

	private ImageButton BackBtn;
	private ImageView SettingBtn;
	private TextView SelectorBtn;
	private AutoCompleteTextView SearchTextView;
	private Button SearchBtn;
	private ImageView BestAdImg;
	private ImageView BestAdLogoImg;
	private RelativeLayout BestAdLayout;

	private Bitmap BestAdBit;
	private ArrayList<String> AdData;

	private ArrayList<ArrayList<String>> arList = new ArrayList<ArrayList<String>>();
	private ListView mList;
	private Delivery_CustomAdapter mAdapter;

	private int displayWidth, displayHeight;

	@SuppressWarnings("rawtypes")
	private ArrayAdapter sAdapter;

	private DataBaseHelper mHelper;

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
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!SelectorBtn.getText().toString().equals(" 전체")) {
				SelectorBtn.setText(" 전체");
				arList = new ArrayList<ArrayList<String>>();
				ArrayList<String[]> arCode = FIND_CODES_BY_CATEGORY(null);
				SORT_BY_SPONSER(arCode);
				for (int i = 0; i < arCode.size(); i++)
					arList.add(FIND_DATA_BY_CODE(arCode.get(i)[0]));
				mAdapter = new Delivery_CustomAdapter(Delivery_List.this,
						arList);
				mList.setAdapter(mAdapter);

				if (GET_BESTAD_DATA().size() != 0)
					open();
			} else {
				finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, msg);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delivery_list);

		mHelper = new DataBaseHelper(this);

		Display display = getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		if (displayWidth > displayHeight) {
			displayHeight = display.getWidth();
			displayWidth = display.getHeight();
		}

		mList = (ListView) findViewById(R.id.delivery_listview);
		BackBtn = (ImageButton) findViewById(R.id.delivery_navi_backbtn);
		SettingBtn = (ImageView) findViewById(R.id.delivery_navi_settingbtn);
		SelectorBtn = (TextView) findViewById(R.id.delivery_navi_selector);
		SearchTextView = (AutoCompleteTextView) findViewById(R.id.delivery_autosearch);
		SearchBtn = (Button) findViewById(R.id.delivery_searchbtn);
		BestAdImg = (ImageView) findViewById(R.id.delivery_bestadimg);
		BestAdLayout = (RelativeLayout) findViewById(R.id.delivery_bestadlayout);
		BestAdLogoImg = (ImageView) findViewById(R.id.delivery_bestadlogo);

		// Etc
		BackBtn.setOnClickListener(mOnClick);
		SettingBtn.setOnClickListener(mOnClick);
		SelectorBtn.setOnClickListener(mOnClick);
		SearchBtn.setOnClickListener(mOnClick);

		SelectorBtn.setText(" 전체");

		ArrayList<String[]> arCode = FIND_CODES_BY_CATEGORY(null);
		SORT_BY_SPONSER(arCode);

		for (int i = 0; i < arCode.size(); i++)
			arList.add(FIND_DATA_BY_CODE(arCode.get(i)[0]));

		mAdapter = new Delivery_CustomAdapter(this, arList);

		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(this);

		// 검색 기능
		String[] col = new String[arList.size()];
		for (int i = 0; i < arList.size(); i++) {
			col[i] = arList.get(i).get(A.Delivery_name);
		}
		sAdapter = new ArrayAdapter(this,
				android.R.layout.simple_dropdown_item_1line, col);
		SearchTextView.setAdapter(sAdapter);
		SearchTextView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		SearchTextView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							String Searching_name = SearchTextView.getText()
									.toString();
							SelectorBtn.setText(" 검색 : " + Searching_name);
							mAdapter = new Delivery_CustomAdapter(
									Delivery_List.this,
									FIND_DATA_BY_NAME(Searching_name));
							mList.setAdapter(mAdapter);
							SearchTextView.setText("");
							SearchTextView.postDelayed(new Runnable() {
								@Override
								public void run() {
									InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									keyboard.hideSoftInputFromWindow(
											SearchTextView.getWindowToken(), 0);
								}
							}, 200);
							if (GET_BESTAD_DATA().size() != 0)
								open();
							return true;
						}
						return false;
					}
				});

		SearchTextView.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.hideSoftInputFromWindow(
						SearchTextView.getWindowToken(), 0);
			}
		}, 200);

		// Best AD
		if (GET_BESTAD_DATA().size() == 0)
			BestAdLayout.setVisibility(View.GONE);
		else
			open();
	}

	private View.OnClickListener mOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.delivery_searchbtn:
				// XXX
				String Searching_name = SearchTextView.getText().toString();
				SelectorBtn.setText(" 검색 : " + Searching_name);
				mAdapter = new Delivery_CustomAdapter(Delivery_List.this,
						FIND_DATA_BY_NAME(Searching_name));
				mList.setAdapter(mAdapter);
				SearchTextView.setText("");
				SearchTextView.postDelayed(new Runnable() {
					@Override
					public void run() {
						InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						keyboard.hideSoftInputFromWindow(
								SearchTextView.getWindowToken(), 0);
					}
				}, 200);
				if (GET_BESTAD_DATA().size() != 0)
					open();
				break;
			case R.id.delivery_navi_backbtn:
				finish();
				break;
			case R.id.delivery_navi_selector:
				AlertDialog.Builder selectorDialog = new AlertDialog.Builder(
						Delivery_List.this);
				selectorDialog.setTitle("카테고리");
				ArrayList<String> arrStr = new ArrayList<String>();
				arrStr.add("전체");
				arrStr.add("치킨/탕수육");
				arrStr.add("피자");
				arrStr.add("중국집");
				arrStr.add("족발/보쌈");
				arrStr.add("식사류");
				ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(
						Delivery_List.this,
						android.R.layout.select_dialog_item, arrStr);
				selectorDialog.setAdapter(arrayAdt,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									SelectorBtn.setText(" 전체");

									arList = new ArrayList<ArrayList<String>>();
									ArrayList<String[]> arCode = FIND_CODES_BY_CATEGORY(null);
									SORT_BY_SPONSER(arCode);
									for (int i = 0; i < arCode.size(); i++)
										arList.add(FIND_DATA_BY_CODE(arCode
												.get(i)[0]));
									break;
								case 1:
									SelectorBtn.setText(" 치킨/탕수육");

									arList = new ArrayList<ArrayList<String>>();
									arCode = FIND_CODES_BY_CATEGORY("치킨/탕수육");
									SORT_BY_SPONSER(arCode);
									for (int i = 0; i < arCode.size(); i++)
										arList.add(FIND_DATA_BY_CODE(arCode
												.get(i)[0]));
									break;
								case 2:
									SelectorBtn.setText(" 피자");

									arList = new ArrayList<ArrayList<String>>();
									arCode = FIND_CODES_BY_CATEGORY("피자");
									SORT_BY_SPONSER(arCode);
									for (int i = 0; i < arCode.size(); i++)
										arList.add(FIND_DATA_BY_CODE(arCode
												.get(i)[0]));
									break;
								case 3:
									SelectorBtn.setText(" 중국집");

									arList = new ArrayList<ArrayList<String>>();
									arCode = FIND_CODES_BY_CATEGORY("중국집");
									SORT_BY_SPONSER(arCode);
									for (int i = 0; i < arCode.size(); i++)
										arList.add(FIND_DATA_BY_CODE(arCode
												.get(i)[0]));
									break;
								case 4:
									SelectorBtn.setText(" 족발/보쌈");

									arList = new ArrayList<ArrayList<String>>();
									arCode = FIND_CODES_BY_CATEGORY("족발/보쌈");
									SORT_BY_SPONSER(arCode);
									for (int i = 0; i < arCode.size(); i++)
										arList.add(FIND_DATA_BY_CODE(arCode
												.get(i)[0]));
									break;
								case 5:
									SelectorBtn.setText(" 식사류");

									arList = new ArrayList<ArrayList<String>>();
									arCode = FIND_CODES_BY_CATEGORY("식사류");
									SORT_BY_SPONSER(arCode);
									for (int i = 0; i < arCode.size(); i++)
										arList.add(FIND_DATA_BY_CODE(arCode
												.get(i)[0]));
									break;
								}
								mAdapter = new Delivery_CustomAdapter(
										Delivery_List.this, arList);
								mList.setAdapter(mAdapter);
								if (GET_BESTAD_DATA().size() != 0)
									open();
							}
						});
				selectorDialog.show();
				break;
			case R.id.delivery_navi_settingbtn:
				Intent intent = new Intent(Delivery_List.this, Setting.class);
				startActivity(intent);
				break;
			}
		}
	};

	@SuppressWarnings("unchecked")
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		ArrayList<String> data = (ArrayList<String>) parent
				.getItemAtPosition(position);
		Bundle extras = new Bundle();
		extras.putString("Intent_Code", data.get(A.Delivery_name));
		Intent intent = new Intent(this, Delivery_Detail.class);

		intent.putExtras(extras);
		startActivity(intent);
	}

	// XXX BestAD
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					displayWidth, displayWidth / 6);
			BestAdImg.setLayoutParams(rlp);
			rlp = new RelativeLayout.LayoutParams(displayWidth / 6,
					displayWidth / 8);
			rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			BestAdLogoImg.setLayoutParams(rlp);
			Drawable draw = (Drawable) new BitmapDrawable(BestAdBit);

			if (BestAdImg == null)
				;
			else {
				BestAdImg.setBackgroundDrawable(draw);
				BestAdImg.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Bundle extras = new Bundle();
						extras.putString("Intent_Code",
								AdData.get(A.Delivery_code));
						Intent intent = new Intent(Delivery_List.this,
								Delivery_Detail.class);
						intent.putExtras(extras);
						startActivity(intent);
					}
				});
			}
		}
	};

	void open() {
		try {
			process();
		} catch (IOException e) {
		}
	}

	void process() throws IOException {
		final Handler mHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					ArrayList<ArrayList<String>> tmpDataList = GET_BESTAD_DATA();
					int rand = (int) (Math.random() * tmpDataList.size());
					AdData = tmpDataList.get(rand);
					InputStream is = new URL(AdData.get(A.Delivery_bestad))
							.openStream();
					BestAdBit = BitmapFactory.decodeStream(is);
					is.close();

					mHandler.post(new Runnable() {
						public void run() {
							handler.sendEmptyMessage(0);
						}
					});
				} catch (Exception e) {
				}
			}
		}.start();
	}

	// DB 검색

	private ArrayList<String[]> FIND_CODES_BY_CATEGORY(String Category) {

		SQLiteDatabase db;

		ArrayList<String[]> returnStr = new ArrayList<String[]>();
		String[] columns = { DataBaseHelper.DB_ROW00_CODE,
				DataBaseHelper.DB_ROW01_NAME, DataBaseHelper.DB_ROW02_CALL,
				DataBaseHelper.DB_ROW03_IS_DELIVERY,
				DataBaseHelper.DB_ROW04_TIME, DataBaseHelper.DB_ROW05_LOCATION,
				DataBaseHelper.DB_ROW06_CATEGORY,
				DataBaseHelper.DB_ROW07_MENUIMG,
				DataBaseHelper.DB_ROW08_MENUTXT,
				DataBaseHelper.DB_ROW09_BANNER,
				DataBaseHelper.DB_ROW10_SPONSER, DataBaseHelper.DB_ROW11_BESTAD };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0)
			returnStr = new ArrayList<String[]>();
		else {
			returnStr = new ArrayList<String[]>();
			while (cursor.moveToNext()) {
				if (Category != null) {
					if (cursor.getString(A.Delivery_category).equals(Category)) {
						String[] str = new String[2];
						str[0] = cursor.getString(A.Delivery_code);
						str[1] = cursor.getString(A.Delivery_sponser);
						returnStr.add(str);
					}
				} else {
					if (Integer.parseInt(cursor.getString(A.Delivery_code)) < 0) {

					} else {
						String[] str = new String[2];
						str[0] = cursor.getString(A.Delivery_code);
						str[1] = cursor.getString(A.Delivery_sponser);
						returnStr.add(str);
					}
				}

			}
		}
		cursor.close();
		mHelper.close();
		return returnStr;
	}

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
				DataBaseHelper.DB_ROW09_BANNER,
				DataBaseHelper.DB_ROW10_SPONSER, DataBaseHelper.DB_ROW11_BESTAD };

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

	private ArrayList<ArrayList<String>> FIND_DATA_BY_NAME(String name) {

		SQLiteDatabase db;

		ArrayList<ArrayList<String>> returnStr;
		String[] columns = { DataBaseHelper.DB_ROW00_CODE,
				DataBaseHelper.DB_ROW01_NAME, DataBaseHelper.DB_ROW02_CALL,
				DataBaseHelper.DB_ROW03_IS_DELIVERY,
				DataBaseHelper.DB_ROW04_TIME, DataBaseHelper.DB_ROW05_LOCATION,
				DataBaseHelper.DB_ROW06_CATEGORY,
				DataBaseHelper.DB_ROW07_MENUIMG,
				DataBaseHelper.DB_ROW08_MENUTXT,
				DataBaseHelper.DB_ROW09_BANNER,
				DataBaseHelper.DB_ROW10_SPONSER, DataBaseHelper.DB_ROW11_BESTAD };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0)
			returnStr = new ArrayList<ArrayList<String>>();
		else {
			returnStr = new ArrayList<ArrayList<String>>();
			while (cursor.moveToNext()) {
				if (name != null) {
					if (cursor.getString(A.Delivery_name).indexOf(name) != -1) {
						if (Integer.parseInt(cursor.getString(A.Delivery_code)) < 0) {

						} else {
							ArrayList<String> tmpArray = new ArrayList<String>();
							for (int i = 0; i < 11; i++)
								tmpArray.add(cursor.getString(i));
							returnStr.add(tmpArray);
						}
					}
				} else {
					if (Integer.parseInt(cursor.getString(A.Delivery_code)) < 0) {

					} else {
						ArrayList<String> tmpArray = new ArrayList<String>();
						for (int i = 0; i < 11; i++)
							tmpArray.add(cursor.getString(i));
						returnStr.add(tmpArray);
					}
				}

			}
		}
		cursor.close();
		mHelper.close();
		return returnStr;
	}

	private ArrayList<ArrayList<String>> GET_BESTAD_DATA() {

		SQLiteDatabase db;

		ArrayList<ArrayList<String>> returnStr;
		String[] columns = { DataBaseHelper.DB_ROW00_CODE,
				DataBaseHelper.DB_ROW01_NAME, DataBaseHelper.DB_ROW02_CALL,
				DataBaseHelper.DB_ROW03_IS_DELIVERY,
				DataBaseHelper.DB_ROW04_TIME, DataBaseHelper.DB_ROW05_LOCATION,
				DataBaseHelper.DB_ROW06_CATEGORY,
				DataBaseHelper.DB_ROW07_MENUIMG,
				DataBaseHelper.DB_ROW08_MENUTXT,
				DataBaseHelper.DB_ROW09_BANNER,
				DataBaseHelper.DB_ROW10_SPONSER, DataBaseHelper.DB_ROW11_BESTAD };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0)
			returnStr = new ArrayList<ArrayList<String>>();
		else {
			returnStr = new ArrayList<ArrayList<String>>();
			while (cursor.moveToNext()) {
				if (!cursor.getString(A.Delivery_bestad).equals("false")) {
					ArrayList<String> tmpArray = new ArrayList<String>();
					for (int i = 0; i < 12; i++)
						tmpArray.add(cursor.getString(i));
					returnStr.add(tmpArray);
				}
			}
		}
		cursor.close();
		mHelper.close();
		return returnStr;
	}

	private ArrayList<String[]> SORT_BY_SPONSER(ArrayList<String[]> data) {
		ArrayList<String[]> returnDATA = data;
		String[] tmp;

		for (int i = 0; i < returnDATA.size(); i++) {
			for (int j = i + 1; j < returnDATA.size(); j++) {
				if (Integer.parseInt(returnDATA.get(i)[1]) < Integer
						.parseInt(returnDATA.get(j)[1])) {
					tmp = returnDATA.get(i);
					returnDATA.set(i, returnDATA.get(j));
					returnDATA.set(j, tmp);
				}
			}
		}

		return returnDATA;
	}
}
