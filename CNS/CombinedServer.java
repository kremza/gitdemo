import java.io.*;
import java.net.*;

public class CombinedServer {
    public static void main(String[] args) {
        int port = 65430;  // common port for all services
        System.out.println("Starting Combined Server on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("\nWaiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // First, read the command from client (hello/file/calc)
                String command = in.readLine();
                if (command == null) {
                    clientSocket.close();
                    continue;
                }

                switch (command.toLowerCase()) {
                    case "hello":
                        handleHello(in, out);
                        break;
                    case "file":
                        handleFileTransfer(clientSocket);
                        break;
                    case "calc":
                        handleCalculator(in, out);
                        break;
                    default:
                        out.println("Unknown command");
                }

                clientSocket.close();
                System.out.println("Client disconnected.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleHello(BufferedReader in, PrintWriter out) throws IOException {
        String msg = in.readLine();
        System.out.println("Received (hello): " + msg);
        out.println("Hello from server!");
    }

    private static void handleFileTransfer(Socket clientSocket) throws IOException {
        System.out.println("Receiving file...");
        InputStream in = clientSocket.getInputStream();
        FileOutputStream fos = new FileOutputStream("received_file");

        byte[] buffer = new byte[1024];
        int bytesRead;
        // Note: client must close output stream to end transfer
        while ((bytesRead = in.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
        System.out.println("File received and saved as 'received_file'");
    }

    private static void handleCalculator(BufferedReader in, PrintWriter out) throws IOException {
        System.out.println("Calculator mode started");

        String expression;
        while ((expression = in.readLine()) != null) {
            System.out.println("Calculating: " + expression);
            try {
                double result = evaluate(expression);
                out.println(result);
            } catch (Exception e) {
                out.println("Error: Invalid expression");
            }
        }
    }

    // Simple evaluator supporting +, -, *, /
    private static double evaluate(String expr) throws Exception {
        expr = expr.replaceAll("\\s", "");
        return parseAddSub(expr);
    }

    // Parser helpers
    private static int index = 0;
    private static String expression;

    private static double parseAddSub(String expr) throws Exception {
        expression = expr;
        index = 0;
        double value = parseTerm();
        while (index < expression.length()) {
            char op = expression.charAt(index);
            if (op == '+' || op == '-') {
                index++;
                double nextTerm = parseTerm();
                if (op == '+') value += nextTerm;
                else value -= nextTerm;
            } else {
                break;
            }
        }
        return value;
    }

    private static double parseTerm() throws Exception {
        double value = parseFactor();
        while (index < expression.length()) {
            char op = expression.charAt(index);
            if (op == '*' || op == '/') {
                index++;
                double nextFactor = parseFactor();
                if (op == '*') value *= nextFactor;
                else value /= nextFactor;
            } else {
                break;
            }
        }
        return value;
    }

    private static double parseFactor() throws Exception {
        StringBuilder sb = new StringBuilder();
        // Handle numbers, including decimals
        if (index < expression.length() && (expression.charAt(index) == '+' || expression.charAt(index) == '-')) {
            // handle unary + or -
            sb.append(expression.charAt(index));
            index++;
        }
        while (index < expression.length() &&
                (Character.isDigit(expression.charAt(index)) || expression.charAt(index) == '.')) {
            sb.append(expression.charAt(index));
            index++;
        }
        if (sb.length() == 0) throw new Exception("Expected number at position " + index);
        return Double.parseDouble(sb.toString());
    }
}
