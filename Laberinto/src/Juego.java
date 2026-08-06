import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Juego {
    
    private static final int CASILLA_TAMAÑO_BASE = 26;

    public static Scene crear(Stage stage, Dificultad dificultad){
        return buildGameScene(stage, dificultad);
    }

    private static Scene buildGameScene(Stage stage, Dificultad dificultad) {
        Laberinto lab = new Laberinto(dificultad.getFilas(), dificultad.getCols());
        Player player = new Player(lab.getEntryFila(), lab.getEntryCol());

        // Tamaño de celda dinámico
        double[] casillaTam = {CASILLA_TAMAÑO_BASE};

        Canvas canvas = new Canvas(lab.getCols() * casillaTam[0], lab.getFilas() * casillaTam[0]);
        GraphicsContext j = canvas.getGraphicsContext2D();
        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background:#1e1e2f; -fx-background-color: transparent;");
        // Evita que aparezcan barras de scroll 
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Label dificultadLabel = new Label ("Dificultad: " + dificultad.getNombre());
        dificultadLabel.setTextFill(Color.LIGHTGRAY);
        dificultadLabel.setFont(Font.font(13));

        Label tiempoLabel = new Label();
        tiempoLabel.setFont(Font.font(20));
        tiempoLabel.setTextFill(Color.WHITE);

        Button NuevoLabBtn = new Button("Nuevo boton");
        Button PistaBtn = new Button("Pista");
        Button menuBtn = new Button("Menu");
        String btnStyle = "-fx-background-color:#3498db; -fx-text-fill:white; "
                        + "-fx-background-radius:6; -fx-cursor:hand;";
        NuevoLabBtn.setStyle(btnStyle);
        PistaBtn.setStyle("-fx-background-color:#f1c40f; -fx-text-fill:#1e1e2f; "
                        + "-fx-background-radius:6; -fx-cursor:hand; -fx-font-weight:bold;");
        menuBtn.setStyle("-fx-background-color:#7f8c8d; -fx-text-fill:white; "
                        + "-fx-background-radius:6; -fx-cursor:hand;");

        HBox BarraSuperior = new HBox(18, dificultadLabel,tiempoLabel,NuevoLabBtn,PistaBtn,menuBtn);
        BarraSuperior.setPadding(new Insets(10,14,10,14));
        BarraSuperior.setAlignment(Pos.CENTER_LEFT);
        BarraSuperior.setStyle("-fx-background-color:#26263f;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#1e1e2f;");
        root.setTop(BarraSuperior);
        root.setCenter(scrollPane);
        root.setFocusTraversable(true);

        double sceneWidth = Math.min(960, lab.getCols() * casillaTam[0] + 40);
        double sceneHeight = Math.min(720, lab.getFilas() * casillaTam[0] + 100);
        Scene scene = new Scene(root, sceneWidth, sceneHeight);

        boolean[] gameOver = {false};
        int[] segundosP = {0};
        AtomicReference<List<int[]>> hintPath = new AtomicReference<>();

        Runnable reDibujar = () -> crearLaberinto(j, lab, player, hintPath.get(), casillaTam[0]);

        // Recalcula el tamaño de celda según el espacio real disponible en el ScrollPane
        Runnable ajustarTamaño = () -> {
            double anchoDisponible = scrollPane.getViewportBounds() != null
                    ? scrollPane.getViewportBounds().getWidth()
                    : scrollPane.getWidth();
            double altoDisponible = scrollPane.getViewportBounds() != null
                    ? scrollPane.getViewportBounds().getHeight()
                    : scrollPane.getHeight();

            if (anchoDisponible <= 0 || altoDisponible <= 0) return;

            double tamPorAncho = anchoDisponible / lab.getCols();
            double tamPorAlto = altoDisponible / lab.getFilas();
            double nuevoTam = Math.min(tamPorAncho, tamPorAlto);

            // Evita celdas demasiado pequeñas en ventanas muy chicas
            nuevoTam = Math.max(8, nuevoTam);

            casillaTam[0] = nuevoTam;
            canvas.setWidth(lab.getCols() * casillaTam[0]);
            canvas.setHeight(lab.getFilas() * casillaTam[0]);
            reDibujar.run();
        };

        // Se dispara cada vez que cambia el tamaño visible del ScrollPane
        scrollPane.viewportBoundsProperty().addListener((obs, oldB, newB) -> ajustarTamaño.run());

        //Para el cambio de pantalla completa
        stage.fullScreenProperty().addListener((obs, wasFull, isFull) -> Platform.runLater(ajustarTamaño));

        Timeline tiempo = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if(gameOver[0])return;
            segundosP[0]++;

            if(dificultad.getTimeLimitSeconds() > 0){
                int restante = dificultad.getTimeLimitSeconds() - segundosP[0];
                if(restante <= 0){
                    gameOver[0] = true;
                    tiempoLabel.setText("00:00");
                    showTimeUpDialog(stage,dificultad);
                    return;
                }
                tiempoLabel.setText(formatTime(restante) + "restante");
            }else{
                tiempoLabel.setText(formatTime(segundosP[0]));
            }
        }));
        tiempo.setCycleCount(Timeline.INDEFINITE);
        tiempo.play();
        NuevoLabBtn.setOnAction(event ->{
            tiempo.stop();
            stage.setScene(buildGameScene(stage, dificultad));
        });
        PistaBtn.setOnAction(event ->{
            if(gameOver[0])return;
            hintPath.set(LabSolucionador.solucion(lab, player.getFila(), player.getCol()));
            reDibujar.run();
            PauseTransition pausa = new PauseTransition(Duration.seconds(2));
            pausa.setOnFinished(ev ->{
                hintPath.set(null);
                reDibujar.run();
            });
            pausa.play();
        });
        menuBtn.setOnAction(e -> {
            tiempo.stop();
            stage.setScene(Menu.crear(stage));
        });
        scene.setOnKeyPressed(e -> {
            if (gameOver[0]) return;
            Direccion dir = keyToDirection(e.getCode());
            if (dir == null) return;

            boolean moved = player.move(lab, dir);
            if (moved) {
                reDibujar.run();
                if (lab.isExit(player.getFila(), player.getCol())) {
                    gameOver[0] = true;
                    tiempo.stop();
                    showVictoryDialog(stage, dificultad, segundosP[0]);
                }
            }
        });

        reDibujar.run();
        Platform.runLater(() -> {
            root.requestFocus();
            ajustarTamaño.run();
        });

        return scene;
    }

    private static Direccion keyToDirection(KeyCode code) {
        switch (code) {
            case UP:
            case W:
                return Direccion.NORTE;
            case DOWN:
            case S:
                return Direccion.SUR;
            case LEFT:
            case A:
                return Direccion.OESTE;
            case RIGHT:
            case D:
                return Direccion.ESTE;
            default:
                return null;
        }
    }

    private static void crearLaberinto(GraphicsContext j, Laberinto laberinto, Player player, List<int[]> hintPath, double casillaTamaño) {
        double w = j.getCanvas().getWidth();
        double h = j.getCanvas().getHeight();

        j.setFill(Color.web("#1e1e2f"));
        j.fillRect(0, 0, w, h);

        // Entrada y salida
        j.setFill(Color.web("#2ecc71"));
        j.fillRect(laberinto.getEntryCol() * casillaTamaño, laberinto.getEntryFila() * casillaTamaño, casillaTamaño, casillaTamaño);
        j.setFill(Color.web("#e74c3c"));
        j.fillRect(laberinto.getExitCol() * casillaTamaño, laberinto.getExitFila() * casillaTamaño, casillaTamaño, casillaTamaño);

        // Camino de pista
        if (hintPath != null) {
            j.setFill(Color.web("#f1c40f", 0.55));
            for (int[] p : hintPath) {
                j.fillRect(p[1] * casillaTamaño, p[0] * casillaTamaño, casillaTamaño, casillaTamaño);
            }
        }

        // Paredes y rejilla
        Casilla[][] grid = laberinto.getGrid();
        j.setStroke(Color.web("#f5f5f5"));
        j.setLineWidth(Math.max(1, casillaTamaño / 13.0));
        for (int f = 0; f < laberinto.getFilas(); f++) {
            for (int c = 0; c < laberinto.getCols(); c++) {
                Casilla cas = grid[f][c];
                double x = c * casillaTamaño;
                double y = f * casillaTamaño;
                if (cas.hasTopWall(Direccion.NORTE))
                    j.strokeLine(x, y, x + casillaTamaño, y);

                if (cas.hasRightWall(Direccion.ESTE))
                    j.strokeLine(x + casillaTamaño, y, x + casillaTamaño, y + casillaTamaño);

                if (cas.hasBottomWall(Direccion.SUR))
                    j.strokeLine(x, y + casillaTamaño, x + casillaTamaño, y + casillaTamaño);

                if (cas.hasLeftWall(Direccion.OESTE))
                    j.strokeLine(x, y, x, y + casillaTamaño);
            }
        }

        // Jugador
        double px = player.getCol() * casillaTamaño + casillaTamaño / 2.0;
        double py = player.getFila() * casillaTamaño + casillaTamaño / 2.0;
        j.setFill(Color.web("#3498db"));
        j.fillOval(px - (casillaTamaño - 8) / 2.0, py - (casillaTamaño - 8) / 2.0, casillaTamaño - 8, casillaTamaño - 8);
    }

    private static void showVictoryDialog(Stage stage, Dificultad dificultad, int segundosP) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("¡Victoria!");
        alert.setHeaderText("Llegaste a la salida");
        alert.setContentText("Tiempo total: " + formatTime(segundosP));

        ButtonType deNuevo = new ButtonType("Jugar de nuevo");
        ButtonType menu = new ButtonType("Menu");
        alert.getButtonTypes().setAll(deNuevo, menu);

        alert.showAndWait().ifPresent(response -> {
            if (response == deNuevo) {
                stage.setScene(buildGameScene(stage, dificultad));
            } else {
                stage.setScene(Menu.crear(stage));
            }
        });
    }

    private static void showTimeUpDialog(Stage stage, Dificultad dificultad) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Tiempo agotado");
        alert.setHeaderText("Se acabo el tiempo");
        alert.setContentText("No lo lograste.");

        ButtonType reitentar = new ButtonType("Reintentar");
        ButtonType menu = new ButtonType("Menu");
        alert.getButtonTypes().setAll(reitentar, menu);

        alert.showAndWait().ifPresent(response -> {
            if (response == reitentar) {
                stage.setScene(buildGameScene(stage, dificultad));
            } else {
                stage.setScene(Menu.crear(stage));
            }
        });
    }

    private static String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}