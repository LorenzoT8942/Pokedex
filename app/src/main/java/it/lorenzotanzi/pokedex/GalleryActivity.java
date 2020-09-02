package it.lorenzotanzi.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GalleryActivity extends AppCompatActivity implements GalleryAdapter.OnItemClickListener{

    public static final String EXTRA_URL = "extra_pkurl";
    public static final String EXTRA_SPRITE = "extra_pksprite";
    public static final String EXTRA_PKNAME="extra_pkname";
    public static final String EXTRA_PKTYPE1="extra_pktype1";
    public static final String EXTRA_PKTYPE2="extra_pktype2";
    public static final String EXTRA_PKID="extra_pkid";

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
        mRecyclerView.setLayoutManager(new GridAutoFitLayoutManager(this, 200));
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        LinearLayout layout = findViewById(R.id.gallayout);

        String type1col = colors.get(type1);
        if (type2 != null) {
            String type2col = colors.get(type2);
            int [] gradientColors = {Color.parseColor((type1col)), Color.parseColor(type2col)};
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
            gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            //gd.setCornerRadius(30);
            layout.setBackground(gd);
        } else {
            int backgroundColor = Color.parseColor(type1col);
            layout.setBackgroundColor(backgroundColor);
        }

        TextView tvgal = findViewById(R.id.tvgal);

        //Aggiustamento della stringa id
        String id;
        if (Integer.parseInt(pkid) < 10) id = new StringBuilder().append("#00").append(pkid).toString();
        else if (Integer.parseInt(pkid) < 100)
            id = new StringBuilder().append("#0").append(pkid).toString();
        else id = new StringBuilder().append("#").append(pkid).toString();
        String name = pkname;
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        tvgal.setText(id+" "+name);

        mGalleryList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);

        mGalleryAdapter = new GalleryAdapter(GalleryActivity.this, mGalleryList);
        mRecyclerView.setAdapter(mGalleryAdapter);
        //mGalleryAdapter.setOnItemClickListener(GalleryActivity.this);

        parseJSON(pkid,pkname,type1,type2);
    }

    private void parseJSON(final String pkid, final String pkname, final String type1, final String type2) {

        //int id=Integer.valueOf(pkid);

        String url = "https://pokeapi.co/api/v2/pokemon/"+pkid;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jsonObject = response.getJSONObject("sprites");
                            String front_default = jsonObject.getString("front_default");
                            String front_female = jsonObject.getString("front_female");
                            String front_shiny= jsonObject.getString("front_shiny");
                            String front_shiny_female= jsonObject.getString("front_shiny_female");
                            String back_default = jsonObject.getString("back_default");
                            String back_female= jsonObject.getString("back_female");
                            String back_shiny= jsonObject.getString("back_shiny");
                            String back_shiny_female= jsonObject.getString("back_shiny_female");

                            if(front_default!="null") mGalleryList.add(new Item(front_default, "Front Default",pkid,pkname,type1,type2));
                            if(front_female!="null") mGalleryList.add(new Item(front_female, "Front Female",pkid,pkname,type1,type2));
                            if(front_shiny!="null")mGalleryList.add(new Item(front_shiny, "Front Shiny",pkid,pkname,type1,type2));
                            if(front_shiny_female!="null")mGalleryList.add(new Item(front_shiny_female, "Front Shiny Female",pkid,pkname,type1,type2));
                            if(back_default!="null")mGalleryList.add(new Item(back_default, "Back Default",pkid,pkname,type1,type2));
                            if(back_female!="null")mGalleryList.add(new Item(back_female, "Back Female",pkid,pkname,type1,type2));
                            if(back_shiny!="null")mGalleryList.add(new Item(back_shiny, "Back Shiny",pkid,pkname,type1,type2));
                            if(back_shiny_female!="null")mGalleryList.add(new Item(back_shiny_female, "Back Shiny Female",pkid,pkname,type1,type2));

                            //mGalleryAdapter = new GalleryAdapter(GalleryActivity.this, mGalleryList);
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

    @Override
    public void onItemClick(int position) {

        //ShowDialogBox(position);

        Intent detailIntent = new Intent(this, FullActivity.class);
        Item clickedItem = mGalleryList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, clickedItem.getUrl());
        bundle.putString(EXTRA_SPRITE, clickedItem.getSprite());
        bundle.putString(EXTRA_PKNAME,clickedItem.getName());
        bundle.putString(EXTRA_PKID,clickedItem.getId());
        bundle.putString(EXTRA_PKTYPE1,clickedItem.getType1());
        bundle.putString(EXTRA_PKTYPE2,clickedItem.getType2());
        detailIntent.putExtras(bundle);
        startActivity(detailIntent);
    }

    /*public void ShowDialogBox(int position){
        Item clickedItem = mGalleryList.get(position);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_full);
        ImageView image = dialog.findViewById(R.id.image_view);
        Picasso.get().load(clickedItem.getUrl()).fit().centerInside().into(image);
        dialog.show();
    }*/

}
