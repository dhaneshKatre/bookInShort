package exception.com.bookinshort.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        alphaAnimation.setDuration(1500);
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
                isStoragePermissionGranted();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv.startAnimation(alphaAnimation);
        textView.startAnimation(alphaAnimation);
    }
    public  void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                if(!lang.isEmpty()){
                    Intent intent =new Intent(SplashScreen.this,WelcomeActivity.class);
                    startActivity(intent);
                }
                else {
                    startActivity(new Intent(SplashScreen.this,LanguageSelection.class));
                }



            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startActivity(new Intent(this,LanguageSelection.class));
                    finish();
                }
                else
                {
                    Toast.makeText(this, "Please Grant Permission", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }
}
