package pankaj.cdac.dtcbusroute;

import static pankaj.cdac.dtcbusroute.MySQLiteOpenHelper.*;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class RouteBetween extends Activity{
	
	Button btnSubmit, btnAddRoute, btnViewRoutesByOthers;
	AutoCompleteTextView actvSrc, actvDest;
	ListView listRoutes;

	
	MySQLiteOpenHelper myHelper;
	SQLiteDatabase db;
	ArrayList<String> busRoutes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routedetails);
		bindItems();
		setAdapters();
		myListener();
	}

	private void bindItems() {
		btnSubmit = (Button) findViewById(R.id.btnFindRoutes);
		btnViewRoutesByOthers = (Button) findViewById(R.id.btnOthers);
		btnAddRoute = (Button) findViewById(R.id.btnAddRoute);
		actvSrc = (AutoCompleteTextView) findViewById(R.id.actvSource);
		actvDest = (AutoCompleteTextView) findViewById(R.id.actvDestination);
		listRoutes = (ListView) findViewById(R.id.listRoutes);
	}
	
	private void setAdapters(){
		String []busStands = getResources().getStringArray(R.array.busStands);
		actvSrc.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, busStands));
		actvDest.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, busStands));
	}
	
	private void myListener(){
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				findRoutes(actvSrc.getText().toString(),actvDest.getText().toString());
			}
		});
		
		btnViewRoutesByOthers.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				getRoutesByOtherUsers();
			}
		});
		
		btnAddRoute.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent activityOpener = new Intent();
				activityOpener.setClassName("pankaj.cdac.dtcbusroute", "pankaj.cdac.dtcbusroute.AddRoute");
				activityOpener.putExtra("source", actvSrc.getText().toString());
				activityOpener.putExtra("destination", actvDest.getText().toString());
				startActivity(activityOpener);
			}
		});
		
	}
	
	private void getRoutesByOtherUsers(){
		ArrayList<String> routeList = new ArrayList<String>();
		DefaultHttpClient httpClient = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet("http://engineerinme.com/dtc/index.php?source="+actvSrc.getText().toString().replace(' ', '+')+"&destination="+actvDest.getText().toString().replace(' ', '+'));
    	ResponseHandler<String> responseString = new BasicResponseHandler();
    	try {
			String page = httpClient.execute(httpGet, responseString);
			//Toast.makeText(getApplicationContext(), page, Toast.LENGTH_LONG).show();
			if(page.contains("Not found")){
				Toast.makeText(getApplicationContext(), "Could not find any suitable route.\nPlease add if you have knowledge!", Toast.LENGTH_LONG).show();
			}
			else{
				String []routes = page.split("<route>");	
				String route = "";
				for(int i=1;i<routes.length;i++)
				{
					String temp [] = routes[i].split("</route>")[0].split(":");
					for(int j=0;j<temp.length;j++)
					{
						if(! temp[j].equals(""))
						{
							if(j%2==0)
								route += "Take "+temp[j].replace(",", "or")+"\n";
							else
								route += "Deboard at: "+temp[j]+"\n";
						}
					}
					routeList.add(route);
				}
				listRoutes.setVisibility(1);
				btnViewRoutesByOthers.setVisibility(8);
				btnAddRoute.setVisibility(8);
				listRoutes.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, routeList));
			}
				
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Failed to fetch Data.", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void findRoutes(String src, String dest){
		myHelper = new MySQLiteOpenHelper(this);
		db = myHelper.getReadableDatabase();
		
		Cursor cr = db.rawQuery("SELECT "+COL_ROUTENO+" FROM "+TABLE_NAME+" WHERE "+COL_ROUTE+" LIKE '%"+src+"%' AND "+COL_ROUTE+" LIKE '%"+dest+"%';", null);
		
		if(cr.getCount()==0){
			Toast.makeText(getApplicationContext(), "No Direct Route is Available.", Toast.LENGTH_LONG).show();
			
			listRoutes.setVisibility(8);
			btnViewRoutesByOthers.setVisibility(1);
			btnAddRoute.setVisibility(1);
			
			/*
			 * 
			 *	Removed Advanced Search to save computation. Instead added an Option to Add Route
			 * 
			 Cursor cr1 = db.rawQuery("SELECT "+COL_ROUTENO+" FROM "+TABLE_NAME+" WHERE "+COL_ROUTE+" LIKE '%"+src+"%';", null);
			Cursor cr2 = db.rawQuery("SELECT "+COL_ROUTENO+" FROM "+TABLE_NAME+" WHERE "+COL_ROUTE+" LIKE '%"+dest+"%';", null);
			
			String commonPoint = null;
			
			for(int k=0;k<cr1.getCount();k++)
			{
				cr1.moveToPosition(k);
				String bus1 = cr1.getString(0);
				ArrayList<String> srcBus = getStands(bus1);				
				for(int j=0;j<cr2.getCount();j++)
				{
					cr2.moveToPosition(j);
					String bus2 = cr2.getString(0);
					ArrayList<String> destBus = getStands(bus2);
					//Toast.makeText(getApplicationContext(), bus2+bus1, Toast.LENGTH_SHORT).show();
					for(int i=0;i<srcBus.size();i++){
						for(int l=0;l<destBus.size();l++){
							if(srcBus.get(i).equalsIgnoreCase(destBus.get(l)))
								commonPoint = destBus.get(l);
						}
					}
					
				}
			}
			//set final adapter here.
			if(commonPoint==null)
				Toast.makeText(getApplicationContext(), "Couldn't find a common point.", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), commonPoint, Toast.LENGTH_SHORT).show();*/
			return;
		}
		

		
        
		
		busRoutes = new ArrayList<String>();
		
		if(!cr.isAfterLast())
			cr.moveToFirst();
		
		do{
			String routeNo = cr.getString(0);
			busRoutes.add(routeNo);
			Toast.makeText(getApplicationContext(), routeNo, Toast.LENGTH_SHORT).show();
			cr.moveToNext();
		}while(!cr.isAfterLast());

		listRoutes.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, busRoutes));
	}
	
	
}