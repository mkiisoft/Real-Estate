package com.renderas.soldty.sql;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PropertyProvider extends ContentProvider {
    // fields for my content provider
    static final String PROVIDER_NAME = "com.renderas.soldty.sql.PropertyProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/property";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    // fields for the database
    static final String ID = "_id";

    // Property database

    public static final String TITLE = "title";
    public static final String PRICE = "price";
    public static final String SCREEN = "list_thumb";
    public static final String FULLIMG = "full_image";
    public static final String AUTHORID = "author_id";
    public static final String AUTHORNAME = "author_name";
    public static final String AUTHORIMG = "author_image";
    public static final String AUTHOREMAIL = "author_email";
    public static final String AUTHOREPHONE = "phone";
    public static final String CONTENT = "content";
    public static final String WEBID = "ID";
    public static final String TYPE = "property_type";
    public static final String COUNTRY = "country";
    public static final String CITY = "city";
    public static final String STATUS = "name_status";
    public static final String MAPLAT = "lat_map";
    public static final String MAPLNG = "lng_map";
    public static final String BEDROOMS = "bedrooms";
    public static final String BUILDYEAR = "built_year";
    public static final String CONDITION = "condition";
    public static final String AREA = "area";
    public static final String PREMIUM = "premium";
    public static final String GALLERY = "gallery_array";

    // integer values used in content URI
    static final int PROPERTY = 1;
    static final int PROPERTY_ID = 2;

    // insert or replace
    public static final String SQL_INSERT_OR_REPLACE = "__sql_insert_or_replace__";

    DBHelper dbHelper;

    // projection map for a query
    private static HashMap<String, String> PropertyMap;

    // maps content URI "patterns" to the integer values that were set above
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "property", PROPERTY);
        uriMatcher.addURI(PROVIDER_NAME, "property/row", PROPERTY_ID);
    }

    // database declarations
    private SQLiteDatabase database;
    static final String DATABASE_NAME = "Property";
    static final String TABLE_NAME = "propertyTable";
    static final int DATABASE_VERSION = 3;
    static final String CREATE_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " title TEXT NOT NULL, " + " content TEXT NOT NULL, " + " price TEXT NOT NULL, " +
                    " list_thumb TEXT NOT NULL, " + " full_image TEXT NOT NULL, " + " author_id TEXT NOT NULL, " +
                    " author_name TEXT NOT NULL, " + " author_image TEXT NOT NULL, " + " author_email TEXT NOT NULL, " +
                    " property_type TEXT NOT NULL, " + " country TEXT NOT NULL, " + " ID TEXT NOT NULL unique, " +
                    " city TEXT NOT NULL, " + " name_status TEXT NOT NULL, " + " lat_map TEXT NOT NULL, " +
                    " lng_map TEXT NOT NULL, " + " bedrooms TEXT NOT NULL, " + " built_year TEXT NOT NULL, " +
                    " condition TEXT NOT NULL, " + " area TEXT NOT NULL, " + " premium TEXT NOT NULL, " +
                    " phone TEXT NOT NULL, " + " gallery_array TEXT NOT NULL);";


    // class that creates and manages the provider's database
    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            Log.w(DBHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ". Old data will be destroyed");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        Context context = getContext();
        dbHelper = new DBHelper(context);
        // permissions to be writable
        database = dbHelper.getWritableDatabase();

        if (database == null)
            return false;
        else
            return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // the TABLE_NAME to query on
        queryBuilder.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            // maps all database column names
            case PROPERTY:
                queryBuilder.setProjectionMap(PropertyMap);
                break;
            case PROPERTY_ID:
                queryBuilder.setProjectionMap(PropertyMap);
                //queryBuilder.appendWhere( ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == "") {
            // No sorting-> sort on names by default
            sortOrder = TITLE;
        }
        Cursor cursor = queryBuilder.query(database, projection, selection,
                selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PROPERTY:
                insertOrUpdateById(database, uri, TABLE_NAME,
                        values, "ID");
                getContext().getContentResolver().notifyChange(uri, null, false);
                return uri;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private void insertOrUpdateById(SQLiteDatabase db, Uri uri, String table,
                                    ContentValues values, String column) throws SQLException {
        try {
            db.insertOrThrow(table, null, values);
        } catch (SQLiteConstraintException e) {
            int nrRows = update(uri, values, column + "=?",
                    new String[]{values.getAsString(column)});
            if (nrRows == 0)
                throw e;
        }
    }

	/*@Override
    public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		long row = database.insert(TABLE_NAME, "", values);
	      
		// If record is added successfully
	      if(row > 0) {
	         Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
	         getContext().getContentResolver().notifyChange(newUri, null);
	         return newUri;
	      }
	      
	      return uri;
	      //throw new SQLException("Fail to add a new record into " + uri);
	}*/

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case PROPERTY:
                count = database.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case PROPERTY_ID:
                count = database.update(TABLE_NAME, values, ID +
                        " = " + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case PROPERTY:
                // delete all the records of the table
                count = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case PROPERTY_ID:
                String ids = uri.getLastPathSegment();    //gets the id
                count = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;


    }
	
	/*public void deleteFavOrNot(int cardId) {
		// Delete from DB where id match
		database.delete("favornot", "_id = " + cardId, null);
	}*/

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        switch (uriMatcher.match(uri)) {
            // Get all records
            case PROPERTY:
                return "vnd.android.cursor.dir/com.renderas.soldty.sql.PropertyProvider";
            // Get a particular rec
            case PROPERTY_ID:
                return "vnd.android.cursor.item/com.renderas.soldty.sql.PropertyProvider";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


}
