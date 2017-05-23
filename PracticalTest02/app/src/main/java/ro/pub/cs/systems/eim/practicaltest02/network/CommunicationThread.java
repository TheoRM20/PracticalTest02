package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.TimeInformation;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");
            String time = bufferedReader.readLine();
            String clientOp = bufferedReader.readLine();
            if (time == null || time.isEmpty() || clientOp == null || clientOp.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            String[] timeArray = time.split(",");

            if (clientOp.equals("SET")) {
                TimeInformation newAlarm = new TimeInformation(timeArray[0], timeArray[1], timeArray[2]);
                serverThread.setData(time, newAlarm);

                printWriter.println(time);
                printWriter.flush();
                return;
            }

            if (clientOp.equals("RESET")) {
                HashMap<String, TimeInformation> data = serverThread.getData();
                if (data.containsKey(time)) {
                    data.remove(time);
                    printWriter.println("REMOVED " + time);
                    printWriter.flush();
                }

                else {
                    printWriter.println("DID NOT FOUND ALARM " + time);
                    printWriter.flush();
                }

                return;
            }

            if (clientOp.equals("POLL")) {
                HashMap<String, TimeInformation> data = serverThread.getData();
                if (data.containsKey(time)) {
                    Socket socket = new Socket("utcnist.colorado.edu", 13);
                    if (socket == null) {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Could not create socket!");
                        return;
                    }
                    BufferedReader bufferedReaderS = Utilities.getReader(socket);
                    bufferedReaderS.readLine();
                    String line = bufferedReaderS.readLine();
                    String[] timeData = line.split(" ");
                    String[] timeArray2 = timeData[2].split(":");

                    Log.i(Constants.TAG, timeData[2]);

                    TimeInformation alarm = data.get(time);
                    if (Integer.parseInt(alarm.getHours()) > Integer.parseInt(timeArray2[0])) {
                        printWriter.println("NOT EXPIRED");
                    }

                    else if (Integer.parseInt(alarm.getMinutes()) > Integer.parseInt(timeArray2[1])) {
                        printWriter.println("NOT EXPIRED");
                    }

                    else if (Integer.parseInt(alarm.getSeconds()) > Integer.parseInt(timeArray2[2])) {
                        printWriter.println("NOT EXPIRED");
                    }

                    else {
                        printWriter.println("EXPIRED");
                    }

                    printWriter.flush();
                    socket.close();
                }

                else {
                    printWriter.println("DID NOT FOUND ALARM " + time);
                    printWriter.flush();
                }

                return;
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
