package it.lorenzotanzi.pokedex.threads;

import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoadFileInfoThread extends Thread{

    private Context context;
    private InputStream inputStream;
    public static final String NOTIFICATION = "it.lorenzotanzi.pokedex.threads.LoadFileInfoThread.result";

    public LoadFileInfoThread(InputStream is, Context context){

        inputStream = is;
        this.context = context;

    }

    public void run(){

        /* send results to AboutActivity */
        String fileInfo = loadFileInfo();
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("info", fileInfo);
        context.sendBroadcast(intent);

    }

    /* read from info-%s.txt and return its content */
    private String loadFileInfo(){

        String ret = "";

        try{
            String string;
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while((string = br.readLine()) != null){
                stringBuilder.append("\n").append(string);
            }
            ret = stringBuilder.toString();
            br.close();
        }catch (IOException ignored){}

        return ret;
    }

}
