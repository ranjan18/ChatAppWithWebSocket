import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Client extends JFrame{
	Socket socket;
    BufferedReader br;
	PrintWriter out;

    //Component declaration for GUI
    private JLabel heading=new JLabel("Client Area");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font=new Font("Roboto",Font.PLAIN,20);

    public Client(){
        try{
            System.out.println("Sending request to server");
            socket=new Socket("192.168.1.32",7777);
            System.out.println("connection done");
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out=new PrintWriter(socket.getOutputStream());


            createGUI();
            handleEvents();
			startReading();
			// startWriting();

        }catch(Exception e){}
    }
	
	private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
                if(e.getKeyCode()==10){
                    String contentToSend=messageInput.getText();
                    messageArea.append("Me :"+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
            
        });
    }

    private void createGUI() {
        //"this" keyword represents Frame window
        this.setTitle("Client Messager [END]");
        this.setSize(500,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //code for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setIcon(new ImageIcon("chat.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        messageArea.setEditable(false);
        //Frame ka layout set
        this.setLayout(new BorderLayout());
        
        //adding components to frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messageArea);
        JScrollBar verticalScrollBar = jScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void startReading() {
		Runnable r1=()->{
			System.out.println("Reader started..");
            try{
                while(true) {
                    
                        String msg=br.readLine();
                        if(msg.equals("exit")) {
                            //System.out.println("Server terminated the chat..");
                            JOptionPane.showMessageDialog(this, "Server terminated the chat..");
                            messageInput.setEnabled(false);
                            socket.close();
                            break;
                        }
                        //System.out.println("Server :"+msg);
                        messageArea.append("Server :"+msg+"\n");
                }
            }catch(Exception e){
                System.out.println("Connection is closed");
            }
		};
		new Thread(r1).start();
		
	}
	
	public void startWriting() {
		Runnable r2=()->{
			System.out.println("Writer started..");
            try{
                while(!socket.isClosed()) {
                        BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));
                        String content=br1.readLine();
                        out.println(content);
                        out.flush();

                        if(content.equals("exit")){
                            socket.close();
                            break;
                        }
                }
            }catch(Exception e){
            }
		};
		new Thread(r2).start();
	}
	
	public static void main(String[] args) {
		System.out.println("this is client");
		new Client();
	}
}