package exception.com.bookinshort.activities;

public class bookTab {
    private String tab,fromLocation,replacedTab;

    public bookTab() {
    }

    public bookTab(String tab,String fromLocation) {
        this.fromLocation=fromLocation;
        this.tab = tab;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getReplacedTab() {
        return replacedTab;
    }

    public void setReplacedTab(String replacedTab) {
        this.replacedTab = replacedTab;
    }
}
