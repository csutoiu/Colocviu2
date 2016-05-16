package ro.pub.cs.systems.pdsd.practicaltest02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;
import android.widget.TextView;

public class Client extends Thread {

    private String   address;
    private int      port;

    private String city;
    private String option;
    private TextView info;

    private Socket socket;

    public Client(String address, int port, String city, String option, TextView info) {
        this.address = address;
        this.port = port;
        this.city = city;
        this.option = option;
        this.info = info;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT] Could not create socket!");
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter    = Utilities.getWriter(socket);
            if (bufferedReader != null && printWriter != null) {
                printWriter.println(city + "," + option);
                printWriter.flush();
                String infoFromSV;
                while ((infoFromSV = bufferedReader.readLine()) != null) {
                    final String SVSays = infoFromSV;
                    info.post(new Runnable() {
                        @Override
                        public void run() {
                            info.append("[SERVER]: " + SVSays + "\n");
                        }
                    });
                }
            } else {
                Log.e(Constants.TAG, "[CLIENT] BufferedReader / PrintWriter are null!");
            }
            socket.close();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

}
