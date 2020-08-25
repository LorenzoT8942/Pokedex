package it.lorenzotanzi.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import androidx.appcompat.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;
//import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.lorenzotanzi.pokedex.interfaces.SelectMode;

// deve implementare anche searchView.setOnQueryTextListener -- REGOLI'S OBBLIGATION
public class MainActivity extends AppCompatActivity implements SelectMode, SearchView.OnQueryTextListener{

    private MainViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PokemonRvAdapter mAdapter;
    private ActionMode mActionMode; // new add for menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MAIN", "onCreate");
        MyViewModelFactory myViewModelFactory = new MyViewModelFactory(this.getApplication());
        mViewModel = new ViewModelProvider(this, myViewModelFactory).get(MainViewModel.class);
        initListeners();
        initObservers();
        initRecyclerView();

        /* creazione cartella dove andranno salvate le immagini di front-default dei pokemon in locale */
        File imgCacheFolder = new File(getCacheDir() + "/pokemons");
        if (!imgCacheFolder.exists()) {
            imgCacheFolder.mkdir();
        }

    }

    private void initRecyclerView(){
        Log.d("MAIN", "initializing recycler view");
        mRecyclerView = findViewById(R.id.rv_pkmn);
        mRecyclerView.setHasFixedSize(true); // new add for filter search
        mLayoutManager = new LinearLayoutManager(this);
//        DividerItemDecoration itemDecorator = (new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
//        itemDecorator.setDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rv_divider));
//        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PokemonRvAdapter(R.layout.cardview_pokemon_detail, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListeners(){
    }

    private void initObservers(){
        Log.d("MAIN", "initializing observers");
        mViewModel.getAllPokemons().observe(this, new Observer<List<Pokemon>>() {
            @Override
            public void onChanged(List<Pokemon> pokemons) {
                mAdapter.setPokemonList(pokemons);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /* ---- FOR DINAMICAL SEARCH ON SEARCH MENU ---- */

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        //searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_favor){
            Intent intent = new Intent(MainActivity.this, FavoritesPokemonActivity.class);
            intent.putParcelableArrayListExtra("favorites", (ArrayList<? extends Parcelable>) mAdapter.chosenFavorites());
            startActivity(intent);
        }

        /* add also About Icon --> need to show app's rules and developers  */
        if(item.getItemId() == R.id.menu_about) {

            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);

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
            mode.getMenuInflater().inflate(R.menu.menu_context, menu);

            return true;
        }

        // don't care
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if(item.getItemId() == R.id.mn_cont_favor) {

                mAdapter.fillFavorPkmonList();

                Toast toast = Toast.makeText(getApplicationContext(), "FAVORITES UPDATED", Toast.LENGTH_SHORT);
                View view = toast.getView();
                view.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(Color.RED);
                toast.show();

                mode.finish();

            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.deselectAll();
            mActionMode = null;
        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        mAdapter.getFilter().filter(newText);

        return false;
    }

}

