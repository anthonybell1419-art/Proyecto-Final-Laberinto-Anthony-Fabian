public class Player {
    private int row;
    private int col;

    public Player(int row,int col){
        this.row = row;
        this.col = col;
    }
    public int getRow(){return row;}
    public int getCol(){return col;}


    public boolean move(Laberinto lab, Direccion dire){
        if(!lab.canMove(row,col,dire)){
            return false;
        }
        row += dire.getDRow();
        col += dire.getDCol();
        return true;
    }
}
