public class Laberinto {
    private final int filas;
    private final int cols;
    private final Casilla[][] grid; 

    private final int entryFila = 0;
    private final int entryCol = 0;
    private final int exitFila;
    private final int exitCol;

    public Laberinto(int filas, int cols){
        this.filas = filas;
        this.cols = cols;
        this.grid = LabCreador.generar(filas, cols);
        this.exitFila = filas - 1;
        this.exitCol = cols - 1;
    }

    public boolean canMove(int fila, int col, Direccion dire){
        if(fila < 0 || fila >= filas || col < 0 || col >= cols){
            return false;
        }
        Casilla cas = grid[fila][col];
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
    public boolean isExit(int fila, int col){
        return fila == exitFila && col == exitCol;
    }
    public int getFilasFilas(){return filas;}
    public int getCols(){return cols;}
    public Casilla[][] getGrid(){return grid;}
    public int getEntryFila(){return entryFila;}
    public int getEntryCol(){return entryCol;}
    public int getExitFila(){return exitFila;}
    public int getExitCol(){return exitCol;}
}
