package it.lorenzotanzi.pokedex;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// version = 2 added because of entity's attribute update
@Database(entities = {Pokemon.class}, version = 2, exportSchema = false)
public abstract class PokemonRoomDatabase extends RoomDatabase {

    public abstract PokemonDao pokemonDao();
    private static  PokemonRoomDatabase INSTANCE;

    static PokemonRoomDatabase getDatabase (final Context context){
        if (INSTANCE == null){
            synchronized (PokemonRoomDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PokemonRoomDatabase.class, "pokemon_database")
                            /*.fallbackToDestructiveMigration()*/.allowMainThreadQueries().build(); // added fallback because of problem after entity's attribute update
                }
            }
        }
        return INSTANCE;
    }
}
