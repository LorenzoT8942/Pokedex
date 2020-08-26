package it.lorenzotanzi.pokedex;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AboutActivity extends AppCompatActivity {

    List<Pokemon> favoritesList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        favoritesList = getIntent().getParcelableArrayListExtra("favorites");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.rv_developers);
        RecyclerView.Adapter mAdapter = new MyAdapter(loadDevelopersGeneral(), loadEmail());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        /* inserirli in un holder senza argomento */
        TextView tv_content_info = findViewById(R.id.tv_content_info);
        tv_content_info.setText(loadFile());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_favor) {
            Intent intent = new Intent(this, FavoritesPokemonActivity.class);
            intent.putParcelableArrayListExtra("favorites", (ArrayList<? extends Parcelable>) favoritesList);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.menu_home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        return true;
    }

    private List<String> loadDevelopersGeneral(){
        String singleGeneral;
        List<String> developersGeneral = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("developers.txt")));
            while((singleGeneral = br.readLine()) != null){
                developersGeneral.add(singleGeneral);
            }
            br.close();
        }catch (IOException e){}

        return developersGeneral;
    }

    private List<String> loadEmail(){
        String singleEmail;
        List<String> developersEmail = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("email.txt")));
            while((singleEmail = br.readLine()) != null){
                developersEmail.add(singleEmail);
            }
            br.close();
        }catch (IOException e){}

        return developersEmail;
    }

    private String loadFile(){
        String ret = "";
        Locale l = Locale.getDefault();

        String fileContent;

        if(l.getCountry().compareTo("IT") == 0){
            fileContent = String.format(l, "info-%s.txt", l.getCountry());
        }else{
            fileContent = String.format(l, "info-%s.txt", "EN");
        }
        try{
            StringBuilder stringBuilder = new StringBuilder();
            String string;

            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(fileContent)));
            while((string = br.readLine()) != null){
                stringBuilder.append("\n").append(string);
            }
            ret = stringBuilder.toString();
            br.close();
        }catch(IOException e){}

        return ret;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder>{
        List<String> mGenerals;
        List<String> mEmails;

        //String fileInfo;

        MyAdapter(List<String> Generals, List<String> Emails){
            mGenerals = Generals;
            mEmails = Emails;
            //fileInfo = file;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            CardView cl = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_about, parent, false);

            return new Holder(cl);
        }


        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {

            holder.tv_developer_general.setText(mGenerals.get(position));
            holder.tv_developer_mail.setText(mEmails.get(position));

            if(position == 0){
                holder.iv_image.setImageResource(R.drawable.ic_claudio);
                //holder.cl_item_about.setBackgroundColor(Color.LTGRAY);

            }
            if(position == 1){
                holder.iv_image.setImageResource(R.drawable.ic_ana);
                //holder.cl_item_about.setBackgroundColor(Color.GRAY);
            }
            if(position == 2){
                holder.iv_image.setImageResource(R.drawable.ic_dominique);
                //holder.cl_item_about.setBackgroundColor(Color.LTGRAY);
            }
            if(position == 3){
                holder.iv_image.setImageResource(R.drawable.ic_alison);
                //holder.cl_item_about.setBackgroundColor(Color.GRAY);
            }
            /*if(position == 4){
                holder.iv_image.setImageResource(R.drawable.ic_lorenzo);
                holder.cl_item_about.setBackgroundColor(Color.LTGRAY);
            }*/
        }

        @Override
        public int getItemCount() {
            return mGenerals.size();
        }

        class Holder extends RecyclerView.ViewHolder{

            ImageView iv_image;
            TextView tv_developer_general;
            TextView tv_developer_mail;
            ConstraintLayout cl_item_about;

            Holder(CardView cl) {
                super(cl);
                iv_image = cl.findViewById(R.id.iv_image);
                tv_developer_general = cl.findViewById(R.id.tv_developer_general);
                tv_developer_mail = cl.findViewById(R.id.tv_developer_mail);
                cl_item_about = cl.findViewById(R.id.cl_item_about);
            }
        }

    }

}
