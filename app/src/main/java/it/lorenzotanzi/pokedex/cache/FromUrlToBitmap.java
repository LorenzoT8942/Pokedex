package it.lorenzotanzi.pokedex.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import it.lorenzotanzi.pokedex.Pokemon;
import it.lorenzotanzi.pokedex.R;

public class FromUrlToBitmap extends AsyncTask<String, Void, Bitmap> {

    @SuppressLint("StaticFieldLeak")
    private ImageView imageView;
    private int pokePosition;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private List<Pokemon> currPkmnlist;

    private int choice; // needed in order to correct instantiation of path (favorites pokemons/standards pokemons)

    public FromUrlToBitmap(ImageView view, int index, Context context, List<Pokemon> poke, int choice){

        this.imageView = view;
        this.pokePosition = index;
        this.context = context;
        this.currPkmnlist = poke;
        this.choice = choice;

    }


    @Override
    protected Bitmap doInBackground(String... url) {

        String stringUrl = url[0];
        Bitmap bitmap = null;

        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(stringUrl);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {

                bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Bitmap bitmap){
        super.onPostExecute(bitmap);

        /* save image into internal storage for next accesses */
        try {
            saveImgIntoInernalStorage(bitmap, pokePosition, context, currPkmnlist, choice);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* Internal Storage */
    public void saveImgIntoInernalStorage(Bitmap bitmap, int position, Context context, List<Pokemon> poke, int choice) throws FileNotFoundException {

        String path = null;

        if(choice == 0) {
            path = context.getCacheDir() + "/favorites";
            path += "/" + poke.get(position).getPkmnName() + ".png";
        }else{
            path = context.getCacheDir() + "/pokemons";
            path += "/" + poke.get(position).getPkmnName() + ".png";
        }

        OutputStream outputStream = new FileOutputStream(new File((path)));
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        Picasso.get().load(path).placeholder(R.drawable.pokeball).into(imageView);

    }
}
