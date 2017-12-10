import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by ivan9 on 10.12.2017.
 */
public class Client {
    private GraphicalClientPart gui;
    private ObjectInputStream reciever;
    private ObjectOutputStream sender;
    private Socket mysocket;
    private String ip;
    private String nickname;
    private int port;

    public Client(String nickname, String ip, int port, GraphicalClientPart gui) {
        this.gui = gui;
        this.ip = ip;
        this.nickname = nickname;
        this.port = port;
    }

    public boolean Launch() {
        try {
            mysocket = new Socket(ip, port);
        } catch (Exception ex) {
            gui.Print(String.valueOf(ex));
            return false;
        }
        gui.Print("You've has been successfully connected to: " + ip);
        try {
            sender = new ObjectOutputStream(mysocket.getOutputStream());
            reciever = new ObjectInputStream(mysocket.getInputStream());
        } catch (Exception ex) {
            gui.Print(String.valueOf(ex));
            return false;
        }
        Thread th = new Thread(new ClientThread());
        try{
            sender.writeObject(nickname);
        } catch (IOException ex) {
            gui.Print(String.valueOf(ex));
            try {
                CloseConnection();
            } catch (IOException e) {
            }
            return false;
        }
        return true;
    }

    private void CloseConnection() throws IOException {
        gui.ConnectionProblem();
        if(sender!=null){
            sender.close();
        }
        if (reciever!=null){
            reciever.close();
        }
        if(mysocket!=null){
            mysocket.close();
        }

    }
    public void sendSmth(ObjectToSend smth){
        try {
            sender.writeObject(smth);
        } catch (IOException ex) {
            gui.Print(String.valueOf(ex));
        }
    }

    class ClientThread implements Runnable {
        public void run() {
            while (true) {
                try {
                    gui.Print(String.valueOf(reciever.readObject()));
                } catch (IOException ex) {
                    gui.Print("You've been disconnected from Server");
                    gui.ConnectionProblem();
                } catch (ClassNotFoundException ex) {
                }
            }
        }
    }
}
