package exception.com.bookinshort.activities;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.com.bookinshort.R;

import static java.lang.String.valueOf;

public class TabBookOpenActivity extends AppCompatActivity{

    private ViewPager viewPager;
    private DatabaseReference databaseReference;
    private String lang,genre,name;
    private tabPagerAdapter tpa;
    private Toolbar toolbar;
    private List<bookTab> bookList;
    private TabLayout tabLayout;
    private Pattern pattern;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_book_open);
        viewPager = (ViewPager) findViewById(R.id.tabOpenViewPager);
        toolbar = (Toolbar)findViewById(R.id.toolbarInOpenBook);
        setSupportActionBar(toolbar);
        lang = getIntent().getExtras().getString("lang");
        genre = getIntent().getExtras().getString("genre");
        name = getIntent().getExtras().getString("name");
        toolbar.setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        List<String> tokens = new ArrayList<String>();
        tokens.add("<strong>");
        tokens.add("<br>");
        tokens.add("<h1>");
        tokens.add("<italic>");
        String patternString =TextUtils.join("", tokens);
         pattern = Pattern.compile(patternString);

        bookList = new ArrayList<>();
        tpa = new tabPagerAdapter(this, bookList);
        viewPager.setAdapter(tpa);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
        SharedPreferences getBookName = getSharedPreferences(name, MODE_PRIVATE);
        int lastPage = getBookName.getInt("lastPage",0);
        int count = getBookName.getInt("tabCount", 0);
        if (count!=0) {
            for (int i = 1; i <= count; i++) {
                String abc = getBookName.getString(valueOf(i), "");
                String tabContent = getBookName.getString(abc, "");
                escapeSeq(tabContent);
                bookTab bt = new bookTab(tabContent);
                bookList.add(bt);
                tpa.notifyDataSetChanged();
            }
            viewPager.setCurrentItem(lastPage);
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("Books").child(lang).child(genre).child(name).child("Content");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long count = dataSnapshot.getChildrenCount();
                    HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                    SharedPreferences val = getSharedPreferences(name, MODE_PRIVATE);
                    SharedPreferences.Editor editor = val.edit();
                    for (long i = 1; i <= count; i++) {
                        String abc = "tab" + valueOf(i);
                        bookTab bt = new bookTab((String) value.get(abc));
                        String tabNumber = "tab" + valueOf(i);
                        String tabContent = bt.getTab();
                        escapeSeq(tabContent);
                        bookList.add(bt);
                        tpa.notifyDataSetChanged();
                        editor.putInt("tabCount", (int) i);
                        editor.putString(valueOf(i), abc);
                        editor.putString(abc, (String) value.get(abc));
                        editor.apply();
                    }
                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        initSharedPref();
    }


    private void escapeSeq(String tabContent) {
        Matcher matcher = pattern.matcher(tabContent);
        while (matcher.matches()){
            String special = tabContent.substring(matcher.start(),matcher.end()-1);
            Toast.makeText(TabBookOpenActivity.this, special, Toast.LENGTH_SHORT).show();

        }
    }

    private void initSharedPref() {
        SharedPreferences storeBookName = getSharedPreferences("bookNames", MODE_PRIVATE);
        SharedPreferences.Editor addBookName = storeBookName.edit();
        int c = storeBookName.getInt("current", 0);
        for (int i = 0; i <= c; i++) {
            String nemo = storeBookName.getString(String.valueOf(i), "");
            if (nemo.equals(name)) {
                addBookName.remove(String.valueOf(i));
                addBookName.apply();
            }
        }
            c++;
            addBookName.putInt("current", c);
            addBookName.putString(String.valueOf(c), name);
            addBookName.apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences getBookName = getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor lastPage = getBookName.edit();
        lastPage.putInt("lastPage",tabLayout.getSelectedTabPosition());
        lastPage.apply();
        super.onDestroy();
    }
}