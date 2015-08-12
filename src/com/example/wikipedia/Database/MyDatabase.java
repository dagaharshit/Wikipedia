package com.example.wikipedia.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase extends SQLiteOpenHelper {

	// Database Name and version
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "WikiDiscussion.db";

	// table name and its column..
	private static final String TABLE_DISCUSSION = "tblDiscussion";
	private static final String KEY_ID = "ID";
	private static final String KEY_SEARCH = "Search";
	private static final String KEY_RESULT = "Result";



	public MyDatabase(Context context, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_TABLE_DISCUSSION = "CREATE TABLE " + TABLE_DISCUSSION
				+ "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_SEARCH + " Text NOT NULL, " + KEY_RESULT + " Text);";

		db.execSQL(CREATE_TABLE_DISCUSSION);



	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		String DROP_TABLE_DISCUSSION = "Drop table if exists "
				+ TABLE_DISCUSSION;

		db.execSQL(DROP_TABLE_DISCUSSION);

		onCreate(db);
	}

	public long addDiscussion(String search, String result) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_SEARCH, search);
		values.put(KEY_RESULT, result);

		long l = db.insert(TABLE_DISCUSSION, null, values);
		db.close();
		
		return l;
	}

	public Cursor getDiscussion() throws SQLException {
		String columns[] = { KEY_ID, KEY_SEARCH, KEY_RESULT };
		Cursor c = getReadableDatabase().query(TABLE_DISCUSSION, columns, null, null,
				null, null, null);

		// c.close();
		return c;

	}

	public String getID(String data) throws SQLException {
		// TODO Auto-generated method stub
		String columns[] = { KEY_ID };
		Cursor c = getReadableDatabase().query(TABLE_DISCUSSION, columns,
				KEY_SEARCH + "='" + data + "'", null, null, null, null);

		if (c != null) {
			c.moveToFirst();
			String s = c.getString(0);
			c.close();
			return s;
		}
		return null;

	}

	public void delete()throws SQLException
	{
		SQLiteDatabase db = this.getWritableDatabase();
//		db.execSQL("delete * from " + TABLE_DISCUSSION);
		db.delete(TABLE_DISCUSSION, null, null);
		
		db.close();
		
	}
	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		super.close();
	}

}
