package it.lorenzotanzi.pokedex;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
public class Details extends AppCompatActivity {
    private AsyncTaskDetails task;
    private Pokemon pokemon;
    private Map<String, String> colors = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        colors.put("Normal", "#A8A77A");
        colors.put("Fire", "#EE8130");
        colors.put("Water","#6390F0");
        colors.put("Electric", "#F7D02C");
        colors.put("Grass", "#7AC74C");
        colors.put("Ice", "#96D9D6");
        colors.put("Fighting", "#C22E28");
        colors.put("Poison", "#A33EA1");
        colors.put("Ground", "#E2BF65");
        colors.put("Flying", "#A98FF3");
        colors.put("Psychic", "#F95587");
        colors.put("Bug", "#A6B91A");
        colors.put("Rock", "#B6A136");
        colors.put("Ghost", "#735797");
        colors.put("Dragon", "#6F35FC");
        colors.put("Dark", "#705746");
        colors.put("Steel", "#B7B7CE");
        colors.put("Fairy", "#D685AD");



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Bundle bundle = getIntent().getExtras();

        String name = bundle.getString("name");
        String id = bundle.getString("ID");
        String codice = id;
        String codice_n = codice;
        String type1 = bundle.getString("Type1");
        String type2 = bundle.getString("Type2");
        String img = bundle.getString("Img");


        TextView textView = findViewById(R.id.tvName);
        TextView textView1 = findViewById(R.id.tvId);
        ImageView imageView1 = findViewById(R.id.ivType1);
        ImageView imageView2 = findViewById(R.id.ivType2);
        ImageView imageView = findViewById(R.id.ivPokeball);
        ConstraintLayout cl = findViewById(R.id.clGeneral);

        //STRING ADJUSTMENTS
        if (Integer.parseInt(id) < 10) id = new StringBuilder().append("#00").append(id).toString();
        else if (Integer.parseInt(id) < 100)
            id = new StringBuilder().append("#0").append(id).toString();
        else id = new StringBuilder().append("#").append(id).toString();
        textView.setText(id);

        String type1col = colors.get(type1);
        //SETTING IMAGE VIEW OF TYPE 1
        String _type1str = type1.substring(0,1).toLowerCase() + type1.substring(1);
        int index = this.getResources().getIdentifier(_type1str , "drawable", this.getPackageName());
        imageView1.setImageResource(index);



        //SE IL POKEMON IN POSIZIONE position HA UN TIPO 2 ALLORA VIENE SETTATO IL TESTO DELLA IMAGE VIEW
        //imageView2 E VIENE CREATA UNA NUOVA ISTANZA DI GradientDrawable CHE VERRA' USATA COME
        //BACKGROUND DELLA DETAILS

        if (type2 != null) {
            String type2col = colors.get(type2);
            imageView2.setVisibility(View.VISIBLE);

            String _type2str = type2.substring(0,1).toLowerCase() + type2.substring(1);
            int id2 = this.getResources().getIdentifier(_type2str , "drawable", this.getPackageName());
            int [] gradientColors = {Color.parseColor((type1col)), Color.parseColor(type2col)};
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
            gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gd.setCornerRadius(30);
            cl.setBackground(gd);
            imageView2.setImageResource(id2);
            //Colore del notch
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setBackgroundDrawable(gd);
        } else {
            /*Se invece il Pokemon ha un solo tipo viene settata la ImageView imageView2 come "invisible"
             * e viene settato la sfondo della card view al colore  dell'unico tipo relativo al Pokemon*/
            imageView2.setVisibility(View.INVISIBLE);
            int backgroundColor = Color.parseColor(type1col);
            cl.setBackgroundColor(backgroundColor);
            //Colore del notch
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(backgroundColor);
        }


        Glide.with(this).load(img).into(imageView);
        //Aggiustamento della stringa nome
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        textView1.setText(name);


        //Chiama il thread che si occupa di trovare le informazioni del pokémon
        task = new AsyncTaskDetails(this, codice_n);
        task.execute();

    }
}




