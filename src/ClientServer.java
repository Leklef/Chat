import javax.swing.*;

/**
 * Created by lenar on 07.05.16.
 */
public class ClientServer {
    public static void main(String[] args) {
        Object[] selectioValue = {"Server", "Client"};
        String initialSection = "Server";
        Object selection = JOptionPane.showInputDialog(null, "Login as:", "MyChatApp", JOptionPane.QUESTION_MESSAGE, null, selectioValue, initialSection);
        if (selection.equals("Server")){
            String[] arguments = new String[]{};
            new MultiThreadChatServerSync().main(arguments);
        }
        else{
            if (selection.equals("Client")){
                String IPServer = JOptionPane.showInputDialog("Enter the Server IP adress");
                String[] arguments = new String[]{IPServer};
                new ChatClient().main(arguments);
            }
        }
    }
}
