package exception.com.bookinshort.activities;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        View view = layoutInflater.inflate(R.layout.blank_tab_textview,container,false);
        TextView textView = (TextView)view.findViewById(R.id.mainContent);
        textView.setMovementMethod(new ScrollingMovementMethod());
        bookTab bt = bookList.get(position);
        textView.setText(bt.getTab());
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
