package proinfactory.com.hoyeonnuri;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.proinlab.hoyeonnuri.functions.DataBaseHelper;

public class DBEditFn {

	public boolean INSERT(SQLiteOpenHelper mHelper, ArrayList<String> arData) {

		SQLiteDatabase db;
		ContentValues row;
		
		Log.e("TAG","1");
		db = mHelper.getWritableDatabase();
		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW00_CODE, arData.get(0));
		row.put(DataBaseHelper.DB_ROW01_NAME, arData.get(1));
		row.put(DataBaseHelper.DB_ROW02_CALL, arData.get(2));
		row.put(DataBaseHelper.DB_ROW03_IS_DELIVERY, arData.get(3));
		row.put(DataBaseHelper.DB_ROW04_TIME, arData.get(4));
		row.put(DataBaseHelper.DB_ROW05_LOCATION, arData.get(5));
		row.put(DataBaseHelper.DB_ROW06_CATEGORY, arData.get(6));
		row.put(DataBaseHelper.DB_ROW07_MENUIMG, arData.get(7));
		row.put(DataBaseHelper.DB_ROW08_MENUTXT, arData.get(8));
		row.put(DataBaseHelper.DB_ROW09_BANNER, arData.get(9));

		Log.e("TAG","2");
		
		db.insert(DataBaseHelper.DB_TABLE_NAME, null, row);
		Log.e("TAG","3");
		
		mHelper.close();

		return true;
	}

	public boolean DELETE_ALL(SQLiteOpenHelper mHelper) {
		SQLiteDatabase db;
		db = mHelper.getWritableDatabase();
		db.delete(DataBaseHelper.DB_TABLE_NAME, null, null);
		mHelper.close();
		return true;
	}

}
