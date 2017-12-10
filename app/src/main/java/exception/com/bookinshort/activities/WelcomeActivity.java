package exception.com.bookinshort.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import exception.com.bookinshort.R;

import static java.lang.String.valueOf;

public class WelcomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private String language;
    private ProgressDialog progressDialog;
    private BookAdapter bookAdapter;
    private String rating,genre,name,location;
    private List<BookData> bookModelList;
    private DatabaseReference bookReference,EngSciFiRef;
    private StorageReference bookIconReference,EngSciFiImageRef;
    private RecyclerView bookRecyclerView;
    private FirebaseAuth firebaseAuth;
    private ItemTouchHelper itemTouchHelper;
    private boolean isLoading=false;
    private int lastVisibleItem,totalItemCount;
    private long totalBooksInFb;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 69;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        checkPermissions();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) Toast.makeText(getApplicationContext(),"Welcome "+user.getDisplayName(),Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(),"Login to unlock exciting features!",Toast.LENGTH_SHORT).show();

        drawer = (DrawerLayout)findViewById(R.id.draw);
        drawerToggle = new ActionBarDrawerToggle(this,drawer, toolbar,R.string.drawer_open,R.string.drawer_close);

        bookRecyclerView = (RecyclerView)findViewById(R.id.mainRecyclerView);
        bookRecyclerView.setHasFixedSize(true);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookModelList = new ArrayList<>();
        final NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_home:default:
                        location="local";
                        loadHomeData();
                        break;
                    case R.id.genre_scifi:
                        loadData("Sci-fi");
                        genre="Sci-fi";
                        break;
                    case R.id.genre_novel:
                        loadData("Novel");
                        genre="Novel";
                        break;
                    case R.id.genre_poem:
                        loadData("Poem");
                        genre="Poem";
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
        loadHomeData();
    }
    private void bookSetAdapter(final String genre) {
        final DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Books").child(language).child(genre);
                if(bookModelList.isEmpty() || bookModelList == null){
                    dr.limitToFirst(3).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 0) {
                                Toast.makeText(getApplicationContext(), "Empty List", Toast.LENGTH_LONG).show();
                                Log.e("BookInShort",dr.toString());
                                return;
                            }
                            for (final DataSnapshot data : dataSnapshot.getChildren()) {
                                final File temp = new File(Environment.getExternalStorageDirectory(), "bookInShort");
                                if (!temp.exists()) {
                                    temp.mkdir();
                                }
                                String auth, describ;
                                final File localFile = new File(temp, data.getKey() + ".jpeg");
                                if (localFile.exists()) {
                                    SharedPreferences namePref = getSharedPreferences(data.getKey(), MODE_PRIVATE);
                                    auth = namePref.getString("author", "");
                                    describ = namePref.getString("describ", "");
                                    rating = namePref.getString("rating", "");
                                    BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()), data.getKey(), describ, auth, rating);
                                    bookModelList.add(bookData);
                                    bookAdapter.notifyDataSetChanged();
                                } else {
                                    final HashMap<String, Object> value = (HashMap<String, Object>) data.getValue();
                                    final StorageReference exactRef = EngSciFiImageRef.child(data.getKey()+ ".jpg");
                                    final String bookDesString = value.get("Describ").toString();
                                    final String bookAuthorString = value.get("Author").toString();
                                    final String rating = value.get("rating").toString();
                                    exactRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                            BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()), data.getKey(), bookDesString, bookAuthorString, rating);
                                            bookModelList.add(bookData);
                                            bookAdapter.notifyDataSetChanged();
                                            SharedPreferences bookNameAddVal = getSharedPreferences(data.getKey(), MODE_PRIVATE);
                                            SharedPreferences.Editor nameAddEdit = bookNameAddVal.edit();
                                            nameAddEdit.putString("author", bookAuthorString);
                                            nameAddEdit.putString("rating", rating);
                                            nameAddEdit.putString("bookName", data.getKey());
                                            nameAddEdit.putString("describ", bookDesString);
                                            nameAddEdit.putString("lang", language);
                                            nameAddEdit.putString("genre", WelcomeActivity.this.genre);
                                            nameAddEdit.apply();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("WelcomeActivity", e.getMessage());
                                            Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            dr.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(WelcomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                  //  bookModelList.remove(bookModelList.size()-1);
                    BookData bd = bookModelList.get(bookModelList.size()-1);
                    Log.e("BookInShort",bd.getName());
                    dr.orderByKey().startAt(bd.getName()).limitToFirst(4).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 0) {
                                Toast.makeText(getApplicationContext(), "Empty List", Toast.LENGTH_LONG).show();
                                return;
                            }
                            int i = 0;
                            for (final DataSnapshot data : dataSnapshot.getChildren()) {
                                if(i!=0){
                                    final File temp = new File(Environment.getExternalStorageDirectory(), "bookInShort");
                                    if (!temp.exists()) {
                                        temp.mkdir();
                                    }
                                    String auth, describ;
                                    final File localFile = new File(temp, data.getKey() + ".jpeg");
                                    if (localFile.exists()) {
                                        SharedPreferences namePref = getSharedPreferences(data.getKey(), MODE_PRIVATE);
                                        auth = namePref.getString("author", "");
                                        describ = namePref.getString("describ", "");
                                        rating = namePref.getString("rating", "");
                                        BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()), data.getKey(), describ, auth, rating);
                                        Log.e("BookInShort","Book Added- "+data.getKey());
                                        bookModelList.add(bookData);
                                        bookAdapter.notifyDataSetChanged();
                                        isLoading=false;
                                    } else {
                                        final HashMap<String, Object> value = (HashMap<String, Object>) data.getValue();
                                        EngSciFiImageRef = bookIconReference.child(language).child(genre);
                                        final StorageReference exactRef = EngSciFiImageRef.child(data.getKey()+ ".jpg");
                                        final String bookDesString = value.get("Describ").toString();
                                        final String bookAuthorString = value.get("Author").toString();
                                        final String rating = value.get("rating").toString();
                                        exactRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                                BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()), data.getKey(), bookDesString, bookAuthorString, rating);
                                                bookModelList.add(bookData);
                                                bookAdapter.notifyDataSetChanged();

                                                Log.e("BookInShort","Book Added- "+data.getKey());
                                                SharedPreferences bookNameAddVal = getSharedPreferences(data.getKey(), MODE_PRIVATE);
                                                SharedPreferences.Editor nameAddEdit = bookNameAddVal.edit();
                                                nameAddEdit.putString("author", bookAuthorString);
                                                nameAddEdit.putString("rating", rating);
                                                nameAddEdit.putString("bookName", data.getKey());
                                                nameAddEdit.putString("describ", bookDesString);
                                                nameAddEdit.putString("lang", language);
                                                nameAddEdit.putString("genre", WelcomeActivity.this.genre);
                                                nameAddEdit.apply();
                                                isLoading=false;
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("WelcomeActivity", e.getMessage());
                                                Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            }
                                        });
                                    }
                                }
                                i++;
                            }
                            dr.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(WelcomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
    }

    private void setRecycleScrollListener() {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) bookRecyclerView.getLayoutManager();
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        bookRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            totalItemCount = linearLayoutManager.getItemCount();
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            if(!isLoading&&(lastVisibleItem==totalItemCount-1)&&(totalItemCount<totalBooksInFb)){
              /* bookModelList.add(new BookData(null,"qaz",null,null,null));
                bookAdapter.notifyDataSetChanged(); */
                Log.e("BookInShort","Welcome Activity - pass "+totalItemCount+totalBooksInFb+lastVisibleItem );
                isLoading=true;
                bookSetAdapter(genre);

                }
            else{
              // Log.e("BookInShort","Welcome Activity - fail "+totalItemCount+totalBooksInFb+isLoading+lastVisibleItem );
            }
        }
    });
    }

    public boolean checkPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Important Permission")
                        .setMessage("Kindly grant this permission for neat functioning of this app. App needs this permission for storing loaded images for faster results.").setPositiveButton("okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(WelcomeActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_STORAGE);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_STORAGE);
            }
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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
        bookAdapter = new BookAdapter (bookModelList,this,language,"local","local");
        bookRecyclerView.setAdapter(bookAdapter);
        bookModelList.clear();
        SharedPreferences bookName = getSharedPreferences("bookNames",MODE_PRIVATE);
        int count = bookName.getInt("current",0);
        for (int i = count;i>=1;i--){
            String nemo = bookName.getString(String.valueOf(i),"");
            if (!nemo.isEmpty()){
                loadFromDevice(nemo);
            }
         }
         swipeToDel();
    }

    private void swipeToDel() {
        final ItemTouchHelper.SimpleCallback swipe = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final BookData bookData = bookModelList.get(position);
                if (direction == ItemTouchHelper.LEFT) {
                    final String nem = bookData.getName();
                    bookModelList.remove(position);
                    bookAdapter.notifyItemRemoved(position);
                    Snackbar.make(getWindow().getDecorView(),nem+" is removed",Snackbar.LENGTH_LONG)
                            .addCallback( new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            switch (event){
                                case DISMISS_EVENT_TIMEOUT:
                                    removeFromSP(position,nem);
                                    break;

                            }
                        }
                    }).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bookModelList.add(position,bookData);
                            bookAdapter.notifyItemInserted(position);
                            bookAdapter.notifyItemRangeChanged(position, bookAdapter.getItemCount());
                        }
                    }).show();
                }
            }
        };
        itemTouchHelper = new ItemTouchHelper(swipe);
        itemTouchHelper.attachToRecyclerView(bookRecyclerView);
    }
    private void removeFromSP(int position, String nem) {
        SharedPreferences storeBookName = getSharedPreferences("bookNames", MODE_PRIVATE);
        SharedPreferences.Editor addBookName = storeBookName.edit();
        int c = storeBookName.getInt("current", 0);
        for (int i = 0; i <= c; i++) {
            String nemo = storeBookName.getString(valueOf(i), "");
            if (nemo.equalsIgnoreCase(nem)){
                addBookName.remove(String.valueOf(i));
                addBookName.apply();
            }
        }
    }

    private void loadFromDevice(String nemo) {
        location="local";
        if (!checkPermissions()) {
            Toast.makeText(getApplicationContext(), "No permission!", Toast.LENGTH_LONG).show();
            return;
        }
        File rootPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "bookInShort");
        if (!rootPath.exists()) {
            Toast.makeText(this, "OPEN A BOOK FIRST", Toast.LENGTH_SHORT).show();
        } else {
            final File localFile = new File(rootPath, nemo + ".jpeg");
            if (localFile.exists()) {
                String auth, describ, rating;
                SharedPreferences namePref = getSharedPreferences(nemo, MODE_PRIVATE);
                auth = namePref.getString("author", "");
                describ = namePref.getString("describ", "");
                rating = namePref.getString("rating", "");
                BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()), nemo, describ, auth, rating);
                bookModelList.add(bookData);
                bookAdapter.notifyDataSetChanged();
            }
        }
    }

    public void loadData(final String genre){
        if(!checkPermissions()) {
            Toast.makeText(getApplicationContext(), "No permission!", Toast.LENGTH_LONG).show();
            drawer.closeDrawers();
            return;
        }
        location="firebase";
        itemTouchHelper.attachToRecyclerView(null);
        bookAdapter = new BookAdapter(bookModelList,this,language,genre,"firebase");
        bookRecyclerView.setAdapter(bookAdapter);
        bookModelList.clear();
        drawer.closeDrawers();
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(genre);
        EngSciFiRef = bookReference.child(language).child(genre);
        EngSciFiImageRef = bookIconReference.child(language).child(genre);
                EngSciFiRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            Toast.makeText(getApplicationContext(), "Empty List", Toast.LENGTH_LONG).show();
                            return;
                        }
                        totalBooksInFb = dataSnapshot.getChildrenCount();
                        EngSciFiRef.removeEventListener(this);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Log.e("mkdkpro",databaseError.getMessage());
                    }

                });
                bookSetAdapter(genre);
                setRecycleScrollListener();
                progressDialog.dismiss();
                onBackPressed();
            }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.welcome_options_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.searchAction);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
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
                SharedPreferences names = getSharedPreferences(name,MODE_PRIVATE);
                SharedPreferences.Editor edit= ms.edit();
                SharedPreferences.Editor editor = names.edit();
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"bookInShort");
                deleteRecursive(file);
                edit.clear();
                editor.clear();
                editor.apply();
                edit.apply();
                return true;
                case R.id.logOut:
                firebaseAuth.signOut();
                if(firebaseAuth.getCurrentUser() == null)
                    Toast.makeText(getApplicationContext(),"Logged out successfully!",Toast.LENGTH_LONG).show();
                else  Toast.makeText(getApplicationContext(),"Log out failed!",Toast.LENGTH_LONG).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
    void deleteRecursive(File file){
        if (file.isDirectory())
            for (File child : file.listFiles()){
                deleteRecursive(child);
            }
            file.delete();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<BookData> newList = new ArrayList<>();
        newText = newText.toLowerCase();
        String loc="local",fb="firebase";
        if(location.equalsIgnoreCase(loc)){
            Log.e("BookInShort","Searching Local");
            RecyclerView.Adapter newAdapter = new BookAdapter(newList,this,language,genre,"local");
            bookRecyclerView.setAdapter(newAdapter);
            for(BookData bookData: bookModelList){
                String name = bookData.getName().toLowerCase();
                if(name.contains(newText)){
                    newList.add(bookData);
                    newAdapter.notifyDataSetChanged();
                }
               else newAdapter.notifyDataSetChanged();
            }
        }
        if (location.equalsIgnoreCase(fb)){
            Log.e("BookInShort","Searching Firebase");
            final RecyclerView.Adapter newAdapter = new BookAdapter(newList,this,language,genre,"firebase");
            bookRecyclerView.setAdapter(newAdapter);
            final String finalNewText = newText;
                EngSciFiRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot books : dataSnapshot.getChildren()){
                            String name = books.getKey().toLowerCase();
                            if (name.contains(finalNewText)){
                                loadSearchedBook(books,newList,newAdapter);
                            }
                            else{
                                newAdapter.notifyDataSetChanged();
                            }
                        }
                        EngSciFiRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        return true;
    }

    private void loadSearchedBook(final DataSnapshot book, final List<BookData> newList, final RecyclerView.Adapter newAdapter) {
        final String n=book.getKey();
        final File temp = new File(Environment.getExternalStorageDirectory(), "bookInShort");
        if (!temp.exists()) {
            temp.mkdir();
        }
        String auth, describ;
        final File localFile = new File(temp, n + ".jpeg");
        if (localFile.exists()) {
            SharedPreferences namePref = getSharedPreferences(book.getKey(), MODE_PRIVATE);
            auth = namePref.getString("author", "");
            describ = namePref.getString("describ", "");
            rating = namePref.getString("rating", "");
            BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()),n, describ, auth, rating);
            newList.add(bookData);
            newAdapter.notifyDataSetChanged();
        } else {
            final HashMap<String, Object> value = (HashMap<String, Object>) book.getValue();
            final StorageReference exactRef = EngSciFiImageRef.child(book.getKey()+ ".jpg");
            final String bookDesString = value.get("Describ").toString();
            final String bookAuthorString = value.get("Author").toString();
            final String rating = value.get("rating").toString();
            exactRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    BookData bookData = new BookData(BitmapFactory.decodeFile(localFile.getAbsolutePath()),n, bookDesString, bookAuthorString, rating);
                    newList.add(bookData);
                    newAdapter.notifyDataSetChanged();
                    SharedPreferences bookNameAddVal = getSharedPreferences(n, MODE_PRIVATE);
                    SharedPreferences.Editor nameAddEdit = bookNameAddVal.edit();
                    nameAddEdit.putString("author", bookAuthorString);
                    nameAddEdit.putString("rating", rating);
                    nameAddEdit.putString("bookName", book.getKey());
                    nameAddEdit.putString("describ", bookDesString);
                    nameAddEdit.putString("lang", language);
                    nameAddEdit.putString("genre", genre);
                    nameAddEdit.apply();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("WelcomeActivity", e.getMessage());
                }
            });
        }
    }
    }























