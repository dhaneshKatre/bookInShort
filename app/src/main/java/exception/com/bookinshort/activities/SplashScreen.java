package exception.com.bookinshort.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;


import exception.com.bookinshort.R;

public class SplashScreen extends AppCompatActivity {
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final ImageView iv = (ImageView)findViewById(R.id.imageView);
        final TextView textView = (TextView)findViewById(R.id.bookText);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setStartOffset(10);
        alphaAnimation.setFillAfter(true);
        SharedPreferences sharedPreferences = getSharedPreferences("defaultLang",MODE_PRIVATE);
        lang=sharedPreferences.getString("Lang","");
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!lang.isEmpty()) startActivity(new Intent(SplashScreen.this,WelcomeActivity.class));
                else startActivity(new Intent(SplashScreen.this,LanguageSelection.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv.startAnimation(alphaAnimation);
        textView.startAnimation(alphaAnimation);
    }
}
