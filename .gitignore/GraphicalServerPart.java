import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by ivan9 on 10.12.2017.
 */
public class GraphicalServerPart extends JFrame implements ActionListener {
    private JPanel all;
    private JTextField portField;
    private JButton startandstopButton;
    private JTextArea chatField, eventField;
    private Server myserver;

    public GraphicalServerPart(int port) {
        super("Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        eventField.setEditable(false);
        chatField.setEditable(false);
        setSize(800, 600);
        setVisible(true);
        startandstopButton.addActionListener(this);
        add(all);
        portField.setText(String.valueOf(port));
        chatField.setText("Chat room.\n");
        eventField.setText("Event log.\n");
    }

    public void actionPerformed(ActionEvent e) {
        if (myserver != null) {
            myserver.FinishWork();
            myserver = null;
            portField.setEditable(true);
            startandstopButton.setText("Start");
            return;
        }
        int tp;
        try {
            tp = Integer.parseInt(portField.getText().trim());
            if (tp < 1 && tp > 2) {
                PrintToEvent("Wrong port!\n");
                return;
            }
        } catch (Exception ex) {
            PrintToEvent("Wrong port!\n");
            return;
        }

        myserver = new Server(tp, this);
        Thread ts = new Thread(new ServerLaunchingThread());
        ts.start();

        portField.setEditable(false);
        startandstopButton.setText("Stop");


    }
    public static void main(String[] args){
        new GraphicalServerPart(11155);
    }

    public void PrintToChat(String msg) {
        chatField.append(msg+"\n");
        chatField.setCaretPosition(chatField.getText().length() - 1);
    }

    public void PrintToEvent(String msg) {
        eventField.append(msg);
        eventField.setCaretPosition(eventField.getText().length() - 1);
    }


    class ServerLaunchingThread implements Runnable {

        public void run() {
            myserver.Start();
            portField.setEditable(true);
            startandstopButton.setText("Start");
            PrintToEvent("Server has been stopped!\n");
            myserver = null;
        }
    }
}
