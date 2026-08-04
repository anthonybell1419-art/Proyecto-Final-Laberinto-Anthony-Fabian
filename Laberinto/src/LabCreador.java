import java.util.*;

public class LabCreador {
    public static Casilla[][] generar(int filas, int cols){
        Casilla[][] grid = new Casilla[filas][cols];
        for(int f = 0; f < filas; f++){
            for(int c = 0; c < cols; c++){
                grid[f][c] = new Casilla(f, c);
            }
        } 
        Random rand = new Random();
        Deque<Casilla> stack = new ArrayDeque<>();

        Casilla start = grid[0][0];
        start.setVisited(true);
        stack.push(start);

        while(!stack.isEmpty()){
            Casilla current = stack.peek();
            List<Direccion> direcciones = new ArrayList<>(Arrays.asList(Direccion.values()));
            Collections.shuffle(direcciones,rand);

            Casilla next = null;
            Direccion chosenDir = null;
            for(Direccion dire : direcciones){
                int newFila = current.getFila() + dire.getDRow();
                int newCol = current.getCol() + dire.getDCol();

                if(newFila >= 0 && newFila < filas && newCol >= 0 && newCol < cols && !grid[newFila][newCol].isVisited()){
                    next = grid[newFila][newCol];
                    chosenDir = dire;
                    break;
                }
        }
        
            if(next != null){
                removeWallBetween(current, next, chosenDir);
                next.setVisited(true);
                stack.push(next);
            } else {
                stack.pop();
            }
        }
        return grid;
    }
    private static void removeWallBetween(Casilla current, Casilla next, Direccion dire){
        switch(dire){
            case ARRIBA:
                current.setTopWall(false);
                next.setBottomWall(false);
                break;
            case ABAJO:
                current.setBottomWall(false);
                next.setTopWall(false);
                break;
            case IZQUIERDA:
                current.setLeftWall(false);
                next.setRightWall(false);
                break;
            case DERECHA:
                current.setRightWall(false);
                next.setLeftWall(false);
                break;
        }
    }
}
