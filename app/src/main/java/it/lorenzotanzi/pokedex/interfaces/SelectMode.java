package it.lorenzotanzi.pokedex.interfaces;

/* it's necessary for communication among MainActivity and PokemonRvAdapter for Menu Management - same with favor variant */
public interface SelectMode {
    void onSelect(int size);
}
