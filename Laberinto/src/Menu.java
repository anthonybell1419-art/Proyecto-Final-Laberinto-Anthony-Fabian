import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Menu {
    private static MediaPlayer musica;
    public static Scene crear(Stage stage){
        VBox root = new VBox(22);

        Media sonido = new Media(Menu.class.getResource("/Musica/MenuMusica.mp3").toExternalForm());
        musica = new MediaPlayer(sonido);
        musica.setCycleCount(MediaPlayer.INDEFINITE);
        musica.setVolume(0.5);
        musica.play();

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle(
            "-fx-background-image: url('/Imagenes/Menu.png');" +
            "-fx-background-size: cover;" +
            "-fx-background-position: center center;" +
            "-fx-background-repeat: no-repeat;");

        VBox btnBox = new VBox(14);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(200, 0, 0, 0)); 
        for(Dificultad dificultad : Dificultad.values()){
        String ruta = "";

    switch (dificultad) {
        case FACIL:
            ruta = "/Imagenes/shaggy.jpg";
            break;
        case NORMAL:
            ruta = "/Imagenes/fred.jpg";
            break;
        case DIFICIL:
            ruta = "/Imagenes/velma.jpg";
            break;
    }

    ImageView img = new ImageView(new Image(Menu.class.getResourceAsStream(ruta)));
    img.setFitWidth(70);
    img.setFitHeight(70);
    img.setPreserveRatio(true);

    Label texto = new Label(dificultad.getNombre());

    texto.setTextFill(Color.WHITE);
    texto.setFont(Font.font(14));

    HBox contenido = new HBox(10);
    contenido.setAlignment(Pos.CENTER_LEFT);
    contenido.getChildren().addAll(img, texto);

    Button btn = new Button();
    btn.setGraphic(contenido);
    btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

    btn.setPrefWidth(200);
    btn.setPrefHeight(70);

    btn.setStyle(
            "-fx-background-color:#4C1D95;" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:8;" +
            "-fx-cursor:hand;");

    btn.setOnMouseEntered(event ->
            btn.setStyle(
                    "-fx-background-color:#6D28D9;" +
                    "-fx-text-fill:white;" +
                    "-fx-background-radius:8;" +
                    "-fx-cursor:hand;"));

    btn.setOnMouseExited(event ->
            btn.setStyle(
                    "-fx-background-color:#4C1D95;" +
                    "-fx-text-fill:white;" +
                    "-fx-background-radius:8;" +
                    "-fx-cursor:hand;"));
    btn.setOnAction(event -> stage.setScene(Juego.crear(stage, dificultad)));
    btnBox.getChildren().add(btn);
}
        Button btnMusica = new Button("🔊 Música");
        btnMusica.setPrefWidth(100);
        btnMusica.setPrefHeight(30);
        btnMusica.setFont(Font.font(16));
        btnMusica.setOnAction(e -> {

        if(musica.getStatus() == MediaPlayer.Status.PLAYING){
            musica.pause();
            btnMusica.setText("🔇 Música");
        }else{
            musica.play();
            btnMusica.setText("🔊 Música");
    }});

        btnMusica.setStyle(
            "-fx-background-color:#4C1D95;" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:8;" +
            "-fx-cursor:hand;");

        btnMusica.setOnMouseEntered(e ->
        btnMusica.setStyle(
            "-fx-background-color:#6D28D9;" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:8;" +
            "-fx-cursor:hand;"));

        btnMusica.setOnMouseExited(e ->
        btnMusica.setStyle(
            "-fx-background-color:#4C1D95;" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:8;" +
            "-fx-cursor:hand;"));

        root.getChildren().addAll(btnBox, btnMusica);
        return new Scene(root, 420, 520);
    }
}