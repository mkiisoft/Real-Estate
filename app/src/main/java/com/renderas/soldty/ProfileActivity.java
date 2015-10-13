package com.renderas.soldty;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.renderas.soldty.utils.KeySaver;
import com.renderas.soldty.utils.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mariano on 09/08/2015.
 */
public class ProfileActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    private Typeface mThin, mBold, mLight;
    private Button mUserLogout, mUserEditBtn;
    private TextView mUserName, mUserNameVisible, mUserEmailVisible;
    private RoundedImageView mUserAvatar;
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private FormEditText mUserNameEditText, mUserEmailEditText;
    boolean editOrNot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mThin = Typeface.createFromAsset(getResources().getAssets(), "Thin.ttf");
        mBold = Typeface.createFromAsset(getResources().getAssets(), "Bold.ttf");
        mLight = Typeface.createFromAsset(getResources().getAssets(), "Light.ttf");

        final AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mi Perfil");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")));

        // VISIBILITY GONE

        mUserEditBtn = (Button) findViewById(R.id.user_edit_btn);
        mUserEditBtn.setEnabled(false);

        mUserNameVisible = (TextView) findViewById(R.id.info_name_text);
        mUserNameVisible.setText(KeySaver.getStringSavedShare(this, "user_name"));
        mUserNameEditText = (FormEditText) findViewById(R.id.info_name_edit);
        mUserNameEditText.setText(KeySaver.getStringSavedShare(this, "user_name"));
        mUserNameEditText.setEnabled(false);

        mUserEmailVisible = (TextView) findViewById(R.id.info_email_text);
        mUserEmailVisible.setText(KeySaver.getStringSavedShare(this, "user_email"));
        mUserEmailEditText = (FormEditText) findViewById(R.id.info_email_edit);
        mUserEmailEditText.setText(KeySaver.getStringSavedShare(this, "user_email"));
        mUserEmailEditText.setEnabled(false);

        mUserName = (TextView) findViewById(R.id.edit_profile_user);
        mUserName.setText(KeySaver.getStringSavedShare(this, "user_nick"));
        mUserName.setTypeface(mLight);

        mUserAvatar = (RoundedImageView) findViewById(R.id.edit_profile_image);
        Picasso.with(this).load(KeySaver.getStringSavedShare(this, "user_avatar")).into(mUserAvatar);


        mUserEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FormEditText[] allFields    = { mUserNameEditText, mUserEmailEditText };


                boolean allValid = true;
                for (FormEditText field: allFields) {
                    allValid = field.testValidity() && allValid;
                }

                if (allValid) {
                    new MyAsyncTask().execute();
                }else{

                }

            }
        });

        mUserLogout = (Button) findViewById(R.id.user_logout);
        mUserLogout.setTypeface(mBold);
        mUserLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                KeySaver.removeKey(ProfileActivity.this, "user_id");
                KeySaver.removeKey(ProfileActivity.this, "user_name");
                KeySaver.removeKey(ProfileActivity.this, "user_nick");
                KeySaver.removeKey(ProfileActivity.this, "user_avatar");
                KeySaver.removeKey(ProfileActivity.this, "user_email");
                KeySaver.removeKey(ProfileActivity.this, "soldty_auth_left");
                KeySaver.removeKey(ProfileActivity.this, "soldty_auth_right");

                finish();
            }
        });
    }

    String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {

        Exception error;

        protected void onPreExecute() {
            super.onPreExecute();

            mUserEditBtn.animate().alpha(0).setDuration(500);

        }

        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                postData();
                return true;


            } catch (Exception e) {
                error = e;

                return false;
            }
        }

        protected void onPostExecute(Boolean result) {

            if(result){
                KeySaver.saveShare(ProfileActivity.this, "user_name", mUserNameEditText.getText().toString());
                KeySaver.saveShare(ProfileActivity.this, "user_email", mUserEmailEditText.getText().toString());

                mUserNameVisible.setText(mUserNameEditText.getText().toString());
                mUserEmailVisible.setText(mUserEmailEditText.getText().toString());

                invalidateOptionsMenu();
                editOrNot = false;

                setLogout();
            }else{
                mUserEditBtn.animate().alpha(1).setDuration(500);
            }

        }

        public void postData() {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://dev.soldty.com/wp-json/users/" + KeySaver.getStringSavedShare(ProfileActivity.this, "user_id"));

            String responseBody = "";

            HttpResponse response = null;

            try {

                String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                        (KeySaver.getStringSavedShare(ProfileActivity.this, "soldty_auth_left") + ":" + KeySaver.getStringSavedShare(ProfileActivity.this, "soldty_auth_right")).getBytes(),
                        Base64.NO_WRAP);


                httppost.setHeader("Authorization", base64EncodedCredentials);

                httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

                JSONObject obj = new JSONObject();

                obj.put("name", mUserNameEditText.getText().toString());
                obj.put("email", mUserEmailEditText.getText().toString());


                httppost.setEntity(new StringEntity(obj.toString(), "UTF-8"));

                // Execute HTTP Post Request
                response = httpclient.execute(httppost);

                if (response.getStatusLine().getStatusCode() == 200) {
                    Log.d("response ok", "ok response :/");
                } else {
                    Log.d("response not ok", "Something went wrong :/");
                }

                responseBody = EntityUtils.toString(response.getEntity());

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, 0, 0, "Editar").setIcon(R.drawable.ic_pencil_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 0 && editOrNot == false){
            invalidateOptionsMenu();
            setLogout();
        }
        else if(item.getItemId() == android.R.id.home){
            finish();
        }else if(item.getItemId() == 0 && editOrNot ==  true){
            invalidateOptionsMenu();
            setEditable();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(editOrNot){
            menu.getItem(0).setIcon(R.drawable.ic_close_white_24dp);
            menu.getItem(0).setTitle("Cancelar");
            editOrNot = false;
        }
        else{
            menu.getItem(0).setIcon(R.drawable.ic_pencil_white_24dp);
            menu.getItem(0).setTitle("Editar");
            editOrNot = true;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void setEditable() {
        mUserLogout.animate().alpha(0).
                setDuration(500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mUserLogout.setVisibility(View.INVISIBLE);
                        mUserLogout.setEnabled(false);

                        mUserEditBtn.setAlpha(0);
                        mUserEditBtn.setVisibility(View.VISIBLE);
                        mUserEditBtn.setEnabled(true);
                        mUserEditBtn.animate().alpha(1).setDuration(500);
                    }
                });

        mUserNameVisible.setVisibility(View.INVISIBLE);
        mUserNameEditText.setVisibility(View.VISIBLE);
        mUserNameEditText.setEnabled(true);

        mUserEmailVisible.setVisibility(View.INVISIBLE);
        mUserEmailEditText.setVisibility(View.VISIBLE);
        mUserEmailEditText.setEnabled(true);
    }

    public void setLogout() {

        mUserEditBtn.animate().alpha(0).
                setDuration(500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mUserEditBtn.setVisibility(View.INVISIBLE);
                        mUserEditBtn.setEnabled(false);

                        mUserLogout.setAlpha(0);
                        mUserLogout.setVisibility(View.VISIBLE);
                        mUserLogout.setEnabled(true);
                        mUserLogout.animate().alpha(1).setDuration(500);
                    }
                });

        mUserNameVisible.setVisibility(View.VISIBLE);
        mUserNameEditText.setVisibility(View.INVISIBLE);
        mUserNameEditText.setEnabled(false);

        mUserEmailVisible.setVisibility(View.VISIBLE);
        mUserEmailEditText.setVisibility(View.INVISIBLE);
        mUserEmailEditText.setEnabled(false);
    }
}
