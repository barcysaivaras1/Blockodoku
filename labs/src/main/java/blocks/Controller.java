package blocks;

import blocks.BlockShapes.Piece;
import blocks.BlockShapes.PixelLoc;
import blocks.BlockShapes.Sprite;
import blocks.BlockShapes.SpriteState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class Controller extends MouseAdapter {
    GameView view;
    ModelInterface model;
    Palette palette;
    JFrame frame;
    Sprite selectedSprite = null;
    Piece ghostShape = null;
    String title = "Blocks Puzzle";
    boolean gameOver = false;

    public Controller(GameView view, ModelInterface model, Palette palette, JFrame frame) {
        this.view = view;
        this.model = model;
        this.palette = palette;
        this.frame = frame;
        frame.setTitle(title);
        // force palette to do a layout
        palette.doLayout(view.margin, view.margin + ModelInterface.height * view.cellSize, view.paletteCellSize);
        System.out.println("Palette layout done: " + palette.sprites);
    }

    public void mousePressed(MouseEvent e) {
        // just call the model to try a piece selection given
        // this coordinate, and any other details such as margin and cell size
        // implementation of this method is provided, but you need to make the other controller methods work
        // see todos below
        System.out.println("Mouse pressed: " + e);
        PixelLoc loc = new PixelLoc(e.getX(), e.getY());
        selectedSprite = palette.getSprite(loc, view.paletteCellSize);
        if (selectedSprite == null) {
            return;
        }
        selectedSprite.state = SpriteState.IN_PLAY;
        System.out.println("Selected sprite: " + selectedSprite);
        view.repaint();
    }

    public void mouseDragged(MouseEvent e) {
        // todo: implement
        // if a sprite is selected, update its location to the mouse location
        // and repaint the view
        if (selectedSprite == null) {
            return;
        }
        //int gx = (px - margin + cellSize / 2) / cellSize;
        //We need to find out which cell the mouse is in
        int cellX = (e.getX() - view.margin + view.cellSize/2) / view.cellSize;
        int cellY = (e.getY() - view.margin + view.cellSize/2) / view.cellSize;
        //System.out.println("CellX: " + cellX + " CellY: " + cellY);

        //Create a new shape with cells that are adjusted to the mouse grid location
        BlockShapes.Shape s = new BlockShapes.Shape();
        BlockShapes.Shape d = new BlockShapes.Shape();
        for (var cell : selectedSprite.shape) {
            s.add(new BlockShapes.Cell(cell.x(), cell.y()));
            d.add(new BlockShapes.Cell(cellX+cell.x(), cellY+cell.y()));
        }
        Sprite ghost = new Sprite(s, e.getX(), e.getY());
        Piece viewGhost = new Piece(d, new BlockShapes.Cell(cellX, cellY));

        //view.poppableRegions = model.getPoppableRegions();

        //Snap the sprite to the grid
        //Only show the ghostShape if it can be placed
        if(model.canPlace(ghost.snapToGrid(view.margin, view.cellSize))){
            ghostShape = ghost.snapToGrid(view.margin, view.cellSize);
            view.ghostShape = viewGhost;
            System.out.println("Ghost Shape: " + ghostShape);
            view.poppableRegions = model.getPoppableRegions(ghostShape);
            //System.out.println("Number of Poppable Regions: " + view.poppableRegions.size());
        }
        else{
            //Remove the ghostShape if it cannot be placed
            ghostShape = null;
            view.ghostShape = null;
            view.poppableRegions = null;
        }
        selectedSprite.px = e.getX();
        selectedSprite.py = e.getY();
        view.repaint();
    }

    public void mouseReleased(MouseEvent e) {
        // todo: implement
        // Check if a sprite is selected and if it is over a valid location
        // If it is, place the sprite in the model and update the view
        // If the sprite is not placed, return it to the palette
        if (selectedSprite == null) {
            return;
        }

        //Check if selected sprite is in pallete
        if(ghostShape == null){
            selectedSprite.state = SpriteState.IN_PALETTE;
            view.repaint();
            return;
        }

        //Check if we can place the shape, if so, place it
        piecePlacement(ghostShape,selectedSprite);

        //Replenish the palette if it is empty
        replenishPalette();

        //Check if a region is complete and remove it
        regionCompletion();

        //Check if the game is over
        isItGameOver();

        reset();
    }

    private void isItGameOver(){
        if(model.isGameOver(palette.getShapesToPlace())){
            gameOver = true;
        }
    }

    private void reset(){
        // reset values after placing shape
        ghostShape = null;
        view.ghostShape = null;
        selectedSprite = null;
        // update the title with the score and whether the game is over
        frame.setTitle(getTitle());
        view.repaint();
    }

    private void piecePlacement(Piece shape, Sprite palleteSprite){
        System.out.println("Placing piece: " + shape);
        if (model.canPlace(shape)) {
            System.out.println("We have placed the piece");
            model.place(shape);
            palleteSprite.state = SpriteState.PLACED;
            palette.getSprites().removeIf(sprite -> sprite.state == SpriteState.PLACED);
            ghostShape = null;
            view.ghostShape = null;
        }
    }

    private void regionCompletion(){
        if(!view.poppableRegions.isEmpty()) {
            ArrayList<BlockShapes.Shape> regionsToRemove = new ArrayList<>();
            regionsToRemove.addAll(view.poppableRegions);
            for(BlockShapes.Shape region : regionsToRemove){
                model.remove(region);
            }
            view.poppableRegions = null;
        }
    }

    private void replenishPalette(){
        if(palette.getSprites().isEmpty()){
            palette.replenish();
            palette.doLayout(view.margin, view.margin + ModelInterface.height * view.cellSize, view.paletteCellSize);
        }
    }

    private String getTitle() {
        // make the title from the base title, score, and add GameOver if the game is over
        String title = this.title + " Score: " + model.getScore();
        if (gameOver) {
            title += " Game Over!";
        }
        return title;
    }

    private void playRandomPieceInARandomLocation() {
        if (gameOver) {
            return;
        }



        //Make a list of sprites that CAN be placed
        ArrayList<Integer> validSpritesIndex = new ArrayList<>();
        ArrayList<BlockShapes.Shape> palleteShapes = new ArrayList<>();

        for (Sprite sprite : palette.getSprites()) {
            palleteShapes.add(sprite.shape);
            if(!(model.isGameOver(palleteShapes))){
                validSpritesIndex.add(palette.getSprites().indexOf(sprite));
            }
            palleteShapes.remove(0);
        }

        int randomNum = (int) (Math.random() * validSpritesIndex.size());
        Integer spriteIndex =  validSpritesIndex.get(randomNum);

        System.out.println("Palette size: " + palette.getSprites().size());
        System.out.println("Palette " + palette.getSprites());

        //Find all valid locations to place this sprite
        ArrayList<BlockShapes.Cell> validLocations = new ArrayList<>();
        for(int i = 0; i < ModelInterface.width; i++){
            for(int j = 0; j < ModelInterface.height; j++){
                System.out.println("i: " + i + " j: " + j);
                if(model.canPlace(new Piece(palette.getSprites().get(spriteIndex).shape, new BlockShapes.Cell(i, j)))){
                    System.out.println("Adding to valid locations");
                    validLocations.add(new BlockShapes.Cell(i, j));
                }
            }
        }
        randomNum = (int) (Math.random() * validLocations.size());
        BlockShapes.Cell location = validLocations.get(randomNum);

        Piece randomPiece = new Piece(palette.getSprites().get(spriteIndex).shape, location);

        view.poppableRegions = model.getPoppableRegions(randomPiece);

        piecePlacement(randomPiece, palette.getSprites().get(spriteIndex));
        replenishPalette();
        regionCompletion();
        isItGameOver();



        reset();
    }


    private Timer timer;

    public void startContinuousPlay() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    playRandomPieceInARandomLocation();
                } else {
                    timer.stop();
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //ModelInterface model = new ModelSet();
        ModelInterface model = new Model2dArray();
        Palette palette = new Palette();
        GameView view = new GameView(model, palette);
        Controller controller = new Controller(view, model, palette, frame);
        view.addMouseListener(controller);
        view.addMouseMotionListener(controller);

        //AI play button
        view.button.addActionListener(e -> {
            controller.startContinuousPlay();
        });

        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
