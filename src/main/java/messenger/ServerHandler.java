package messenger;

import java.net.*;
import java.util.Enumeration;

public class ServerHandler {

    private static InetAddress ipAddress;
    private static int port;
    private static int userID = 1;

    public static String getLocalIPAddress() {
        InetAddress inetAddress = null;
        try {
            for (
                    final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                    interfaces.hasMoreElements();
                    ) {
                final NetworkInterface cur = interfaces.nextElement();
                if (cur.isLoopback()) {
                    continue;
                }
                for (final InterfaceAddress addr : cur.getInterfaceAddresses()) {
                    final InetAddress inet_addr = addr.getAddress();

                    if (!(inet_addr instanceof Inet4Address)) {
                        continue;
                    }
                    inetAddress = inet_addr;
                    System.out.println(
                            "  address: " + inet_addr.getHostAddress() +
                                    "/" + addr.getNetworkPrefixLength()
                    );
                    ipAddress = inet_addr;
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (inetAddress != null) {
            return inetAddress.getHostAddress();
        }
        return "No network Connection!";
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        ServerHandler.port = port;
    }

    public static InetAddress getIpAddress() {
        return ipAddress;
    }

    public static int getUserID() {
        return userID;
    }
}
