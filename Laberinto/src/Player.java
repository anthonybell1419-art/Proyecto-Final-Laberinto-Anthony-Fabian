public class Player {
    private int fila;
    private int col;

    public Player(int fila,int col){
        this.fila = fila;
        this.col = col;
    }
    public int getFila(){return fila;}
    public int getCol(){return col;}


    public boolean move(Laberinto lab, Direccion dir){
        if(!lab.canMove(fila,col,dir)){
            return false;
        }
        fila += dir.getDFila();
        col += dir.getDCol();
        return true;
    }
}
