package exception.com.bookinshort.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import exception.com.bookinshort.R;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<BookData> bookModelList;
    private Context context;

    public BookAdapter(List<BookData> bookModelList, Context context) {
        this.bookModelList = bookModelList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookData bookData = bookModelList.get(position);
        holder.bookIcon.setImageBitmap(bookData.getIcon());
        holder.bookName.setText(bookData.getName());
        holder.bookAuthor.setText(bookData.getAuthor());
        holder.bookDescription.setText(bookData.getDescription());
    }

    @Override
    public int getItemCount() {
        return bookModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView bookIcon;
        public TextView bookName, bookAuthor, bookDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            bookIcon = (ImageView)itemView.findViewById(R.id.bookIcon);
            bookName = (TextView)itemView.findViewById(R.id.bookName);
            bookAuthor = (TextView)itemView.findViewById(R.id.bookAuthor);
            bookDescription = (TextView)itemView.findViewById(R.id.bookDescription);
        }
    }

}
