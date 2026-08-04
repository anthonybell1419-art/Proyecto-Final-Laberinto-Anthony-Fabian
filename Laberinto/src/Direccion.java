public enum Direccion{
    ARRIBA(-1,0),
    ABAJO(1,0),
    IZQUIERDA(0,-1),
    DERECHA(0,1);

    private final int dFila;
    private final int dCol;

    Direccion(int dFila, int dCol){
        this.dFila = dFila;
        this.dCol = dCol;
    }
    public int getDRow(){return dFila;}
    public int getDCol(){return dCol;}
    }
