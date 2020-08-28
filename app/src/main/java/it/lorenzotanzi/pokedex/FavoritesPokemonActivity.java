package it.lorenzotanzi.pokedex;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.lorenzotanzi.pokedex.interfaces.SelectMode;

// deve implementare anche searchView.setOnQueryTextListener -- REGOLI'S OBBLIGATION
public class FavoritesPokemonActivity extends AppCompatActivity implements SelectMode, SearchView.OnQueryTextListener {

    private ActionMode mActionMode;
    private FavoritesPokemonRvAdapter favoritesAdapter;
    List<Pokemon> favoritesList = new ArrayList<>();
    List<String> list = new ArrayList<>();

    private boolean isInActionMode = false;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_pokemon);

        mp = MediaPlayer.create(this, R.raw.pikachu_angry);

        if (savedInstanceState != null && savedInstanceState.getBoolean("ActionMode", false)) {
            startSupportActionMode(mActionModeCallback);
        }

        favoritesList = getIntent().getParcelableArrayListExtra("favorites");

        /* creare classe a parte in 'package: cache' in cui vi sia un metodo che implementa ciò e tutto il resto che segue inerente al
         * salvataggio immagini */
        /* creazione sotto-directory per memorizzare le immagini dei pokemon preferiti */
        File imgCacheFolder = new File(getCacheDir() + "/favorites");
        if(!imgCacheFolder.exists()){
            imgCacheFolder.mkdir();
        }

        favoritesAdapter = new FavoritesPokemonRvAdapter(this, favoritesList);

        /* start pattern for fill Recycle View of favorites */
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.rv_favor_pkmn);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(favoritesAdapter);

    }

    /* NEDDED FILL WITH CLASSICAL APPROACH SHOWED REGOLI */


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_favorites, menu);

        /* ---- TEST FOR DINAMICAL SEARCH ON SEARCH MENU ---- */
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        //searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == R.id.menu_home){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        if(item.getItemId() == R.id.menu_del_all){

            favoritesAdapter.removeAllFavorites();

            favoritesAdapter.notifyDataSetChanged();

            mp.start();

        }
        /* ci va la condizione dell'About */
        if(item.getItemId() == R.id.menu_about) {

            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            intent.putParcelableArrayListExtra("favorites", (ArrayList<? extends Parcelable>) favoritesList);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }else{

            return super.onContextItemSelected(item);
        }

        return true;
    }

    @Override
    public void onSelect(int size) {

        if(mActionMode != null) {
            if(size == 0) {
                mActionMode.finish();
            }
            return;
        }
        mActionMode = startSupportActionMode(mActionModeCallback);
    }
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_context_favor, menu);

            isInActionMode = true;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if(item.getItemId() == R.id.mn_cont_del_favor){

                favoritesAdapter.deleteSelected();

                favoritesAdapter.notifyDataSetChanged(); // TEST

                mp.start();
                mode.finish();

            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            isInActionMode = false;

            favoritesAdapter.deselectAll();
            mActionMode = null;
        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        favoritesAdapter.getFilter().filter(newText);

        return false;
    }

    /* new add */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("ActionMode", isInActionMode);

        outState.putParcelableArrayList("favorites", (ArrayList<? extends Parcelable>) favoritesAdapter.getFavorites());
        outState.putParcelable("myBooleanArray", new SparseBooleanArrayParcelable(favoritesAdapter.getSelectedList()));

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        List<Pokemon> pokemons = savedInstanceState.getParcelableArrayList("favorites");
        SparseBooleanArray array = (SparseBooleanArray) savedInstanceState.getParcelable("myBooleanArray");

        favoritesAdapter.setFavorites(pokemons);
        favoritesAdapter.setSparseBooleanArray(array);

        super.onRestoreInstanceState(savedInstanceState);
    }

}
