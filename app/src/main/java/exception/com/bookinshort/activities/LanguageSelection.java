package exception.com.bookinshort.activities;

import android.content.Intent;
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
        final Button english = (Button)findViewById(R.id.English);
        final Button marathi = (Button)findViewById(R.id.Marathi);
        final Button all = (Button)findViewById(R.id.All);
        final Intent intent = new Intent(LanguageSelection.this,WelcomeActivity.class);
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Language","English");
                startActivity(intent);
                finish();
            }
        });
        marathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Language","Marathi");
                startActivity(intent);
                finish();
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Language","All");
                startActivity(intent);
                finish();
            }
        });
    }
}
