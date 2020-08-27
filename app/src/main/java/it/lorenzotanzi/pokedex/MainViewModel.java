package it.lorenzotanzi.pokedex;

import android.app.Application;
import android.util.Log;
import java.util.List;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
public class MainViewModel extends AndroidViewModel {

    private PokemonRepository repository;
    private LiveData<List<Pokemon>> allPokemons;
    private MutableLiveData<List<Pokemon>>  searchResults;

    public MainViewModel(Application application) {
        super(application);
        Log.d("VM", "CONSTRUCTING VIEW MODEL CLASS");
        repository = new PokemonRepository(application);
        Log.d("VM", "repo obtained");
        allPokemons = repository.getAllPokemons();
        Log.d("VM", "Pokemon list obtained");
        searchResults = repository.getSearchResults();
        Log.d("VM", "searchResults requested");
    }

    public MutableLiveData<List<Pokemon>> getSearchResults(){
        return repository.getSearchResults();
    }

    public LiveData<List<Pokemon>> getAllPokemons(){
        return repository.getAllPokemons();
    }

    void insertPokemon (Pokemon pokemon){
        repository.insert(pokemon);
    }

    void findPokemon (String name) {
        repository.findPokemon(name);
    }


}
