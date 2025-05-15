package udpchat.peer;

import udpchat.network.UDPConnection;

/** Peer C – solo charla con B.*/
public class PeerC {
    public static void main(String[] args){
        UDPConnection conn = UDPConnection.get();

        conn.bind(PeerInfo.PORT_C);
        conn.addHandler((m,ip,port)-> System.out.printf("[C-RX] %s:%d > %s%n",ip,port,m));
        conn.iniciar();

        conn.enviarAsync("Hola B, soy C", PeerInfo.IP_B, PeerInfo.PORT_B);
    }
}
