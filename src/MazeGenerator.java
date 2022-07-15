public class MazeGenerator {
    UnionFind unionFind = new UnionFind();

    public void run(int n) {

        // creates all cells
        Cell[][] mazeMap = new Cell[n][n];
        initializeCells(mazeMap);

        // create a list of all internal walls, and links the cells and walls
        Wall[] walls = getWalls(mazeMap);

        createMaze(walls, mazeMap);

        printMaze(mazeMap);

    }

    public void createMaze(Wall[] walls, Cell[][] mazeMap) {
        // Creates the maze
        makeSets(mazeMap);
        makeMazeExits(mazeMap);
        generateMazeIteratively(walls);
    }

    private void makeMazeExits(Cell[][] mazeMap){
        // Creates the maze's opening and ending
        mazeMap[0][0].left.visible = false;
        mazeMap[mazeMap.length-1][mazeMap[0].length-1].right.visible = false;
    }

    // lmfao good times. Recursion is >>>
    private void generateMazeRecursively(Wall[] walls, int actualSize){
        // A recursive method to generate the maze
        if (actualSize < 1) return;
        int indexToRemove = pickRandomIndex(actualSize);
        Wall wallToBeRemoved = walls[indexToRemove];
        if (onlyOneSetRemains()) return; // Base Case
        if (checkCycleRule(wallToBeRemoved) && wallToBeRemoved.visible){
            removeWall(wallToBeRemoved);
        }
        arrayAdjuster(walls, indexToRemove, actualSize);
        generateMazeRecursively(walls, actualSize-1);
    }

    private void arrayAdjuster(Wall[] walls, int index, int actualSize){
        // Swaps the current index with the index the actualSize is pointing to
        Wall temp = walls[index];
        walls[index] = walls[actualSize-1];
        walls[actualSize-1] = temp;
    }
    
    private void generateMazeIteratively(Wall[] walls){
        // Generates the maze using a while loop
        int actualSize = walls.length-2;
        while (unionFind.numSets() > 1) {
            int randomWallIndex = (int) (Math.random() * actualSize);
            Wall wallToDelete = walls[randomWallIndex];
            // find if the cells next to the wall are in the same set or not.
            if (checkCycleRule(wallToDelete)) {
                removeWall(wallToDelete);
            }
            arrayAdjuster(walls, randomWallIndex, actualSize);
            actualSize--;
        }
    }

    private int pickRandomIndex(int wallsLength){
        // Picks random index
        return (int) (Math.random() * wallsLength);
    }

    private void removeWall(Wall wall){
        // Removes a wall and merges its adjacent cell sets
        wall.visible = false;
        unionFind.union(wall.first, wall.second);
    }

    private boolean isSafeToRemove(Wall wall){
        return checkCycleRule(wall) && onlyOneSetRemains();
    }

    private boolean checkCycleRule(Wall wall){
        // Checks if wall removal might cause cycles
        Cell c1 = wall.first;
        Cell c2 = wall.second;
        if (c1 == null || c2 == null) return false;
        return unionFind.find(c1) != unionFind.find(c2);
    }

    private boolean onlyOneSetRemains(){
        // Returns true if at most there is one set
        return unionFind.numSets() <= 1;
    }

    private void makeSets(Cell[][] mazeMap){
        // Initializes all the sets for each cell
        for (int i = 0; i < mazeMap.length; i++) {
            for (int j = 0; j < mazeMap[i].length; j++) {
                unionFind.makeset(mazeMap[i][j]);
            }
        }
    }

    // print out the maze in a specific format
    public void printMaze(Cell[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            // print the up walls for row i
            for (int j = 0; j < maze.length; j++) {
                Wall up = maze[i][j].up;
                if (up != null && up.visible) System.out.print("+--");
                else System.out.print("+  ");
            }
            System.out.println("+");

            // print the left walls and the cells in row i
            for (int j = 0; j < maze.length; j++) {
                Wall left = maze[i][j].left;
                if (left != null && left.visible) System.out.print("|  ");
                else System.out.print("   ");
            }

            //print the last wall on the far right of row i
            Wall lastRight = maze[i][maze.length - 1].right;
            if (lastRight != null && lastRight.visible) System.out.println("|");
            else System.out.println(" ");
        }

        // print the last row's down walls
        for (int i = 0; i < maze.length; i++) {
            Wall down = maze[maze.length - 1][i].down;
            if (down != null && down.visible) System.out.print("+--");
            else System.out.print("+  ");
        }
        System.out.println("+");


    }

    // create a new Cell for each position of the maze
    public void initializeCells(Cell[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                maze[i][j] = new Cell();
            }
        }
    }

    // create all walls and link walls and cells
    public Wall[] getWalls(Cell[][] mazeMap) {

        int n = mazeMap.length;

        Wall[] walls = new Wall[2 * n * (n + 1)];
        int wallCtr = 0;

        // each "inner" cell adds its right and down walls
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // add down wall
                if (i < n - 1) {
                    walls[wallCtr] = new Wall(mazeMap[i][j], mazeMap[i + 1][j]);
                    mazeMap[i][j].down = walls[wallCtr];
                    mazeMap[i + 1][j].up = walls[wallCtr];
                    wallCtr++;
                }

                // add right wall
                if (j < n - 1) {
                    walls[wallCtr] = new Wall(mazeMap[i][j], mazeMap[i][j + 1]);
                    mazeMap[i][j].right = walls[wallCtr];
                    mazeMap[i][j + 1].left = walls[wallCtr];
                    wallCtr++;
                }
            }
        }

        // "outer" cells add their outer walls
        for (int i = 0; i < n; i++) {
            // add left walls for the first column
            walls[wallCtr] = new Wall(null, mazeMap[i][0]);
            mazeMap[i][0].left = walls[wallCtr];
            wallCtr++;

            // add up walls for the top row
            walls[wallCtr] = new Wall(null, mazeMap[0][i]);
            mazeMap[0][i].up = walls[wallCtr];
            wallCtr++;

            // add down walls for the bottom row
            walls[wallCtr] = new Wall(null, mazeMap[n - 1][i]);
            mazeMap[n - 1][i].down = walls[wallCtr];
            wallCtr++;

            // add right walls for the last column
            walls[wallCtr] = new Wall(null, mazeMap[i][n - 1]);
            mazeMap[i][n - 1].right = walls[wallCtr];
            wallCtr++;
        }


        return walls;
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            int n = Integer.parseInt(args[0]);
            new MazeGenerator().run(n);
        } else new MazeGenerator().run(5);
    }

}