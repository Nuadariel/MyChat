import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ivan9 on 10.12.2017.
 */
public class GraphicalClientPart extends JFrame implements ActionListener {
    private JPanel all;
    private JButton Logout,Login,Online;
    private JTextField portField,ipField,msgandnickField;
    private JLabel serverIpAddressLabel,portNumberLabel,nickandmsgLabel;
    private JTextArea msgarea;
    private Client myclient;
    private boolean work;
    private String ip;
    private int port;

    public GraphicalClientPart(String ip, int port){
    super("Client");
        Logout.addActionListener(this);
        Login.addActionListener(this);
        Online.addActionListener(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800,600);
        setVisible(true);
        this.port=port;
        this.ip=ip;
        add(all);
        portField.setText(String.valueOf(port));
        ipField.setText(ip);
        msgandnickField.setText("User");
        msgarea.setText("Massage field\n");
        Logout.setEnabled(false);
        Online.setEnabled(false);
    }

    public void Print(String message) {
        msgarea.append(message+"");
        msgarea.setCaretPosition(msgarea.getText().length() - 1);
    }
    public void ConnectionProblem(){
        Login.setEnabled(true);
        msgandnickField.setEditable(true);
        nickandmsgLabel.setText("Enter your username below:");
        Logout.setEnabled(false);
        Online.setEnabled(false);
        work=false;
    }
public static void main(String[] args){
    new GraphicalClientPart("127.0.0.1",11155);
}
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Login")){

            String tn = msgandnickField.getText().trim();
            String ti = ipField.getText().trim();
            String tp = portField.getText().trim();
            if(tn.length()==0 && ti.length()==0 && tp.length()==0){
                return;
            }
            myclient = new Client(tn,ti,Integer.parseInt(tp),this);
            if(!myclient.Launch()){
                return;
            }
            msgandnickField.setText("");
            nickandmsgLabel.setText("Enter your message below:");
            work=true;
            Logout.setEnabled(true);
            Online.setEnabled(true);
            Login.setEnabled(false);
            ipField.setEditable(false);
            portField.setEditable(false);
            msgandnickField.addActionListener(this);
            return;
        }
        if (e.getActionCommand().equals("Online List")){
            myclient.sendSmth(new ObjectToSend("","online"));
            return;
        }
        if(e.getActionCommand().equals("Logout")){
            myclient.sendSmth(new ObjectToSend("","logout"));
            return;
        }
        if(work){
            myclient.sendSmth(new ObjectToSend(msgandnickField.getText(),"message"));
            msgandnickField.setText("");
            return;
        }
    }
}
