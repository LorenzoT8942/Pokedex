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

    public MainViewModel(Application application) {
        super(application);
        repository = new PokemonRepository(application);
        allPokemons = repository.getAllPokemons();
    }

    public LiveData<List<Pokemon>> getAllPokemons(){
        allPokemons = repository.getAllPokemons();
        return allPokemons;
    }
}
