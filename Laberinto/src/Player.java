public class Player {
    private int fila;
    private int col;
    private Direccion direccion;

    public Player(int fila, int col) {
        this.fila = fila;
        this.col = col;
        this.direccion = Direccion.SUR; 
    }

    public int getFila() { return fila; }
    public int getCol() { return col; }
    public Direccion getDireccion() { return direccion; }

    public boolean move(Laberinto lab, Direccion dir) {
        this.direccion = dir; 
        if (!lab.canMove(fila, col, dir)) {
            return false;
        }
        fila += dir.getDFila();
        col += dir.getDCol();
        return true;
    }
}