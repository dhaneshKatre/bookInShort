package exception.com.bookinshort.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import exception.com.bookinshort.R;

public class WelcomeActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private String language;
    private ProgressDialog progressDialog;
    private RecyclerView.Adapter bookAdapter;
    private List<BookData> bookModelList;
    private NavigationView navigationView;
    private DatabaseReference bookReference;
    private StorageReference bookIconReference;
    private RecyclerView bookRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPreferences sharedPreferences = getSharedPreferences("defaultLang",MODE_PRIVATE);
        language=sharedPreferences.getString("Lang","");
        if(language.isEmpty()){
            language="English";
        }

        bookReference = FirebaseDatabase.getInstance().getReference("Books");
        bookIconReference = FirebaseStorage.getInstance().getReference("Books");

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);

        drawer = (DrawerLayout)findViewById(R.id.draw);
        drawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.drawer_open,R.string.drawer_close);

        bookRecyclerView = (RecyclerView)findViewById(R.id.mainRecyclerView);
        bookRecyclerView.setHasFixedSize(true);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookModelList = new ArrayList<>();

        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_home:default:
                        loadHomeData();
                        break;
                    case R.id.genre_scifi:
                        loadData("Sci-fi");
                        break;
                    case R.id.genre_novel:
                        loadData("Novel");
                        break;
                    case R.id.genre_poem:
                        loadData("Poem");
                        break;
                    case R.id.nav_about_us:
                        drawer.closeDrawers();
                        startActivity(new Intent(WelcomeActivity.this,AboutUs.class));
                        break;
                    case R.id.loginMenu:
                        drawer.closeDrawers();
                        startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                        break;
                }
                if(item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                item.setChecked(true);
                return true;
            }
        });

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawers();
            return;
        }
        assert getSupportActionBar() != null;
        assert getSupportActionBar().getTitle() != null;
        if(getSupportActionBar().getTitle().equals(getResources().getString(R.string.app_name))){
            super.onBackPressed();
            return;
        }
        super.onBackPressed();
    }

    public void loadHomeData() {
        drawer.closeDrawers();
        getSupportActionBar().setTitle("Home");
        bookAdapter = new BookAdapter(bookModelList,this,language,null);
        bookRecyclerView.setAdapter(bookAdapter);
        bookModelList.clear();
        SharedPreferences bookName = getSharedPreferences("bookNames",MODE_PRIVATE);
        int count = bookName.getInt("current",0);
        for (int i = count;i>=1;i--){
            if(bookName.getString(String.valueOf(i),"").equalsIgnoreCase("null")){
                continue;
            }
            String nemo = bookName.getString(String.valueOf(i),"");
            loadFromDevice(nemo);

         }
    }
    private void loadFromDevice(String nemo) {
        File rootPath=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"bookInShort");
        if (!rootPath.exists()){
            Toast.makeText(this, "OPEN A BOOK FIRST", Toast.LENGTH_SHORT).show();
        }
        final File localFile =  new File(rootPath,nemo+".jpeg");
        String auth,describ;
        SharedPreferences namePref = getSharedPreferences(nemo,MODE_PRIVATE);
        auth=namePref.getString("author","");
        describ=namePref.getString("describ","");
        BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()), nemo, describ, auth);
        bookModelList.add(bookData);
        bookAdapter.notifyDataSetChanged();
        onBackPressed();
    }


    public void loadData(final String genre){
        bookAdapter = new BookAdapter(bookModelList,this,language,genre);
        bookRecyclerView.setAdapter(bookAdapter);
        bookModelList.clear();
        drawer.closeDrawers();
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(genre);
        final DatabaseReference EngSciFiRef = bookReference.child(language).child(genre);
        final StorageReference EngSciFiImageRef = bookIconReference.child(language).child(genre);
        progressDialog.setMessage("Loading Content...");
        progressDialog.show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EngSciFiRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            Toast.makeText(getApplicationContext(), "Empty List", Toast.LENGTH_LONG).show();
                            return;
                        }
                        for (final DataSnapshot data : dataSnapshot.getChildren()) {
                            final String name = data.getKey();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final File temp = new File(Environment.getExternalStorageDirectory(),"bookInShort");
                                    if(!temp.exists()){
                                        temp.mkdir();
                                    }
                                    String auth,describ;
                                        final File localFile =  new File(temp,name+".jpeg");
                                        if(localFile.exists()){
                                            SharedPreferences namePref = getSharedPreferences(name,MODE_PRIVATE);
                                            auth=namePref.getString("author","");
                                            describ=namePref.getString("describ","");
                                            BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()), name, describ, auth);
                                            bookModelList.add(bookData);
                                            bookAdapter.notifyDataSetChanged();
                                        }
                                        else {
                                        final HashMap<String, Object> value = (HashMap<String, Object>) data.getValue();
                                        final StorageReference exactRef = EngSciFiImageRef.child(name+".jpg");
                                        final String bookDesString = value.get("Describ").toString();
                                        final String bookAuthorString = value.get("Author").toString();
                                        exactRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                                BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()), name ,bookDesString  ,bookAuthorString );
                                                bookModelList.add(bookData);
                                                bookAdapter.notifyDataSetChanged();

                                                //Writing To Shared Preference

                                                SharedPreferences bookGenre = getSharedPreferences(genre,MODE_PRIVATE);
                                                SharedPreferences bookNameAddVal = getSharedPreferences(name,MODE_PRIVATE);
                                                SharedPreferences.Editor nameAddEdit = bookNameAddVal.edit();
                                                SharedPreferences.Editor genreEdit = bookGenre.edit();

                                                int c = bookGenre.getInt("count", 0);
                                                c++;
                                                genreEdit.putInt("count", c);
                                                genreEdit.putString(String.valueOf(c), name);
                                                nameAddEdit.putString("author",bookAuthorString);
                                                nameAddEdit.putString("bookName",name);
                                                nameAddEdit.putString("describ",bookDesString);
                                                nameAddEdit.apply();
                                                genreEdit.apply();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("mkdkpro", e.getMessage());
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Log.e("mkdkpro",databaseError.getMessage());
                    }
                });
            }
        });
        progressDialog.dismiss();
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.welcome_options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.languageChange:
                startActivity(new Intent(WelcomeActivity.this,LanguageSelection.class));
                finish();
                 return  true;
            case R.id.clearData:
                SharedPreferences ms = getSharedPreferences("bookNames",MODE_PRIVATE);
                SharedPreferences.Editor edit= ms.edit();
                File file = new File(Environment.getExternalStorageDirectory(),"bookInShort");
                file.delete();
                edit.clear();
                edit.apply();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

}






















