package it.lorenzotanzi.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_PKTYPE1;
import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_PKTYPE2;
import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_SPRITE;
import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_URL;
import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_PKNAME;
import static it.lorenzotanzi.pokedex.GalleryActivity.EXTRA_PKID;

public class FullActivity extends AppCompatActivity {

    private Map<String, String> colors = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        colors.put("Normal", "#A8A77A");
        colors.put("Fire", "#EE8130");
        colors.put("Water","#6390F0");
        colors.put("Electric", "#F7D02C");
        colors.put("Grass", "#7AC74C");
        colors.put("Ice", "#96D9D6");
        colors.put("Fighting", "#C22E28");
        colors.put("Poison", "#A33EA1");
        colors.put("Ground", "#E2BF65");
        colors.put("Flying", "#A98FF3");
        colors.put("Psychic", "#F95587");
        colors.put("Bug", "#A6B91A");
        colors.put("Rock", "#B6A136");
        colors.put("Ghost", "#735797");
        colors.put("Dragon", "#6F35FC");
        colors.put("Dark", "#705746");
        colors.put("Steel", "#B7B7CE");
        colors.put("Fairy", "#D685AD");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        Bundle bundle = getIntent().getExtras();
        String imageUrl = bundle.getString(EXTRA_URL);
        String sprite = bundle.getString(EXTRA_SPRITE);
        String id =bundle.getString(EXTRA_PKID);
        String name=bundle.getString(EXTRA_PKNAME);
        String type1=bundle.getString(EXTRA_PKTYPE1);
        String type2=bundle.getString(EXTRA_PKTYPE2);

        ImageView imageView = findViewById(R.id.image_view);
        //TextView textViewSprite= findViewById(R.id.text_view_sprite);
        //TextView textViewId= findViewById(R.id.text_view_id);
        //TextView textViewName= findViewById(R.id.text_view_name);

        NestedScrollView scrollLayout=findViewById(R.id.scrollLayout);

        String type1col = colors.get(type1);
        if (type2 != null) {
            String type2col = colors.get(type2);
            int [] gradientColors = {Color.parseColor((type1col)), Color.parseColor(type2col)};
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
            gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            //gd.setCornerRadius(30);
            scrollLayout.setBackground(gd);
        } else {
            int backgroundColor = Color.parseColor(type1col);
            scrollLayout.setBackgroundColor(backgroundColor);
        }

        Picasso.get().load(imageUrl).fit().centerInside().into(imageView);
        //textViewId.setText(id);
        //textViewName.setText(name);
        //textViewSprite.setText(sprite);
        //layout.setBackgroundColor(color);
    }
}
