package exception.com.bookinshort.activities;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private String lang,genre,name;
    private tabPagerAdapter tpa;
    private List<bookTab> bookList;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_book_open);
        viewPager = (ViewPager) findViewById(R.id.tabOpenViewPager);
        lang = getIntent().getExtras().getString("lang");
        genre = getIntent().getExtras().getString("genre");
        name = getIntent().getExtras().getString("name");
        bookList = new ArrayList<>();
        tpa = new tabPagerAdapter(this, bookList);
        viewPager.setAdapter(tpa);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
        if (genre == null) {
            SharedPreferences getBookName = getSharedPreferences(name, MODE_PRIVATE);

            int count = getBookName.getInt("tabCount", 0);
            for (int i = 1; i <= count; i++) {
                String abc = getBookName.getString(valueOf(i), "");
                String tab = getBookName.getString(abc, "");
                bookTab bt = new bookTab(tab);
                bookList.add(bt);
                tpa.notifyDataSetChanged();

            }
            int lastPage = getBookName.getInt("lastPage",0);
            viewPager.setCurrentItem(lastPage);


        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("Books").child(lang).child(genre).child(name).child("Content");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long count = dataSnapshot.getChildrenCount();
                    HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                    SharedPreferences val = getSharedPreferences(name, MODE_PRIVATE);
                    SharedPreferences.Editor editor = val.edit();
                    for (long i = 1; i <= count; i++) {
                        String abc = "tab" + valueOf(i);
                        bookTab bt = new bookTab((String) value.get(abc));
                        bookList.add(bt);
                        tpa.notifyDataSetChanged();
                        editor.putInt("tabCount", (int) i);
                        editor.putString(valueOf(i), abc);
                        editor.putString(abc, (String) value.get(abc));
                        editor.apply();
                    }
                    int lastPage = val.getInt("lastPage",0);
                    viewPager.setCurrentItem(lastPage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("mkdk", databaseError.getDetails());
                    Toast.makeText(TabBookOpenActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            initSharedPref();

        }
    }

    private void initSharedPref() {

        SharedPreferences storeBookName = getSharedPreferences("bookNames", MODE_PRIVATE);
        SharedPreferences.Editor addBookName = storeBookName.edit();
        int c = storeBookName.getInt("current", 0);
        for (int i = 0; i <= c; i++) {
            String nemo = storeBookName.getString(valueOf(i), "");
            if (nemo.equalsIgnoreCase(name)) {
                addBookName.putString(String.valueOf(i),"null");
                addBookName.apply();
            }
        }
            c++;
            addBookName.putInt("current", c);
            addBookName.putString(valueOf(c), name);
            addBookName.apply();
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
