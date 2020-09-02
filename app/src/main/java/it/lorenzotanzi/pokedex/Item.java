package it.lorenzotanzi.pokedex;

public class Item {
    // elemento della galleria
    private String mUrl; // stringa url dell'immagine
    private String mId; // id del Pokemon
    private String mName; // nome pokemon
    private String mSprite; // tipo di sprite
    private String mType1; // tipo 1
    private String mType2; // tipo 2

    public Item(String url, String sprite, String id, String name, String type1, String type2) {
        mUrl = url;
        mId= id;
        mName=name;
        mSprite = sprite;
        mType1=type1;
        mType2=type2;
    }

    public String getUrl() {
        return mUrl;
    }
    public void setUrl(String url) { this.mUrl=url; }
    public String getId() {
        return mId;
    }
    public void setId(String id) { this.mId=id; }
    public String getName() {
        return mName;
    }
    public void setName(String name) { this.mName=name; }
    public String getSprite() {
        return mSprite;
    }
    public void setSprite(String sprite) { this.mSprite=sprite; }
    public String getType1() {
        return mType1;
    }
    public void setType1(String type1) { this.mType1=type1; }
    public String getType2() {
        return mType2;
    }
    public void setType2(String type2) { this.mType2=type2; }

}
