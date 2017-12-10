package exception.com.bookinshort.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import exception.com.bookinshort.R;

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BookData> bookModelList;
    private Context context;
    private String lang,genre,fromLocation;
    private int VIEW_TYPE=0;

    BookAdapter(List<BookData> bookModelList, Context context, String lang, String genre, String fromLocation) {
        this.bookModelList = bookModelList;
        this.context = context;
        this.lang=lang ;
        this.genre=genre;
        this.fromLocation=fromLocation;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        else if (viewType==2){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_book_found, parent, false);
            return new EmptyViewHolder(view);

        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case 0:
                final BookData bookData = bookModelList.get(position);
                final UserViewHolder userViewHolder = (UserViewHolder) holder;
                if(!(bookData.getName() ==null)) {
                    final String name = bookData.getName();
                    userViewHolder.bookIcon.setImageBitmap(bookData.getIcon());
                    userViewHolder.bookName.setText(name);
                    userViewHolder.bookAuthor.setText(bookData.getAuthor());
                    userViewHolder.bookDescription.setText(bookData.getDescription());
                    userViewHolder.rateCount.setText(bookData.getRating());
                    userViewHolder.rateBook.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
                            final AlertDialog dial;
                            View mView = View.inflate(v.getContext(), R.layout.alert_dial_rate_book, null);
                            TextView tvName = (TextView) mView.findViewById(R.id.rateBookName);
                            tvName.setText("Please rate '" + name + "' Thank You!");
                            final RatingBar ratingBar = (RatingBar) mView.findViewById(R.id.rateBar);
                            ratingBar.setNumStars(5);
                            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                    Toast.makeText(context, "Your selected rating: " + rating, Toast.LENGTH_SHORT).show();
                                }
                            });
                            SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                            final String bookGenre = sharedPreferences.getString("genre", "");
                            final String bookLang = sharedPreferences.getString("lang", "");
                            final Button rateButton = (Button) mView.findViewById(R.id.rateButton);
                            mBuilder.setView(mView);
                            dial = mBuilder.create();
                            dial.show();
                            rateButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String rating = String.valueOf(ratingBar.getRating());
                                    int currentRating = Integer.parseInt(bookData.getRating());
                                    String avgRating = String.valueOf((Integer.parseInt(rating)+currentRating)/2);
                                    FirebaseDatabase.getInstance().getReference("Books").child(bookLang).child(bookGenre).child(name).child("rating").setValue(avgRating);
                                    editor.putString("rating", rating);
                                    editor.apply();
                                    userViewHolder.rateCount.setText(rating);
                                    dial.dismiss();
                                }
                            });
                        }

                    });
                    userViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, TabBookOpenActivity.class);
                            intent.putExtra("lang", lang);
                            intent.putExtra("genre", genre);
                            intent.putExtra("fromLocation", fromLocation);
                            intent.putExtra("name", bookData.getName());
                            context.startActivity(intent);
                        }
                    });
                }
                break;
            case 1:
                Log.e("BookInShort","Progress bar");
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
                break;
            case 2:
                Log.e("BookInShort","Empty Holder Text View");
                break;
        }
        }


    @Override
    public int getItemCount() {
        return bookModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
       BookData bd = bookModelList.get(position);
        String a="qaz";
        if (bookModelList.isEmpty()||bookModelList == null) return VIEW_TYPE=2;
        else if(bd.getName().equalsIgnoreCase(a)){
            return VIEW_TYPE=1;
        }
        else return VIEW_TYPE=0;
    }

    //"Progress Bar" Viewholder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    //"No Book Found" Viewholder
    private class EmptyViewHolder extends RecyclerView.ViewHolder{
        private TextView noBookTv;
        private  EmptyViewHolder(View view){
            super(view);
            noBookTv=(TextView)view.findViewById(R.id.noBookFoundTV);
        }
    }

    // "Normal item" ViewHolder
    private class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView bookIcon;
        ImageButton rateBook;
        TextView bookName, bookAuthor, bookDescription, rateBookName, rateCount;
        RelativeLayout relativeLayout;
        UserViewHolder(View view) {
            super(view);
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
