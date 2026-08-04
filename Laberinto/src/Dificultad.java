public enum Dificultad {
    FACIL("Facil", 15, 15, -1),
    NORMAL("Normal", 21, 21,180),
    DIFICIL("Dificil",27, 27, 120);

    private final String nombre;
    private final int filas;
    private final int cols;
    private final int timeLimitSeconds;

    Dificultad(String nombre, int filas, int cols, int timeLimitSeconds) {
        this.nombre = nombre;
        this.filas = filas;
        this.cols = cols;
        this.timeLimitSeconds = timeLimitSeconds;
    }
    public String getNombre(){return nombre;}
    public int getFilas(){return filas;}
    public int getCols(){return cols;}
    public int getTimeLimitSeconds(){return timeLimitSeconds;}
}
