package com.renderas.soldty.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class ContentDAO {

	private SQLiteDatabase db;
	private SQLiteHelper dbHelper;

	public ContentDAO(Context context) {
		dbHelper = new SQLiteHelper(context);
		db = dbHelper.getWritableDatabase();
	}

	// Close the db
	public void close() {
		db.close();
	}

	/**
	 * Create new object
	 *
	 */
	public void createFavOrNot(Integer position, String mTitle, String mPrice, String mThumb, String mFullImg, String mAuthorID, String mAuthorName,
							   String mAuthorImg, String mAuthorEmail, String mPhone, String mContent, String mWebID, String mType,
							   String mCountry, String mCity, String mStatus, String mLat, String mLng, String mBedrooms, String mBuild,
							   String mCondition, String mArea, String mPremium, String mGallery, String favOrNot) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("positionFav", position);
		contentValues.put("title", mTitle);
		contentValues.put("price", mPrice);
		contentValues.put("list_thumb", mThumb);
		contentValues.put("full_image", mFullImg);
		contentValues.put("author_id", mAuthorID);
		contentValues.put("author_name", mAuthorName);
		contentValues.put("author_image", mAuthorImg);
		contentValues.put("author_email", mAuthorEmail);
		contentValues.put("phone", mPhone);
		contentValues.put("content", mContent);
		contentValues.put("webID", mWebID);
		contentValues.put("property_type", mType);
		contentValues.put("country", mCountry);
		contentValues.put("city", mCity);
		contentValues.put("name_status", mStatus);
		contentValues.put("lat_map", mLat);
		contentValues.put("lng_map", mLng);
		contentValues.put("bedrooms", mBedrooms);
		contentValues.put("built_year", mBuild);
		contentValues.put("condition", mCondition);
		contentValues.put("area", mArea);
		contentValues.put("premium", mPremium);
		contentValues.put("gallery_array", mGallery);
		contentValues.put("favOrNot", favOrNot);
		// Insert into DB
		db.insert("favornot", null, contentValues);
	}

	/**
	 * Delete object
	 *
	 */
	public void deleteFavOrNot(int cardId) {
		// Delete from DB where id match
		db.delete("favornot", "_id = " + cardId, null);
	}

	/**
	 * Update object
	 *
	 */
	public void updateFavOrNot(int cardId, String favOrNot){
		ContentValues contentValues = new ContentValues();
		contentValues.put("favOrNot", favOrNot);
		db.update("favornot", contentValues, "_id = " + cardId, null);
	}


	public int existFavorNot(String webID, String favOrNot){
		int result=-1;

		String thequery = "SELECT _id FROM favornot WHERE " +
				"webID='" + webID +"' AND favOrNot='" + favOrNot + "'";

		Cursor cursor = db.rawQuery(thequery, null);
		cursor.moveToFirst();

		if (cursor!=null){
			if (cursor.getCount()>0)
			{
				result=cursor.getInt(0);

			}
		}
		return result;
	}

	/**
	 * Get all TODOs.
	 * @return
	 */
	public List<PropertyDB> getFavorNot() {
		List<PropertyDB> favOrNotList = new ArrayList<PropertyDB>();

		// Name of the columns we want to select
		String[] tableColumns = new String[] {"_id", "positionFav", "title", "price", "list_thumb", "full_image", "author_id", "author_name",
				"author_image", "author_email", "phone", "content", "webID", "property_type", "country", "city", "name_status", "lat_map",
				"lng_map", "bedrooms", "built_year", "condition", "area", "premium", "gallery_array", "favOrNot"};

		// Query the database
		Cursor cursor = db.query("favornot", tableColumns, null, null, null, null, null);
		cursor.moveToFirst();

		// Iterate the results
		while (!cursor.isAfterLast()) {
			PropertyDB favOrNot = new PropertyDB();
			favOrNot.setPosition(cursor.getInt(0));
			favOrNot.setIdProperty(cursor.getInt(1));
			favOrNot.setTitle(cursor.getString(2));
			favOrNot.setPrice(cursor.getString(3));
			favOrNot.setImgThumb(cursor.getString(4));
			favOrNot.setImgFull(cursor.getString(5));
			favOrNot.setAuthorID(cursor.getString(6));
			favOrNot.setAuthorName(cursor.getString(7));
			favOrNot.setAuthorImg(cursor.getString(8));
			favOrNot.setAuthorEmail(cursor.getString(9));
			favOrNot.setPhone(cursor.getString(10));
			favOrNot.setDescription(cursor.getString(11));
			favOrNot.setPropertyID(cursor.getString(12));
			favOrNot.setPropertyType(cursor.getString(13));
			favOrNot.setPropertyCountry(cursor.getString(14));
			favOrNot.setPropertyCity(cursor.getString(15));
			favOrNot.setPropertyStatus(cursor.getString(16));
			favOrNot.setMapLat(cursor.getString(17));
			favOrNot.setMapLng(cursor.getString(18));
			favOrNot.setBedrooms(cursor.getString(19));
			favOrNot.setBuildYear(cursor.getString(20));
			favOrNot.setCondition(cursor.getString(21));
			favOrNot.setArea(cursor.getString(22));
			favOrNot.setPremium(cursor.getString(23));
			favOrNot.setGallery(cursor.getString(24));
			favOrNot.setFavOrNot(cursor.getString(25));


			// Add to the DB
			favOrNotList.add(favOrNot);

			// Move to the next result
			cursor.moveToNext();
		}

		return favOrNotList;
	}

}
