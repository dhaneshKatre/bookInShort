package exception.com.bookinshort.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        language = getIntent().getExtras().getString("Language","English");
        bookReference = FirebaseDatabase.getInstance().getReference("Books");
        bookIconReference = FirebaseStorage.getInstance().getReference("Books");

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);

        drawer = (DrawerLayout)findViewById(R.id.draw);
        drawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.drawer_open,R.string.drawer_close);

        final RecyclerView bookRecyclerView = (RecyclerView)findViewById(R.id.mainRecyclerView);
        bookRecyclerView.setHasFixedSize(true);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookModelList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookModelList,this);
        bookRecyclerView.setAdapter(bookAdapter);

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
        if (!getSupportActionBar().getTitle().equals(getResources().getString(R.string.app_name))) {
            loadHomeData();
        }
    }

    public void loadHomeData(){
        navigationView.getMenu().getItem(0).setChecked(true);
        drawer.closeDrawers();
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show();
    }
    public void loadData(String genre){
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
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            final String name = data.getKey();
                            final HashMap<String, Object> value = (HashMap<String, Object>) data.getValue();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    File extStorage = Environment.getExternalStorageDirectory();
                                    File temp = new File(extStorage,"bookInShort");
                                    if(!temp.exists()){
                                        temp.mkdir();
                                    }
                                    final File rootPath = new File(temp,name);
                                    if ( !rootPath.exists())
                                    {   rootPath.mkdir();
                                    }
                                    final StorageReference exactRef = EngSciFiImageRef.child(name+".jpg");
                                    final File localFile =  new File(rootPath,name+".jpeg");
                                    final File bookName = new File(rootPath,"name");
                                    final File bookDes = new File(rootPath,"Describ");
                                    final File bookAuthor = new File(rootPath,"Author");


                                    String localName,localDescrib,localAuthor;

                                    if (localFile.exists()&&bookDes.exists()&&bookName.exists()&&bookAuthor.exists()) {
                                             try {
                                                 FileInputStream fisName = new FileInputStream(bookName);
                                                 FileInputStream fisDes = new FileInputStream(bookDes);
                                                 FileInputStream fisAuth = new FileInputStream(bookAuthor);

                                                 InputStreamReader isrName = new InputStreamReader(fisName);
                                                 InputStreamReader isrDes = new InputStreamReader(fisDes);
                                                 InputStreamReader isrAuthor = new InputStreamReader(fisAuth);

                                                 BufferedReader brName = new BufferedReader(isrName);
                                                 BufferedReader brAuthor = new BufferedReader(isrAuthor);
                                                 BufferedReader brDes = new BufferedReader(isrDes);

                                                 StringBuffer sbDes = new StringBuffer();
                                                 StringBuffer sbName = new StringBuffer();
                                                 StringBuffer sbAuthor = new StringBuffer();
                                                 while ((localDescrib=brDes.readLine())!=null)
                                                 {
                                                     sbDes.append(localDescrib+"\n");
                                                 }
                                                 while ((localName=brName.readLine())!=null)
                                                 {
                                                     sbName.append(localName+"\n");
                                                 }
                                                 while ((localAuthor=brAuthor.readLine())!=null)
                                                 {
                                                     sbAuthor.append(localAuthor+"\n");
                                                 }
                                                 BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()), name, sbDes.toString(), sbAuthor.toString());
                                                 bookModelList.add(bookData);
                                                 bookAdapter.notifyDataSetChanged();
                                             } catch (IOException e) {
                                                 e.printStackTrace();
                                             }
                                         } else {
                                             final String bookDesString = value.get("Describ").toString();
                                             final String bookAuthorString = value.get("Author").toString();
                                             exactRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                                     BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()), name ,bookDesString  ,bookAuthorString );
                                                     bookModelList.add(bookData);
                                                     bookAdapter.notifyDataSetChanged();

                                                     //Writing TO Local Directory

                                                     try {
                                                         FileOutputStream fos = new FileOutputStream(bookDes);
                                                         FileOutputStream fon = new FileOutputStream(bookName);
                                                         FileOutputStream foa = new FileOutputStream(bookAuthor);
                                                         fos.write(bookDesString.getBytes());
                                                         fon.write(name.getBytes());
                                                         foa.write(bookAuthorString.getBytes());
                                                         fos.close();
                                                         fon.close();
                                                         foa.close();
                                                     } catch (IOException e) {
                                                         e.printStackTrace();
                                                         Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                     }

                                                 }
                                             }).addOnFailureListener(new OnFailureListener() {
                                                 @Override
                                                 public void onFailure(@NonNull Exception e) {
                                                     Log.e("mkdkpro", e.getMessage());
                                                     //Toast.makeText(WelcomeActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();

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
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }



    public void loadScifiData() {
        bookModelList.clear();
        drawer.closeDrawers();
        getSupportActionBar().setTitle("Sci-Fi");
        switch (language) {
            case "English":
            default:
                final DatabaseReference EngSciFiRef = bookReference.child("English").child("Sci-fi");
                final StorageReference EngSciFiImageRef = bookIconReference.child("English").child("Sci-fi");
                progressDialog.setMessage("Loading Content...");
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
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    //final Bitmap bookIcon = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
                                    final HashMap<String, Object> value = (HashMap<String, Object>) data.getValue();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            StorageReference exactRef = EngSciFiImageRef.child(value.get("Name").toString()+".jpg");
                                            try {
                                                final File tempFile = File.createTempFile("bookIcon","jpg");
                                                exactRef.getFile(tempFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                                        BookData bookData = new BookData(BitmapFactory.decodeFile(tempFile.getAbsolutePath()),value.get("Name").toString(),value.get("Describ").toString(),value.get("Author").toString());
                                                        bookModelList.add(bookData);
                                                        bookAdapter.notifyDataSetChanged();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("mkdkpro",e.getMessage());
                                                    }
                                                });
                                            } catch (IOException e) {
                                                e.printStackTrace();
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
                break;
            case "Marathi":
                break;
            case "All":
                break;
        }
    }
    public void loadNovelData(){
        drawer.closeDrawers();
        bookModelList.clear();
        bookAdapter.notifyDataSetChanged();
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Novels");
    }
    public void loadPoemData(){
        drawer.closeDrawers();
        bookModelList.clear();
        bookAdapter.notifyDataSetChanged();
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Poems");
    }

}






















