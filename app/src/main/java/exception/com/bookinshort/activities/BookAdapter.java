package exception.com.bookinshort.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
            holder.noOfPages.setText(bookData.getNoOfPages());
            holder.rateBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
                    final AlertDialog dial;
                    View mView = View.inflate(v.getContext(), R.layout.alert_dial_rate_book, null);
                    TextView tvName = (TextView) mView.findViewById(R.id.rateBookName);
                    tvName.setText("Please rate '" + bookData.getName() + "' Thank You!");
                    final RatingBar ratingBar = (RatingBar)mView.findViewById(R.id.rateBar);
                    ratingBar.setNumStars(5);
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            Toast.makeText(context,"Your selected rating: " + rating,Toast.LENGTH_SHORT).show();
                        }
                    });
                    final Button rateButton = (Button)mView.findViewById(R.id.rateButton);
                    mBuilder.setView(mView);
                    dial = mBuilder.create();
                    dial.show();
                    rateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            float rating = ratingBar.getRating();
                            Toast.makeText(context,"Your final rating: " + rating,Toast.LENGTH_SHORT).show();
                            bookData.setRateCount(""+rating);
                            holder.rateCount.setText(bookData.getRateCount());
                            dial.dismiss();
                        }
                    });
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
        public ImageView bookIcon;
        public ImageButton rateBook;
        public TextView bookName, bookAuthor, bookDescription, rateBookName, noOfPages, rateCount;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.layoutBookRecycle) ;
            rateBook = (ImageButton)itemView.findViewById(R.id.rateBook);
            bookIcon = (ImageView)itemView.findViewById(R.id.bookIcon);
            bookName = (TextView)itemView.findViewById(R.id.bookName);
            bookAuthor = (TextView)itemView.findViewById(R.id.bookAuthor);
            bookDescription = (TextView)itemView.findViewById(R.id.bookDescription);
            rateBookName = (TextView)itemView.findViewById(R.id.rateBookName);
            noOfPages = (TextView)itemView.findViewById(R.id.noOfPages);
            rateCount = (TextView)itemView.findViewById(R.id.rateCount);
        }
    }
}
