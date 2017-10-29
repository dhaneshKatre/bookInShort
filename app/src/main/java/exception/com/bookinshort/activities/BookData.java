package exception.com.bookinshort.activities;

import android.graphics.Bitmap;

public class BookData {

    private Bitmap icon;
    private String name, description, author,rating,lang,genre;

    public BookData() {
    }

    public BookData(Bitmap icon, String name, String description, String author,String rating) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.author = author;
        this.rating = rating;

    }
    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Bitmap getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }


}
