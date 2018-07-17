

import java.io.*;
import java.lang.System;
import java.net.Socket;
import java.util.*;


//
// This is an implementation of a simplified version of a command
// line ftp client. The program always takes two arguments
///

public class CSftp {

    static final int MAX_LEN = 255;
    static final int ARG_CNT = 2;
    static Socket socket = null;

    public static void main(String[] args) throws IOException {
        byte cmdString[] = new byte[MAX_LEN];
        String hostName = "";

        // Make instances of other classes:
        BytesToString b2s = new BytesToString();
        CSftpSockets socketInstance = new CSftpSockets();
        ftpCommands ftpCommandsInstance = new ftpCommands();

        // Get command line arguments and connected to FTP
        // If the arguments are invalid or there aren't enough of them
        // then exit.
        //
        // Aside from checking if they are not equal to arg.length of 2
        // We also can take in one argument, so we have to check for length of 1
        // If input is valid then we start up a connection to the server
        if (args.length != ARG_CNT && args.length != 1) {
            System.out.print("Usage: cmd ServerAddress ServerPort\n");
            return;
        } else {
            if (socket == null) {
                if (args.length == ARG_CNT) {
                    hostName = args[0];
                    try {
                        int port = Integer.parseInt(args[1]);
                        socket = socketInstance.ftpConnect(socket, hostName, port);
                    } catch (IOException e) {
                        System.out.println("Error 0x002: Invalid number of arguments.");
                    }
                } else if (args.length == 1) {
                    hostName = args[0];
                    try {
                        int port = 21;
                        socket = socketInstance.ftpConnect(socket, hostName, port);
                    } catch (IOException e) {
                        System.out.println("Error 0x002: Invalid number of arguments.");
                    }
                } else {
                    System.out.println("Error 0x002: Invalid number of arguments.");
                }
            } else {
                System.out.println("Error 0x001: Invalid command");
            }
        }


        try {
            for (int len = 1; len > 0; ) {
                if (cmdString.length != 0) {
                    cmdString = new byte[MAX_LEN];
                }
                System.out.print("csftp> ");
                len = System.in.read(cmdString);

                // We have an upper limit on the chars to be 255, so we can add this constraint
                if (len <= 0 || len > 255)
                    break;

                // Start processing the command here.

                String command = b2s.readableText(cmdString, 0, 255);
                String[] userInput = command.split(" ");


                // We are using this to test for the case when a user just has empty spaces
                // Before, this would crash the program.
                if (userInput.length != 0) {

                    // We use switch cases for each of the user inputs
                    // Each input arguement should be related to a FTP Command
                    switch (userInput[0].toLowerCase().trim()) {

                        case "user":
                            // user case
                            if (userInput.length == 2)
                                ftpCommandsInstance.userCase(userInput[1], socket);
                            else {
                                System.out.println("Error 0x002: Invalid number of arguments");
                            }
                            break;

                        case "pw":
                            // pw case
                            if (userInput.length == 2)
                                ftpCommandsInstance.pwCase(userInput[1], socket);
                            else {
                                System.out.println("Error 0x002: Invalid number of arguments");
                            }
                            break;


                        case "quit":
                            if (userInput.length == 1) {
                                ftpCommandsInstance.quitCase(socket);
                            } else {
                                System.out.println("Please type 'quit' if you want to end session");
                            }
                            break;

                        case "get":
                            if (userInput.length == 2)
                                ftpCommandsInstance.getCase(userInput[1], socket);
                            else {
                                System.out.println("Error 0x002: Invalid number of arguments");
                            }
                            break;
                        case "features":
                            if (userInput.length == 1)
                                ftpCommandsInstance.featuresCase(socket);
                            else {
                                System.out.println("Error 0x002: Invalid number of arguments");
                            }
                            break;
                        case "cd":
                            if (userInput.length == 2) {
                                ftpCommandsInstance.cdCase(userInput[1], socket);
                            } else {
                                System.out.println("Error 0x002 Incorrect number of arguments");
                            }
                            break;
                        case "dir":
                            if (userInput.length == 1)
                                ftpCommandsInstance.dirCase(socket);
                            else {
                                System.out.println("Error 0x002: Invalid number of arguments");
                            }
                            break;
                        default:
                            // User inputs starting with # are silently ignored
                            if (userInput[0].startsWith("#")) {
                                break;
                            } else {
                                System.out.println("0x001 Invalid command.");
                            }
                    }
                } else {
                    System.out.println("Please enter a valid command.");
                }
            }
        } catch (IOException e) {
            System.err.println("998 Input error while reading commands, terminating.");
        }
    }
}