package it.lorenzotanzi.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity implements GalleryAdapter.OnItemClickListener{

    public static final String EXTRA_URL = "extra_pkurl";
    public static final String EXTRA_SPRITE = "extra_pksprite";
    public static final String EXTRA_PKNAME="extra_pkname";
    public static final String EXTRA_PKCOLOR="extra_pkcolor";
    public static final String EXTRA_PKID="extra_pkid";

    private RecyclerView mRecyclerView;
    private GalleryAdapter mGalleryAdapter;
    private ArrayList<Item> mGalleryList;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Intent intent=getIntent();
        int pkid=intent.getIntExtra("PKID",1);
        String pkname=intent.getStringExtra("PKNAME");
        int pkcolor=intent.getIntExtra("PKCOLOR", Color.WHITE);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setLayoutManager(new GridAutoFitLayoutManager(this, 200));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mGalleryList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);
        parseJSON(pkid,pkname,pkcolor);
    }

    private void parseJSON(final int pkid, final String pkname, final int pkcolor) {

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

                            if(front_default!="null") mGalleryList.add(new Item(front_default, "front default",pkid,pkname,pkcolor));
                            if(front_female!="null") mGalleryList.add(new Item(front_female, "front female",pkid,pkname,pkcolor));
                            if(front_shiny!="null")mGalleryList.add(new Item(front_shiny, "front shiny",pkid,pkname,pkcolor));
                            if(front_shiny_female!="null")mGalleryList.add(new Item(front_shiny_female, "front shiny female",pkid,pkname,pkcolor));
                            if(back_default!="null")mGalleryList.add(new Item(back_default, "back default",pkid,pkname,pkcolor));
                            if(back_female!="null")mGalleryList.add(new Item(back_female, "back female",pkid,pkname,pkcolor));
                            if(back_shiny!="null")mGalleryList.add(new Item(back_shiny, "back shiny",pkid,pkname,pkcolor));
                            if(back_shiny_female!="null")mGalleryList.add(new Item(back_shiny_female, "back shiny female",pkid,pkname,pkcolor));

                            mGalleryAdapter = new GalleryAdapter(GalleryActivity.this, mGalleryList);
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
        Intent detailIntent = new Intent(this, FullActivity.class);
        Item clickedItem = mGalleryList.get(position);

        detailIntent.putExtra(EXTRA_URL, clickedItem.getUrl());
        detailIntent.putExtra(EXTRA_SPRITE, clickedItem.getSprite());
        detailIntent.putExtra(EXTRA_PKNAME,clickedItem.getName());
        detailIntent.putExtra(EXTRA_PKCOLOR,clickedItem.getColor());
        detailIntent.putExtra(EXTRA_PKID,clickedItem.getId());

        startActivity(detailIntent);
    }

}
