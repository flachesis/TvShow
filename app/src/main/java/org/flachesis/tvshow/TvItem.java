package org.flachesis.tvshow;

public class TvItem {
    public String name;
    public String id;

    public TvItem(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
