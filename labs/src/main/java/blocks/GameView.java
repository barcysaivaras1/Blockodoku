package blocks;


import blocks.BlockShapes.Shape;
import blocks.BlockShapes.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

// class should work in a basic way as provided if all the todo's are implemented in the other classes
// though you need to provide or complete the implementations for the methods in todos below


public class GameView extends JComponent {
    ModelInterface model;
    Palette palette;
    int margin = 5;
    int shapeRegionHeight;
    int cellSize = 40;
    int paletteCellSize = 20;
    int shrinkSize = 30;
    Piece ghostShape = null;
    List<Shape> poppableRegions = null;
    JButton button;

    public GameView(ModelInterface model, Palette palette) {
        this.model = model;
        this.palette = palette;
        this.shapeRegionHeight = cellSize * ModelInterface.height / 2;
        button = new JButton("Play Random Turn");
        button.setBounds(0, ModelInterface.height * cellSize + 2 * margin + shapeRegionHeight, 200, 50);
        add(button);
    }

    private void paintShapePalette(Graphics g, int cellSize) {
        // paint a background colour
        // then get the list of current shapes from the palette
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(margin, margin + ModelInterface.height * cellSize, ModelInterface.width * cellSize, shapeRegionHeight);
        for (Sprite sprite : palette.getSprites()) {
            // todo: paint the sprite in the palette
            int y0 =  sprite.py;
            int x0 = sprite.px;
            for(var cell : sprite.shape){
                int x = x0;
                int y = y0;
                if (sprite.state == SpriteState.IN_PLAY) {
                    x = x + cell.x() * cellSize + margin;
                    y = y + cell.y() * cellSize + margin;
                    g.setColor(Color.BLUE);
                    g.fillRect(x ,y ,shrinkSize,shrinkSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(x ,y ,shrinkSize,shrinkSize);
                } else {
                    x = x + cell.x() * paletteCellSize;
                    y = y + cell.y() * paletteCellSize;
                    g.setColor(Color.BLUE);
                    g.fillRect(x ,y ,paletteCellSize,paletteCellSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(x ,y ,paletteCellSize,paletteCellSize);
                }

            }
        }
    }

    private void paintPoppableRegions(Graphics g, int cellSize) {
        // todo: implement
        //Colours the grid when a player hovers a region that when a shape is placed in it, it will be popped
        if (poppableRegions == null) {
            return;
        }
        for (Shape region : poppableRegions) {
            for (Cell cell : region) {
                int x = margin + cell.x() * cellSize;
                int y = margin + cell.y() * cellSize;
                g.setColor(new Color(99, 155, 69));
                g.fillRect(x, y, cellSize, cellSize);
                g.setColor(Color.YELLOW);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }

    }

    private void paintGhostShape(Graphics g, int cellSize) {
        // todo: implement
        //Colours the grid behind a shape to show where it will be placed
        if (ghostShape == null) {
            return;
        }
        for (Cell cell : ghostShape.shape()) {
            int x = margin + cell.x() * cellSize;
            int y = margin + cell.y() * cellSize;
            g.setColor(Color.CYAN);
            g.fillRect(x, y, cellSize, cellSize);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, cellSize, cellSize);
        }
    }

    private void paintGrid(Graphics g) {
        int x0 = margin;
        int y0 = margin;
        int width = ModelInterface.width * cellSize;
        int height = ModelInterface.height * cellSize;
        Set<Cell> occupiedCells = model.getOccupiedCells();
        g.setColor(Color.BLACK);
        g.drawRect(x0, y0, width, height);
        for (int x = 0; x < ModelInterface.width; x++) {
            for (int y = 0; y < ModelInterface.height; y++) {
                // todo: paint the cells that are occupied in a different colour
                if(occupiedCells.contains(new Cell(x, y))){
                    g.setColor(Color.GREEN);
                    g.fill3DRect(x0 + x * cellSize, y0 + y * cellSize, cellSize, cellSize, true);
                }
                else {
                    g.setColor(Color.WHITE);
                    g.fill3DRect(x0 + x * cellSize, y0 + y * cellSize, cellSize, cellSize, true);
                }
            }
        }
    }

    private void paintMiniGrids(Graphics2D g) {
        // for now we're going to do this based on the cellSize multiple
        int s = ModelInterface.subSize;
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);
        for (int x = 0; x < ModelInterface.width; x += s) {
            for (int y = 0; y < ModelInterface.height; y += s) {
                g.drawRect(margin + x * cellSize, margin + y * cellSize, s * cellSize, s * cellSize);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintGrid(g);
        paintMiniGrids((Graphics2D) g); // cosmetic
        paintGhostShape(g, cellSize);
        paintPoppableRegions(g, cellSize);
        paintShapePalette(g, cellSize);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                ModelInterface.width * cellSize + 2 * margin,
                ModelInterface.height * cellSize + 2 * margin + shapeRegionHeight
        );
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Clean Blocks");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ModelInterface model = new ModelSet();
        Shape shape = new ShapeSet().getShapes().get(0);
        Piece piece = new Piece(shape, new Cell(0, 0));
        Palette palette = new Palette();
        model.place(piece);
        frame.add(new GameView(model, palette));
        frame.pack();
        frame.setVisible(true);
    }
}
