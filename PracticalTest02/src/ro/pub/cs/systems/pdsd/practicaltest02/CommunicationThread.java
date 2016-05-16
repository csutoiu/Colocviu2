package ro.pub.cs.systems.pdsd.practicaltest02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CommunicationThread extends Thread {

    private Server serverThread;
    private Socket socket;

    public CommunicationThread(Server serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket       = socket;
    }


    @Override
    public void run() {
        if (socket != null) {
            try {
                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader != null && printWriter != null) {
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client!");

                    String data = bufferedReader.readLine();

                   /* chat.post(new Runnable() {
                        @Override
                        public void run() {
                            chat.append("[CLIENT]: " + CLSays + "\n");
                        }
                    });*/

                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Client says: " + data);

                    String[] params = data.split(",");
                    String city = params[0];
                    String option = params[1];
                    String resp = null;
                    
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                    HttpClient httpClient = new DefaultHttpClient();
                    
                    /*HttpPost httpPost = new HttpPost(Constants.WEATHER_SERVICE_INTERNET_ADDREESS);
                    List<NameValuePair> parameter = new ArrayList<NameValuePair>();   
                    parameter.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, city));
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(parameter, HTTP.UTF_8);
                    httpPost.setEntity(urlEncodedFormEntity);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                    Log.d(Constants.TAG, pageSourceCode);*/
                    
                    HttpGet httpGet = new HttpGet(Constants.WEATHER_SERVICE_INTERNET_ADDREESS
							+ "?" + Constants.QUERY_ATTRIBUTE + "=" + city);
					ResponseHandler<String> responseHandlerGet = new BasicResponseHandler();
					try {
						resp = httpClient.execute(httpGet, responseHandlerGet);
						if (resp != null) {
							Document document = Jsoup.parse(resp);
							Element element = document.child(0);
				            Elements scripts = element.getElementsByTag(Constants.SCRIPT_TAG);
							
				            for (Element script: scripts) {
				                String scriptData = script.data();
				                if (scriptData.contains(Constants.SEARCH_KEY)) {
				                	int position = scriptData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
				                	scriptData = scriptData.substring(position);
				                	JSONObject content = new JSONObject(scriptData);
				                	JSONObject currentObservation = content.getJSONObject(Constants.CURRENT_OBSERVATION);
				                	String temperature = currentObservation.getString(Constants.TEMPERATURE);
				                	String windSpeed = currentObservation.getString(Constants.WIND_SPEED);
				                	String condition = currentObservation.getString(Constants.CONDITION);
				                	String pressure = currentObservation.getString(Constants.PRESSURE);
				                	String humidity = currentObservation.getString(Constants.HUMIDITY);
				                  
				                }
				            }
						}
						
					} catch (ClientProtocolException clientProtocolException) {
						Log.e(Constants.TAG, clientProtocolException.getMessage());
						if (Constants.DEBUG) {
							clientProtocolException.printStackTrace();
						}
					} catch (IOException ioException) {
						Log.e(Constants.TAG, ioException.getMessage());
						if (Constants.DEBUG) {
							ioException.printStackTrace();
						}
					}

                    printWriter.println(resp);
                    printWriter.flush();
                }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        } else {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
        }

    }
}
