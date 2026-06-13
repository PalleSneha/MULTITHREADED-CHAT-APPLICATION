import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 3: Multithreaded Chat Application (Server-Side)
 * Listens for incoming socket connections and dynamically provisions 
 * background execution threads to manage multiple concurrent client channels.
 */
public class ChatServer {
    // [1] Defines the dedicated virtual hardware port number where this server application binds and listens
    private static final int PORT = 5000;
    
    // [2] Holds references to all active client output streams; must be synchronized manually to ensure safe access
    private static final List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        // [3] Logs server initialization status directly to the host machine terminal console window
        System.out.println("🖥️ Central Chat Server starting up...");
        
        // [4] Instantiates ServerSocket using a try-with-resources statement for guaranteed port safety and automatic cleanup
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // [5] Notifies that the application layer has successfully occupied the designated port system address
            System.out.println("🛰️ Server successfully bound and listening on Port: " + PORT);

            // [6] Enters an infinite blocking execution loop to continuously catch incoming network client connections
            while (true) {
                // [7] Execution freezes here until a remote network client establishes a connection hand-shake protocol
                Socket clientSocket = serverSocket.accept();
                // [8] Logs connection acknowledgement details immediately upon intercepting a client packet interface
                System.out.println("👥 New participant joined the network gateway!");

                // [9] Provisions an output data transmitter pipeline to safely write data directly back down to the newly connected client
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                
                // [10] Acquires an exclusive monitor lock on the shared directory array to prevent multi-threaded data collisions
                synchronized (clientWriters) {
                    // [11] Registers the fresh client writer stream instance directly into the global ecosystem lookup list
                    clientWriters.add(writer);
                }

                // [12] Provisions a decoupled background Thread container mapping to an independent execution path
                Thread clientThread = new Thread(new ClientHandler(clientSocket, writer));
                // [13] Signals the operating system scheduler to instantly allocate resources and execute the custom thread lifecycle
                clientThread.start();
            }
        } catch (IOException e) {
            // [14] Exception catch routine if the system layer blocks port access or experiences network disruptions
            System.out.println("⚠️ Server socket execution anomaly occurred: " + e.getMessage());
        }
    }

    /**
     * Broadcasts a text message package out to every active participant stream registered.
     */
    public static void broadcastMessage(String message, PrintWriter senderWriter) {
        // [15] Enforces strict thread isolation barriers across the data collection frame during iteration loops
        synchronized (clientWriters) {
            // [16] Iterates sequentially through every individual client output channel registered in the system memory map
            for (PrintWriter writer : clientWriters) {
                // [17] Compares reference structures to prevent echo problems so the message is not bounced back to the author
                if (writer != senderWriter) {
                    // [18] Forwards the text data packet across the wire to the targeted remote participant terminal screen
                    writer.println(message);
                }
            }
        }
    }

    /**
     * Internal Runnable implementation processing isolated real-time network streams per user.
     */
    private static class ClientHandler implements Runnable {
        // [19] Local memory variable locking the low-level connection pipeline for this designated user profile
        private final Socket socket;
        // [20] Local reference tracking the specialized text transmission interface assigned to this profile channel
        private final PrintWriter writer;

        // [21] Worker class constructor to establish proper instance reference bindings across independent stacks
        public ClientHandler(Socket socket, PrintWriter writer) {
            this.socket = socket;
            this.writer = writer;
        }

        @Override
        public void run() {
            // [22] Initializes a buffered reader layer inside try-with-resources to capture incoming client input packets smoothly
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                // [23] Dispatches an introductory system instruction directly to the client screen console view
                writer.println("✅ Connection established! Type your identity nickname to start chatting:");
                // [24] Blocking assignment statement reading the identity string payload transmitted over the wire
                String clientName = reader.readLine();
                // [25] Implements clean defensive validation maps to handle empty, whitespace, or broken client names safely
                if (clientName == null || clientName.trim().isEmpty()) {
                    // [26] Fallback naming structure allocating a unique identity signature index using the background thread ID
                    clientName = "Anonymous-" + Thread.currentThread().getId();
                }
                
                // [27] Triggers a global broadcast message notifying all connected players of the new client's identity
                broadcastMessage("📢 " + clientName + " has entered the arena!", writer);
                // [28] Records the official registration parameters locally inside the central tracking logger console
                System.out.println("📝 Registered alias tracking identifier: " + clientName);

                // [29] String memory block allocation to constantly hold incoming stream text entries
                String incomingLine;
                // [30] Continuous listen evaluation loop parsing text lines arriving over the network pipeline from the client
                while ((incomingLine = reader.readLine()) != null) {
                    // [31] Conditional intercept checking mechanism to capture explicit teardown instruction sequences
                    if (incomingLine.equalsIgnoreCase("/exit")) {
                        // [32] Breaks the listening loop sequence to proceed directly into resource de-allocation stages
                        break;
                    }
                    // [33] Relays the intercepted message buffer package outward to everyone currently registered in the room
                    broadcastMessage("[" + clientName + "]: " + incomingLine, writer);
                }

                // [34] Broadcasts a notification informing all active connections that this individual has exited the session
                broadcastMessage("🚪 " + clientName + " has left the chat.", writer);
            } catch (IOException e) {
                // [35] Fail-safe error tracking logic catching sudden drops or force-close disruptions on the socket wire
                System.out.println("ℹ️ Client connection dropped standard processing thread loop.");
            } finally {
                // [36] Final execution block dedicated to safely updating the system map and avoiding memory leaks
                synchronized (clientWriters) {
                    // [37] Unregisters the dead stream reference marker directly out of the active network distribution list
                    clientWriters.remove(writer);
                }
                try {
                    // [38] Explicitly tears down the underlying socket connection pipeline and releases hardware port parameters
                    socket.close();
                } catch (IOException e) {
                    // Fallback block handling hardware stream closing anomalies safely
                }
                // [39] Confirms total cleanup routines have run and that thread worker allocations are successfully recycled
                System.out.println("🔌 Thread resources successfully recycled for disconnected slot.");
            }
        }
    }
}