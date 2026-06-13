import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Task 3: Multithreaded Chat Application (Client-Side)
 * Connects directly to the chat host server and provisions a secondary background 
 * input monitoring thread to read inbound text packets while concurrently taking user inputs.
 */
public class ChatClient {
    // [1] Target server network coordinates pointing to the local host machine loopback interface
    private static final String SERVER_IP = "127.0.0.1"; 
    // [2] Dedicated TCP communication channel port matching the central server endpoint definition
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        // [3] Printing connection initiation status telemetry notification onto the client terminal
        System.out.println("🔌 Attempting connection to Central Gateway at " + SERVER_IP + ":" + SERVER_PORT);

        // [4] Initializing network Socket link inside try-with-resources context to mandate automatic closure
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            // [5] Confirming successful network bridge establishment and documenting application exit sequence
            System.out.println("🚀 Connected successfully! (Type /exit anytime to drop connection)\n");

            // [6] Initializing character input stream reader pipeline bound to the network socket receiver channel
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // [7] Initializing autoflush output text message transmitter pipe aimed directly at the socket output stream
            PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);

            // [8] Initializing secondary worker process thread explicitly mapped to isolate inbound socket read blocks
            Thread inboundListenerThread = new Thread(() -> {
                try {
                    // [9] Buffer reference tracking active real-time global broadcast message packet transfers
                    String incomingBroadcast;
                    // [10] Infinite blocking listener evaluation loop running until the network stream channel severs
                    while ((incomingBroadcast = serverReader.readLine()) != null) {
                        // [11] Directing incoming network broadcast content onto standard client output screen console
                        System.out.println(incomingBroadcast);
                    }
                } catch (IOException e) {
                    // [12] Processing fallback error reporting triggers when server connectivity terminates abruptly
                    System.out.println("❌ Disconnected from central system chat stream.");
                }
            });
            // [13] Dispatching worker listener lifecycle sequence to independent CPU runtime context schedules
            inboundListenerThread.start();

            // [14] Initializing keyboard entry scanner instance within try-with-resources boundary limits
            try (Scanner scanner = new Scanner(System.in)) {
                // [15] Primary program execution thread captures keystroke buffer blocks continuously
                while (scanner.hasNextLine()) {
                    // [16] Extracting string content data line from manual user interactive entry controls
                    String userInput = scanner.nextLine();
                    
                    // [17] Pushing formatted text data across the network socket directly down to server interface layers
                    outStream.println(userInput);
                    
                    // [18] Evaluating loop breaking conditional logic maps to facilitate clean session teardowns
                    if (userInput.equalsIgnoreCase("/exit")) {
                        // [19] Breaking current loop sequencing execution tracking boundaries cleanly
                        break;
                    }
                }
            } // [20] Automatic scanner resource container cleanup sequence triggers precisely here on block exit
            
            // [21] Echoing clean termination summary updates back to screen console layers
            System.out.println("🔌 Session disconnected safely.");

        } catch (IOException e) {
            // [22] Executing fallback print notices if target host machine refuses port synchronization requests
            System.out.println("⚠️ Unable to establish system connection link: " + e.getMessage());
        }
    } // [23] Automatic parent Socket asset collection reclamation handles memory pool resets right here
}
