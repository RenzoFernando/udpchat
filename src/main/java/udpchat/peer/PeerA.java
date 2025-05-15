package udpchat.peer;

import udpchat.network.UDPConnection;

/** Peer A – solo chatea con B (regla del diagrama). */
public class PeerA {
    public static void main(String[] args){
        UDPConnection conn = UDPConnection.get();

        conn.bind(PeerInfo.PORT_A);
        conn.addHandler((m,ip,port)-> System.out.printf("[A-RX] %s:%d > %s%n",ip,port,m));
        conn.iniciar();

        conn.enviarAsync("Hola B, soy A", PeerInfo.IP_B, PeerInfo.PORT_B);
    }
}
