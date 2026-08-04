public class Player {
    private int fila;
    private int col;

    public Player(int fila,int col){
        this.fila = fila;
        this.col = col;
    }
    public int getRow(){return fila;}
    public int getCol(){return col;}


    public boolean move(Laberinto lab, Direccion dire){
        if(!lab.canMove(fila,col,dire)){
            return false;
        }
        fila += dire.getDRow();
        col += dire.getDCol();
        return true;
    }
}
