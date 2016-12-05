import java.io.*;
import java.net.Socket;

/**
 * Created by lenar on 07.05.16.
 */
public class clientThread extends Thread {
    private String clientName = null;
    private BufferedReader is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;

    public clientThread(Socket clientSocket, clientThread[] threads){
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run(){
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;
        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            String name;
            while (true) {
                os.println("Введи свое имя!");
                name = is.readLine().trim();
                if (name.indexOf('@') == -1) {
                    os.println("Здравствуй, "+name+"! Чтобы отправить сообщение нужно нажать Enter или кнопку Send, чтобы  покинуть чат напиши '/выход'");
                    break;
                } else {
                    os.println("Имя не должно содержать '@'");
                }
            }
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        clientName = "@" + name;
                        break;
                    }
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].os.println("Новый пользователь " + name + " присоединился к чату! Ура!");
                    }
                }
            }
            while (true) {
                String line = is.readLine();
                if (line.startsWith("/выход")) {
                    break;
                }
                else {
                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].os.println("<" + name + "> " + line);
                            }
                        }
                    }
                }
            }
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null) {
                        threads[i].os.println("Пользователь " + name
                                + " покинул чат. Печалька(");
                    }
                }
            }
            os.println(name + ", давай, до свидания");
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
            is.close();
            os.close();
            clientSocket.close();
            System.exit(0);
        } catch (IOException e) {
        }
    }
}
