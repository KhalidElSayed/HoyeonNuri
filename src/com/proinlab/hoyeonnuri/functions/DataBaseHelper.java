/*****************************************************************************
 * @author PROIN LAB [ DB ±¸Á¶ ]
 *         -------------------------------------------------------------------
 *         TABLE : FILE_LIST, CATEGORY_LIST
 *         -------------------------------------------------------------------
 *         FILE_LIST ROW : FILENAME LATESTTIME FIRSTTIME CATEGORY FILEDIR
 *         FILETYPE
 *         -------------------------------------------------------------------
 *         CATEGORY_LIST ROW : CATEGORY
 *****************************************************************************/

package com.proinlab.hoyeonnuri.functions;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DataBaseHelper extends SQLiteOpenHelper {

	public static final String DATABASENAME = Environment
			.getExternalStorageDirectory().toString()
			+ "/.HoyeonNuri/database.db";

	public static final String DB_TABLE_NAME = "DeliveryData";

	public static final String DB_ROW00_CODE = "CODE";
	public static final String DB_ROW01_NAME = "NAME";
	public static final String DB_ROW02_CALL = "CALL";
	public static final String DB_ROW03_IS_DELIVERY = "ISDELIVERY";
	public static final String DB_ROW04_TIME = "TIME";
	public static final String DB_ROW05_LOCATION = "LOCATION";
	public static final String DB_ROW06_CATEGORY = "CATEGORY";
	public static final String DB_ROW07_MENUIMG = "MENUIMG";
	public static final String DB_ROW08_MENUTXT = "MENUTXT";
	public static final String DB_ROW09_BANNER = "BANNER";
	public static final String DB_ROW10_SPONSER = "SPONSER";
	public static final String DB_ROW11_BESTAD = "BESTAD";

	public static final String[] DB_COLUMNS = { "FILENAME", "FILEDETAIL",
			"FILEDIR" };

	public DataBaseHelper(Context context) {
		super(context, DATABASENAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DB_TABLE_NAME
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + DB_ROW00_CODE
				+ " TEXT, " + DB_ROW01_NAME + " TEXT, " + DB_ROW02_CALL
				+ " TEXT, " + DB_ROW03_IS_DELIVERY + " TEXT, " + DB_ROW04_TIME
				+ " TEXT, " + DB_ROW05_LOCATION + " TEXT, " + DB_ROW06_CATEGORY
				+ " TEXT, " + DB_ROW07_MENUIMG + " TEXT, " + DB_ROW08_MENUTXT
				+ " TEXT, " + DB_ROW09_BANNER + " TEXT, " + DB_ROW10_SPONSER
				+ " TEXT, " + DB_ROW11_BESTAD + " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
		onCreate(db);
	}

}
