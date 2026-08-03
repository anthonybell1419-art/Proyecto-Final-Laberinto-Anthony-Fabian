public enum Direccion{
    ARRIBA(-1,0),
    ABAJO(1,0),
    IZQUIERDA(0,-1),
    DERECHA(0,1);

    private final int dRow;
    private final int dCol;

    Direccion(int dRow, int dCol){
        this.dRow = dRow;
        this.dCol = dCol;
    }
    public int getDRow(){return dRow;}
    public int getDCol(){return dCol;}
    }
