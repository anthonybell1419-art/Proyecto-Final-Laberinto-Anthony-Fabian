import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.text.Normalizer;
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

        double[] casillaTam = {CASILLA_TAMAÑO_BASE};

        Canvas canvas = new Canvas(lab.getCols() * casillaTam[0], lab.getFilas() * casillaTam[0]);
        GraphicsContext j = canvas.getGraphicsContext2D();
        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background:#1e1e2f; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // --- Panel lateral con la imagen de la dificultad
        ImageView imagenDificultadView = new ImageView();
        imagenDificultadView.setPreserveRatio(true);
        imagenDificultadView.setSmooth(true);

        StackPane panelImagen = new StackPane(imagenDificultadView);
        panelImagen.setPadding(new Insets(20));
        panelImagen.setStyle("-fx-background-color:#20203a;");
        HBox.setHgrow(panelImagen, Priority.ALWAYS);

        Image imgDificultad = cargarImagenDificultad(dificultad);
        if (imgDificultad != null) {
            imagenDificultadView.setImage(imgDificultad);
        } else {
            Label placeholder = new Label("Coloca aquí:\nimages/" + nombreArchivoDificultad(dificultad));
            placeholder.setTextFill(Color.web("#5b5b7a"));
            placeholder.setFont(Font.font(13));
            placeholder.setStyle("-fx-text-alignment:center;");
            panelImagen.getChildren().add(placeholder);
        }
        imagenDificultadView.fitWidthProperty().bind(panelImagen.widthProperty().subtract(40));
        imagenDificultadView.fitHeightProperty().bind(panelImagen.heightProperty().subtract(40));

        HBox centerBox = new HBox(scrollPane, panelImagen);

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
        root.setCenter(centerBox);
        root.setFocusTraversable(true);

        double sceneWidth = Math.min(960, lab.getCols() * casillaTam[0] + 40);
        double sceneHeight = Math.min(720, lab.getFilas() * casillaTam[0] + 100);
        Scene scene = new Scene(root, sceneWidth, sceneHeight);

        boolean[] gameOver = {false};
        int[] segundosP = {0};
        AtomicReference<List<int[]>> hintPath = new AtomicReference<>();

        Image scoobyNorte = cargarImagen("/Imagenes/scoobyNorte.png");
        Image scoobySur   = cargarImagen("/Imagenes/scoobySur.png");
        Image scoobyEste  = cargarImagen("/Imagenes/scoobyEste.png");
        Image scoobyOeste = cargarImagen("/Imagenes/scoobyOeste.png");

        Runnable reDibujar = () -> crearLaberinto(j, lab, player, hintPath.get(), casillaTam[0],
                scoobyNorte, scoobySur, scoobyEste, scoobyOeste);

        // Recalcula el tamaño de celda 
        Runnable ajustarTamaño = () -> {
            double alturaTotal = scene.getHeight();
            double anchoTotal = scene.getWidth();
            double alturaBarra = BarraSuperior.getHeight() > 0 ? BarraSuperior.getHeight() : 52;
            double altoDisponible = alturaTotal - alturaBarra - 20;
            if (altoDisponible <= 0 || anchoTotal <= 0) return;

            double anchoImagen = clamp(anchoTotal * 0.28, 220, 420);
            double anchoDisponibleLab = anchoTotal - anchoImagen - 20;
            if (anchoDisponibleLab < 150) {
                anchoDisponibleLab = anchoTotal * 0.55;
            }

            double tamPorAncho = anchoDisponibleLab / lab.getCols();
            double tamPorAlto = altoDisponible / lab.getFilas();
            double nuevoTam = Math.max(8, Math.min(tamPorAncho, tamPorAlto));

            casillaTam[0] = nuevoTam;
            canvas.setWidth(lab.getCols() * casillaTam[0]);
            canvas.setHeight(lab.getFilas() * casillaTam[0]);
            reDibujar.run();
        };

        scene.widthProperty().addListener((obs, o, n) -> ajustarTamaño.run());
        scene.heightProperty().addListener((obs, o, n) -> ajustarTamaño.run());
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
            reDibujar.run();
            if (moved) {
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

    private static double clamp(double valor, double min, double max) {
        return Math.max(min, Math.min(max, valor));
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

    private static void crearLaberinto(GraphicsContext j, Laberinto laberinto, Player player,
                                        List<int[]> hintPath, double casillaTamaño,
                                        Image scoobyNorte, Image scoobySur,
                                        Image scoobyEste, Image scoobyOeste) {
        double w = j.getCanvas().getWidth();
        double h = j.getCanvas().getHeight();

        j.setFill(Color.web("#1e1e2f"));
        j.fillRect(0, 0, w, h);

        j.setFill(Color.web("#2ecc71"));
        j.fillRect(laberinto.getEntryCol() * casillaTamaño, laberinto.getEntryFila() * casillaTamaño, casillaTamaño, casillaTamaño);
        j.setFill(Color.web("#e74c3c"));
        j.fillRect(laberinto.getExitCol() * casillaTamaño, laberinto.getExitFila() * casillaTamaño, casillaTamaño, casillaTamaño);

        if (hintPath != null) {
            j.setFill(Color.web("#f1c40f", 0.55));
            for (int[] p : hintPath) {
                j.fillRect(p[1] * casillaTamaño, p[0] * casillaTamaño, casillaTamaño, casillaTamaño);
            }
        }

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

        double px = player.getCol() * casillaTamaño;
        double py = player.getFila() * casillaTamaño;
        Image imgJugador = seleccionarImagenJugador(player.getDireccion(),
                scoobyNorte, scoobySur, scoobyEste, scoobyOeste);

        if (imgJugador != null) {
            j.drawImage(imgJugador, px, py, casillaTamaño, casillaTamaño);
        } else {
            double cx = px + casillaTamaño / 2.0;
            double cy = py + casillaTamaño / 2.0;
            j.setFill(Color.web("#3498db"));
            j.fillOval(cx - (casillaTamaño - 8) / 2.0, cy - (casillaTamaño - 8) / 2.0, casillaTamaño - 8, casillaTamaño - 8);
        }
    }

    private static Image seleccionarImagenJugador(Direccion dir, Image norte, Image sur, Image este, Image oeste) {
        if (dir == null) return sur;
        switch (dir) {
            case NORTE: return norte;
            case SUR:   return sur;
            case ESTE:  return este;
            case OESTE: return oeste;
            default:    return sur;
        }
    }

    private static void showVictoryDialog(Stage stage, Dificultad dificultad, int segundosP) {
        stage.setScene(buildVictoryScene(stage, dificultad));
    }

    private static Scene buildVictoryScene(Stage stage, Dificultad dificultad) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color:#1e1e2f;");

        Image imgVictoria = cargarImagen("/Imagenes/victoria.png");
        if (imgVictoria != null) {
            ImageView fondo = new ImageView(imgVictoria);
            fondo.setPreserveRatio(false);
            fondo.fitWidthProperty().bind(root.widthProperty());
            fondo.fitHeightProperty().bind(root.heightProperty());
            root.getChildren().add(fondo);
        } else {
            Label placeholder = new Label("Coloca aquí: Imagenes/victoria.png");
            placeholder.setTextFill(Color.web("#5b5b7a"));
            placeholder.setFont(Font.font(16));
            root.getChildren().add(placeholder);
        }

        Button jugarDeNuevoBtn = new Button("Jugar de nuevo");
        Button menuBtn = new Button("Menu");

        String estiloBotonMenu =
                "-fx-background-color:#6c3483; -fx-text-fill:white; -fx-font-size:16px; "
            + "-fx-font-weight:bold; -fx-background-radius:10; -fx-padding:14 34 14 34; "
            + "-fx-cursor:hand;";
        jugarDeNuevoBtn.setStyle(estiloBotonMenu);
        menuBtn.setStyle(estiloBotonMenu);

        VBox botones = new VBox(16, jugarDeNuevoBtn, menuBtn);
        botones.setAlignment(Pos.CENTER);
        StackPane.setAlignment(botones, Pos.BOTTOM_CENTER);
        StackPane.setMargin(botones, new Insets(100, 0, 60, 0));
        root.getChildren().add(botones);

        jugarDeNuevoBtn.setOnAction(e -> stage.setScene(buildGameScene(stage, dificultad)));
        menuBtn.setOnAction(e -> stage.setScene(Menu.crear(stage)));

        double anchoActual = stage.getScene() != null ? stage.getScene().getWidth() : 960;
        double altoActual = stage.getScene() != null ? stage.getScene().getHeight() : 720;
        return new Scene(root, anchoActual, altoActual);
    }

    private static void showTimeUpDialog(Stage stage, Dificultad dificultad) {
        stage.setScene(buildTimeUpScene(stage, dificultad));
    }

    private static Scene buildTimeUpScene(Stage stage, Dificultad dificultad) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color:#1e1e2f;");

        Image imgTiempoAgotado = cargarImagen("/Imagenes/agotado.png");
        if (imgTiempoAgotado != null) {
            ImageView fondo = new ImageView(imgTiempoAgotado);
            fondo.setPreserveRatio(false);
            fondo.fitWidthProperty().bind(root.widthProperty());
            fondo.fitHeightProperty().bind(root.heightProperty());
            root.getChildren().add(fondo);
        } else {
            Label placeholder = new Label("Coloca aquí: Imagenes/agotado.png");
            placeholder.setTextFill(Color.web("#5b5b7a"));
            placeholder.setFont(Font.font(16));
            root.getChildren().add(placeholder);
        }

        Button reintentarBtn = new Button("Reintentar");
        Button menuBtn = new Button("Menu");

        String estiloBotonMenu =
                "-fx-background-color:#6c3483; -fx-text-fill:white; -fx-font-size:16px; "
            + "-fx-font-weight:bold; -fx-background-radius:10; -fx-padding:14 34 14 34; "
            + "-fx-cursor:hand;";
        reintentarBtn.setStyle(estiloBotonMenu);
        menuBtn.setStyle(estiloBotonMenu);

        VBox botones = new VBox(16, reintentarBtn, menuBtn);
        botones.setAlignment(Pos.CENTER);
        StackPane.setAlignment(botones, Pos.BOTTOM_CENTER);
        StackPane.setMargin(botones, new Insets(0, 0, 60, 0));
        root.getChildren().add(botones);

        reintentarBtn.setOnAction(e -> stage.setScene(buildGameScene(stage, dificultad)));
        menuBtn.setOnAction(e -> stage.setScene(Menu.crear(stage)));

        double anchoActual = stage.getScene() != null ? stage.getScene().getWidth() : 960;
        double altoActual = stage.getScene() != null ? stage.getScene().getHeight() : 720;
        return new Scene(root, anchoActual, altoActual);
    }

    private static String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    private static Image cargarImagenDificultad(Dificultad dificultad) {
        return cargarImagen("/Imagenes/" + nombreArchivoDificultad(dificultad));
    }

    private static String nombreArchivoDificultad(Dificultad dificultad) {
        return normalizarNombre(dificultad.getNombre()) + ".png";
    }

    private static Image cargarImagen(String rutaClasspath) {
        try (InputStream stream = Juego.class.getResourceAsStream(rutaClasspath)) {
            if (stream == null) return null;
            return new Image(stream);
        } catch (Exception e) {
            return null;
        }
    }

    private static String normalizarNombre(String nombre) {
        String sinTildes = Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().trim().replaceAll("\\s+", "_");
    }
}