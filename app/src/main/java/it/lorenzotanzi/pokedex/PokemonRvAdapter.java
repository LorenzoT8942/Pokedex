package it.lorenzotanzi.pokedex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import it.lorenzotanzi.pokedex.cache.FromUrlToBitmap;
import it.lorenzotanzi.pokedex.interfaces.SelectMode;

public class PokemonRvAdapter extends RecyclerView.Adapter<PokemonRvAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener, Filterable {

    private Map<String, String> colors = new HashMap<>();
    private int pokemonItemLayout;
    private Context context;
    private List<Pokemon> pokemonList;
    private List<Pokemon> supportPokemonList; // needed for filter research
    private SelectMode mListener;
    private SparseBooleanArray selectedList = new SparseBooleanArray();
    private List<Pokemon> favorPkmnList = new ArrayList<>(); // need for not sure choices
    private List<Pokemon> supportFavorPkmnList; // need for confermed choices - removed redundant initializer
    private PokemonDao pokemonDao;

    private int orientation;

    PokemonRvAdapter(int layoutId, Context context, int orientation){
        pokemonItemLayout = layoutId;
        this.context = context;
        mListener = (SelectMode) context; /* MainActivity */
        PokemonRoomDatabase db = PokemonRoomDatabase.getDatabase(this.context);
        pokemonDao = db.pokemonDao();

        this.orientation = orientation;

        supportFavorPkmnList = pokemonDao.getAllFavorites(true);

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

    void setPokemonList(List<Pokemon> pokemons){
        pokemonList = pokemons;

        /* new add necessary for filter search */
        supportPokemonList = new ArrayList<>(pokemonList);

        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PokemonRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pokemon_detail, parent, false);
        cv.setOnClickListener(this);
        cv.setOnLongClickListener(this);

        reordering();

        return new ViewHolder(cv);
    }

    @SuppressLint({"CheckResult", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull PokemonRvAdapter.ViewHolder viewHolder, final int position) {

        Log.d("ON BIND VIEW HOLDER","POKEMON RV ADAPTER");

        TextView tv_pkmn_num = viewHolder.tv_pkmn_num;
        TextView tv_pkmn_name = viewHolder.tv_pkmn_name;
        ImageView iv_pkmn_type1 = viewHolder.iv_pkmn_type1;
        ImageView iv_pkmn_type2 = viewHolder.iv_pkmn_type2;
        ImageView iv_pkmn_icon = viewHolder.iv_pkmn_icon;
        View cardView = viewHolder.cardView;
        ImageView iv_pkmn_status = viewHolder.iv_pkmn_status; // new add


       String idString = pokemonList.get(position).getPkmnNum().toString();
       String pkmnNameString = pokemonList.get(position).getPkmnName();
       String type1str = pokemonList.get(position).getType1();
       String type2str = pokemonList.get(position).getType2();
       String type1col = colors.get(type1str);

        iv_pkmn_icon.setImageResource(R.drawable.pokeball);


        // onClickListener sull'immagine pokemon per accedere tramite intent alla galleria
        iv_pkmn_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, GalleryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("PKID",pokemonList.get(position).getPkmnNum().toString());
                bundle.putString("PKNAME",pokemonList.get(position).getPkmnName());
                bundle.putString("PKTYPE1",pokemonList.get(position).getType1());
                bundle.putString("PKTYPE2",pokemonList.get(position).getType2());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });


        //final String imgPkmnUrl = pokemonList.get(position).getImg();

        Picasso.get()
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonList.get(position).getPkmnNum() + ".png")
                .placeholder(R.drawable.pokeball)
                .error(R.drawable.pokeball)
                .into(iv_pkmn_icon);

          /* if pokemon's image not in cache then download it, else load it into the pokemon's ImageView */
//        File pokeImg = new File(context.getCacheDir() + "/pokemons" + "/" + pokemonList.get(position).getPkmnName() + ".png");
//        if(!pokeImg.exists()) {
//            new FromUrlToBitmap(iv_pkmn_icon, position, context, pokemonList, 1).execute(imgPkmnUrl);
//        }else{
//            Glide.with(context).load(pokeImg).into(iv_pkmn_icon);
//        }

        //STRING ADJUSTMENTS
        if (Integer.parseInt(idString) < 10) idString = new StringBuilder().append("#00").append(idString).toString();
        else if (Integer.parseInt(idString) < 100) idString = new StringBuilder().append("#0").append(idString).toString();
        else idString = new StringBuilder().append("#").append(idString).toString();
        pkmnNameString = pkmnNameString.substring(0, 1).toUpperCase() + pkmnNameString.substring(1);

        //BINDING OF POKEMON INFO AND CREATION OF GRADIENT BACKGROUND
        tv_pkmn_num.setText(idString);
        tv_pkmn_name.setText(pkmnNameString);

        //SETTING IMAGE VIEW OF TYPE 1
        String _type1str = type1str.substring(0,1).toLowerCase() + type1str.substring(1);
        int id = context.getResources().getIdentifier(_type1str , "drawable", context.getPackageName());
        iv_pkmn_type1.setImageResource(id);
        Log.d("ADAPTER", "Pokemon " + idString + "drawable 1 id:" + Integer.toString(id));

        /* needed in order to discriminate favorite pokemons from normal pokemon */
        if(!pokemonList.get(position).getFavorite()) {
            iv_pkmn_status.setImageResource(R.drawable.ic_pkm_free);
        } else{
            iv_pkmn_status.setImageResource(R.drawable.ic_pkm_capt);
        }


        //SE IL POKEMON IN POSIZIONE position HA UN TIPO 2 ALLORA VIENE SETTATO IL TESTO DELLA TEXT VIEW
        //tv_pkmn_type2 E VIENE CREATA UNA NUOVA ISTANZA DI GradientDrawable CHE VERRA' USATA COME
        //BACKGROUND DELLA CARDVIEW -- /* STESSO APPROCCIO PER FavoritePokemonRvAdapter */
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
            /*SE INVECE IL POKEMON HA UN SOLO TIPO VIENE SETTATA LA IMAGE VIEW iv_pkmn_type2 COME "INVISIBLE"
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
        return pokemonList == null? 0 : pokemonList.size();
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

                    if(poke.getPkmnName().toLowerCase().contains(filterPattern) && poke.getPkmnName()
                            .startsWith(String.valueOf(filterPattern.charAt(0)))){

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

            pokemonList.clear();
            pokemonList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_pkmn_icon;
        TextView tv_pkmn_num;
        TextView tv_pkmn_name;
        ImageView iv_pkmn_type1;
        ImageView iv_pkmn_type2;
        View cardView;
        ImageView iv_pkmn_status;



        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_pkmn_icon = itemView.findViewById(R.id.iv_pkmn_icon);
            tv_pkmn_name = itemView.findViewById(R.id.tv_pkmn_name);
            tv_pkmn_num = itemView.findViewById(R.id.tv_pkmn_num);
            iv_pkmn_type1 = itemView.findViewById(R.id.iv_pkmn_type1);
            iv_pkmn_type2 = itemView.findViewById(R.id.iv_pkmn_type2);
            cardView = itemView.findViewById(R.id.cl_card);
            iv_pkmn_status = itemView.findViewById(R.id.iv_pkmn_status); /* new add */

        }

    }

    /* if LongPress previously then repeat onLongClick actions */
    @Override
    public void onClick(View v) {
        if(selectedList.size() > 0){
            onLongClick(v);
        }else{
            final int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
            Pokemon pokemon = pokemonList.get(position);
            Intent intent = new Intent(context, Details.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", pokemonList.get(position).getPkmnName());
            bundle.putString("ID", pokemonList.get(position).getPkmnNum().toString());
            bundle.putString("Type1", pokemonList.get(position).getType1());
            bundle.putString("Type2", pokemonList.get(position).getType2());
            bundle.putString("Img", pokemonList.get(position).getImg());
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

            if( orientation == Configuration.ORIENTATION_PORTRAIT) {

                if (!supportFavorPkmnList.contains(pokemonList.get(position))) {

                    v.setSelected(false);
                    selectedList.delete(position);

                    pokemonList.get(position).setFavorites(false);
                    favorPkmnList.remove(pokemonList.get(position));

                }
            }else{
                if (!pokemonDao.getFavorite(pokemonList.get(position).getPkmnName())) {

                    v.setSelected(false);
                    selectedList.delete(position);

                    pokemonList.get(position).setFavorites(false);
                    favorPkmnList.remove(pokemonList.get(position));

                }
            }

        }else{

            if(orientation == Configuration.ORIENTATION_PORTRAIT) {

                if (!supportFavorPkmnList.contains(pokemonList.get(position))) {

                    v.setSelected(true);
                    selectedList.put(position, true);

                    pokemonList.get(position).setFavorites(true);
                    favorPkmnList.add(pokemonList.get(position));
                }
            }else{
                if (!pokemonDao.getFavorite(pokemonList.get(position).getPkmnName())) {

                    v.setSelected(true);
                    selectedList.put(position, true);

                    pokemonList.get(position).setFavorites(true);
                    favorPkmnList.add(pokemonList.get(position));
                }
            }

        }

        /* thanks to this menu doesn't appear even if client touch on pokemon added to the favorites */
        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (mListener != null && !supportFavorPkmnList.contains(pokemonList.get(position))) {
                mListener.onSelect(selectedList.size());
            }
        }else{
            if (mListener != null && !pokemonDao.getFavorite(pokemonList.get(position).getPkmnName())) {
                mListener.onSelect(selectedList.size());
            }
        }
        notifyDataSetChanged();
        return true;
    }


    public void deselectAll() {

        for(int index = 0; index < pokemonList.size(); index++){
            if(favorPkmnList.contains(pokemonList.get(index))){
                pokemonList.get(index).setFavorites(false);
            }
        }

        favorPkmnList.removeAll(favorPkmnList);
        selectedList.clear();
        notifyDataSetChanged();
    }



    public void fillFavorPkmonList(){

        /* thanks to allow main-thread queries it work correctly */

        for(int index = 0; index < favorPkmnList.size(); index++){
            supportFavorPkmnList.add(favorPkmnList.get(index));
            pokemonDao.setFavorite(favorPkmnList.get(index));
        }
        favorPkmnList.removeAll(favorPkmnList);
        selectedList.clear();

    }


    public List<Pokemon> chosenFavorites(){
        return supportFavorPkmnList;
    }


    /* delete double and update pokemonList */
    public void reordering(){

        for(int position = 0; position < supportFavorPkmnList.size(); position++){
            for(int index = 0; index < pokemonList.size(); index++){
                if(supportFavorPkmnList.get(position).getPkmnName().equals(pokemonList.get(index).getPkmnName())){
                    pokemonList.remove(index);
                    pokemonList.add(index, supportFavorPkmnList.get(position));
                    break;
                }
            }
        }

    }

    public List<Pokemon> getPokemonList(){
        return pokemonList;
    }

    public List<Pokemon> getFavorPkmnList(){
        return favorPkmnList;
    }

    public SparseBooleanArray getSelectedList(){
        return selectedList;
    }

    public void setFavorPkmnList(List<Pokemon> pokemons){
        favorPkmnList = pokemons;
    }

    public void setSparseBooleanArray(SparseBooleanArray array){
        selectedList = array;
    }

    public void setOrientation(int currOrientation){
        orientation = currOrientation;
    }

}
