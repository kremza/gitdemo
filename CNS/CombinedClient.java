import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CombinedClient {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 65430;

        Scanner scanner = new Scanner(System.in); // Create only once here

        while (true) {
            System.out.println("\nChoose service: hello / file / calc / exit");
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();

            if (command.equals("exit")) {
                System.out.println("Exiting client.");
                break;
            }

            switch (command) {
                case "hello":
                    runHelloClient(host, port, scanner);
                    break;
                case "file":
                    runFileClient(host, port, scanner);
                    break;
                case "calc":
                    runCalcClient(host, port, scanner);
                    break;
                default:
                    System.out.println("Unknown command, please try again.");
            }
        }

        scanner.close();
    }

    private static void runHelloClient(String host, int port, Scanner scanner) {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("hello");
            System.out.print("Enter message to send: ");
            String message = scanner.nextLine();  // Use main scanner
            out.println(message);

            String response = in.readLine();
            System.out.println("Received from server: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runFileClient(String host, int port, Scanner scanner) {
        System.out.print("Enter path to file to send: ");
        String filePath = scanner.nextLine();

        try (Socket socket = new Socket(host, port);
             FileInputStream fis = new FileInputStream(filePath);
             OutputStream out = socket.getOutputStream();
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            pw.println("file"); // Send command first

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            socket.shutdownOutput();  // signals end of file

            String response = in.readLine();
            if (response != null) {
                System.out.println("Server response: " + response);
            } else {
                System.out.println("File sent successfully.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runCalcClient(String host, int port, Scanner scanner) {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("calc"); // Send command first

            String expr;
            while (true) {
                System.out.print("Enter expression (or 'exit' to quit calc): ");
                expr = scanner.nextLine();  // Use main scanner
                if ("exit".equalsIgnoreCase(expr)) {
                    break;
                }
                out.println(expr);
                String response = in.readLine();
                System.out.println("Result: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
