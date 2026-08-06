public class Casilla {
    private final int fila;
    private final int col;

    private boolean ParedArriba = true;
    private boolean ParedAbajo = true;
    private boolean ParedIzquierda = true;
    private boolean ParedDerecha = true;

    private boolean visitado = false;

    public Casilla(int fila,int col){
        this.fila = fila;
        this.col = col;
    }
    public int getFila(){return fila;}
    public int getCol(){return col;}
    public boolean hasTopWall(Direccion dir){return ParedArriba;}
    public boolean hasBottomWall(Direccion dir){return ParedAbajo;}
    public boolean hasLeftWall(Direccion dir){return ParedIzquierda;}
    public boolean hasRightWall(Direccion dir){return ParedDerecha;}
    public boolean isVisited(){return visitado;}
    public void setVisited(boolean visitado){this.visitado = visitado;}
    public void setTopWall(boolean ParedArriba){this.ParedArriba = ParedArriba;}
    public void setBottomWall(boolean ParedAbajo){this.ParedAbajo = ParedAbajo;}
    public void setLeftWall(boolean ParedIzquierda){this.ParedIzquierda = ParedIzquierda;}
    public void setRightWall(boolean paredDerecha){this.ParedDerecha = paredDerecha;}
    
}
