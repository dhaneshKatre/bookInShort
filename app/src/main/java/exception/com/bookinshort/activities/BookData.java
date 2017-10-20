package exception.com.bookinshort.activities;

import android.graphics.Bitmap;

public class BookData {

    private Bitmap icon;
    private String name, description, author, noOfPages, rateCount;

    public BookData() {
    }

    public BookData(Bitmap icon, String name, String description, String author, String noOfPages) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.author = author;
        this.noOfPages = noOfPages;
    }

    public void setRateCount(String rateCount) {
        this.rateCount = rateCount;
    }

    public String getNoOfPages() {
        return noOfPages;
    }

    public String getRateCount() {
        return rateCount;
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
