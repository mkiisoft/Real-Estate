package com.renderas.soldty;

import android.animation.TimeInterpolator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.renderas.soldty.utils.KeySaver;

import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;


public class AccessActivity extends ActionBarActivity {

    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private Button access_agent;
    private Toolbar mToolbar;
    private LinearLayout user_form;
    private FormEditText username,password;
    private TextView welcome;

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private Typeface mThin, mBold, mLight;
    private TextView mAcessInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mThin = Typeface.createFromAsset(getResources().getAssets(), "Thin.ttf");
        mBold = Typeface.createFromAsset(getResources().getAssets(), "Bold.ttf");
        mLight = Typeface.createFromAsset(getResources().getAssets(), "Light.ttf");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")));

        final AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();

        user_form = (LinearLayout) findViewById(R.id.user_form);
        username = (FormEditText) findViewById(R.id.et_username);
        password = (FormEditText) findViewById(R.id.et_password);

        welcome = (TextView) findViewById(R.id.user_welcome);
        welcome.setTypeface(mBold);
        welcome.setAlpha(0);
        welcome.setVisibility(View.GONE);
        welcome.setEnabled(false);
        welcome.setClickable(false);

        mAcessInfo = (TextView) findViewById(R.id.text_access_info);
        mAcessInfo.setTypeface(mLight);

        access_agent = (Button) findViewById(R.id.access_agent);
        access_agent.setTypeface(mBold);
        access_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FormEditText[] allFields    = { username, password };


                boolean allValid = true;
                for (FormEditText field: allFields) {
                    allValid = field.testValidity() && allValid;
                }

                if (allValid) {
                    client.setBasicAuth(username.getText().toString(), password.getText().toString(), new AuthScope("dev.soldty.com", 80, AuthScope.ANY_REALM));
                    client.get("http://dev.soldty.com/wp-json/users/me", new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart(){
                            username.clearFocus();
                            password.clearFocus();
                            access_agent.animate().alpha(0).
                                    setDuration(500)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            access_agent.setVisibility(View.GONE);
                                            access_agent.setClickable(false);
                                        }
                                    });

                        }

                        @Override
                        public void onSuccess(int statuscode, Header[] headers, byte[] response) {
                            try {

                                JSONObject obj = new JSONObject(decodeUTF8(response));
                                KeySaver.saveShare(AccessActivity.this, "user_id", String.valueOf(obj.getString("ID")));
                                String userClean = String.valueOf(obj.getString("name")).replace("&amp;", "&");
                                KeySaver.saveShare(AccessActivity.this, "user_name", userClean);
                                KeySaver.saveShare(AccessActivity.this, "user_nick", String.valueOf(obj.getString("nickname")));
                                KeySaver.saveShare(AccessActivity.this, "user_avatar", String.valueOf(obj.getString("avatar")));
                                KeySaver.saveShare(AccessActivity.this, "user_email", String.valueOf(obj.getString("email")));
                                KeySaver.saveShare(AccessActivity.this, "soldty_auth_left", username.getText().toString());
                                KeySaver.saveShare(AccessActivity.this, "soldty_auth_right", password.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    welcome.setVisibility(View.VISIBLE);
                                    if(KeySaver.isExist(AccessActivity.this, "user_name")){
                                        welcome.setText("Bienvenido/a, " + KeySaver.getStringSavedShare(AccessActivity.this, "user_name"));
                                    }
                                    welcome.animate().setDuration(800).
                                            alpha(1).setInterpolator(sDecelerator).
                                            withEndAction(new Runnable() {
                                                public void run() {
                                                    welcome.animate().
                                                            setDuration(2000).alpha(1).
                                                            withEndAction(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    AccessActivity.this.finish();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }

                        @Override
                        public void onFailure(int codeResponse, Header[] headers, byte[] errorResponse, Throwable throwable) {
                            loginVisible();
                            try {
                                if(decodeUTF8(errorResponse) != null){
                                    JSONArray array_error = new JSONArray(decodeUTF8(errorResponse));
                                    JSONObject obj_error = array_error.getJSONObject(0);
                                    if(obj_error.getString("code").contentEquals("invalid_username")){
                                        username.setError("Usuario no válido");
                                    }else if(obj_error.getString("code").contentEquals("incorrect_password")){
                                        password.setError("Password incorrecto");
                                    }
                                }else{
                                    loginVisible();
                                }
                            } catch (Exception e) {

                            }

                        }
                    });
                } else {

                }

            }
        });
    }

    String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    public void loginVisible(){
        access_agent.setAlpha(0);
        access_agent.setVisibility(View.VISIBLE);
        access_agent.setClickable(true);
        access_agent.animate().alpha(1).setDuration(500);
    }

    private String getB64Auth(String login, String pass) {
        String source = "user" + ":" + "pass";
        String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        return ret;
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
