package udpchat.network;

import java.net.InetAddress;

/* ——— CÓDIGO DEL PROFESOR (sin tocar) ——— */
/* Interfaz para el patrón Observer. la estamos usando para notificar a los oyentes
 * que llegó un mensaje UDP. El oyente debe implementar esto. */
@FunctionalInterface
public interface MessageHandler {
    void onMessage(String msg, InetAddress from, int port);
}
