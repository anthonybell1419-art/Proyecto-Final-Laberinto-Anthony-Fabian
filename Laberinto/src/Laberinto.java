public class Laberinto {
    private final int rows;
    private final int cols;
    private final Casilla[][] grid; // Make sure Casilla class is imported

    private final int entryRow = 0;
    private final int entryCol = 0;
    private final int exitRow;
    private final int exitCol;

    public Laberinto(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.grid = LabGenerador.generate(rows,cols);
        this.exitRow = rows - 1;
        this.exitCol = cols - 1;
    }

    public boolean canMove(int row, int col, Direccion dire){
        if(row < 0 || row >= rows || col < 0 || col >= cols){
            return false;
        }
        Casilla cas = grid[row][col];
        switch(dire){
            case ARRIBA:
                return !cas.hasTopWall(Direccion.ARRIBA);
            case ABAJO:
                return !cas.hasBottomWall(Direccion.ABAJO);
            case IZQUIERDA:
                return !cas.hasLeftWall(Direccion.IZQUIERDA);
            case DERECHA:
                return !cas.hasRightWall(Direccion.DERECHA);
                default:
                    return false;
        }
    }
    public boolean isExit(int row, int col){
        return row == exitRow && col == exitCol;
    }
    public int getRows(){return rows;}
    public int getCols(){return cols;}
    public Casilla[][] getGrid(){return grid;}
    public int getEntryRow(){return entryRow;}
    public int getEntryCol(){return entryCol;}
    public int getExitRow(){return exitRow;}
    public int getExitCol(){return exitCol;}
}
