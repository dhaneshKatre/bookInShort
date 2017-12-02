package exception.com.bookinshort.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import exception.com.bookinshort.R;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.String.valueOf;

public class tabPagerAdapter extends PagerAdapter {
    private Context context;
    private List<bookTab> bookList;
    private String name;
    private String src;
    private String path;


    public tabPagerAdapter(Context context, List<bookTab> bookList, String name) {
        this.context = context;
        this.bookList = bookList;
        this.name = name;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.scroll_view_inside_viewpager, container, false);
        final TextView textView = (TextView) view.findViewById(R.id.mainContent);
        textView.setMovementMethod(new ScrollingMovementMethod());
        final bookTab bt = bookList.get(position);
        String loc = bt.getFromLocation();
        File file = new File(Environment.getExternalStorageDirectory() + "/bookInShort/content", name);
        if (!file.exists()) {
            file.mkdirs();
        }
        final File f1 = new File(file, position+".jpeg");
        if (f1.exists()) displayTV(bt.getTab(), position, textView);
        if (loc.equalsIgnoreCase("local")) {
            displayTV(bt.getTab(), position, textView);
        } else {
                CharSequence sequence = Html.fromHtml(bt.getTab(), new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {

                        path=f1.getAbsolutePath();
                        SourceAsync sourceAsync = new SourceAsync();
                        sourceAsync.execute(source);
                        src=source;
                        Bitmap bitmap = null;
                        try {
                            bitmap=sourceAsync.get();
                        } catch (InterruptedException e) {
                            Log.e("tabPagerAdapter",e.getMessage());
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            Log.e("tabPagerAdapter",e.getMessage());
                            e.printStackTrace();
                        }
                        Drawable bmp = new BitmapDrawable(context.getResources(), bitmap);
                        bmp.setBounds(0, 0, bmp.getIntrinsicWidth(), bmp.getIntrinsicHeight());
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(f1);
                        } catch (FileNotFoundException e) {
                            Log.e("tabPagerAdapter",e.getMessage());
                            e.printStackTrace();
                        }
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
                        try {
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return bmp;
                    }
                }, null);
            String replace;
            if (!(src==null||src.isEmpty())) replace = bt.getTab().replace(src,path);
                else replace =bt.getTab();
            bt.setTab(replace);
            textView.setText(sequence);
        }
        container.addView(view);
        return view;
    }

    private void displayTV(final String tab, final int position, TextView textView) {
        CharSequence cs = Html.fromHtml(tab, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Log.e("mjk", tab);
                Bitmap bitmap = BitmapFactory.decodeFile(source);
                Drawable bmp = new BitmapDrawable(context.getResources(), bitmap);
                bmp.setBounds(0, 0, bmp.getIntrinsicWidth(), bmp.getIntrinsicHeight());
                return bmp;
            }
        }, null);
        textView.setText(cs);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public class SourceAsync extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            InputStream is = null;
            try {
                is = (InputStream) new URL(strings[0]).getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        }
    }
}