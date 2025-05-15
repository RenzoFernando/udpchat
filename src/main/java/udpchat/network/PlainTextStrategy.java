package udpchat.network;

/* ——— CÓDIGO DEL PROFESOR ——— */
/* Estrategia para serializar/parsear mensajes (texto plano, cifrado, etc.) */
public class PlainTextStrategy implements MessageStrategy {

    @Override public byte[] prepare(String p){
        return p.getBytes();
    }

    @Override public String  parse(byte[] d,int len){
        return new String(d,0,len).trim();
    }

}
