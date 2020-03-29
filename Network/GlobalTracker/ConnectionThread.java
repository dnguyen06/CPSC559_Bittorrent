package Network.GlobalTracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionThread extends Thread {

    private GlobalTracker gt;
    private boolean running;

    ConnectionThread(GlobalTracker gt) {
        this.gt = gt;
        this.running = false;
    }

    @Override
    public void run() {
        this.running = true;
        try {
            ServerSocket socket = new ServerSocket(1962, 5);
            while (this.running) {
                Socket client = socket.accept();

                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String msg = in.readLine();
                if (msg.equals("connect")) {
                    StringBuilder s = new StringBuilder();
                    String[] nodes = this.gt.getConnectedNodes();
                    for (int i = 0; i < nodes.length; i++) {
                        s.append(nodes[i]);
                        if (i != nodes.length - 1) {
                            s.append(",");
                        }
                    }
                    out.println(s.toString());

                    String address = client.getInetAddress().getCanonicalHostName();
                    this.gt.addNode(address);
                    System.out.println(">> Added " + address);
                }

                in.close();
                out.close();
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void finish() {
        this.running = false;
    }
}
