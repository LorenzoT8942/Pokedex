package it.lorenzotanzi.pokedex;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

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
public class FavoritesPokemonActivity extends AppCompatActivity implements SelectMode, SearchView.OnQueryTextListener, View.OnFocusChangeListener, SearchView.OnCloseListener{

    private ActionMode mActionMode; /* for contextual menu's appearence */
    private FavoritesPokemonRvAdapter favoritesAdapter;
    List<Pokemon> favoritesList = new ArrayList<>();
    private boolean isInActionMode = false; /* to preserve choices in contextual menu mode after device rotation */
    private MediaPlayer mp; /* sound effect after deletion event */

    private SearchView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_pokemon);

        mp = MediaPlayer.create(this, R.raw.pikachu_sad);

        /* re-creation of contextual menu after device rotation */
        if (savedInstanceState != null && savedInstanceState.getBoolean("ActionMode", false)) {
            startSupportActionMode(mActionModeCallback);
        }

        favoritesList = getIntent().getParcelableArrayListExtra("favorites");

        /* creation of sub-directory in order to memorize favorite pokemon's images */
        File imgCacheFolder = new File(getCacheDir() + "/favorites");
        if(!imgCacheFolder.exists()){
            imgCacheFolder.mkdir();
        }

        favoritesAdapter = new FavoritesPokemonRvAdapter(this, favoritesList);

        /* initialization of favorite pokemon's layout */
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.rv_favor_pkmn);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(favoritesAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_favorites, menu);

        /* ---- DINAMICAL SEARCH ON SEARCH MENU ---- */
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextFocusChangeListener(this);
        searchView.setOnCloseListener(this);

        searchIcon = searchView;

        /* change color of 'Free All' from black to white */
        MenuItem item_del = menu.findItem(R.id.menu_del_all);
        SpannableString spannableStringDel = new SpannableString(item_del.getTitle().toString());
        spannableStringDel.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableStringDel.length(), 0);
        item_del.setTitle(spannableStringDel);

        /* change color of 'About' from black to white*/
        MenuItem item_about = menu.findItem(R.id.menu_about);
        SpannableString spannableStringAbout = new SpannableString(item_about.getTitle().toString());
        spannableStringAbout.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableStringAbout.length(), 0);
        item_about.setTitle(spannableStringAbout);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        /* return into home page */
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

        /* go to about page */
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

    /* preparation of contextual menu's actions */
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
                favoritesAdapter.notifyDataSetChanged();

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

    /* get text typed from client */
    @Override
    public boolean onQueryTextChange(String newText) {

        favoritesAdapter.getFilter().filter(newText);

        return false;
    }

    /* save all necessary in order to preserve actions taken a moment before device rotation */
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean("ActionMode", isInActionMode);
        outState.putParcelableArrayList("favorites", (ArrayList<? extends Parcelable>) favoritesAdapter.getFavorites());
        outState.putParcelable("myBooleanArray", new SparseBooleanArrayParcelable(favoritesAdapter.getSelectedList()));

        super.onSaveInstanceState(outState);
    }

    /* restore all actions taken a moment before device rotation */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        List<Pokemon> pokemons = savedInstanceState.getParcelableArrayList("favorites");
        SparseBooleanArray array = (SparseBooleanArray) savedInstanceState.getParcelable("myBooleanArray");

        favoritesAdapter.setFavorites(pokemons);
        favoritesAdapter.setSparseBooleanArray(array);

        super.onRestoreInstanceState(savedInstanceState);
    }

    /* NEW ADD */
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus && !isInActionMode){
            favoritesAdapter.getFilter().filter("");
            searchIcon.clearFocus();
            searchIcon.onActionViewCollapsed();
            onClose();
        }
    }

    @Override
    public boolean onClose() {
        return false;
    }
}
