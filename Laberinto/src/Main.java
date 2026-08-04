import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage){
        stage.setTitle("Laberinto");
        stage.setScene(menu.create(stage));
        stage.setResizable(true);
        stage.show();
    }
    public static void main(String[] args){
        launch(args);
    }
}
