package it.lorenzotanzi.pokedex;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AsyncTaskDetails extends AsyncTask<String, Integer, Integer>{
    private Activity activity;
    private Context context;
    private String id;
    private TextView Height, Weight, BE, stat1, stat2, stat3, stat4, stat5, stat6;
    private ProgressBar pb;
    private ProgressBar speed_bar, specialDef_bar, specialAtt_bar, defense_bar, attack_bar, health_bar;
    private LinearLayout llAbilities;
    private LinearLayout llMoves;
    private String HeightT, WeightT, BET;
    private int speed, spec_def, spec_att, def, att, hp;
    private ArrayList<String> ability = new ArrayList<>();
    private ArrayList<Move> arrayOfMoves = new ArrayList<>();





    private int flag = 0;



    public AsyncTaskDetails(Activity activity, String id) {
        this.activity = activity;
        this.id = id;


        context = activity.getApplicationContext();
    }



    @Override
    protected void onPreExecute() {
        Height = activity.findViewById(R.id.tvHeight2);
        Weight = activity.findViewById(R.id.tvWeight2);
        BE = activity.findViewById(R.id.tvBE2);

        stat1 = activity.findViewById(R.id.tvStat1);
        stat2 = activity.findViewById(R.id.tvStat2);
        stat3 = activity.findViewById(R.id.tvStat3);
        stat4 = activity.findViewById(R.id.tvStat4);
        stat5 = activity.findViewById(R.id.tvStat5);
        stat6 = activity.findViewById(R.id.tvStat6);

        llAbilities = activity.findViewById(R.id.llAbilities);
        llMoves = activity.findViewById(R.id.llMoves);

        speed_bar = activity.findViewById(R.id.pbSpeedBar);
        speed_bar.setMax(255);
        speed_bar.setProgress(0);

        specialDef_bar = activity.findViewById(R.id.pbSpecDefBar);
        specialDef_bar.setMax(255);
        specialDef_bar.setProgress(0);

        specialAtt_bar = activity.findViewById(R.id.pbSpecAttBar);
        specialAtt_bar.setMax(255);
        specialAtt_bar.setProgress(0);

        defense_bar = activity.findViewById(R.id.pbDefBar);
        defense_bar.setMax(255);
        defense_bar.setProgress(0);

        attack_bar = activity.findViewById(R.id.pbAttBar);
        attack_bar.setMax(255);
        attack_bar.setProgress(0);

        health_bar = activity.findViewById(R.id.pbHealthBar);
        health_bar.setMax(255);
        health_bar.setProgress(0);

        pb = activity.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);


    }

    @Override
    protected Integer doInBackground(String... strings) {

        //Controllo della connessione
        CheckNetwork cn = new CheckNetwork(context);
        while(!cn.isNetworkAvailable()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String url = String.format("https://pokeapi.co/api/v2/pokemon/%s/", id);
        Log.d("domi", "ciao");



            RequestQueue requestQueue = Volley.newRequestQueue(context);


            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        //Informazioni generali sul pokemon: peso etc..
                        HeightT = response.getInt("height") / 10.0 + " m";
                        WeightT = response.getInt("weight") / 10.0 + " Kg";
                        BET = response.getString("base_experience");

                        /* statistiche */

                        JSONArray stats = response.getJSONArray("stats");

                        speed = (int) stats.getJSONObject(0).getInt("base_stat");
                        spec_def = (int) stats.getJSONObject(1).getInt("base_stat");
                        spec_att = (int) stats.getJSONObject(2).getInt("base_stat");
                        def = (int) stats.getJSONObject(3).getInt("base_stat");
                        att = (int)stats.getJSONObject(4).getInt("base_stat");
                        hp = (int) stats.getJSONObject(5).getInt("base_stat");


                        /* abilità */

                        JSONArray abilities = response.getJSONArray("abilities");

                        for (int i = 0; i < abilities.length(); i++) {

                            ability.add(abilities.getJSONObject(i).getJSONObject("ability").getString("name"));

                        }

                        /* mosse */

                        JSONArray temp_moves = response.getJSONArray("moves");

                        Move move;
                        String name;
                        Integer level;

                        for (int i = 0; i < temp_moves.length(); i++) {
                            name = temp_moves.getJSONObject(i).getJSONObject("move").getString("name");
                            level = temp_moves.getJSONObject(i).getJSONArray("version_group_details").getJSONObject(0).getInt("level_learned_at");
                            move = new Move(name, level);
                            arrayOfMoves.add(move);
                        }

                        flag = 1;

                    } catch (JSONException e1) {
                        flag = -1;
                        e1.printStackTrace();
                    }


                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    flag = -1;
                    error.printStackTrace();
                }
            });
            requestQueue.add(request);

            while (flag == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return flag;

    }


    @Override
    protected void onPostExecute(Integer r) {

        if (r != -1) {

            Height.setText(HeightT);
            Weight.setText(WeightT);
            BE.setText(BET);

            speed_bar.setProgress(speed);
            specialDef_bar.setProgress(spec_def);
            specialAtt_bar.setProgress(spec_att);
            defense_bar.setProgress(def);
            attack_bar.setProgress(att);
            health_bar.setProgress(hp);

            stat1.setText(Integer.toString(hp));
            stat2.setText(Integer.toString(att));
            stat3.setText(Integer.toString(def));
            stat4.setText(Integer.toString(spec_att));
            stat5.setText(Integer.toString(spec_def));
            stat6.setText(Integer.toString(speed));


            for (int i = 0; i < ability.size(); i++) {
                View view = activity.getLayoutInflater().inflate(R.layout.profile_activity_move, null);
                TextView t = view.findViewById(R.id.tvMoveName);
                t.setText(ability.get(i));
                llAbilities.addView(view);
            }

            for (int i = 0; i < arrayOfMoves.size(); i++) {
                View v = activity.getLayoutInflater().inflate(R.layout.profile_activity_move, null);
                TextView t1 = v.findViewById(R.id.tvMoveName);
                TextView t2 = v.findViewById(R.id.tvLevel);
                t1.setText(arrayOfMoves.get(i).getMoveName());
                String lv;
                if (arrayOfMoves.get(i).getLevel() == 0){
                    lv = "MT/MN";
                }else {
                    lv = "lv. " + arrayOfMoves.get(i).getLevel();
                }
                t2.setText(lv);
                llMoves.addView(v);
            }


        }

        pb.setVisibility(View.GONE);
    }
}

