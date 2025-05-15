package udpchat.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Carga de FXML
        Parent root = FXMLLoader.load(getClass().getResource("/udpchat/peer.fxml"));
        Scene scene = new Scene(root);

        // Logoo ajjjaja, quedo bien o que?
        Image logo = new Image(
                getClass().getResourceAsStream("/udpchat/icono.png")
        );
        stage.getIcons().add(logo);

        // Título y escena
        stage.setTitle("UDP Chat Peer");
        stage.setScene(scene);

        // Toamar el tamaño de la ventana fijo
        stage.setWidth(700);   // ancho más grande
        stage.setHeight(450);  // alto un poco mayor
        stage.setResizable(false);

        // Y mostar
        stage.show();
    }

    //Ve profe, no me mate, si se como hacer un main() en JavaFX
    //funciona, pero JavaFX? se intento lo del bono pero ajola este bien
    //Y espero que lo demas no lo haya hecho tan mal
    public static void main(String[] args) {
        launch(args);
    }
}
