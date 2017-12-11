import javax.imageio.IIOException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ivan9 on 10.12.2017.
 */
public class Server {
    private GraphicalServerPart gui;
    private static int Pn;
    private int port;
    private String ip;
    private boolean work;
    private ArrayList<ClientThread> clients;
    private SimpleDateFormat time;

    public Server(int port, GraphicalServerPart gui) {
        time = new SimpleDateFormat("HH:mm:ss");
        this.port = port;
        this.gui = gui;
        clients = new ArrayList<ClientThread>();
    }

    public synchronized void DeleteUser(int pn){
        for (int i=0;i<clients.size();i++){
            ClientThread t = clients.get(i);
            if(t.pn == pn){
                clients.remove(i);
                return;
            }
        }
    }

    public void Start() {
        work = true;
        ServerSocket mss;
        try {
            mss = new ServerSocket(port);
            gui.PrintToEvent("Server waiting for Clients on port:" + port + ".\n");
            while (work) {
                Socket s = mss.accept();
                if (!work)
                    break;
                ClientThread newClient = new ClientThread(s);
                clients.add(newClient);
                newClient.start();
            }
            try {
                mss.close();
                for (int i = 0; i < clients.size(); i++) {
                    ClientThread t = clients.get(i);
                    try {
                        t.reciever.close();
                        t.sender.close();
                        t.mysocket.close();
                    } catch (IOException ex) {
                    }
                }
            } catch (Exception ex) {
            }
        } catch (IOException ex) {
            gui.PrintToEvent(ex + "\n");
        }
    }
    public void FinishWork() {
        work=false;

    }

    public synchronized void SayToAll(String msg){
        String comlpmsg = time.format(new Date())+" "+msg;
        gui.PrintToChat(comlpmsg);
        for(int i = clients.size();--i>=0;){
            ClientThread t = clients.get(i);
            try {
                if(!t.SendIfICan(comlpmsg+"\n")){
                    clients.remove(i);
                    gui.PrintToEvent(t.nickname+" has been disconnected\n");
                }
            } catch (IOException e) {
            }

        }
    }

    class ClientThread extends Thread {
        int pn;
        ObjectInputStream reciever;
        ObjectOutputStream sender;
        Socket mysocket;
        String nickname;
        ObjectToSend clienmessage;

        public ClientThread(Socket mysocket) {
            this.mysocket = mysocket;
            pn = Pn;
            Pn++;
            try {
                reciever = new ObjectInputStream(mysocket.getInputStream());
                sender = new ObjectOutputStream(mysocket.getOutputStream());
                nickname = String.valueOf(reciever.readObject());
                gui.PrintToEvent(nickname + " just connected.\n");
            } catch (IOException ex) {
            } catch (ClassNotFoundException ex) {
            }
        }

        public void run() {
            boolean work = true;
            while (work) {
                try {
                    clienmessage = (ObjectToSend) reciever.readObject();
                } catch (IOException ex) {
                    gui.PrintToEvent(nickname+" has been suddenly disconnected: " + String.valueOf(ex)+"\n");
                    break;
                } catch (ClassNotFoundException ex) {
                    break;
                }
                String messagecont =clienmessage.getContent();
                String type =clienmessage.getType();
                if(type.equals("message")){
                    SayToAll(nickname+": "+messagecont);
                    continue;
                }else if(type.equals("logout")){
                    gui.PrintToEvent(nickname+" disconnected.\n");
                    work=false;
                    continue;
                }else if(type.equals("online")){
                    try {
                        SendIfICan("Current online users:\n");
                    } catch (IOException e) {
                    }
                    for (int i = 0;i<clients.size();i++){
                        ClientThread t =clients.get(i);
                        try {
                            SendIfICan(i+1+") "+t.nickname+"\n");
                        } catch (IOException e) {

                        }
                        continue;
                    }
                }

            }
            DeleteUser(pn);
            try {
                if(sender != null) sender.close();
            }
            catch(Exception e) {}
            try {
                if(reciever != null) reciever.close();
            }
            catch(Exception e) {};
            try {
                if(mysocket != null) mysocket.close();
            }
            catch (Exception e) {}

        }
        private boolean SendIfICan(String msg) throws IOException {
            if(!mysocket.isConnected()){
                if(reciever != null){
                    reciever.close();
                }
                if(sender != null){
                    sender.close();
                }
                if(mysocket!=null){
                    mysocket.close();
                }
                return false;
            }
            try {
                sender.writeObject(msg);
            }catch (IOException ex){
                gui.PrintToEvent("Problem with sending message: "+String.valueOf(ex)+"\n");
            }
            return true;
        }
    }

}

