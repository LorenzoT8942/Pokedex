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
import android.widget.Button;
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
public class Details extends AppCompatActivity implements View.OnClickListener{
    private AsyncTaskDetails task;
    private Pokemon pokemon;
    private Map<String, String> colors = new HashMap<>();

    // necessarie come parametri per openGallery
    private String pkid = new String();
    private String pkname = new String();
    private String pktype1 = new String();
    private String pktype2 = new String();

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

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_bottom); /* NEW ADD */

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

        // assegna parametri openGallery
        pkid = bundle.getString("ID");
        pkname = bundle.getString("name");
        pktype1 = bundle.getString("Type1");
        pktype2 = bundle.getString("Type2");
        Button btnGallery=findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(this);


        //Aggiustamento della stringa id
        if (Integer.parseInt(id) < 10) id = new StringBuilder().append("#00").append(id).toString();
        else if (Integer.parseInt(id) < 100)
            id = new StringBuilder().append("#0").append(id).toString();
        else id = new StringBuilder().append("#").append(id).toString();
        textView.setText(id);

        String type1col = colors.get(type1);
        //imageView settata al tipo1
        String _type1str = type1.substring(0,1).toLowerCase() + type1.substring(1);
        int index = this.getResources().getIdentifier(_type1str , "drawable", this.getPackageName());
        imageView1.setImageResource(index);



        //se il pokemonn in posizione position ha un tipo2 allora viene settato il testo della imageView
        //imageView2 e viene creata una nuova istanza di GradientDrawable che verrà usata come
        //background della Details

        if (type2 != null) {
            String type2col = colors.get(type2);
            imageView2.setVisibility(View.VISIBLE);

            String _type2str = type2.substring(0,1).toLowerCase() + type2.substring(1);
            int id2 = this.getResources().getIdentifier(_type2str , "drawable", this.getPackageName());
            int [] gradientColors = {Color.parseColor((type1col)), Color.parseColor(type2col)};
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
            //Colore del notch
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setBackgroundDrawable(gd);
            gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gd.setCornerRadius(30);
            cl.setBackground(gd);
            imageView2.setImageResource(id2);
        } else {
            /*Se invece il Pokemon ha un solo tipo viene settata la ImageView imageView2 come "invisible"
            e viene settato lo sfondo della card view al colore  dell'unico tipo relativo al Pokemon*/
            imageView2.setVisibility(View.INVISIBLE);
            int backgroundColor = Color.parseColor(type1col);
            cl.setBackgroundColor(backgroundColor);
            //Colore del notch
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        Glide.with(this).load(img).into(imageView);
        //Aggiustamento della stringa nome
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        textView1.setText(name);


        //Chiama il thread che si occupa di trovare le informazioni del pokémon
        task = new AsyncTaskDetails(this, codice_n);
        task.execute();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_bottom);
    }

    // onClick per l'icona Pokemon e disabilita doppio click
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnGallery){
            v.setEnabled(false);
            openGallery();
        }
    }

    // apre galleria con intent
    public void openGallery() {
        Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
        Bundle pkbundle = new Bundle();
        pkbundle.putString("PKID",String.valueOf(pkid));
        pkbundle.putString("PKNAME",pkname);
        pkbundle.putString("PKTYPE1",pktype1);
        pkbundle.putString("PKTYPE2",pktype2);
        intent.putExtras(pkbundle);
        startActivity(intent);
    }

    // riabilita click sull'icona Pokemon al ritorno dalla galleria
    @Override
    protected void onResume() {
        super.onResume();
        Button btnGallery = findViewById(R.id.btnGallery);
        btnGallery.setEnabled(true);
    }
}




