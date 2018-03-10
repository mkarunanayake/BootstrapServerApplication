package messenger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Mevan
 */
public class ReceiverHandler implements Runnable{

    private int port;
    private ExecutorService receiverService;

    public ReceiverHandler(int port){
        this.port=port;
        receiverService = new ThreadPoolExecutor(20, 100, 1, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
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
                    receiverService.execute(new Receiver(senderSocket));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}