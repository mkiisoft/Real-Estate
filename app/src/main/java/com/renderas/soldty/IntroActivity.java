package com.renderas.soldty;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.renderas.soldty.utils.KeySaver;

/**
 * Created by Mariano on 18/08/2015.
 */
public class IntroActivity extends AppIntro {
    @Override
    public void init(Bundle bundle) {

        if(KeySaver.isExist(this, "doneintro")){
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        addSlide(AppIntroFragment.newInstance("Soldty", "Bienvenido/a al servicio de propiedades de Soldty", R.drawable.intro_logo, Color.parseColor("#FE1703")));
        addSlide(AppIntroFragment.newInstance("Mapa", "Encuentra propiedades en tu localidad", R.drawable.intro_map, Color.parseColor("#146BCC")));
        addSlide(AppIntroFragment.newInstance("Favoritos", "Revisa tus propiedades favoritas, incluso sin conexión", R.drawable.intro_fav, Color.parseColor("#01B901")));

        showSkipButton(false);
    }

    @Override
    public void onSkipPressed() {

    }

    @Override
    public void onDonePressed() {
        KeySaver.saveShare(IntroActivity.this, "doneintro", "true");
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
