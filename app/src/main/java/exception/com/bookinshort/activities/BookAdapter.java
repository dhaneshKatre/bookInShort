package exception.com.bookinshort.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import exception.com.bookinshort.R;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<BookData> bookModelList;
    private Context context;
    private String lang,genre;

    public BookAdapter(List<BookData> bookModelList, Context context, String lang, String genre) {
        this.bookModelList = bookModelList;
        this.context = context;
        this.lang=lang ;
        this.genre=genre;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BookData bookData = bookModelList.get(position);
        if(!(bookData.getName() ==null)) {
            holder.bookIcon.setImageBitmap(bookData.getIcon());
            holder.bookName.setText(bookData.getName());
            holder.bookAuthor.setText(bookData.getAuthor());
            holder.bookDescription.setText(bookData.getDescription());
            holder.rateBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
                    View mView = View.inflate(v.getContext(), R.layout.alert_dial_rate_book, null);
                    TextView tvName = (TextView) mView.findViewById(R.id.rateBookName);
                    tvName.setText("Please rate '" + bookData.getName() + "' Thank You!");
                    mBuilder.setView(mView);
                    AlertDialog dial = mBuilder.create();
                    dial.show();
                }


            });
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context,TabBookOpenActivity.class);
                intent.putExtra("lang",lang);
                intent.putExtra("genre",genre);
                intent.putExtra("name",bookData.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookModelList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView bookIcon,rateBook;
        public TextView bookName, bookAuthor, bookDescription, rateBookName;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.layoutBookRecycle) ;
            rateBook = (ImageView) itemView.findViewById(R.id.rateBook);
            bookIcon = (ImageView)itemView.findViewById(R.id.bookIcon);
            bookName = (TextView)itemView.findViewById(R.id.bookName);
            bookAuthor = (TextView)itemView.findViewById(R.id.bookAuthor);
            bookDescription = (TextView)itemView.findViewById(R.id.bookDescription);
            rateBookName = (TextView)itemView.findViewById(R.id.rateBookName);

        }

    }

}
