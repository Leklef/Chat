import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Created by lenar on 07.05.16.
 */
public class ChatClient {
    static class ChatAccess extends Observable{
        private Socket socket;
        private OutputStream outputStream;
        private static final String CRLF = "\r\n";

        public void notifyObserers(Object arg){
            super.setChanged();
            super.notifyObservers(arg);
        }

        public ChatAccess(String server, int port) throws IOException{
            socket = new Socket(server, port);
            outputStream = socket.getOutputStream();
            Thread receivingThread = new Thread(){
                @Override
                public void run(){
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line;
                        while ((line = reader.readLine())!= null){
                            notifyObserers(line);
                        }
                    }catch (IOException e){
                        notifyObserers(e);
                    }
                }
            };
            receivingThread.start();
        }

        public void send(String text){
            try {
                outputStream.write((text + CRLF).getBytes());
                outputStream.flush();
            }catch (IOException e){
                notifyObserers(e);
            }
        }

        public void close(){
            try{
                socket.close();
            }catch (IOException e){
                notifyObserers(e);
            }
        }
    }

    static class ChatFrame extends JFrame implements Observer{
        private JTextArea textArea;
        private JTextField inputTextField;
        private JButton sendButton;
        private ChatAccess chatAccess;

        public ChatFrame(ChatAccess chatAccess){
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            buildGUI();
        }

        private void buildGUI(){
            textArea = new JTextArea("Введи свое имя!\n",20, 50);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            add(new JScrollPane(textArea), BorderLayout.CENTER);
            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            inputTextField = new JTextField();
            sendButton = new JButton("Send");
            box.add(inputTextField);
            box.add(sendButton);
            ActionListener sendListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String str = inputTextField.getText();
                    if ((str != null) && (str.trim().length() > 0)){
                        if(str.equals("/выход")){
                            frame.dispose();
                        }
                        chatAccess.send(str);
                    }
                    inputTextField.selectAll();
                    inputTextField.requestFocus();
                    inputTextField.setText("");
                }
            };
            inputTextField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    chatAccess.close();
                }
            });
        }

        public void update(Observable o, Object arg){
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textArea.append(finalArg.toString());
                    textArea.append("\n");
                }
            });
        }
    }

    public static JFrame frame;
    public static void main(String[] args) {
        String server = args[0];
        int port = 2222;
        ChatAccess access = null;
        try {
            access = new ChatAccess(server, port);
        }catch (IOException e){
            System.out.println("Нет соединения с сервером " + server + ":" + port);
            e.printStackTrace();
            System.exit(0);
        }
        frame = new ChatFrame(access);
        frame.setTitle("MyChatApp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
