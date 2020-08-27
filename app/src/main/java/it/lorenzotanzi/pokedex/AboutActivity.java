package it.lorenzotanzi.pokedex;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.lorenzotanzi.pokedex.threads.LoadFileInfoThread;
import it.lorenzotanzi.pokedex.threads.LoadGeneralsThread;

public class AboutActivity extends AppCompatActivity {

    List<Pokemon> favoritesList = new ArrayList<>();

    ArrayList<String> generals = new ArrayList<>();
    ArrayList<String> emails = new ArrayList<>();
    String fileInfo = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        favoritesList = getIntent().getParcelableArrayListExtra("favorites");

        registerReceiver(receiver1, new IntentFilter(LoadGeneralsThread.NOTIFICATION));
        registerReceiver(receiver2, new IntentFilter(LoadFileInfoThread.NOTIFICATION));

        try {

            InputStream inputStream1 = getAssets().open("developers.txt");
            InputStream inputStream2 = getAssets().open("email.txt");
            LoadGeneralsThread loadGeneralsThread = new LoadGeneralsThread(inputStream1, inputStream2, this);
            loadGeneralsThread.start();
            loadFile();

        } catch (IOException ignored) {}

    }

    class Holder{

        TextView tv_content_info;

        Holder(){

            tv_content_info = findViewById(R.id.tv_content_info);
            tv_content_info.setText(fileInfo);

        }

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

    private void loadFile(){

        Locale l = Locale.getDefault();
        String fileContent;

        if(l.getCountry().compareTo("IT") == 0){
            fileContent = String.format(l, "info-%s.txt", l.getCountry());
        }else{
            fileContent = String.format(l, "info-%s.txt", "EN");
        }

        try{
            InputStream is = getAssets().open(fileContent);

            LoadFileInfoThread loadFileInfoThread = new LoadFileInfoThread(is, this);
            loadFileInfoThread.start();

        }catch(IOException ignored){}

    }


    private BroadcastReceiver receiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null){

                generals = bundle.getStringArrayList("generals");
                emails = bundle.getStringArrayList("emails");

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AboutActivity.this);
                RecyclerView recyclerView = findViewById(R.id.rv_developers);
                RecyclerView.Adapter mAdapter = new AboutAdapter(generals, emails);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(mAdapter);
            }
        }
    };

    private BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null){

                fileInfo = bundle.getString("info");
                new Holder();
            }
        }
    };
}
