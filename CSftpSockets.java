import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Daniel on 2017-02-03.
 */
public class CSftpSockets {

    public CSftpSockets() {
    }

    // Used to connect to ftp server through sockets.
    public static Socket ftpConnect(Socket socket, String hostname, int portNumber) throws IOException {
        try {
            socket = new Socket(hostname, portNumber);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if (in != null) {
                String s;
                while ((s = in.readLine()) != null) {
                    System.out.println(s);
                    if (s.startsWith("220 "))
                        break;
                }
            }
            return socket;
        } catch (IOException e) {
            System.out.println("Error 0xFFFC: Control connection to " + hostname + " on port " + portNumber + " failed to open." +
                    " Please try again.");
        }
        return null;
    }

}
