package messenger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/**
 *
 * @author Mevan
 */
public class ReceiverHandler implements Runnable{

    private int port;

    public ReceiverHandler(int port){
        this.port=port;
    }

    @Override
    public void run() {
        System.out.println("Receiver is running!");
        ServerSocket receiverSocket;
        Socket senderSocket;
        try {
            receiverSocket = new ServerSocket(port);
            while (true) {
                try {
                    senderSocket = receiverSocket.accept();
                    Thread t = new Thread(new Receiver(senderSocket));
                    t.start();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}