package udpchat.network;

/** Estrategia para serializar/parsear mensajes (texto plano, cifrado, etc.) */
// toca mejorarlo luego esa muy basico
public interface MessageStrategy {

    byte[] prepare(String plain);

    String  parse(byte[] data, int length);
}
