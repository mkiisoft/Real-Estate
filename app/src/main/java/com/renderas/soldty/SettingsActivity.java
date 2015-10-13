package com.renderas.soldty;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.renderas.soldty.utils.KeySaver;

/**
 * Created by mariano-zorrilla on 06/08/15.
 */
public class SettingsActivity extends ActionBarActivity {

    private TextView mExchangeHint,mConvertText,mPremiumText;
    private Toolbar mToolbar;
    private ToggleButton mToogleExchange, mTooglePremium;
    private Typeface mThin, mBold, mLight;
    private LinearLayout mPremiumSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mThin = Typeface.createFromAsset(getResources().getAssets(), "Thin.ttf");
        mBold = Typeface.createFromAsset(getResources().getAssets(), "Bold.ttf");
        mLight = Typeface.createFromAsset(getResources().getAssets(), "Light.ttf");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ajustes");

        final Intent settingsIntent = getIntent();

        mPremiumSetting = (LinearLayout) findViewById(R.id.premium_setting);

        if (settingsIntent != null && settingsIntent.getStringExtra("nonhome") != null) {
            mPremiumSetting.setVisibility(View.GONE);
        } else {
            mPremiumSetting.setVisibility(View.VISIBLE);
        }

        mTooglePremium = (ToggleButton) findViewById(R.id.premium_toggle);
        if(KeySaver.isExist(SettingsActivity.this, "premium_true")){
            mTooglePremium.setChecked(true);
        }else{
            mTooglePremium.setChecked(false);
        }
        mTooglePremium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isCheckedPremium) {
                if (isCheckedPremium) {
                    if (!KeySaver.isExist(SettingsActivity.this, "premium_true")) {
                        KeySaver.saveShare(SettingsActivity.this, "premium_true", true);
                    }
                } else {
                    if (KeySaver.isExist(SettingsActivity.this, "premium_true")) {
                        KeySaver.removeKey(SettingsActivity.this, "premium_true");
                    }
                }
            }
        });

        mExchangeHint = (TextView) findViewById(R.id.exchange_text);
        mExchangeHint.setTypeface(mLight);

        mConvertText = (TextView) findViewById(R.id.convert_rate);
        mPremiumText = (TextView) findViewById(R.id.premium_text);
        mConvertText.setTypeface(mLight);
        mPremiumText.setTypeface(mLight);
        
        mToogleExchange = (ToggleButton) findViewById(R.id.exchange_toggle);
        if(KeySaver.isExist(SettingsActivity.this, "exchange_true")){
            mToogleExchange.setChecked(true);
        }else{
            mToogleExchange.setChecked(false);
        }

        mToogleExchange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if(isChecked){
                    if(!KeySaver.isExist(SettingsActivity.this, "exchange_true")){
                        KeySaver.saveShare(SettingsActivity.this, "exchange_true", true);
                    }
                }else{
                    if(KeySaver.isExist(SettingsActivity.this, "exchange_true")) {
                        KeySaver.removeKey(SettingsActivity.this, "exchange_true");
                    }
                }
            }
        });

        if(KeySaver.isExist(this, "bob_rate")){
            mExchangeHint.setText("La cotización del Dólar al dia "
                    + KeySaver.getStringSavedShare(this, "bob_date") + " es: b$"
                    + KeySaver.getStringSavedShare(this, "bob_rate"));
        }
    }

    @Override
    public void onBackPressed() {
        if(!KeySaver.isExist(SettingsActivity.this, "premium_true")){
            Intent returnIntent = new Intent();
            setResult(0, returnIntent);
            finish();
        }else if (KeySaver.isExist(SettingsActivity.this, "premium_true")){
            Intent returnIntent = new Intent();
            setResult(-1, returnIntent);
            finish();
        }else{
            Intent returnIntent = new Intent();
            setResult(2, returnIntent);
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!KeySaver.isExist(SettingsActivity.this, "premium_true")){
                    Intent returnIntent = new Intent();
                    setResult(0, returnIntent);
                    finish();
                }else if (KeySaver.isExist(SettingsActivity.this, "premium_true")){
                    Intent returnIntent = new Intent();
                    setResult(-1, returnIntent);
                    finish();
                }else{
                    Intent returnIntent = new Intent();
                    setResult(2, returnIntent);
                    finish();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
