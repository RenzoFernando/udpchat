package udpchat.peer;

/** Mapa central de puertos/IP – pa no enredarse cambiando por toda la app
 *  Tabla de puertos/IP por peer – consolidado mejor pa no enredarse. */
public final class PeerInfo {
    // IPs de los peers, si los puedo cambiar, localhost es 127.0.0.1 y ya para probar toca hacer ipconfig y ponerlos
    public static final String IP_A = "127.0.0.1";
    public static final String IP_B = "127.0.0.1";
    public static final String IP_C = "127.0.0.1";

    // Puertos de los peers, es mejor no cambiarlos
    public static final int PORT_A = 5000;
    public static final int PORT_B = 5001;
    public static final int PORT_C = 5002;

    private PeerInfo(){}   // util class - No instanciable, pilas
}