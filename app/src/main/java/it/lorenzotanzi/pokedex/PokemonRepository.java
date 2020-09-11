package it.lorenzotanzi.pokedex;


import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.util.List;
import androidx.lifecycle.LiveData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class PokemonRepository {
    /* LA CLASSE REPOSITORY HA LA RESPONSABILITA' DI RACCOGLIERE DATI DA WEB SERVICES (ATTRAVERSO API, ETC...)
    E DA DATABASE E PUO' QUINDI COMUNICARE ESCLUSIVAMENTE CON IL ROOM DATABASE E LA CLASSE VIEW MODEL ALLA QUALE
    VERRANNO PASSATI I DATI OTTENUTI.  */

    private PokemonDao pokemonDao;
    private RequestQueue requestQueue;
    private LiveData<List<Pokemon>> allPokemons;

    //CONSTRUCTOR:
    PokemonRepository(Application application) {
        Log.d("REPO", "CONSTRUCTING REPOSITORY");
        PokemonRoomDatabase db = PokemonRoomDatabase.getDatabase(application);
        Log.d("REPO", " DB obtained");
        pokemonDao = db.pokemonDao();
        Log.d("REPO", "DAO obtained");
        requestQueue = Volley.newRequestQueue(application);
        requestQueue.start();
        Log.d("REPO", "Request Queue started");
        allPokemons = pokemonDao.getAllPokemons();
        Log.d("REPO", "Pokemon list obtained from DB");
        initDatabase();

    }

    //METODO NECESSARIO ALLA CLASSE VIEW MODEL PER OTTENERE LA LISTA POKEMON
    LiveData<List<Pokemon>> getAllPokemons() {
        return allPokemons;
    }

    /*METODO CHE PRENDE IN INPUT UN'ISTANZA DELLA CLASSE POKEMON. NECESSARIA PER INSERIRE
     UN NUOVO POKEMON NEL DATABASE TRAMITE ASYNC TASK E QUINDI IN MODO ASINCRONO RISPETTO AL MAIN THREAD*/
    void insert(Pokemon pokemon) {
        new InsertPokemonAsyncTask(pokemonDao).execute(pokemon);
    }

    //
    void initDatabase() {
        new checkDatabaseAT(pokemonDao).execute(requestQueue);
    }

    //QUERIES THE DB TO CHECK IF IT EMPTY OR NOT
    private static class checkDatabaseAT extends AsyncTask<RequestQueue, Void, /*List<Pokemon>*/Integer> {

        private PokemonDao asyncTaskDao;
        private PokemonRepository delegate;

        public checkDatabaseAT(PokemonDao asyncTaskDao) {
            this.asyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Integer doInBackground(RequestQueue... requestQueues) {

            if (asyncTaskDao.checkDatabase().length < 1) {
                for (int i = 1; i < 808; i++) {
                    String Index = Integer.toString(i);
                    String url = String.format("https://pokeapi.co/api/v2/pokemon/%s/", Integer.toString(i));
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String newPkmnId = response.getString("id");
                                        String newPkmnName = response.getString("name");
                                        JSONArray newPkmnTypes = response.getJSONArray("types");
                                        JSONObject jsonType1 = newPkmnTypes.getJSONObject(0);
                                        String sType1 = jsonType1.getJSONObject("type").getString("name");
                                        sType1 = sType1.substring(0, 1).toUpperCase() + sType1.substring(1);
                                        String sType2;
                                        JSONObject sprites = response.getJSONObject("sprites");
                                        String urlPkmnImg = sprites.getString("front_default");
                                        if (newPkmnTypes.isNull(1)) {
                                            sType2 = null;
                                        } else {
                                            sType2 = newPkmnTypes.getJSONObject(1).getJSONObject("type").getString("name");
                                            sType2 = sType2.substring(0, 1).toUpperCase() + sType2.substring(1);
                                        }
                                        Pokemon pokemon = new Pokemon(Integer.parseInt(newPkmnId), false, newPkmnName, sType1, sType2, urlPkmnImg);
                                        new InsertPokemonAsyncTask(asyncTaskDao).execute(pokemon);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                    requestQueues[0].add(jsonObjectRequest);
                }
            }
            return null;
        }
    }

    //INSERT A POKEMON RECORD IN THE DB
    private static class InsertPokemonAsyncTask extends AsyncTask<Pokemon, Void, Void> {

        private PokemonDao asyncTaskDao;
        private PokemonRepository delegate = null;

        InsertPokemonAsyncTask(PokemonDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Pokemon... params) {
            Log.d("ASYNCT", "Inserting pokemon");
            asyncTaskDao.insertPokemon(params[0]);
            return null;
        }
    }
}
