package exception.com.bookinshort.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import exception.com.bookinshort.R;

public class tabPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<bookTab> bookList;


    public tabPagerAdapter(Context context,List<bookTab> bookList) {
        this.context=context;
        this.bookList=bookList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.scroll_view_inside_viewpager,container,false);
        TextView textView = (TextView)view.findViewById(R.id.mainContent);
        textView.setMovementMethod(new ScrollingMovementMethod());
        bookTab bt = bookList.get(position);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        CharSequence cs = Html.fromHtml(bt.getTab(), new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                InputStream is = null;
                try {
                    is = (InputStream) new URL(source).getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Drawable bmp = new BitmapDrawable (context.getResources(), bitmap);

                //Drawable bmp = Drawable.createFromPath(source);
                    bmp.setBounds(0, 0, bmp.getIntrinsicWidth(),bmp.getIntrinsicHeight());//bmp.getIntrinsicWidth(), bmp.getIntrinsicHeight()
                    return bmp;

            }
        }, null);
        textView.setText(cs);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

}
