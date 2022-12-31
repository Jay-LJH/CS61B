package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 307;
    private static final Random RANDOM = new Random(SEED);
    /**
     * @param x  lower left corner point x of the hexagon
     * @param y  lower left corner point y of the hexagon
     */
    private static void addHexagon(TETile[][] tiles,int x,int y,int size,TETile tile){
        if(x-size+1<0 || x+2*size-2>=WIDTH|| y+2*size-1>=HEIGHT || y<0 ){
            System.out.println("Out of bound");
            return;
        }
        for(int i=0;i<size;i++){
            for(int j=(-i);j<size+i;j++){
                tiles[x+j][y+i]=tile;
            }
        }
        y=y+size;
        for(int i=0;i<size;i++){
            for(int j=(-size+i+1);j<2*size-i-1;j++){
                tiles[x+j][y+i]=tile;
            }
        }
    }
    private static void RandomHexWorld(TETile[][] world,int size){
        int x=WIDTH/2-size/2;
        int y=0;
        for(int i=0;i<3;i++){
            for(int j=0;j<5-i;j++){
                addHexagon(world,x-i*(2*size-1),y+j*(2*size)+size*i,size,RandomWorldDemo.randomTile());
                addHexagon(world,x+i*(2*size-1),y+j*(2*size)+size*i,size,RandomWorldDemo.randomTile());
            }
        }
    }
    public static void main(String[] args){
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        RandomHexWorld(world,4);
        ter.renderFrame(world);
    }
}
