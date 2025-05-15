package udpchat.network;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;

/* ——— CÓDIGO DEL PROFESOR (sin tocar) me sirvio asi, pero toco aññadir:———
 *  Patrón Singleton + recepción en hilo aparte. */
public final class UDPConnection implements Runnable {

    /* Singleton */
    private static final UDPConnection INSTANCIA = new UDPConnection();

    public static UDPConnection get(){ return INSTANCIA; }

    private UDPConnection(){}                       // nadie más instancia

    /* Estado */
    private DatagramSocket socket;                  // “enchufe” UDP

    private final ExecutorService poolTX =
            Executors.newCachedThreadPool();        // hilos para enviar sin bloquear

    private final List<MessageHandler> oyentes =
            new CopyOnWriteArrayList<>();           // callbacks que avisan “llegó msg”

    private volatile boolean vivo = false;          // controla el hilo RX

    private MessageStrategy estrategia = new PlainTextStrategy();   // default

    /* ---------- BIND ---------- */
    public void bind(int puertoLocal){
        try{
            socket = new DatagramSocket(puertoLocal);
        }catch(SocketException e){
            throw new RuntimeException("Puerto "+puertoLocal+" ocupado :(", e);
        }
    }

    /* ---------- RECEPCIÓN ---------- */
    public void iniciar(){
        if(!vivo){
            vivo = true;
            new Thread(this,"Hilo-RX-UDP").start();
        }
    }

    /* Hilo que escucha mensajes UDP y los pasa a los oyentes */
    /*es el run, Dahh */
    @Override public void run(){
        byte[] buf = new byte[2048];
        while(vivo){
            try{
                DatagramPacket p = new DatagramPacket(buf,buf.length);
                socket.receive(p);                              // ← bloquea
                String msg = estrategia.parse(p.getData(), p.getLength());
                for(MessageHandler h : oyentes){                // notifica listeners
                    h.onMessage(msg, p.getAddress(), p.getPort());
                }
            }catch(IOException e){
                if(vivo) e.printStackTrace();
            }
        }
    }

    /* ---------- REGISTRO DE OYENTES ---------- */
    public void addHandler(MessageHandler h){
        oyentes.add(h);
    }

    /* ---------- ENVÍO ---------- */
    public void enviarAsync(String msg,String ipDest,int puertoDest){
        poolTX.execute(() -> {
            try{
                byte[] data = estrategia.prepare(msg);
                DatagramPacket p=new DatagramPacket(
                        data, data.length,
                        InetAddress.getByName(ipDest),
                        puertoDest);
                socket.send(p);
            }catch(IOException e){ e.printStackTrace(); }
        });
    }

    /* Para cerrar bonito desde la UI */
    public void cerrar(){
        vivo=false;
        if(socket!=null) socket.close();
        poolTX.shutdownNow();
    }

    /* ---------- GETTERS Y SETTERS ---------- */
    public void setStrategy(MessageStrategy m){
        estrategia = m;
    }
}
