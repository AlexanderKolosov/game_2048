package game;

import java.util.*;
//import java.util.Arrays;


public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    public int score;
    public int maxTile;
    private Stack previousStates = new Stack();
    private Stack previousScores = new Stack();
    private boolean isSaveNeeded = true;

    public Model() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        /*gameTiles = new Tile[][]{
                {new Tile(2), new Tile(4), new Tile(2), new Tile(16)},
                {new Tile(4), new Tile(2), new Tile(4), new Tile(2)},
                {new Tile(2), new Tile(4), new Tile(8), new Tile(16)},
                {new Tile(4), new Tile(2), new Tile(8), new Tile(2)},
        };*/
        score = 0;
        maxTile = 0;
        resetGameTiles();
    }

    public void resetGameTiles(){
        for ( int i = 0; i < gameTiles.length; i++ ){
            for ( int y = 0; y < gameTiles[i].length; y++ ){
                gameTiles[i][y] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove(){
        if (getEmptyTiles().size() > 0) return true;
        for ( int i = 0; i < gameTiles.length; i++ ){
            for (int y = 1; y < gameTiles[i].length; y++){
                if (i == 0){
                    if (gameTiles[i][y].value == gameTiles[i][y-1].value)return true;
                }else {
                    if (gameTiles[i][y].value == gameTiles[i][y-1].value)return true;
                    if (gameTiles[i][y].value == gameTiles[i-1][y].value)return true;
                }
            }
        }
        return false;
    }

    private void addTile(){
        List<Tile> list = this.getEmptyTiles();
        if (list.size() > 0) {
            Tile tile = list.get((int) (Math.random() * list.size()));
            for ( int i = 0; i < gameTiles.length; i++ ) {
                for ( int y = 0; y < gameTiles[i].length; y++ ) {
                    if (gameTiles[i][y].equals(tile))
                        gameTiles[i][y].value = (int) (Math.random() < 0.9 ? 2 : 4);
                }
            }
        }
    }

    private List<Tile> getEmptyTiles(){
        List<Tile> list = new ArrayList<>();
        for ( int i = 0; i < gameTiles.length; i++ ){
            for ( int y = 0; y < gameTiles[i].length; y++ ){
                if (gameTiles[i][y].isEmpty()) {
                    list.add(gameTiles[i][y]);
                }
            }
        }
        return list;
    }

    private boolean compressTiles(Tile[] tiles){
        boolean changes = false;
        for ( int i = 0; i < tiles.length; i++ ){
            if (tiles[i].value == 0 && i != tiles.length-1) {
                for ( int z = i + 1; z < tiles.length; z++ ) {
                    if (!tiles[z].isEmpty()) {
                        tiles[i].value = tiles[z].value;
                        tiles[z].value = 0;
                        changes = true;
                        break;
                    }
                }
            }
        }
        return changes;
    }

    private boolean mergeTiles(Tile[] tiles){
        boolean changes = false;
        for ( int i = 1; i < tiles.length; i++ ) {
            if (tiles[i].value == tiles[i-1].value && tiles[i].value > 0) {
                tiles[i-1].value *= 2;
                if (tiles[i - 1].value > maxTile){
                    maxTile = tiles[i-1].value;
                }
                score += tiles[i-1].value;
                tiles[i].value = 0;
                changes = true;
                compressTiles(tiles);
            }
        }
        return changes;
    }

    public void left(){
        if (isSaveNeeded)saveState(gameTiles);
        boolean changes = false;
        boolean control;
        for (Tile[] tile : gameTiles ){
            control = compressTiles(tile);
            if (changes || control ) changes = true;
            control = mergeTiles(tile);
            if (changes || control ) changes = true;
        }
        if (changes) addTile();
        if (!isSaveNeeded)isSaveNeeded = true;
    }

    public void down(){
        saveState(gameTiles);
        gameTiles = rotateTile();
        left();
        for (int i = 0; i < 3; i++) {
            gameTiles = rotateTile();
        }
    }

    public void right(){
        saveState(gameTiles);
        gameTiles = rotateTile();
        gameTiles = rotateTile();
        left();
        gameTiles = rotateTile();
        gameTiles = rotateTile();
    }

    public void up(){
        saveState(gameTiles);
        for (int i = 0; i < 3; i++) {
            gameTiles = rotateTile();
        }
        left();
        gameTiles = rotateTile();
    }

    public Tile[][] rotateTile(){
        Tile[][] rotatedGameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH]/*[4][4]*/;
        for (int r = 0; r < rotatedGameTiles.length; r++){
            for (int c = 0; c < rotatedGameTiles[r].length; c++) {
                rotatedGameTiles[c][rotatedGameTiles.length-1-r] = this.gameTiles[r][c];
            }
        }
        return rotatedGameTiles;
    }

    private void saveState(Tile[][] tile){
        Tile[][] savedTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int r = 0; r < tile.length; r++){
            for (int c = 0; c < tile[r].length; c++){
                savedTiles[r][c] = new Tile(tile[r][c].value);
            }
        }
        previousStates.push(savedTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback(){
        if (!previousScores.empty() && !previousStates.empty()) {
            gameTiles = (Tile[][]) previousStates.pop();
            score = (int) previousScores.pop();
        }
    }

    public void randomMove(){
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n){
            case 0: left();break;
            case 1: right();break;
            case 2: up();break;
            case 3: down();break;
        }
    }

    public int countTileWeight(Tile[][] tiles){
        int result = 0;
        for (int r = 0; r < tiles.length; r++){
            for (int c = 0; c < tiles[r].length; c++){
                result += tiles[r][c].value;
            }
        }
        return result;
    }

    public boolean hasBoardChanged(){
        int gameTilesWeight = countTileWeight(gameTiles);
        int stakTileWeight = countTileWeight((Tile[][])previousStates.peek());
        if (gameTilesWeight != stakTileWeight) return true;
        return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move){
        int emptyTiles = 0;
        int value = 0;
        move.move();
        if (hasBoardChanged()) {
            emptyTiles = getEmptyTiles().size();
            value = score;
        }else {
            emptyTiles = -1;
            score = 0;
        }
        MoveEfficiency me = new MoveEfficiency(emptyTiles, value, move);
        rollback();
        return me;
    }

    public void autoMove(){
        PriorityQueue<MoveEfficiency> pq = new PriorityQueue<>(4, Collections.reverseOrder());
        pq.add(getMoveEfficiency(this::left));
        pq.add(getMoveEfficiency(this::right));
        pq.add(getMoveEfficiency(this::up));
        pq.add(getMoveEfficiency(this::down));
        pq.peek().getMove().move();
    }

    /*public static void main(String[] args) {
        Model model = new Model();
        model.left();
        for ( Tile[] t : model.gameTiles )
            System.out.println("Array" + Arrays.asList(t));
        System.out.println("=============================");

        for ( Tile[] t : model.gameTiles )
            System.out.println("Array" + Arrays.asList(t));

        System.out.println(model.canMove());
    }*/
}
