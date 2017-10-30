package exception.com.bookinshort.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import exception.com.bookinshort.R;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<BookData> bookModelList;
    private Context context;
    private String lang,genre;
    private View view;

    public BookAdapter(List<BookData> bookModelList, Context context, String lang, String genre) {
        this.bookModelList = bookModelList;
        this.context = context;
        this.lang=lang ;
        this.genre=genre;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

         view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BookData bookData = bookModelList.get(position);
        if(!(bookData.getName() ==null)) {
            final String name = bookData.getName();
            holder.bookIcon.setImageBitmap(bookData.getIcon());
            holder.bookName.setText(name);
            holder.bookAuthor.setText(bookData.getAuthor());
            holder.bookDescription.setText(bookData.getDescription());
            holder.rateCount.setText(bookData.getRating());
            holder.rateBook.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
                    final AlertDialog dial;
                    View mView = View.inflate(v.getContext(), R.layout.alert_dial_rate_book, null);
                    TextView tvName = (TextView) mView.findViewById(R.id.rateBookName);
                    tvName.setText("Please rate '" + name + "' Thank You!");
                    final RatingBar ratingBar = (RatingBar)mView.findViewById(R.id.rateBar);
                    ratingBar.setNumStars(5);
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            Toast.makeText(context,"Your selected rating: " + rating,Toast.LENGTH_SHORT).show();
                        }
                    });
                    SharedPreferences sharedPreferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    final String bookGenre =sharedPreferences.getString("genre","");
                    final String bookLang=sharedPreferences.getString("lang","");
                    final Button rateButton = (Button)mView.findViewById(R.id.rateButton);
                    mBuilder.setView(mView);
                    dial = mBuilder.create();
                    dial.show();
                    rateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String rating = String.valueOf(ratingBar.getRating());
                            FirebaseDatabase.getInstance().getReference("Books").child(bookLang).child(bookGenre).child(name).child("rating").setValue(rating);
                            editor.putString("rating",rating);
                            editor.apply();
                            holder.rateCount.setText(rating);
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



    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView bookIcon;
        public ImageButton rateBook;
        public TextView bookName, bookAuthor, bookDescription, rateBookName, rateCount;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layoutBookRecycle);
            rateBook = (ImageButton) itemView.findViewById(R.id.rateBook);
            bookIcon = (ImageView) itemView.findViewById(R.id.bookIcon);
            bookName = (TextView) itemView.findViewById(R.id.bookName);
            bookAuthor = (TextView) itemView.findViewById(R.id.bookAuthor);
            bookDescription = (TextView) itemView.findViewById(R.id.bookDescription);
            rateBookName = (TextView) itemView.findViewById(R.id.rateBookName);
            rateCount = (TextView) itemView.findViewById(R.id.rateCount);
        }
    }
}
