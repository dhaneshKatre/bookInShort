package exception.com.bookinshort.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import exception.com.bookinshort.R;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        final Toolbar toolbarInAboutUs = (Toolbar)findViewById(R.id.toolbarInAbout);
        setSupportActionBar(toolbarInAboutUs);
        getSupportActionBar().setTitle("About Us");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-1108614541649691/1696878290");
    }
}
