import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Daniel on 2017-02-03.
 */

//
// To make the main class CSftp.java more clean, we move all the methods to another class.
// This ftpCommands is the class for the FTP Command cases and will have the bulk of the code
// This class will include
//
//
public class ftpCommands {

    public ftpCommands() {
    }

    // Cases for USER and PASS FTP Commands
    public static void userCase(String user, Socket socket) {

        if (socket != null) {
            String user_response = sendRequest("USER " + user + "\r\n", socket);
            System.out.println("--> USER " + user);

            if (user_response.startsWith("230-")) {
                try {
                    OutputStream getOutput = socket.getOutputStream();
                    InputStream getInput = socket.getInputStream();
                    InputStreamReader getInputR = new InputStreamReader(getInput);
                    BufferedReader in = new BufferedReader(getInputR);

                    String s = null;

                    while ((s = in.readLine()) != null) {
                        System.out.println("<--" + s);

                        if (s.startsWith("230 "))
                            break;
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Error 0xFFFD: Control connection I/O error, closing control connection.");
                    quitCase(socket); // calls the quit method to end the connection
                }



            }

                if (user_response.startsWith("331 ")) {
                    System.out.println("<-- " + user_response);
                    System.out.println("Please enter Password: ");
                    Scanner input = new Scanner(System.in);
                    pwCase(input.nextLine(), socket);
                }



            }

        }



    // Case for PASS FTP Command
    public static void pwCase(String pw, Socket socket) {

        pw = pw.toLowerCase().trim();

        if (socket != null) {
            if (pw.matches("pw (.+)") ) {
                String pw_response = sendRequest("PASS " + pw + "\r\n", socket);
                System.out.println("--> PASS " + pw);

                if (pw_response != null) {
                    System.out.println("<-- " + pw_response);
                }
            } else {
                System.out.println("Error 0x002: Incorrect number of arguments.");
            }
        }

    }

    // Case for QUIT FTP Command
    public static void quitCase(Socket socket) {

        if (socket != null) {
            String quit_response = sendRequest("QUIT " + "\r\n", socket);
            System.out.println("--> QUIT ");

            if (quit_response != null)
                System.out.println("<-- " + quit_response);

            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                System.out.println("Error 0xFFFF: Processing Error.  Nothing to Close");
            }

            System.out.println("Closing connection");
            System.exit(-1);
        }
    }

    // Case for PASV and RETR FTP Command
    public static void getCase(String remote, Socket socket) throws IOException {

        if (socket != null) {
            String getPASV_Response = sendRequest("PASV " + "\r\n", socket);
            System.out.println("--> PASV ");

            if (getPASV_Response.startsWith("227 Entering Passive")) {
                Socket data = null;

                data = pasvHelper(data, getPASV_Response);
                String get_response = get(data, remote, socket);

                if (get_response != null) {
                    System.out.println("<-- " + get_response);
                }
            } else {
                System.out.println("<-- " + getPASV_Response);
            }
        }
    }

    // Case for FEAT FTP Command
    public static void featuresCase(Socket socket) {

        if (socket != null) {
            String feature_response = sendRequest("FEAT " + "\r\n", socket);
            System.out.println("--> FEAT ");
            if (feature_response != null) {
                System.out.println(feature_response);

                if (feature_response.startsWith("211")) {
                    try {
                        InputStream getInput = socket.getInputStream();
                        InputStreamReader getInputR = new InputStreamReader(getInput);
                        BufferedReader in = new BufferedReader(getInputR);

                        String s = null;

                        while ((s = in.readLine()) != null) {
                            System.out.println(s);
                            if (s.startsWith("211 "))
                                break;
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        System.out.println("0xFFFD Control connection I/O error, closing control connection.");
                        quitCase(socket); // calls the quit method to end the connection
                    }
                }
            }
        }
    }

    // Case for CWD FTP command
    public static void cdCase(String dir, Socket socket) {

        if (socket != null) {
            String DIR = dir;
            String cd_response = sendRequest("CWD " + DIR + "\r\n", socket);
            System.out.println("--> CWD " + DIR);

            if (cd_response != null) {
                System.out.println("<-- " + cd_response);
            }
        }

    }


    // Case for PASV and List FTP Commands
    public static void dirCase(Socket socket) {

        if (socket != null) {
            // Required to set the FTP from Active -> Passive inorder to retrieve DIR
            String pasv_response = sendRequest("PASV " + "\r\n", socket);
            System.out.println("--> PASV ");

            if (pasv_response != null) {
                System.out.println("<-- " + pasv_response);
                if (pasv_response.startsWith("227 Entering Passive")) {
                    Socket data = null;

                    data = pasvHelper(data, pasv_response);

                    if (data != null) {
                        String dataResponse = dirRetrieve(data, socket);

                        if (dataResponse != null) {
                            System.out.println("<-- " + dataResponse);
                        }
                    } else if (pasv_response.startsWith("421 ")) {
                        try {
                            socket.close();
                            socket = null;
                        } catch (IOException e) {
                            System.out.println("0xFFFF Processing error. " + e.getMessage());
                        }
                    }
                }
            }
        }
    }




    //
    //
    //
    // ----------------------------------------------------------------------------------------------------------
    //
    // Helper Cases:
    //
    //
    //


    // Helper function for get
    public static String get(Socket data, String filename, Socket socket) throws IOException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());
            String s = null;


            try {
                InputStream data_in = data.getInputStream();

                try {
                    String directoryOfFile = System.getProperty("user.dir");
                    out.print("RETR " + filename + "\r\n");

                    directoryOfFile += "\\" + filename;

                    String response = in.readLine();

                    if (response.startsWith("150 ")) {
                        boolean success = write(directoryOfFile, data_in);

                        if (!success) {
                            s = "0x3A2 Data transfer connection to" + data.getRemoteSocketAddress().toString()
                                    + "on port" + data.getPort() + "failed to open.";
                            data.close();
                            return s;
                        } else {
                            s = in.readLine();
                        }
                    } else {
                        return response;
                    }
                } catch (IOException e) {
                    System.out.println("0x3A7 Data transfer connection I/O error, closing data connection.");
                    data.close();
                }
            } catch (IOException e) {
                System.out.println("0x3A7 Data transfer connection I/O error, closing data connection.");
                data.close();
            }
            return s;
        } catch (IOException e) {
            System.out.println("0xFFFD Control connection I/O error, closing control connection.");

        }
        return null;
    }


    // Literally just a helper function for above ^ (dirCase()) makes it less messy.
    // parameter is the socket established in dircase, this is the socket needed to retrieve the list
    public static String dirRetrieve(Socket data, Socket socket) {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());

            try {
                BufferedReader dataReceieved = new BufferedReader(new InputStreamReader(data.getInputStream()));
                out.print("LIST \r\n");
                System.out.println("--> LIST");
                System.out.println(in.readLine());

                String current = null;
                while ((current = dataReceieved.readLine()) != null) {
                    System.out.println(current);
                }

                if ((current = in.readLine()) != null) {
                    System.out.println("<-- " + current);
                }

                data.close();

                System.out.println("Data Socket Closed for Directory");
                return null;

            } catch (IOException e) {
                System.out.println("0x3A7 Data transfer connection I/O error, closing data connection.");
                System.out.println(e.getMessage());
                data.close();
            }
        } catch (IOException e) {
            System.out.println("0xFFFD Control connection I/O error, closing control connection");
            System.out.println(e.getMessage());

            try {
                socket.close();
                socket = null;
            } catch (IOException exc) {
                System.out.println("0xFFFF Processing error. " + exc.getMessage());
            }
        }
        return null;
    }

    public static boolean write(String directoryOfFile, InputStream data_in) {

        try {
            File dest = new File(directoryOfFile);
            FileOutputStream file_out = new FileOutputStream(dest);

            int bytes = 0;
            while (bytes > -1) {
                byte[] buffer = new byte[1024];

                try {
                    // storing the bytes read into a buffer of size 1024
                    bytes = data_in.read(buffer);
                } catch (IOException e) {
                    System.out.println("0x3A7 Data transfer connection I/O error, closing data connection.");
                }
                try {
                    // writing into the dest
                    if (bytes > -1) {
                        file_out.write(buffer, 0, bytes);
                    }
                } catch (IOException e) {
                    System.out.println("0xFFFF Processing error " + e);
                }
            }
            try {
                file_out.flush();
                file_out.close();
                return true;
            } catch (IOException e) {
                System.out.println("0xFFFF Processing error " + e);
            }

        } catch (FileNotFoundException f) {
            System.out.println("0x38E Access to local file " + directoryOfFile + " denied");
        }

        return false;


    }

    // Helper method that fills in Socket information for PASV request since there are two cases
    public static Socket pasvHelper(Socket data, String response) {

        int[] ip = parseIP(response);

        int portN = ip[4] * 256 + ip[5]; // port is 4th octet + 5th octet for PASV mode
        String fullIP = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
        System.out.println(fullIP);

        return openSocket(data, fullIP, portN);

    }

    // designed to open a socket for things that need data from the FTP
    // returns a socket that has established a connection with the FTP, if not, return NULL
    public static Socket openSocket(Socket socket, String hostname, int portNumber) {

        try {
            socket = new Socket(hostname, portNumber);
            socket.setSoTimeout(20000);
            return socket;
        } catch (InterruptedIOException iioe)  {
            System.err.println ("Remote host timed out during read operation");
        } catch (IOException e) {
            System.out.println("Error 0xFFFC: Control connection to " + hostname + " on port " + portNumber + " failed to open." +
                    " Please try again.");
        }
        return null;

    }


    /// a helper function that helps send requests to the server when needed
    public static String sendRequest(String request, Socket socket) {

        try {
            OutputStream getOutput = socket.getOutputStream();
            InputStream getInput = socket.getInputStream();
            InputStreamReader getInputR = new InputStreamReader(getInput);
            PrintStream out = new PrintStream(getOutput);
            BufferedReader in = new BufferedReader(getInputR);


            out.print(request);
            String s = in.readLine();
            return s;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("0xFFFD Control connection I/O error, closing control connection.");
            quitCase(socket); // calls the quit method to end the connection
            return null;

        }
    }


    /// Another helper to help us retrieve the IP Address for the Passive Mode IP.
/// RMBR: THE PORT# IS 4th OCTET * 256 + 5th, should not do math in this because sometimes we need to retrieve
/// the individual IP, should return just the numbers in each octet
    public static int[] parseIP(String passive_request) {

        if (passive_request != null) {
            int[] finalNumbers = new int[6];
            String[] passive_split = passive_request.split(" "); // Should split the words up i.e. [4] is the IP
            String[] ipString = passive_split[4].split(",");


            // time to turn string -> int for the IP !
            finalNumbers[0] = Integer.parseInt(ipString[0].replaceAll("[^\\d.]", ""));


            // first thing in the parsed int needs to be a number denoted with "^" and \d represents numbers 0-9, with
            // "." stating that the rest must also be numbers because its surrounded by []
            System.out.println(finalNumbers[0]);
            finalNumbers[1] = Integer.parseInt(ipString[1]);
            finalNumbers[2] = Integer.parseInt(ipString[2]);
            finalNumbers[3] = Integer.parseInt(ipString[3]);

            // time to turn string -> int for the ports 1 and 2 (the last two indexes in our ipString

            finalNumbers[4] = Integer.parseInt(ipString[4]);
                finalNumbers[5] = Integer.parseInt(ipString[5].replaceAll("[^\\d.]\\.?", ""));
            System.out.println(finalNumbers[5]);

            return finalNumbers;


        }
        return null;
    }
}
