package udpchat.ui;

import java.util.Objects;

/**
 * este java es un mensaje de chat.
 *
 * Cada instancia guarda:
 *  - emisor: quién envía el mensaje (p.ej. "PeerA", "PeerB"…)
 *  - texto:  el contenido del mensaje
 *  - outgoing: flag que indica si es un mensaje **saliente**
 *              (true) o **entrante** (false)
 *
 * El flag `outgoing` sirve en la UI para:
 *  • Alinear la burbuja a la derecha o a la izquierda
 *  • Aplicar el estilo de color correspondiente
 */
public class Mensaje {
    private final String emisor;
    private final String texto;
    private final boolean outgoing;

    //Nombre del peer que envía (p.ej. "PeerA")
    public Mensaje(String emisor, String texto, boolean outgoing) {
        this.emisor   = Objects.requireNonNull(emisor);
        this.texto    = Objects.requireNonNull(texto);
        this.outgoing = outgoing;
    }

    //nombre del peer emisor
    public String getEmisor()  { return emisor; }

    //Contenido del mensaje
    public String getTexto()   { return texto; }

    // true si es mensaje saliente
    public boolean isOutgoing(){ return outgoing; }
}
