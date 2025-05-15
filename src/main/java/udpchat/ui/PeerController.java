package udpchat.ui;

// Importaciones de animaciones y utilidades de JavaFX
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

// Clases de red y definición de puertos/IP
import udpchat.network.UDPConnection;
import udpchat.peer.PeerInfo;

import java.util.Map;

/** Controlador de la interfaz gráfica del chat UDP.
 * Permite elegir el peer, enviar mensajes y ver la lista de mensajes.
 */
public class PeerController {

    // Elementos inyectados desde el archivo FXML
    @FXML private ChoiceBox<String> peerChoice;    // Selector de "quién soy" (PeerA, PeerB, PeerC)
    @FXML private ChoiceBox<String> destChoice;    // Selector de destinatario
    @FXML private TextField outMsg;                // Campo de texto para componer mensaje
    @FXML private ListView<Mensaje> mensajesList;  // Lista donde se muestran los mensajes
    @FXML private Button btnReady;                 // Botón para confirmar selección de peer

    // Conexion UDP compartida (singleton)
    private final UDPConnection conn = UDPConnection.get();
    private String currentPeer;                    // Guarda el peer seleccionado

    // Mapas estáticos de puertos e IPs según peer
    private static final Map<String,Integer> PUERTOS = Map.of(
            "PeerA", PeerInfo.PORT_A,
            "PeerB", PeerInfo.PORT_B,
            "PeerC", PeerInfo.PORT_C
    );
    private static final Map<String,String> IPS = Map.of(
            "PeerA", PeerInfo.IP_A,
            "PeerB", PeerInfo.IP_B,
            "PeerC", PeerInfo.IP_C
    );

    /**
     * Método llamado automáticamente por JavaFX tras cargar el FXML.
     * Aquí se bloquea el chat hasta elegir peer y se configura la apariencia
     * de cada celda de mensaje (burbujas + animaciones).
     */
    @FXML
    public void initialize() {
        // Deshabilita temporalmente el UI de chat hasta que el usuario elija su peer
        bloqueaChat(true);

        // Configura cómo se renderiza cada elemento de la ListView de mensajes
        mensajesList.setCellFactory(lv -> new ListCell<Mensaje>() {
            @Override
            protected void updateItem(Mensaje msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    // Si no hay mensaje, no mostrar nada
                    setGraphic(null);
                    return;
                }

                // 1) Creamos la burbuja como Label y aplicamos clases CSS según el emisor
                Label bubble = new Label(msg.getTexto());
                bubble.getStyleClass().addAll(
                        "chat-bubble",
                        switch (msg.getEmisor().trim()) {
                            case "PeerA" -> "peer-a";
                            case "PeerB" -> "peer-b";
                            default      -> "peer-c";
                        }
                );
                bubble.setWrapText(true); // permite que el texto salte de línea

                // 2) Límite de ancho: máximo 75% del ancho de la ListView
                bubble.maxWidthProperty().bind(lv.widthProperty().multiply(0.75));

                // 3) Contenedor HBox que ocupa todo el ancho de la celda y alinea la burbuja
                HBox container = new HBox(bubble);
                container.prefWidthProperty().bind(lv.widthProperty());
                container.setPadding(new Insets(4, 4, 4, 4)); // margen interno alrededor
                // Alinea a la derecha si es mensaje propio, izquierda si es recibido
                container.setAlignment(msg.isOutgoing() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

                // Asigna el HBox como gráfico de la celda
                setGraphic(container);

                // 4) Animación de entrada: deslizamiento + fade
                TranslateTransition tt = new TranslateTransition(Duration.millis(200), bubble);
                tt.setFromX(msg.isOutgoing() ? 50 : -50);
                tt.setToX(0);
                FadeTransition ft = new FadeTransition(Duration.millis(200), bubble);
                ft.setFromValue(0);
                ft.setToValue(1);
                new ParallelTransition(tt, ft).play();
            }
        });

        // Filtra y consume cualquier scroll horizontal en la lista de mensajes
        mensajesList.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaX() != 0) {
                event.consume();
            }
        });

        outMsg.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onSend();
                event.consume();  // evita que haga un “beep” o inserte salto de línea
            }
        });
    }



    /**
     * Maneja el evento de presionar "¡Listo!"
     * Fija el peer actual, configura la conexión UDP y habilita el chat.
     */
    @FXML
    private void onReady() {
        String yo = peerChoice.getValue();
        if (yo == null) return;
        currentPeer = yo;

        // Deshabilita el selector de peer y el botón
        peerChoice.setDisable(true);
        btnReady.setDisable(true);

        // Bind de UDPConnection al puerto correspondiente
        conn.bind(PUERTOS.get(yo));
        // Añade handler para mensajes entrantes
        conn.addHandler((msg, ip, port) ->
                Platform.runLater(() ->
                        addMensaje(new Mensaje(portToName(port), msg, false))
                )
        );
        conn.iniciar(); // inicia el hilo de escucha

        // Configura destinatarios posibles según el peer elegido
        if (yo.equals("PeerA") || yo.equals("PeerC")) {
            destChoice.setItems(FXCollections.observableArrayList("PeerB"));
        } else {
            destChoice.setItems(FXCollections.observableArrayList("PeerA","PeerC"));
        }

        // Habilita la UI de chat (destino + campo de texto)
        bloqueaChat(false);
    }

    /**
     * Evento de clic en "Enviar".
     * Envía el mensaje por UDP y lo añade a la lista con outgoing=true.
     */
    @FXML
    private void onSend() {
        String dest = destChoice.getValue();
        String txt  = outMsg.getText().trim();
        if (dest == null || txt.isEmpty()) return;

        // Envía de forma asíncrona por UDP
        conn.enviarAsync(txt, IPS.get(dest), PUERTOS.get(dest));
        // Muestra el mensaje en la UI
        addMensaje(new Mensaje(currentPeer, txt, true));
        outMsg.clear(); // limpia el campo de texto
    }

    /** Limpia todos los mensajes de la lista. */
    @FXML
    private void onClear() {
        mensajesList.getItems().clear();
    }

    /** Cierra la conexión y sale de la aplicación. */
    @FXML
    private void onExit() {
        conn.cerrar();
        Platform.exit();
    }

    /** Añade un mensaje a la ListView. */
    private void addMensaje(Mensaje m) {
        mensajesList.getItems().add(m);
        // Fuerza el scroll hasta el mensaje recién añadido
        mensajesList.scrollTo(m);
    }

    /** Habilita o deshabilita la parte de chat (destino + campo de texto). */
    private void bloqueaChat(boolean state) {
        destChoice.setDisable(state);
        outMsg.setDisable(state);
    }

    /** Traduce un número de puerto a su nombre de peer. */
    private String portToName(int port) {
        return switch (port) {
            case PeerInfo.PORT_A -> "PeerA";
            case PeerInfo.PORT_B -> "PeerB";
            case PeerInfo.PORT_C -> "PeerC";
            default             -> "Unknown";
        };
    }
}
