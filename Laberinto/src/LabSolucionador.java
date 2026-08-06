import java.util.*;

public class LabSolucionador {
    private static final int PISTA_LENGTH = 8;

    public static List<int[]> solucion(Laberinto lab, int startFila, int startCol) {
        int filas = lab.getFilas();
        int cols = lab.getCols();
        
        boolean[][] visited = new boolean[filas][cols];
        int[][] parentFila = new int[filas][cols];
        int[][] parentCol = new int[filas][cols];
        for(int[] f : parentFila) Arrays.fill(f, -1);
        for(int[] c : parentCol) Arrays.fill(c, -1);

        Deque<int[]> deque = new ArrayDeque<>();
        deque.add(new int[]{startFila, startCol});
        visited[startFila][startCol] = true;

        int exitFila = lab.getExitFila();
        int exitCol = lab.getExitCol();
        boolean found = (startFila == exitFila && startCol == exitCol);

        while(!deque.isEmpty() && !found){
            int[] current = deque.poll();
            int f = current[0];
            int c = current[1];

            for(Direccion dir : Direccion.values()){
                if(!lab.canMove(f,c,dir))continue;
                int newFila = f + dir.getDFila();
                int newCol = c + dir.getDCol();
                if(newFila < 0 || newFila >= filas || newCol < 0 || newCol >= cols) continue;
                visited[newFila][newCol] = true;
                parentFila[newFila][newCol] = f;
                parentCol[newFila][newCol] = c;
                deque.add(new int[]{newFila, newCol});
                if(newFila == exitFila && newCol == exitCol){
                    found = true;
                    break;
                }
            }
        }
        List<int[]> path = new ArrayList<>();
        if(!found)return path;
        int f = exitFila;
        int c = exitCol;
        while(!(f == startFila && c == startCol)){
            path.add(new int[]{f,c});
            int pFila = parentFila[f][c];
            int pCol = parentCol[f][c];
            f = pFila;
            c = pCol;
        }
        Collections.reverse(path);
        if(path.size() > PISTA_LENGTH){
            return new ArrayList<>(path.subList(0, PISTA_LENGTH));
        }
        return path;
    }
}
