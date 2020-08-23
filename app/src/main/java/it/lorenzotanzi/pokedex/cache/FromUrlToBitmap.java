package it.lorenzotanzi.pokedex.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import it.lorenzotanzi.pokedex.Pokemon;

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

        InputStream inputStream;
        try {

            inputStream = new java.net.URL(stringUrl).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);

        }catch (IOException e){
            e.printStackTrace();
        }

        return bitmap;

    }

    @Override
    public void onPostExecute(Bitmap bitmap){
        super.onPostExecute(bitmap);
        imageView.setImageBitmap(bitmap);

        /* conviene salvare l'immagine scaricata in locale per poi non dover ripetere
         * più l'accesso ad internet */
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

    }
}
