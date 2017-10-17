package exception.com.bookinshort.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import exception.com.bookinshort.R;

public class LoginActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tabLayout=(TabLayout) findViewById(R.id.loginTabLayout);
        viewPager=(ViewPager)findViewById(R.id.loginViewPager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        LoginViewPagerAdapter adapter = new LoginViewPagerAdapter(getSupportFragmentManager());
        LoginFragment loginFrag = new LoginFragment();
        LoginSingUpFragment lsuf = new LoginSingUpFragment();
        adapter.addFragment(loginFrag,"Login");
        adapter.addFragment(lsuf,"Register");
        viewPager.setAdapter(adapter);
    }
}
