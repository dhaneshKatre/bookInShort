package exception.com.bookinshort.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private int visibleThreshold = 10;
    private boolean isLoading;
    private RecyclerView recyclerView;
    private OnLoadMoreListener onLoadMoreListener;
    private int totalItemCount,lastVisibleItem;

    public BookAdapter(RecyclerView recyclerView, List<BookData> bookModelList, Context context, String lang, String genre, String fromLocation) {
        this.bookModelList = bookModelList;
        this.context = context;
        this.recyclerView=recyclerView;
        this.lang=lang ;
        this.genre=genre;
        this.fromLocation=fromLocation;


    }

    void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener= onLoadMoreListener;
    }

    public int getLastVisibleItem() {
        return lastVisibleItem;
    }

    public void setLastVisibleItem(int lastVisibleItem) {
        this.lastVisibleItem = lastVisibleItem;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        /*View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item, parent, false);
        return new ViewHolder(view);*/
        if (viewType == VIEW_TYPE_ITEM) {View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder ){
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
                                FirebaseDatabase.getInstance().getReference("Books").child(bookLang).child(bookGenre).child(name).child("rating").setValue(rating);
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
        }
        else if (holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
        setLastVisibleItem(position);
        }


    @Override
    public int getItemCount() {
        return bookModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return bookModelList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    //"Progress Bar" Viewholder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    // "Normal item" ViewHolder
    private class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView bookIcon;
        public ImageButton rateBook;
        public TextView bookName, bookAuthor, bookDescription, rateBookName, rateCount;
        public RelativeLayout relativeLayout;

        public UserViewHolder(View view) {
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


/*

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
    }*/
}
