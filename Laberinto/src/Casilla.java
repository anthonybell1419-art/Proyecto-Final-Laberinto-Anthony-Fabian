public class Casilla {
    private final int fila;
    private final int col;

    private boolean paredArriba = true;
    private boolean paredAbajo = true;
    private boolean ParedIzquierda = true;
    private boolean paredDerecha = true;

    private boolean visitado = false;

    public Casilla(int fila,int col){
        this.fila = fila;
        this.col = col;
    }
    public int getFila(){return fila;}
    public int getCol(){return col;}
    public boolean hasTopWall(Direccion dire){return paredArriba;}
    public boolean hasBottomWall(Direccion dire){return paredAbajo;}
    public boolean hasLeftWall(Direccion dire){return ParedIzquierda;}
    public boolean hasRightWall(Direccion dire){return paredDerecha;}
    public boolean isVisited(){return visitado;}
    public void setVisited(boolean visitado){this.visitado = visitado;}
    public void setTopWall(boolean paredArriba){this.paredArriba = paredArriba;}
    public void setBottomWall(boolean paredAbajo){this.paredAbajo = paredAbajo;}
    public void setLeftWall(boolean ParedIzquierda){this.ParedIzquierda = ParedIzquierda;}
    public void setRightWall(boolean paredDerecha){this.paredDerecha = paredDerecha;}
}
