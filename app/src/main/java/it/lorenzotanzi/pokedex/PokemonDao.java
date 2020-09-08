package it.lorenzotanzi.pokedex;

import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPokemon(Pokemon pokemon);

    @Update
    void setFavorite(Pokemon pokemon);

    @Query("SELECT * FROM Pokemon ORDER BY ID")
    LiveData<List<Pokemon>> getAllPokemons();

    @Query("SELECT * FROM Pokemon WHERE name LIKE :name ")
    List<Pokemon> findPokemon(String name);


    @Query("SELECT * FROM Pokemon LIMIT 1")
    Pokemon[] checkDatabase();

    @Query("SELECT * FROM Pokemon WHERE favorite LIKE :favor ORDER BY ID")
    List<Pokemon> getAllFavorites(Boolean favor); // added for favorite portfolio (page)

    // for favorites
    @Query("SELECT favorite FROM Pokemon WHERE name LIKE :name ")
    Boolean getFavorite(String name);

    @Query("SELECT * FROM Pokemon WHERE id LIKE :id")
    Pokemon findPokemonId(Integer id);

}
