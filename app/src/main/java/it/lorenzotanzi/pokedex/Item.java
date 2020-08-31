package it.lorenzotanzi.pokedex;

public class Item {

    private String mUrl;
    private int mId;
    private String mName;
    private String mSprite;
    private int mColor;

    public Item(String url, String sprite, int id, String name, int color) {
        mUrl = url;
        mId= id;
        mName=name;
        mSprite = sprite;
        mColor=color;
    }

    public String getUrl() {
        return mUrl;
    }
    public int getId() {
        return mId;
    }
    public String getName() {
        return mName;
    }
    public String getSprite() {
        return mSprite;
    }
    public int getColor() {
        return mColor;
    }

}
