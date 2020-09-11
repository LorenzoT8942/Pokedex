package it.lorenzotanzi.pokedex;

public class Item {
    // elemento della galleria
    private String mUrl; // stringa url dell'immagine
    private String mSprite; // tipo di sprite

    public Item(String url, String sprite) {
        mUrl = url;
        mSprite = sprite;
    }

    public String getUrl() {
        return mUrl;
    }
    public void setUrl(String url) { this.mUrl=url; }
    public String getSprite() {
        return mSprite;
    }
    public void setSprite(String sprite) { this.mSprite=sprite; }

}
