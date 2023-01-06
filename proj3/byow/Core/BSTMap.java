package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;


public class BSTMap {
    private long SEED;
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final double SPLITLOW = 0.4;
    private static final double SPLITHEIGH = 0.6;
    private static final double ROOMLOW = 0.3;
    private static final double ROOMHEIGH = 0.7;
    private Random RANDOM;
    public TETile[][] tiles;
    private static final int DEEP = 4;
    private node root;

    public BSTMap(long seed) {
        SEED=seed;
        RANDOM = new Random(SEED);
        tiles = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                tiles[i][j] = Tileset.NOTHING;
        createWorld();
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    private void createWorld() {
        root = new node(0.0, 0.0, (double) WIDTH, (double) HEIGHT, null, 0);
        root.createRoom();
    }

    private class node {
        private boolean direct;
        private node parent;
        private node[] child = new node[2];
        private RectHV shape;
        private RectHV room;
        private int deep;

        public node(Double x0, Double y0, Double x1, Double y1, node parent, int deep) {
            shape = new RectHV(x0, y0, x1, y1);
            this.parent = parent;
            this.deep = deep;
            if (parent == null)
                direct = true;
            else
                direct = !parent.direct;
        }

        private void createRoom() {
            if (this.deep < DEEP) {
                if (direct) {
                    double low = shape.xmin() + (shape.xmax() - shape.xmin()) * SPLITLOW;
                    double heigh = shape.xmin() + (shape.xmax() - shape.xmin()) * SPLITHEIGH;
                    double x = nextDouble(low, heigh);
                    child[0] = new node(shape.xmin(), shape.ymin(), x, shape.ymax(), this, deep + 1);
                    child[1] = new node(x, shape.ymin(), shape.xmax(), shape.ymax(), this, deep + 1);
                    child[0].createRoom();
                    child[1].createRoom();
                    connect();
                } else {
                    double low = shape.ymin() + (shape.ymax() - shape.ymin()) * SPLITLOW;
                    double heigh = shape.ymin() + (shape.ymax() - shape.ymin()) * SPLITHEIGH;
                    double y = nextDouble(low, heigh);
                    child[0] = new node(shape.xmin(), shape.ymin(), shape.xmax(), y, this, deep + 1);
                    child[1] = new node(shape.xmin(), y, shape.xmax(), shape.ymax(), this, deep + 1);
                    child[0].createRoom();
                    child[1].createRoom();
                    connect();
                }
            } else {
                double width = shape.xmax() - shape.xmin();
                double height = shape.ymax() - shape.ymin();
                double roomWidth = nextDouble(width * ROOMLOW, width * ROOMHEIGH);
                double roomHeight = nextDouble(height * ROOMLOW, height * ROOMHEIGH);
                Point2D center = new Point2D((shape.xmax() + shape.xmin()) / 2, (shape.ymax() + shape.ymin()) / 2);
                room = new RectHV(center.x() - roomWidth / 2, center.y() - roomHeight / 2, center.x() + roomWidth / 2, center.y() + roomHeight / 2);
                for (int i = (int) room.xmin(); i < room.xmax(); i++) {
                    for (int j = (int) room.ymin(); j < room.ymax(); j++) {
                        if (i == (int) room.xmin() || i == (int) room.xmax() || j == (int) room.ymin() || j == (int) room.ymax()) {
                            if (tiles[i][j] == Tileset.NOTHING)
                                tiles[i][j] = Tileset.WALL;
                        } else
                            tiles[i][j] = Tileset.FLOOR;
                    }
                }
            }
        }

        private void connect() {
            if (direct) {
                int y = (int) ((shape.ymax() + shape.ymin()) / 2);
                int start = (int) ((child[0].shape.xmax() + child[0].shape.xmin()) / 2);
                int end = (int) ((child[1].shape.xmax() + child[1].shape.xmin()) / 2);
                for (int i = start; i < end; i++) {
                    tiles[i][y] = Tileset.FLOOR;
                    if (tiles[i][y - 1] == Tileset.NOTHING)
                        tiles[i][y - 1] = Tileset.WALL;
                    if (tiles[i][y + 1] == Tileset.NOTHING)
                        tiles[i][y + 1] = Tileset.WALL;
                }
            } else {
                int x = (int) ((shape.xmax() + shape.xmin()) / 2);
                int start = (int) ((child[0].shape.ymax() + child[0].shape.ymin()) / 2);
                int end = (int) ((child[1].shape.ymax() + child[1].shape.ymin()) / 2);
                for (int i = start; i < end; i++) {
                    tiles[x][i] = Tileset.FLOOR;
                    if (tiles[x - 1][i] == Tileset.NOTHING)
                        tiles[x - 1][i] = Tileset.WALL;
                    if (tiles[x + 1][i] == Tileset.NOTHING)
                        tiles[x + 1][i] = Tileset.WALL;
                }
            }
        }

    }

    public static void main(String args[]) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        BSTMap bstMap = new BSTMap(0);
        // initialize tiles
        TETile[][] world = bstMap.getTiles();
        ter.renderFrame(world);
    }

    private double nextDouble(double min, double max) {
        return RANDOM.nextDouble() * (max - min) + min;
    }
}
