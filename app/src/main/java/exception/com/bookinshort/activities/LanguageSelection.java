package exception.com.bookinshort.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import exception.com.bookinshort.R;

public class LanguageSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        SharedPreferences sharedPreferences = getSharedPreferences("defaultLang",MODE_PRIVATE);
        final SharedPreferences.Editor editor= sharedPreferences.edit();
        final Button english = (Button)findViewById(R.id.English);
        final Button marathi = (Button)findViewById(R.id.Marathi);
        final Button all = (Button)findViewById(R.id.All);
        final Intent intent = new Intent(LanguageSelection.this,WelcomeActivity.class);
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                editor.putString("Lang","English");
                editor.apply();
                finish();
            }
        });
        marathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                editor.putString("Lang","Marathi");
                editor.apply();
                finish();
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                editor.putString("Lang","All");
                editor.apply();
                finish();
            }
        });
    }
}
