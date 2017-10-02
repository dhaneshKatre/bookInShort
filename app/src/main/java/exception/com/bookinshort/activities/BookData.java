package exception.com.bookinshort.activities;

import android.graphics.Bitmap;

public class BookData {

    private Bitmap icon;
    private String name, description, author;

    public BookData() {
    }

    public BookData(Bitmap icon, String name, String description, String author) {

        this.icon = icon;
        this.name = name;
        this.description = description;
        this.author = author;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(String author) {
        this.author = author;
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
