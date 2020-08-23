package it.lorenzotanzi.pokedex;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pokemon implements Parcelable{



    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ID")
    private Integer pkmnNum;

    // new add
    @ColumnInfo(name = "favorite")
    private Boolean favorite;

    //@NonNull // TEST --> ERRORE SE SI ABILITA IL @NonNull
    @ColumnInfo(name = "Name")
    private String pkmnName;

    //@NonNull //TEST --> ERRORE SE SI ABILITA IL @NonNull
    @ColumnInfo(name = "Type1")
    private String type1;

    //@NonNull // TEST --> ERRORE SE SI ABILITA IL @NonNull
    @ColumnInfo(name = "Type2")
    private String type2;

    // new add
    @ColumnInfo(name = "Img")
    private String img;



    public Pokemon(@NonNull Integer pkmnNum, Boolean favorite, String pkmnName, String type1, String type2, String img) {
        this.pkmnNum = pkmnNum;
        this.favorite = favorite; /* new add */
        this.pkmnName = pkmnName;
        this.type1 = type1;
        this.type2 = type2;
        this.img = img; /* new add */


    }




    protected Pokemon(Parcel in) {
        if (in.readByte() == 0) {
            pkmnNum = null;
        } else {
            pkmnNum = in.readInt();
        }
        byte tmpFavorite = in.readByte();
        favorite = tmpFavorite == 0 ? null : tmpFavorite == 1;
        pkmnName = in.readString();
        type1 = in.readString();
        type2 = in.readString();
        img = in.readString();
    }

    public static final Creator<Pokemon> CREATOR = new Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

    @NonNull
    public Integer getPkmnNum() {
        return pkmnNum;
    }

    public void setPkmnNum(@NonNull Integer pkmnNum) {
        this.pkmnNum = pkmnNum;
    }

    // new add
    @NonNull
    public Boolean getFavorite(){
        return favorite;
    }

    // new add
    public void setFavorites(Boolean choice){
        this.favorite = choice;
    }

    @NonNull // TEST
    public String getPkmnName() {
        return pkmnName;
    }

    public void setPkmnName(String pkmnName) {
        this.pkmnName = pkmnName;
    }

    //@NonNull //TEST
    public String getType1() {
        //return this.type1;
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    //@NonNull // TEST
    public String getType2() {
        //return this.type2;
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    /* new add */
    public String getImg() {
        return img;
    }

    /* new add */
    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (pkmnNum == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pkmnNum);
        }
        dest.writeByte((byte) (favorite == null ? 0 : favorite ? 1 : 2));
        dest.writeString(pkmnName);
        dest.writeString(type1);
        dest.writeString(type2);
        dest.writeString(img);
    }
}