package ro.pub.cs.systems.pdsd.practicaltest02;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends Activity {
	private Spinner spinner;
    private static final String[]paths = {"temperature", "humidity", "pressure", "wind_speed", "all"};
    private String option;
    
    private EditText server_port, client_address, client_port, city;
    private Button connect_server, get_weather;
    private TextView info;
    
    private Server server;
    private Client client;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practical_test02_main);
		
		server_port = (EditText)findViewById(R.id.editText1);
		client_address = (EditText)findViewById(R.id.editText2);
		client_port = (EditText)findViewById(R.id.editText3);
		city = (EditText)findViewById(R.id.editText4);
		connect_server = (Button)findViewById(R.id.button1);
		connect_server.setOnClickListener(new ConnectServerClickListener());
		
		get_weather = (Button)findViewById(R.id.button2);
		get_weather.setOnClickListener(new GetWeatherForecastButtonClickListener());
		info = (TextView)findViewById(R.id.textView3);
		
		
		spinner = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(PracticalTest02MainActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}
	
	class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	        option = parent.getItemAtPosition(pos).toString();
	        Log.d(Constants.TAG, "Option is " + option);
	    }
	    public void onNothingSelected(AdapterView<?> parent) {
	      // Dummy
	    }
	}
	
	class ConnectServerClickListener implements Button.OnClickListener {
		  @Override
		  public void onClick(View view) {
		    String serverPort = server_port.getText().toString();
		    if (serverPort == null || serverPort.isEmpty()) {
		    	Toast.makeText(getApplicationContext(), "Server port should be filled!",
		    		  		 Toast.LENGTH_SHORT).show();
		    	return;
		    }
		    server = new Server(Integer.parseInt(serverPort));
		    if (server.getServerSocket() != null) {
		    	server.start();
		    } else {
		      Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not creat server thread!");
		    }
		  }
	}
	
	class GetWeatherForecastButtonClickListener implements Button.OnClickListener {
		
		@Override
		public void onClick(View view) {
			String clientAddress = client_address.getText().toString();
		    String clientPort    = client_port.getText().toString();
		    if (clientAddress == null || clientAddress.isEmpty() || clientPort == null || clientPort.isEmpty()) {
		    	Toast.makeText(getApplicationContext(), "Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
		    	return;
		    }
		    if (server == null || !server.isAlive()) {
		    	Log.e(Constants.TAG, "[MAIN ACTIVITY] There is no server to connect to!");
		    	return;
		    }
		    String city_request = city.getText().toString();
		    if (city_request == null || city_request.isEmpty() || option == null || option.isEmpty()) {
		    	Toast.makeText(getApplicationContext(), "Parameters from client (city / information type) should be filled!", Toast.LENGTH_SHORT
		    			).show();
		    	return;
		    }
		    
		    client = new Client(clientAddress, Integer.parseInt(clientPort), city_request, option, info);
		    client.start();
		  }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.practical_test02_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(server.getServerSocket() != null) {
			server.stopThread();
		}
	}

}
