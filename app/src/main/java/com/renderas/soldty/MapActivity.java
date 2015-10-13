package com.renderas.soldty;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.renderas.soldty.utils.FloatingActionButton;
import com.renderas.soldty.utils.Utils;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mariano on 16/08/2015.
 */
public class MapActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {
    private Toolbar mToolbar;
    private FloatingActionButton loadBtn;
    private MapView mapView;
    private Animation a;
    private RotateAnimation rotateAnimation;
    private GoogleMap googleMap;
    private String mParseURL;
    private Integer pager = 1;
    private JSONArray jsonnews;
    private MarkerOptions markerOptions;
    private Marker melbourne;
    private ArrayList<HashMap<String, String>> arraylist;

    // Json Parser
    static String TITLE = "title";
    static String PRICE = "price";
    static String SCREEN = "list_thumb";
    static String FULLIMG = "full_image";
    static String AUTHORID = "author_id";
    static String AUTHORNAME = "author_name";
    static String AUTHORIMG = "author_image";
    static String AUTHOREMAIL = "author_email";
    static String AUTHORPHONE = "phone";
    static String CONTENT = "content";
    static String ID = "ID";
    static String TYPE = "property_type";
    static String COUNTRY = "country";
    static String CITY = "city";
    static String STATUS = "name_status";
    static String MAPLAT = "lat_map";
    static String MAPLNG = "lng_map";
    static String BEDROOMS = "bedrooms";
    static String BUILDYEAR = "built_year";
    static String CONDITION = "condition";
    static String AREA = "area";
    static String PREMIUM = "premium";
    static String GALLERY = "gallery_array";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mapa");

        final Intent mapIntent = getIntent();

        mParseURL = mapIntent.getStringExtra("pathURL");
        Log.e("the url", mParseURL);

        mapView = (MapView) findViewById(R.id.mapViewFull);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        googleMap = mapView.getMap();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-16.620874, -64.713947), 5));

        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        AsyncConnection(mParseURL + String.valueOf(pager));

        loadBtn = (FloatingActionButton) findViewById(R.id.load_floating);
        loadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("nonhome", "true");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return true;
    }

    private class PropertyMap {

        private AsyncHttpClient client = new AsyncHttpClient();

        public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }

        public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.post(getAbsoluteUrl(url), params, responseHandler);
        }

        private String getAbsoluteUrl(String relativeUrl) {
            return relativeUrl;
        }
    }

    public void AsyncConnection(String urlConnection) {

        new PropertyMap().get(urlConnection, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                final int totalPages = Integer.parseInt(Utils.convertHeadersToHashMap(headers).get("X-WP-TotalPages"));

                try {

                    jsonnews = new JSONArray(Utils.decodeUTF8(response));

                    MapActivity.this.runOnUiThread(new Runnable() {
                                                       @Override
                                                       public void run() {

                                                           if(totalPages > 1){
                                                               loadBtn.setVisibility(View.VISIBLE);
                                                           }else{
                                                               loadBtn.setVisibility(View.GONE);
                                                           }

                                                           arraylist = new ArrayList<HashMap<String, String>>();

                                                           for (int markers = 0; markers < jsonnews.length(); markers++) {
                                                               try {
                                                                   HashMap<String, String> map = new HashMap<String, String>();

                                                                   JSONObject json_marker = jsonnews.getJSONObject(markers);

                                                                   map.put("ID", json_marker.getString("ID"));
                                                                   map.put("title", json_marker.getString("title"));
                                                                   map.put("content", json_marker.getString("content"));

                                                                   final String title_map = json_marker.getString("title");

                                                                   JSONObject obj_map = json_marker.getJSONObject("latlng");
                                                                   map.put("lat_map", obj_map.getString("lat"));
                                                                   map.put("lng_map", obj_map.getString("lng"));
                                                                   final String lat = obj_map.getString("lat");
                                                                   final String lng = obj_map.getString("lng");
                                                                   String address = obj_map.getString("address");
                                                                   final String premium_map = String.valueOf(json_marker.getBoolean("premium"));

                                                                   JSONObject json_terms = json_marker.getJSONObject("terms");

                                                                   JSONArray json_price = json_marker.getJSONArray("sale_price");
                                                                   map.put("price", json_price.getString(0));

                                                                   JSONArray json_bedrooms = json_marker.getJSONArray("bedrooms");
                                                                   map.put("bedrooms", json_bedrooms.getString(0));

                                                                   JSONArray json_year = json_marker.getJSONArray("built_year");
                                                                   map.put("built_year", json_year.getString(0));

                                                                   JSONArray json_area = json_marker.getJSONArray("area");
                                                                   map.put("area", json_area.getString(0));

                                                                   JSONArray json_status = json_terms.getJSONArray("property_status");
                                                                   for (int e = 0; e < json_status.length(); e++) {
                                                                       JSONObject json_obj = json_status.getJSONObject(e);

                                                                       map.put("name_status", json_obj.getString("name"));
                                                                   }

                                                                   JSONArray json_property_type = json_terms.getJSONArray("property_type");
                                                                   for (int t = 0; t < json_property_type.length(); t++) {
                                                                       JSONObject json_obj_type = json_property_type.getJSONObject(t);

                                                                       map.put("property_type", json_obj_type.getString("name"));
                                                                   }

                                                                   JSONObject json_author = json_marker.getJSONObject("author");
                                                                   JSONObject json_meta_user = json_author.getJSONObject("meta");
                                                                   JSONObject json_meta_sub = json_meta_user.getJSONObject("meta");
                                                                   JSONArray json_phone = json_meta_sub.getJSONArray("phone");

                                                                   map.put("author_id", json_author.getString("ID"));
                                                                   map.put("author_name", json_author.getString("name"));
                                                                   map.put("author_image", json_author.getString("avatar"));
                                                                   map.put("author_email", json_author.getString("email_contacto"));
                                                                   map.put("phone", json_phone.getString(0));

                                                                   JSONArray json_city = json_terms.getJSONArray("property_city");
                                                                   for (int c = 0; c < json_city.length(); c++) {
                                                                       JSONObject json_country = json_city.getJSONObject(0);
                                                                       map.put("country", json_country.getString("name"));

                                                                       if (c > 0) {
                                                                           JSONObject json_the_city = json_city.getJSONObject(1);
                                                                           map.put("city", json_the_city.getString("name"));

                                                                           Log.e("ciudad", json_the_city.getString("name"));
                                                                       } else {
                                                                           map.put("city", "");
                                                                       }

                                                                   }

                                                                   try {
                                                                       boolean bool = json_marker.getBoolean("detail_images");

                                                                       if (bool == false) {
                                                                           map.put("gallery_array", "[]");
                                                                       }
                                                                   } catch (JSONException e) {
                                                                       JSONArray json_gallery = json_marker.getJSONArray("detail_images");
                                                                       map.put("gallery_array", String.valueOf(json_gallery));
                                                                   }

                                                                   JSONObject json_feature_image = json_marker.getJSONObject("featured_image");
                                                                   JSONObject json_image_meta = json_feature_image.getJSONObject("attachment_meta");
                                                                   JSONObject json_sizes = json_image_meta.getJSONObject("sizes");
                                                                   JSONObject json_thumbnail = json_sizes.getJSONObject("javo-small");
                                                                   final JSONObject json_large = json_sizes.getJSONObject("javo-large");

                                                                   map.put("full_image", json_large.getString("url"));
                                                                   map.put("list_thumb", json_thumbnail.getString("url"));

                                                                   map.put("condition", json_marker.getString("estado"));
                                                                   map.put("premium", String.valueOf(json_marker.getBoolean("premium")));

                                                                   arraylist.add(map);

                                                                   final LatLng LATLNG = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                                                                   if (premium_map.contentEquals("true")) {
                                                                       melbourne = googleMap.addMarker(new MarkerOptions()
                                                                               .position(LATLNG)
                                                                               .title(title_map)
                                                                               .snippet(address)
                                                                               .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_premium)));
                                                                   } else {
                                                                       melbourne = googleMap.addMarker(new MarkerOptions()
                                                                               .position(LATLNG)
                                                                               .title(title_map)
                                                                               .snippet(address)
                                                                               .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_default)));
                                                                   }

                                                                   googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                                                       @Override
                                                                       public void onInfoWindowClick(Marker marker) {

                                                                           Intent intent = new Intent(MapActivity.this, PropertyActivity.class);


                                                                           for(int mk = 0; mk < arraylist.size(); mk++){

                                                                               if (marker.getTitle().contentEquals(arraylist.get(mk).get("title")) ) {
                                                                                   intent.putExtra("title", arraylist.get(mk).get("title"));
                                                                                   intent.putExtra("content", arraylist.get(mk).get("content"));
                                                                                   intent.putExtra("ID", arraylist.get(mk).get("ID"));
                                                                                   intent.putExtra("price", arraylist.get(mk).get("price"));
                                                                                   intent.putExtra("property_type", arraylist.get(mk).get("property_type"));
                                                                                   intent.putExtra("status", arraylist.get(mk).get("name_status"));
                                                                                   intent.putExtra("full_image", arraylist.get(mk).get("full_image"));
                                                                                   intent.putExtra("list_thumb", arraylist.get(mk).get("list_thumb"));
                                                                                   intent.putExtra("lat_map", arraylist.get(mk).get("lat_map"));
                                                                                   intent.putExtra("lng_map", arraylist.get(mk).get("lng_map"));
                                                                                   intent.putExtra("agent", arraylist.get(mk).get("author_name"));
                                                                                   intent.putExtra("author_id", arraylist.get(mk).get("author_id"));
                                                                                   intent.putExtra("agent_img", arraylist.get(mk).get("author_image"));
                                                                                   intent.putExtra("agent_email", arraylist.get(mk).get("author_email"));
                                                                                   intent.putExtra("phone", arraylist.get(mk).get("phone"));
                                                                                   intent.putExtra("premium", arraylist.get(mk).get("premium"));
                                                                                   intent.putExtra("condition", arraylist.get(mk).get("condition"));
                                                                                   intent.putExtra("country", arraylist.get(mk).get("country"));
                                                                                   intent.putExtra("city", arraylist.get(mk).get("city"));
                                                                                   intent.putExtra("bedrooms", arraylist.get(mk).get("bedrooms"));
                                                                                   intent.putExtra("build_year", arraylist.get(mk).get("built_year"));
                                                                                   intent.putExtra("area", arraylist.get(mk).get("area"));
                                                                                   intent.putExtra("gallery", arraylist.get(mk).get("gallery_array"));
                                                                                   intent.putExtra("positionFav", String.valueOf(marker.hashCode()));
                                                                               }

                                                                           }

                                                                           startActivity(intent);


                                                                       }
                                                                   });


                                                               } catch (JSONException e) {
                                                                   e.printStackTrace();
                                                               }
                                                           }


                                                       }
                                                   }

                    );


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
}
