package pankaj.cdac.dtcbusroute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddRoute extends Activity{
	
	TextView txtSource,txtDestination;
	Button btnShowMore, btnSubmit;
	LinearLayout ll2;
	EditText route1, route2,route3;
	AutoCompleteTextView actvStand,actvStand2;
	
	ArrayList<String> routeInfo;
	String source, destination, stands[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addrouteactivity);
		stands = getResources().getStringArray(R.array.busStands);
		bindItems();
		routeInfo = new ArrayList<String>();
		//myListener();		
	}

	private void addInfo(){
		if(!route1.getText().toString().equals(""))
			routeInfo.add(route1.getText().toString());
		if(!actvStand.getText().toString().equals(""))
			routeInfo.add(actvStand.getText().toString());
		if(!route2.getText().toString().equals(""))
			routeInfo.add(route2.getText().toString());
		if(!actvStand2.getText().toString().equals(""))
			routeInfo.add(actvStand2.getText().toString());
		if(!route3.getText().toString().equals(""))
			routeInfo.add(route3.getText().toString());
	}

	private void bindItems() {
		txtSource = (TextView) findViewById(R.id.txtSrc);
		txtDestination = (TextView) findViewById(R.id.txtDest);
		
		actvStand = (AutoCompleteTextView) findViewById(R.id.actvChange1);
		actvStand2 = (AutoCompleteTextView) findViewById(R.id.actvChange2);
		
		actvStand.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stands));
		actvStand2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stands));
		
		btnShowMore = (Button) findViewById(R.id.btnAdd1);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		
		ll2 = (LinearLayout) findViewById(R.id.ll2);
		
		route1 = (EditText) findViewById(R.id.edtRoute1);
		route2 = (EditText) findViewById(R.id.edtRoute2);
		route3 = (EditText) findViewById(R.id.edtll2);
		
		source = getIntent().getStringExtra("source");
		destination = getIntent().getStringExtra("destination");
		
		txtSource.setText("Source: "+source);
		txtDestination.setText("Destination: "+destination);
		
		btnShowMore.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				ll2.setVisibility(1);
				btnShowMore.setVisibility(8);
			}
		});
		
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				addInfo();
				submitInfo();
			}
		});
	}
	
	private void submitInfo(){
		DefaultHttpClient httpClient = new DefaultHttpClient();		  
		HttpPost httpPost = new HttpPost("http://engineerinme.com/dtc/index.php");

    	try {
    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
    		nameValuePairs.add(new BasicNameValuePair("source", source));
    		nameValuePairs.add(new BasicNameValuePair("destination", destination));
    		nameValuePairs.add(new BasicNameValuePair("noOfFields", routeInfo.size()+""));
    		for(int i=0;i<routeInfo.size();i++){
    			String name = "index_"+i;
    			nameValuePairs.add(new BasicNameValuePair(name, routeInfo.get(i)));
    		}

    		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    		HttpResponse response  = httpClient.execute(httpPost);
    		HttpEntity entity = response.getEntity();
    		String page = EntityUtils.toString(entity);
    		//Toast.makeText(getApplicationContext(), page, Toast.LENGTH_LONG).show();
			if(page.contains("Thanks for Submitting route info")){
				//Toast.makeText(getApplicationContext(), "Submitted successfully.", Toast.LENGTH_LONG).show();
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Thanks for Adding. Route will be added Permanently after being verified by users.");
				alert.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						AddRoute.this.finish();
					}
				});
				AlertDialog dialog = alert.create();
				dialog.show();
			}
			else{
				Toast.makeText(getApplicationContext(), "Failed to submit Data.", Toast.LENGTH_LONG).show();
			}
			
				
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Failed to fetch Data.", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Connection Failed.", Toast.LENGTH_LONG).show();
		}
	}
	
}