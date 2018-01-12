package exception.com.bookinshort.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import exception.com.bookinshort.R;

import static java.lang.String.valueOf;

public class TabBookOpenActivity extends AppCompatActivity{

    private ViewPager viewPager;
    private DatabaseReference databaseReference;
    private String lang,genre,name,fromLocation;
    private tabPagerAdapter tpa;
    private Toolbar toolbar;
    private List<bookTab> bookList;
    private static TabLayout tabLayout;
    Boolean tabLayVisivility =true;
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_book_open);
        viewPager = (ViewPager) findViewById(R.id.tabOpenViewPager);
        toolbar = (Toolbar)findViewById(R.id.toolbarInOpenBook);
        setSupportActionBar(toolbar);
        lang = getIntent().getExtras().getString("lang");
        genre = getIntent().getExtras().getString("genre");
        name = getIntent().getExtras().getString("name");
        fromLocation = getIntent().getExtras().getString("fromLocation");
        toolbar.setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bookList = new ArrayList<>();
        tpa = new tabPagerAdapter(this, bookList,name);
        viewPager.setAdapter(tpa);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (fromLocation.equals("local")||genre.equals("local")) loadFromLocal();
        else loadFromFirebase();
        initSharedPref();
        onScroll();
        onTabSelect();
        onTap();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void onTap() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.bookOpenLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("BookInShort","TabOpenActivity viewPager click");
                if(tabLayVisivility) {
                    Log.e("BookInShort","TabOpenActivity viewPager click -> tab is invisible now");
                    tabLayout.setVisibility(View.INVISIBLE);
                    tabLayVisivility = false;
                }
                else {
                    Log.e("BookInShort","TabOpenActivity viewPager click -> tab is visible now");
                    tabLayout.setVisibility(View.VISIBLE);
                    tabLayVisivility=true;
                }
            }
        });
       /* viewPager.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("BookInShort","TabOpenActivity viewPager click");
            if(tabLayVisivility) {
                Log.e("BookInShort","TabOpenActivity viewPager click -> tab is invisible now");
                tabLayout.setVisibility(View.INVISIBLE);
                tabLayVisivility = false;
            }
            else {
                Log.e("BookInShort","TabOpenActivity viewPager click -> tab is visible now");
                tabLayout.setVisibility(View.VISIBLE);
                tabLayVisivility=true;
            }
        }
    });
    */
    }

    private void loadFromLocal() {
        SharedPreferences getBookName = getSharedPreferences(name, MODE_PRIVATE);
        int lastPage = getBookName.getInt("lastPage",0);
        int count = getBookName.getInt("tabCount", 0);
        for (int i = 1; i <= count; i++) {
            TabLayout.Tab newTab = tabLayout.newTab();
            newTab.setText(String.valueOf(i));
            tabLayout.addTab(newTab);
            String tabContent = getBookName.getString("tab"+valueOf(i), "");
            bookTab bt = new bookTab(tabContent,"local");
            bookList.add(bt);
            tpa.notifyDataSetChanged();
        }
        viewPager.setCurrentItem(lastPage);
    }

    private void onTabSelect() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
    }

    private void loadFromFirebase() {
        SharedPreferences sharedPreferences = getSharedPreferences(name, MODE_PRIVATE);
        int i = sharedPreferences.getInt("tabCount", 0);
        if (!String.valueOf(i).isEmpty()) {
            for(int c = 0;c<=i;c++){
                sharedPreferences.edit().remove("tab" + valueOf(c)).apply();
            }
            sharedPreferences.edit().remove("tabCount").apply();
            databaseReference = FirebaseDatabase.getInstance().getReference("Books").child(lang).child(genre).child(name).child("Content");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Long count = dataSnapshot.getChildrenCount();
                            HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                            viewPager.setOffscreenPageLimit(count.intValue());
                            for (long i = 1; i <= count; i++) {
                                String tab_i = "tab" + valueOf(i);
                                bookTab bt = new bookTab((String) value.get(tab_i),"firebase");
                                TabLayout.Tab newTab = tabLayout.newTab();
                                newTab.setText(String.valueOf(i));
                                tabLayout.addTab(newTab);
                                bookList.add(bt);
                                tpa.notifyDataSetChanged();
                                storeToSP(bt);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
        }
    }

    private void storeToSP(bookTab bt) {
        SharedPreferences sharedPreferences = getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int i = sharedPreferences.getInt("tabCount", 0);
        if (String.valueOf(i).isEmpty()) i = 0;
        i++;
        editor.putInt("tabCount", i);
        editor.putString("tab" + valueOf(i), bt.getTab());
        editor.apply();
    }

    @SuppressLint("NewApi")
    private void onScroll() {
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            tabLayout.setVisibility(View.VISIBLE);
            tabLayout.setScrollPosition(position,0f,true);

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    });
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
        lastPage.putInt("lastPage",viewPager.getCurrentItem());
        lastPage.apply();
        super.onDestroy();
    }

}