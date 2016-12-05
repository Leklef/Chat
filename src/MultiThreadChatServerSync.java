import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lenar on 07.05.16.
 */
public class MultiThreadChatServerSync {
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private static final int maxClientCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientCount];

    public static void main(String[] args) {
        int portNumber = 2222;
        if (args.length < 1){}
        else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }
        try {
            serverSocket = new ServerSocket(portNumber);
        }catch (IOException e){
            System.out.println(e);
        }
        while (true){
            try{
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientCount; i++){
                    if (threads[i] == null){
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        break;
                    }
                }
                if (i==maxClientCount){
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Сервер слишком занят. Приходи позже).");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e){
                System.out.println(e);
            }
        }
    }
}
