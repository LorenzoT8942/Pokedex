package it.lorenzotanzi.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_SPRITE;
import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_URL;
import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_PKNAME;
import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_PKID;
import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_PKCOLOR;

public class FullActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(EXTRA_URL);
        String sprite = intent.getStringExtra(EXTRA_SPRITE);
        int id=intent.getIntExtra(EXTRA_PKID,1);
        String name=intent.getStringExtra(EXTRA_PKNAME);
        int color=intent.getIntExtra(EXTRA_PKCOLOR, Color.WHITE);

        ImageView imageView = findViewById(R.id.image_view);
        TextView textViewSprite= findViewById(R.id.text_view_sprite);
        TextView textViewId= findViewById(R.id.text_view_id);
        TextView textViewName= findViewById(R.id.text_view_name);
        ConstraintLayout layout=findViewById(R.id.fullLayout);

        Picasso.get().load(imageUrl).fit().centerInside().into(imageView);
        textViewId.setText(String.valueOf(id));
        textViewName.setText(name);
        textViewSprite.setText(sprite);
        layout.setBackgroundColor(color);
    }
}
