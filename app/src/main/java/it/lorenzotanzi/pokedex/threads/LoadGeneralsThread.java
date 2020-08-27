package it.lorenzotanzi.pokedex.threads;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoadGeneralsThread extends Thread{

    Context context;
    InputStream inputStream1;
    InputStream inputStream2;

    public static final String NOTIFICATION = "it.lorenzotanzi.pokedex.threads.LoadGeneralsThread.result";

    public LoadGeneralsThread(InputStream is1, InputStream is2, Context context){
        inputStream1 = is1;
        inputStream2 = is2;
        this.context = context;

    }

    public void run(){
        List<String> generals = loadDevelopersGenerals();
        List<String> emails = loadDevelopersEmails();

        /* bisogna inviare questo risultato alla 'AboutActivity' */
        Intent intent = new Intent(NOTIFICATION);
        intent.putStringArrayListExtra("generals", (ArrayList<String>) generals);
        intent.putStringArrayListExtra("emails", (ArrayList<String>) emails);
        context.sendBroadcast(intent);
    }

    private List<String> loadDevelopersGenerals(){
        String singleGeneral;
        List<String> generals = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream1));
            while((singleGeneral = br.readLine()) != null){
                generals.add(singleGeneral);
            }
            br.close();
        }catch (IOException ignored){}

        return generals;
    }

    private List<String> loadDevelopersEmails(){
        String singleEmail;
        List<String> emails = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream2));
            while((singleEmail = br.readLine()) != null){
                emails.add(singleEmail);
            }
            br.close();
        }catch (IOException e){}

        return emails;
    }
}
