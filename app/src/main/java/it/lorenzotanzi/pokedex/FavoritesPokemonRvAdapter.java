package it.lorenzotanzi.pokedex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.lorenzotanzi.pokedex.cache.FromUrlToBitmap;
import it.lorenzotanzi.pokedex.interfaces.SelectMode;

public class FavoritesPokemonRvAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener, View.OnLongClickListener, Filterable {

    Bitmap bitmap;
    private PokemonDao pokemonDao;
    private SelectMode mListener;
    private Map<String, String> colors = new HashMap<>();
    private List<Pokemon> favorites;
    private SparseBooleanArray selectedList = new SparseBooleanArray();
    Context context;
    private List<Pokemon> supportPokemonList; // needed for filter research

    FavoritesPokemonRvAdapter(Context context, List<Pokemon> choices){
        favorites = choices;
        this.context = context;

        supportPokemonList = new ArrayList<>(favorites); /* support for filter research */

        mListener = (SelectMode) context; /* FavoritesPokemonActivity */

        PokemonRoomDatabase db = PokemonRoomDatabase.getDatabase(this.context);
        pokemonDao = db.pokemonDao();

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
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pokemon_detail, parent, false);
        cv.setOnClickListener(this);
        cv.setOnLongClickListener(this);
        return new PokemonRvAdapter.ViewHolder(cv);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // position deve essere final per essere aggiunt nel bundle dell'intent per accedere alla galleria
        final int pos=position;


        TextView tv_pkmn_num = holder.itemView.findViewById(R.id.tv_pkmn_num);
        TextView tv_pkmn_name = holder.itemView.findViewById(R.id.tv_pkmn_name);
        ImageView iv_pkmn_type1 = holder.itemView.findViewById(R.id.iv_pkmn_type1);
        ImageView iv_pkmn_type2 = holder.itemView.findViewById(R.id.iv_pkmn_type2);
        final ImageView iv_pkmn_icon = holder.itemView.findViewById(R.id.iv_pkmn_icon);
        ImageView iv_pkmn_status = holder.itemView.findViewById(R.id.iv_pkmn_status);
        View cardView = holder.itemView.findViewById(R.id.cl_card);

        String idString = favorites.get(position).getPkmnNum().toString();
        String pkmnNameString = favorites.get(position).getPkmnName();
        String type1str = favorites.get(position).getType1();
        String type2str = favorites.get(position).getType2();
        String type1col = colors.get(type1str);

        final String imgPkmnUrl = favorites.get(position).getImg();


        // // onClickListener sull'immagine pokemon per accedere tramite intent alla galleria
        iv_pkmn_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, GalleryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("PKID",favorites.get(pos).getPkmnNum().toString());
                bundle.putString("PKNAME",favorites.get(pos).getPkmnName());
                bundle.putString("PKTYPE1",favorites.get(pos).getType1());
                bundle.putString("PKTYPE2",favorites.get(pos).getType2());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });


        //STRING ADJUSTMENTS
        if (Integer.parseInt(idString) < 10) idString = new StringBuilder().append("#00").append(idString).toString();
        else if (Integer.parseInt(idString) < 100) idString = new StringBuilder().append("#0").append(idString).toString();
        else idString = new StringBuilder().append("#").append(idString).toString();
        pkmnNameString = pkmnNameString.substring(0, 1).toUpperCase() + pkmnNameString.substring(1);

        /* if pokemon's image not in cache then download it, else load it into the pokemon's ImageView */
        File pokeImg = new File(context.getCacheDir() + "/favorites" + "/" + favorites.get(position).getPkmnName() + ".png");
        if(!pokeImg.exists()) {
            Picasso.get()
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + favorites.get(position).getPkmnNum() + ".png")
                    .placeholder(R.drawable.pokeball)
                    .error(R.drawable.pokeball)
                    .into(iv_pkmn_icon);
            new FromUrlToBitmap(iv_pkmn_icon, position, context, favorites, 0).execute(imgPkmnUrl);
        }else{
            Picasso.get().load(pokeImg).placeholder(R.drawable.pokeball).error(R.drawable.pokeball).into(iv_pkmn_icon);
        }

        //BINDING OF POKEMON INFO AND CREATION OF GRADIENT BACKGROUND
        tv_pkmn_num.setText(idString);
        tv_pkmn_name.setText(pkmnNameString);

        /* needed in order to discriminate favorite pokemons from normal pokemon */
        if(!favorites.get(position).getFavorite()){
            iv_pkmn_status.setImageResource(R.drawable.ic_pkm_free);
        }else{
            iv_pkmn_status.setImageResource(R.drawable.ic_pkm_capt);
        }

        String _type1str = type1str.substring(0,1).toLowerCase() + type1str.substring(1);
        int id = context.getResources().getIdentifier(_type1str , "drawable", context.getPackageName());
        iv_pkmn_type1.setImageResource(id);
        Log.d("ADAPTER", "Pokemon " + idString + "drawable 1 id:" + Integer.toString(id));
            
        if (type2str != null) {

            String type2col = colors.get(type2str);
            iv_pkmn_type2.setVisibility(View.VISIBLE);

            String _type2str = type2str.substring(0,1).toLowerCase() + type2str.substring(1);
            int id2 = context.getResources().getIdentifier(_type2str , "drawable", context.getPackageName());
            Log.d("ADAPTER", "Pokemon " + idString + "drawable 2 id:" + Integer.toString(id2));
            int[] gradientColors = {Color.parseColor((type1col)), Color.parseColor(type2col)}; // PROBLEMA QUI
            Log.d("ADAPTER", "Pokemon " + idString + " COLORS:" + type1str + " " +  type1col + ", " + type2str + " "+ type2col);
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
            gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gd.setCornerRadius(30);
            cardView.setBackground(gd);
            iv_pkmn_type2.setImageResource(id2);

        } else {
            /*SE INVECE IL POKEMON HA UN SOLO TIPO VIENE SETTATO IL TESTO DELLA TEXT VIEW tv_pkmn_type2 A UNA STRINGA VUOTA
             * E VIENE SETTATO LO SFONDO DELLA CARD VIEW AL COLORE DELL'UNICO TIPO RELATIVO AL POKEMON*/
            iv_pkmn_type2.setVisibility(View.INVISIBLE);
            int backgroundColor = Color.parseColor(type1col);
            cardView.setBackgroundColor(backgroundColor);
        }

        /* if pokemon selected in contextual menu then color it's CardView for show it up */
        boolean isSelected = selectedList.get(position,false);
        if(isSelected) {
            cardView.setSelected(true);
            cardView.setBackgroundColor(Color.LTGRAY);
        } else {
            cardView.setSelected(false);
        }

    }

    @Override
    public int getItemCount() {
        return favorites == null? 0 : favorites.size();
    }

    /* if LongPress previously then repeat onLongClick actions */
    @Override
    public void onClick(View v) {
        if(selectedList.size() > 0){
            onLongClick(v);
        }else {
            final int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
            Pokemon pokemon = favorites.get(position);
            Intent intent = new Intent(context, Details.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", favorites.get(position).getPkmnName());
            bundle.putString("ID", favorites.get(position).getPkmnNum().toString());
            bundle.putString("Type1", favorites.get(position).getType1());
            bundle.putString("Type2", favorites.get(position).getType2());
            bundle.putString("Img", favorites.get(position).getImg());
            intent.putExtras(bundle);
            context.startActivity(intent);
        }

    }

    /* management of contextual menu's actions */
    @Override
    public boolean onLongClick(View v) {

            final int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
            boolean isSel = selectedList.get(position, false);

            if(isSel){

                v.setSelected(false);
                selectedList.delete(position);

                favorites.get(position).setFavorites(true);

            }else{

                v.setSelected(true);
                selectedList.put(position, true);

                favorites.get(position).setFavorites(false);

            }

        if(mListener != null){
            mListener.onSelect(selectedList.size()); /* start contextual menu */
        }
        notifyDataSetChanged();
        return true;
    }

    /* ---- FOR FILTER SEARCH ON SEARCH MENU ---- */
    @Override
    public Filter getFilter() {
        return pokemonFilter;
    }

    private Filter pokemonFilter = new Filter() {

        /* collect pokemon match string's search into results.values */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Pokemon> pkmnFiltList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0 || constraint.toString().isEmpty()){
                pkmnFiltList.addAll(supportPokemonList);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Pokemon poke : supportPokemonList){
                    if(poke.getPkmnName().toLowerCase().contains(filterPattern) && poke.getPkmnName().
                            startsWith(String.valueOf(filterPattern.charAt(0)))){
                        pkmnFiltList.add(poke);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = pkmnFiltList;

            return results;
        }

        /* thanks to this it's possible the visualization of pokemon searched */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            favorites.clear();
            favorites.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    
    public void removeAllFavorites(){

        if(favorites.size() >0 ) {

            for(int position = 0; position < favorites.size(); position++) {
                favorites.get(position).setFavorites(false);
                pokemonDao.setFavorite(favorites.get(position));
            }

            favorites.removeAll(favorites);
            supportPokemonList.removeAll(supportPokemonList); // new add for bug during deleting and then research
            selectedList.clear();
            notifyDataSetChanged();
            notifyItemRangeRemoved(0, favorites.size());
        }
    }


    public void deleteSelected(){
        if(selectedList.size() == 0){
            return;
        }

        /* bug with a lot of elements, if '+700' that's ok */
        for(int index = selectedList.size() + 700; index>=0; index--){
            if(selectedList.get(index, false)){
                favorites.get(index).setFavorites(false);
                pokemonDao.setFavorite(favorites.get(index));
                remove(index);
            }
        }
        selectedList.clear();
        notifyItemRangeRemoved(0, selectedList.size());
        notifyDataSetChanged();
    }


    public void remove(int position){
        supportPokemonList.remove(position); // new add for bug during deleting and then research
        favorites.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(0, position);
        notifyDataSetChanged();
    }


    public void deselectAll(){
        selectedList.clear();

        for(int index = 0; index < favorites.size(); index++){
            favorites.get(index).setFavorites(true);
            pokemonDao.setFavorite(favorites.get(index));
        }
        notifyDataSetChanged();
    }

    public List<Pokemon> getFavorites(){
        return favorites;
    }


    public SparseBooleanArray getSelectedList(){
        return selectedList;
    }

    public void setFavorites(List<Pokemon> pokemons){
        favorites = pokemons;
    }

    public void setSparseBooleanArray(SparseBooleanArray array){
        selectedList = array;
    }

}
