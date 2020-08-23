package it.lorenzotanzi.pokedex;

public class Move {

    public String moveName;
    public int level;

    public Move(String name, Integer lvl) {
        this.moveName = name;
        this.level = lvl;
    }

    public String getMoveName() {
        return moveName;
    }

    public void setMoveName(String moveName) {
        this.moveName = moveName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
