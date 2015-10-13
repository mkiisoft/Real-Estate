package com.renderas.soldty.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates application database.
 * 
 * @author itcuties
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	public SQLiteHelper(Context context) {
		// Databse: todos_db, Version: 1
		super(context, "card_db", null, 1);
	}

	/**
	 * Create simple table
	 * todos
	 * 		_id 	- key
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Execute create table SQL
		db.execSQL("CREATE TABLE favornot (_id INTEGER PRIMARY KEY AUTOINCREMENT, positionFav INTEGER, title TEXT NOT NULL, price TEXT NOT NULL, list_thumb TEXT NOT NULL, full_image TEXT NOT NULL, author_id TEXT NOT NULL, author_name TEXT NOT NULL, author_image TEXT NOT NULL, author_email TEXT NOT NULL, phone TEXT NOT NULL, content TEXT NOT NULL, webID TEXT NOT NULL, property_type TEXT NOT NULL, country TEXT NOT NULL, city TEXT NOT NULL, name_status TEXT NOT NULL, lat_map TEXT NOT NULL, lng_map TEXT NOT NULL, bedrooms TEXT NOT NULL, built_year TEXT NOT NULL, condition TEXT NOT NULL, area TEXT NOT NULL, premium TEXT NOT NULL, gallery_array TEXT NOT NULL, favOrNot TEXT NOT NULL);");
	}

	/**
	 * Recreates table
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		// DROP table
		db.execSQL("DROP TABLE IF EXISTS favornot");
		// Recreate table
		onCreate(db);
	}
	
}
