package com.renderas.soldty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.renderas.soldty.adapter.ListAdapter;
import com.renderas.soldty.utils.CircularProgressBar;
import com.renderas.soldty.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mariano on 25/08/2015.
 */
public class FilterActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private String json_property_type = "http://dev.soldty.com/wp-json/taxonomies/property_type/terms";
    private String json_property_city = "http://dev.soldty.com/wp-json/taxonomies/property_city/terms";
    private String json_property_status = "http://dev.soldty.com/wp-json/taxonomies/property_status/terms";
    private String json_type_filter = "http://dev.soldty.com/wp-json/posts?type=property&filter[taxonomy]=property_type&filter[term]=";
    private String json_city_filter = "http://dev.soldty.com/wp-json/posts?type=property&filter[taxonomy]=property_city&filter[term]=";
    private String json_status_filter = "http://dev.soldty.com/wp-json/posts?type=property&filter[taxonomy]=property_status&filter[term]=";
    private Spinner mFilterSpinner;
    private ArrayAdapter<String> arrayspinner;
    private ArrayList<String> mSpinnerArray, mSpinnerArraySlug, mSpinnerArrayImg, mSpinnerArraySimple, mSpinnerArraySimpleSlug;
    private TextView label;
    private SpinnerAdapter adapter;
    private SpinnerAdapterSimple adaptersimple;
    private JSONArray jsonsearch;
    private ArrayList<HashMap<String, String>> arraylist;
    private JSONObject json_object;
    private ListView mListView;
    private Button mButtonFilter;
    private String mSpinnerTxt = null;
    private CircularProgressBar progressCircular;
    private RelativeLayout searchEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFilterSpinner = (Spinner) findViewById(R.id.filter_menu_spinner);
        mListView = (ListView) findViewById(R.id.filter_menu_list);

        mButtonFilter = (Button) findViewById(R.id.filter_search);

        searchEmpty = (RelativeLayout) findViewById(R.id.search_empty_box);

        progressCircular = (CircularProgressBar) findViewById(R.id.circularProgress);
        progressCircular.setVisibility(View.INVISIBLE);

        final Intent filterIntent = getIntent();

        if (filterIntent != null && filterIntent.getStringExtra("location") != null) {
            getSupportActionBar().setTitle("Elegir Ubicación");
            AsyncConnectionSimple(json_property_city);

            mFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                    int item = mFilterSpinner.getSelectedItemPosition();
                    mSpinnerTxt = mFilterSpinner.getSelectedItem().toString();

                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            mButtonFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSpinnerTxt != null) {
                        AsyncFilterResult(json_city_filter + mSpinnerTxt);
                    }
                }
            });
        } else if (filterIntent != null && filterIntent.getStringExtra("sale") != null) {
            getSupportActionBar().setTitle("Elegir Operación");
            AsyncConnectionSimple(json_property_status);

            mFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                    int item = mFilterSpinner.getSelectedItemPosition();
                    mSpinnerTxt = mFilterSpinner.getSelectedItem().toString();

                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            mButtonFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSpinnerTxt != null) {
                        AsyncFilterResult(json_status_filter + mSpinnerTxt);
                    }
                }
            });
        } else if (filterIntent != null && filterIntent.getStringExtra("type") != null) {
            getSupportActionBar().setTitle("Elegir Tipo de Propiedad");
            AsyncConnection(json_property_type);

            mFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                    int item = mFilterSpinner.getSelectedItemPosition();
                    mSpinnerTxt = mFilterSpinner.getSelectedItem().toString();

                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            mButtonFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSpinnerTxt != null){
                        AsyncFilterResult(json_type_filter + mSpinnerTxt);
                    }
                }
            });
        }
    }

    private class filterClient {

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

    public void emprtySearchAppear(){
        searchEmpty.setAlpha(0);
        searchEmpty.setVisibility(View.VISIBLE);
        searchEmpty.animate().alpha(1).setDuration(500);
    }

    public void AsyncFilterResult(String urlConnection) {
        new filterClient().get(urlConnection, null, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                progressCircular.setVisibility(View.VISIBLE);
                searchEmpty.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {

                try {

                    arraylist = new ArrayList<HashMap<String, String>>();

                    jsonsearch = new JSONArray(Utils.decodeUTF8(response));

                    if (jsonsearch.length() == 0) {
                        emprtySearchAppear();
                    }

                    for (int e = 0; e < jsonsearch.length(); e++) {
                        HashMap<String, String> map = new HashMap<String, String>();

                        json_object = jsonsearch.getJSONObject(e);

                        // Retrive JSON Objects
                        map.put("ID", json_object.getString("ID"));
                        map.put("title", json_object.getString("title"));
                        Log.e("name", json_object.getString("title"));
                        map.put("author", json_object.getString("author"));
                        map.put("content", json_object.getString("content"));
                        JSONObject json_author = json_object.getJSONObject("author");
                        JSONObject json_meta_user = json_author.getJSONObject("meta");
                        JSONObject json_meta_sub = json_meta_user.getJSONObject("meta");
                        JSONArray json_phone = json_meta_sub.getJSONArray("phone");
                        map.put("phone", json_phone.getString(0));
                        map.put("author_id", json_author.getString("ID"));
                        map.put("author_name", json_author.getString("name"));
                        map.put("author_image", json_author.getString("avatar"));
                        map.put("author_email", json_author.getString("email_contacto"));
                        JSONObject json_map = json_object.getJSONObject("latlng");
                        map.put("lat_map", json_map.getString("lat"));
                        map.put("lng_map", json_map.getString("lng"));
                        JSONObject json_feature_image = json_object.getJSONObject("featured_image");
                        JSONObject json_image_meta = json_feature_image.getJSONObject("attachment_meta");
                        JSONObject json_sizes = json_image_meta.getJSONObject("sizes");
                        JSONObject json_thumbnail = json_sizes.getJSONObject("javo-small");
                        JSONObject json_large = json_sizes.getJSONObject("javo-large");
                        map.put("list_thumb", json_thumbnail.getString("url"));
                        map.put("full_image", json_large.getString("url"));
                        JSONObject json_terms = json_object.getJSONObject("terms");
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

                        JSONArray json_property_type = json_terms.getJSONArray("property_type");
                        for (int t = 0; t < json_property_type.length(); t++) {
                            JSONObject json_obj_type = json_property_type.getJSONObject(t);

                            map.put("property_type", json_obj_type.getString("name"));
                        }

                        JSONArray json_status = json_terms.getJSONArray("property_status");
                        for (int x = 0; x < json_status.length(); x++) {
                            JSONObject json_obj = json_status.getJSONObject(x);

                            map.put("name_status", json_obj.getString("name"));
                        }

                        JSONArray json_price = json_object.getJSONArray("sale_price");
                        map.put("price", json_price.getString(0));

                        JSONArray json_bedrooms = json_object.getJSONArray("bedrooms");
                        map.put("bedrooms", json_bedrooms.getString(0));

                        JSONArray json_year = json_object.getJSONArray("built_year");
                        map.put("built_year", json_year.getString(0));

                        JSONArray json_area = json_object.getJSONArray("area");
                        map.put("area", json_area.getString(0));

                        try {
                            boolean bool = json_object.getBoolean("detail_images");

                            if (bool == false) {
                                map.put("gallery_array", "[]");
                            }
                        } catch (JSONException f) {
                            JSONArray json_gallery = json_object.getJSONArray("detail_images");
                            map.put("gallery_array", String.valueOf(json_gallery));
                        }

                        map.put("condition", json_object.getString("estado"));
                        map.put("premium", String.valueOf(json_object.getBoolean("premium")));

                        // Set the JSON Objects into the array
                        arraylist.add(map);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressCircular.setVisibility(View.GONE);

                        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new ListAdapter(getApplicationContext(), arraylist));
                        swingBottomInAnimationAdapter.setAbsListView(mListView);

                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
                        swingBottomInAnimationAdapter.getViewAnimator().setAnimationDurationMillis(400);

                        mListView.setAdapter(swingBottomInAnimationAdapter);
                    }
                });
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public void AsyncConnectionSimple(String urlConnection) {
        new filterClient().get(urlConnection, null, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {

                try {
                    JSONArray jsontype = new JSONArray(Utils.decodeUTF8(response));

                    mSpinnerArraySimple = new ArrayList<String>();
                    mSpinnerArraySimpleSlug = new ArrayList<String>();

                    for (int e = 0; e < jsontype.length(); e++) {

                        JSONObject jsonobj = jsontype.getJSONObject(e);

                        mSpinnerArraySimple.add(jsonobj.getString("name"));
                        mSpinnerArraySimpleSlug.add(jsonobj.getString("slug"));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adaptersimple = new SpinnerAdapterSimple(FilterActivity.this, R.layout.spinner_row_simple, mSpinnerArraySimpleSlug);
                            adaptersimple.setDropDownViewResource(R.layout.spinner_row_simple);
                            mFilterSpinner.setAdapter(adaptersimple);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }


    public void AsyncConnection(String urlConnection) {
        new filterClient().get(urlConnection, null, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {

                try {
                    JSONArray jsontype = new JSONArray(Utils.decodeUTF8(response));

                    mSpinnerArray = new ArrayList<String>();
                    mSpinnerArraySlug = new ArrayList<String>();
                    mSpinnerArrayImg = new ArrayList<String>();

                    for (int e = 0; e < jsontype.length(); e++) {

                        JSONObject jsonobj = jsontype.getJSONObject(e);

                        JSONObject jsonacf = jsonobj.getJSONObject("acf");

                        mSpinnerArray.add(jsonobj.getString("name"));
                        mSpinnerArraySlug.add(jsonobj.getString("slug"));
                        mSpinnerArrayImg.add(jsonacf.getString("imagen_thumb"));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new SpinnerAdapter(FilterActivity.this, R.layout.spinner_row_simple, mSpinnerArraySlug);
                            adapter.setDropDownViewResource(R.layout.spinner_row_simple);
                            mFilterSpinner.setAdapter(adapter);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public class SpinnerAdapterSimple extends ArrayAdapter<String> {

        private Context context;

        public SpinnerAdapterSimple(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View rows = inflater.inflate(R.layout.spinner_row_simple, parent, false);

            label = (TextView) rows.findViewById(R.id.spinner_txt);
            label.setText(mSpinnerArraySimple.get(position));

            RelativeLayout mLetterBg = (RelativeLayout) rows.findViewById(R.id.spinner_item_letter);
            mLetterBg.setVisibility(View.VISIBLE);
            TextView mLetter = (TextView) rows.findViewById(R.id.spinner_simple_letter);
            mLetter.setText(mSpinnerArraySimple.get(position).toUpperCase().substring(0, 1));

            return rows;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row_simple, parent, false);

            label = (TextView) row.findViewById(R.id.spinner_txt);
            label.setText(mSpinnerArraySimple.get(position));

            RelativeLayout mLetterBg = (RelativeLayout) row.findViewById(R.id.spinner_item_letter);
            mLetterBg.setVisibility(View.GONE);

            return row;
        }

    }

    public class SpinnerAdapter extends ArrayAdapter<String> {

        private Context context;

        public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View rows = inflater.inflate(R.layout.spinner_row, parent, false);

            label = (TextView) rows.findViewById(R.id.spinner_txt);
            label.setText(mSpinnerArray.get(position));

            ImageView icon = (ImageView) rows.findViewById(R.id.spinner_img);

            Glide.with(FilterActivity.this).load(mSpinnerArrayImg.get(position)).into(icon);

            return rows;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row_simple, parent, false);

            label = (TextView) row.findViewById(R.id.spinner_txt);
            label.setText(mSpinnerArray.get(position));

            RelativeLayout mLetterBg = (RelativeLayout) row.findViewById(R.id.spinner_item_letter);
            mLetterBg.setVisibility(View.GONE);

            return row;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
