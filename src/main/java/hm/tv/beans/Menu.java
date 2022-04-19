package hm.tv.beans;

import java.util.List;

public class Menu {
    String category;
    List<PlayItem> items;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<PlayItem> getItems() {
        return items;
    }

    public void setItems(List<PlayItem> items) {
        this.items = items;
    }
}
