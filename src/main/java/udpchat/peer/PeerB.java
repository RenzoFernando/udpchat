package udpchat.peer;

import udpchat.network.UDPConnection;

/** Peer B – puede hablar con A y C.*/
//La verdad muy raro pense que todos los peers podian hablar con todos pero segun el diagrama no, entonces asi quedo
public class PeerB {
    public static void main(String[] args){
        UDPConnection conn = UDPConnection.get();

        conn.bind(PeerInfo.PORT_B);
        conn.addHandler((m,ip,port)-> System.out.printf("[B-RX] %s:%d > %s%n",ip,port,m));
        conn.iniciar();

        conn.enviarAsync("Ey A, saludos desde B", PeerInfo.IP_A, PeerInfo.PORT_A);
        conn.enviarAsync("Ey C, saludos desde B", PeerInfo.IP_C, PeerInfo.PORT_C);
    }
}
