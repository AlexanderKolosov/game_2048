package game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
    private Model model;
    private View view;
    private static final int WINNING_TILE = 2048;

    public Controller(Model model) {
        this.model = model;
        view = new View(this);
    }

    public Tile[][] getGameTiles(){
        return model.getGameTiles();
    }

    public int getScore(){
        return model.score;
    }

    public View getView() {
        return view;
    }

    public void resetGame(){
        model.score = 0;
        view.isGameLost = false;
        view.isGameWon = false;
        model.resetGameTiles();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) resetGame();
        if (e.getKeyCode() == KeyEvent.VK_Z) model.rollback();
        if (e.getKeyCode() == KeyEvent.VK_R) model.randomMove();
        if (e.getKeyCode() == KeyEvent.VK_A) model.autoMove();
        if (model.canMove() == false) view.isGameLost = true;
        if (view.isGameLost == false && view.isGameWon == false) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) model.left();
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT) model.right();
            else if (e.getKeyCode() == KeyEvent.VK_UP) model.up();
            else if (e.getKeyCode() == KeyEvent.VK_DOWN) model.down();
        }
        if (model.maxTile == WINNING_TILE) view.isGameWon = true;
        view.repaint();
    }
}
