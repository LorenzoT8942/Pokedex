package it.lorenzotanzi.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
// questa activity mostra le sprites del Pokemon selezionato in un formato a griglia
public class GalleryActivity extends AppCompatActivity implements GalleryAdapter.OnItemClickListener{

    // stringhe usate per lanciare la FullActivity
    public static final String EXTRA_URL = "extra_pkurl";
    public static final String EXTRA_PKTYPE1="extra_pktype1";
    public static final String EXTRA_PKTYPE2="extra_pktype2";

    // variabi usate per estrarre i dati dall'intent che ha lanciato la GalleryActivity
    public String galname;
    public String galid;
    public String galtype1;
    public String galtype2;

    private RecyclerView mRecyclerView;
    private GalleryAdapter mGalleryAdapter;
    private ArrayList<Item> mGalleryList;
    private RequestQueue mRequestQueue;

    private Map<String, String> colors = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // mappiamo i tipi di pokemon a colori per il colore di background dell'activity
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
        setContentView(R.layout.activity_gallery);

        // creo un bundle e lo uso per estrarre i dati dall'intent che ha lanciato l'activity
        Bundle bundle = getIntent().getExtras();
        String pkname = bundle.getString("PKNAME");
        String pkid = bundle.getString("PKID");
        String type1 = bundle.getString("PKTYPE1");
        String type2 = bundle.getString("PKTYPE2");
        galname=pkname;
        galid=pkid;
        galtype1=type1;
        galtype2=type2;

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setHasFixedSize(true);

        LinearLayout layout = findViewById(R.id.galLayout);
        // in base ai due tipi determino il colore di background del LinearLayout
        String type1col = colors.get(type1);
        // se e due tipi allora creo gradiente
        if (type2 != null) {
            String type2col = colors.get(type2);
            int [] gradientColors = {Color.parseColor((type1col)), Color.parseColor(type2col)};
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
            gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            layout.setBackground(gd);
        } else { // un tipo
            int backgroundColor = Color.parseColor(type1col);
            layout.setBackgroundColor(backgroundColor);
        }

        TextView tvgal = findViewById(R.id.tvgal);
        // Aggiustamento della stringa id
        String id;
        if (Integer.parseInt(pkid) < 10) id = new StringBuilder().append("#00").append(pkid).toString();
        else if (Integer.parseInt(pkid) < 100)
            id = new StringBuilder().append("#0").append(pkid).toString();
        else id = new StringBuilder().append("#").append(pkid).toString();
        // Aggiustamento della stringa name
        String name = pkname;
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        tvgal.setText(id+" "+name);

        // creo lista di elementi della galleria
        mGalleryList = new ArrayList<>();
        // creo richiesta volley
        mRequestQueue = Volley.newRequestQueue(this);
        // sdoppiato per evitare l'errore no Adapter
        mGalleryAdapter = new GalleryAdapter(GalleryActivity.this, mGalleryList);
        mRecyclerView.setAdapter(mGalleryAdapter);

        parseJSON(pkid);
    }
    // metodo per estrarre gli url delle sprites dal JSON associato al pokemon selezionato tramite id
    private void parseJSON(final String pkid) {

        String url = "https://pokeapi.co/api/v2/pokemon/"+pkid;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // la lista delle sprites è un JSONObject
                            JSONObject jsonObject = response.getJSONObject("sprites");
                            // estrago dal JSON le stringhe url associate alle sprites
                            String front_default = jsonObject.getString("front_default");
                            String front_female = jsonObject.getString("front_female");
                            String front_shiny= jsonObject.getString("front_shiny");
                            String front_shiny_female= jsonObject.getString("front_shiny_female");
                            String back_default = jsonObject.getString("back_default");
                            String back_female= jsonObject.getString("back_female");
                            String back_shiny= jsonObject.getString("back_shiny");
                            String back_shiny_female= jsonObject.getString("back_shiny_female");
                            // popolo la lista di elementi della galleria se l'url estratto non è null
                            if(front_default!="null") mGalleryList.add(new Item(front_default, getResources().getString(R.string.front_default)));
                            if(front_female!="null") mGalleryList.add(new Item(front_female, getResources().getString(R.string.front_female)));
                            if(front_shiny!="null")mGalleryList.add(new Item(front_shiny, getResources().getString(R.string.front_shiny)));
                            if(front_shiny_female!="null")mGalleryList.add(new Item(front_shiny_female, getResources().getString(R.string.front_shiny_female)));
                            if(back_default!="null")mGalleryList.add(new Item(back_default, getResources().getString(R.string.back_default)));
                            if(back_female!="null")mGalleryList.add(new Item(back_female, getResources().getString(R.string.back_female)));
                            if(back_shiny!="null")mGalleryList.add(new Item(back_shiny, getResources().getString(R.string.back_shiny)));
                            if(back_shiny_female!="null")mGalleryList.add(new Item(back_shiny_female, getResources().getString(R.string.back_shiny_female)));
                            // setto adapter per la recycler view
                            mRecyclerView.setAdapter(mGalleryAdapter);
                            mGalleryAdapter.setOnItemClickListener(GalleryActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }
    // se l'utente preme su un elemento della galleria, viene lanciata la FullActivity
    // creo intent e bundle e lo riempio dei dati da passare alla FullActivity
    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, FullActivity.class);
        Item clickedItem = mGalleryList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, clickedItem.getUrl());
        bundle.putString(EXTRA_PKTYPE1,galtype1);
        bundle.putString(EXTRA_PKTYPE2,galtype2);
        detailIntent.putExtras(bundle);
        startActivity(detailIntent);
    }

}
