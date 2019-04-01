package game;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        int result = Integer.compare(numberOfEmptyTiles,o.numberOfEmptyTiles);
        if (result != 0) return result;

        return Integer.compare(score,o.score);
    }

    /*@Override
    public int compareTo(Object o) {
        MoveEfficiency me = (MoveEfficiency) o;

        int result = numberOfEmptyTiles - me.numberOfEmptyTiles;
        if (result != 0) return result;

        return score - me.score;
    }*/
}
