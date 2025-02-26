package blocks;

import blocks.BlockShapes.*;

import java.util.ArrayList;
import java.util.List;

public class Palette {
    ArrayList<Shape> shapes = new ArrayList<>();
    List<Sprite> sprites;
    int nShapes = 3;

    public Palette() {
        shapes.addAll(new ShapeSet().getShapes());
        sprites = new ArrayList<>();
        replenish();
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    public ArrayList<Shape> getShapesToPlace() {
        // todo: implement
        ArrayList<Shape> shapesToPlace = new ArrayList<>();
        for (Sprite sprite : sprites) {
            if (sprite.state == SpriteState.IN_PALETTE) {
                shapesToPlace.add(sprite.shape);
            }
        }
        // return a list of shapes that are in the palette - could use streams to filter this
        return shapesToPlace;
    }

    public List<Sprite> getSprites() {
        return sprites;
    }

    // if we have a sprite that contains the point (px, py), return it
    // and the size of the cells - the sprite location is already in pixel coordinates
    public Sprite getSprite(PixelLoc mousePoint, int cellSize) {
        // todo: implement
        for (Sprite sprite : getSprites()) {
            if (sprite.contains(mousePoint, cellSize)) {
                return sprite;
            }
        }
        return null;
    }

    private int nReadyPieces() {
        int count = 0;
        for (Sprite sprite : sprites) {
            if (sprite.state == SpriteState.IN_PALETTE || sprite.state == SpriteState.IN_PLAY) {
                count++;
            }
        }
        System.out.println("nReadyPieces: " + count);
        return count;
    }

    public void doLayout(int x0, int y0, int cellSize) {
        // todo: implement

        // layout the sprites in the palette
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.get(i);
            sprite.px = x0 + i * (cellSize * 6);
            sprite.py = y0;
        }
    }

    public void replenish() {
        // todo: implement
        if (nReadyPieces() > 0) {
            return;
        }
        // clear the sprites and add new randomly selected shapes
        sprites.clear();
        for (int i = 0; i < nShapes; i++) {
            Shape shape = shapes.get((int) (Math.random() * shapes.size()));
            int px = 10 + i * 30;
            int py = 10;
            sprites.add(new Sprite(shape, px, py));
        }
        System.out.println("Replenished: " + sprites);
    }

    public static void main(String[] args) {
        Palette palette = new Palette();
        System.out.println(palette.shapes);
        System.out.println(palette.sprites);
        palette.doLayout(0, 0, 20);
        System.out.println(palette.sprites);
    }
}
