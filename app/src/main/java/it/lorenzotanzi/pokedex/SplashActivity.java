package it.lorenzotanzi.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

// Al lancio dell'applicazione mostra la schermata del logo prima di far partire la MainActivity
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // il layout viene implementato tramite manifest
        // SplashActivity lancia MainActivity
        startActivity(new Intent(this, MainActivity.class));
    }
}
