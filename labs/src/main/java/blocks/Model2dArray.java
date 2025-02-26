package blocks;

/**
 * Logical model for the Blocks Puzzle
 * This handles the game logic, such as the grid, the pieces, and the rules for
 * placing pieces and removing lines and subgrids.
 * <p>
 * Note this has no dependencies on the UI or the game view, and no
 * concept of pixel-space or screen coordinates.
 * <p>
 * The standard block puzzle is on a 9x9 grid, so all placeable shapes will have
 * cells in that range.
 */

import blocks.BlockShapes.Cell;
import blocks.BlockShapes.Piece;
import blocks.BlockShapes.Shape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model2dArray extends State2dArray implements ModelInterface {
    List<Shape> regions = new RegionHelper().allRegions();
    int consecutiveCompletions = 1;

    public Model2dArray() {
        grid = new boolean[width][height];
        // initially all cells are empty (false) - they would be by default anyway
        // but this makes it explicit
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = false;
            }
        }
    }

    public int getScore() {
        return score;
    }


    // interestingly, for canPlace we could also use sets to store the occupied cells
    // and then check if the shape's cells intersect with the occupied cells

    public boolean canPlace(Piece piece) {
        // todo: implement
        for(Cell cell : piece.cells()){
            if(cell.x() >= 9 || cell.y() >= 9 || cell.x() < 0 || cell.y() < 0){
                return false;
            }
            if(grid[cell.x()][cell.y()]){
                return false;
            }
        }
        // check if the shape can be placed at this loc
        return true;
    }

    @Override
    public void place(Piece piece) {
        // todo: implement
        //Places a piece on the grid

        if(!getPoppableRegions(piece).isEmpty()){
            for(Shape region : getPoppableRegions(piece)){
                score += 10  * consecutiveCompletions;
                consecutiveCompletions++;
                remove(region);
            }
        }else {
            consecutiveCompletions = 1;
        }

        for(Cell cell : piece.cells()){
            grid[cell.x()][cell.y()] = true;
        }
        isComplete(piece.shape());
    }

    @Override
    public void remove(Shape region) {
        // todo: implement
        //Removes a region from the grid
        for(Cell cell : region){
            grid[cell.x()][cell.y()] = false;
        }
    }

    public boolean isComplete(Shape region) {
        // todo: implement
        // check if the shape is complete, i.e. all cells are occupied
        for(Cell cell : region){
            if(!grid[cell.x()][cell.y()]){
                return false;
            }
        }
        return true;
    }

    private boolean wouldBeComplete(Shape region, List<Cell> toAdd) {
        // todo: implement
        // check if the shape is complete, i.e. all cells are occupied
        for(Cell cell : region){
            if(!grid[cell.x()][cell.y()] && !toAdd.contains(cell)){
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isGameOver(List<Shape> palettePieces) {
        // todo: implement
        // if any shape in the palette can be placed, the game is not over

        for(Shape shape : palettePieces){
            if(canPlaceAnywhere(shape)){
                return false;
            }
        }
        return true;
    }

    public boolean canPlaceAnywhere(Shape shape) {
        // todo: implement
        // check if the shape can be placed anywhere on the grid
        // by checking if it can be placed at any loc
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (canPlace(new Piece(shape, new Cell(i, j)))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Shape> getPoppableRegions(Piece piece) {
        // todo: implement
        // iterate over the regions
        List<Shape> poppable = new ArrayList<>();
        for (Shape region : regions) {
            // check if the region would be complete if the piece was placed
            if (wouldBeComplete(region, piece.cells())) {
                poppable.add(region);
            }
        }
        return poppable;
    }

    @Override
    public Set<Cell> getOccupiedCells() {
        // todo: implement
        //Return a set of cells that are occupied
        Set<Cell> occupiedCells = new HashSet<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j]) {
                    occupiedCells.add(new Cell(i, j));
                }
            }
        }
        return occupiedCells;
    }
}

