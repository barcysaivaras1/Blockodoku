package blocks;

import blocks.BlockShapes.Cell;
import blocks.BlockShapes.Piece;
import blocks.BlockShapes.Shape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelSet extends StateSet implements ModelInterface {

    Set<Cell> locations = new HashSet<>();
    List<Shape> regions = new RegionHelper().allRegions();
    int consecutiveCompletions = 1;

    // we need a constructor to initialise the regions
    public ModelSet() {
        super();
        initialiseLocations();
    }
    // method implementations below ...

    public int getScore() {
        return score;
    }

    private void initialiseLocations() {
        // having all grid locations in a set is in line with the set based approach
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                locations.add(new Cell(i, j));
            }
        }
    }

    @Override
    public boolean canPlace(Piece piece) {
        // can be placed if the cells are not occupied i.e. not in the occupiedCells set
        // though each one must be within the bounds of the grid
        // use a stream to check if all the cells are not occupied

        // todo: implement
        return piece.cells().stream().allMatch(cell -> !occupiedCells.contains(cell) && locations.contains(cell));
    }

    @Override
    public void place(Piece piece) {
        // todo: implement
        // add the cells in the Piece to the occupiedCells set
        // then remove all the poppable regions
        // increment the score as function of the regions popped

        if(getPoppableRegions(piece).size() > 0){
            for(Shape region : getPoppableRegions(piece)){
                score += 10  * consecutiveCompletions;
                consecutiveCompletions++;
                remove(region);
            }
        }
        else {
            consecutiveCompletions = 1;
        }
        occupiedCells.addAll(piece.cells());
    }

    @Override
    public void remove(Shape region) {
        // todo: implement
        // remove the cells from the occupiedCells set
        occupiedCells.removeAll(region);
    }

    @Override
    public boolean isComplete(Shape region) {
        // todo: implement
        // use a stream to check if all the cells in the region are occupied
        if(region.stream().allMatch(occupiedCells::contains)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isGameOver(List<Shape> palettePieces) {
        // todo: implement
        // if any shape in the palette can be placed, the game is not over
        // use a helper function to check whether an indiviual shape can be placed anywhere
        return !palettePieces.stream().anyMatch(shape -> canPlaceAnywhere(shape));
    }

    public boolean canPlaceAnywhere(Shape shape) {
        // todo: implement

        // check if the shape can be placed anywhere on the grid
        // by checking if it can be placed at any loc

        return locations.stream().anyMatch(cell -> canPlace(new Piece(shape, cell)));
    }

    @Override
    public List<Shape> getPoppableRegions(Piece piece) {
        // todo: implement

        // return the regions that would be popped if the piece is placed
        // to do this we need to iterate over the regions and check if the piece overlaps enough to complete it
        // i.e. we can make a new set of occupied cells and check if the region is complete
        // if it is complete, we add it to the list of regions to be popped
        ArrayList<Shape> regionReturn = new ArrayList<>();
        Set<Cell> newOccupiedCells = new HashSet<>(occupiedCells);
        newOccupiedCells.addAll(piece.cells());
        for (Shape region : regions) {
            if (region.stream().allMatch(newOccupiedCells::contains)) {
                regionReturn.add(region);
            }
        }
        return regionReturn;
        //return regions.stream().anyMatch(this::isComplete) ? List.of(regions.get(0)) : new ArrayList<>();

    }

    @Override
    public Set<Cell> getOccupiedCells() {
        // todo: implement
        //Return a set of cells that are occupied
        return occupiedCells;
    }
}
