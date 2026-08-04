public enum Direccion{
    NORTE(-1,0),
    SUR(1,0),
    OESTE(0,-1),
    ESTE(0,1);

    private final int dFila;
    private final int dCol;

    Direccion(int dFila, int dCol){
        this.dFila = dFila;
        this.dCol = dCol;
    }
    public int getDFila(){return dFila;}
    public int getDCol(){return dCol;}
    }
