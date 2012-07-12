package pankaj.cdac.dtcbusroute;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static pankaj.cdac.dtcbusroute.MySQLiteOpenHelper.*;

public class RouteActivity extends Activity{
	
	ArrayList<String> busRoutes;
	ListView listStands;
	AutoCompleteTextView actvRoutes;
	Button btnSubmit;
	TextView txtRouteDetails;
	MySQLiteOpenHelper myHelper;
	SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routeactivity);
		
		bindItems();
		addAdapterToSpinner();
		myListener();
	}
	
	
	private void addAdapterToSpinner() {
		myHelper = new MySQLiteOpenHelper(this);
		db = myHelper.getWritableDatabase();
		busRoutes = new ArrayList<String>();
		
		Cursor cr = db.query(TABLE_NAME, new String[]{COL_ROUTENO}, null, null, null, null, null);
		if(!cr.isAfterLast())
			cr.moveToFirst();
		do{
			busRoutes.add(cr.getString(0));
			cr.moveToNext();
		}while(!cr.isAfterLast());
		actvRoutes.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, busRoutes));
		cr.close(); 

	}


	private void myListener() {
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), actvRoutes.getText().toString(), Toast.LENGTH_LONG).show();
				txtRouteDetails.setVisibility(TextView.VISIBLE);
				Cursor cr = db.query(TABLE_NAME, new String[]{COL_ROUTE,COL_DESTINATION}, COL_ROUTENO+" = \'"+actvRoutes.getText().toString()+"\'", null, null, null, null);
				if(!cr.isAfterLast())
					cr.moveToFirst();
				
				busRoutes = getListOfStands(cr.getString(0),cr.getString(1));
				listStands.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, busRoutes));
				cr.close();
			}			
		});
	}
	
	public ArrayList<String> getListOfStands(String string,String destination) {
		ArrayList<String> busRoutes = new ArrayList<String>();
		for(int i=1;string.indexOf(""+(i+1))!=-1;i++){
			int beg;
			if(i<10)
				beg=string.indexOf(""+i)+1;
			else
				beg=string.indexOf(""+i)+2;
			String tempString = null;
			try{
				tempString = string.substring(beg, string.indexOf(""+(i+1)));
				tempString = tempString.replace('.', ' ');
                tempString = tempString.replace(',', ' ');
                busRoutes.add(tempString);
			}
			catch(Exception ioe)
			{
				ioe.printStackTrace();
			}
                
		}
		
		busRoutes.add(" "+destination);		
		return busRoutes;
		
	}

	private void bindItems() {
		txtRouteDetails = (TextView) findViewById(R.id.txtRouteDetails);
		actvRoutes = (AutoCompleteTextView) findViewById(R.id.actvRoutes);
		btnSubmit = (Button) findViewById(R.id.btnRouteSubmit);
		listStands = (ListView) findViewById(R.id.listStands);
	}
	
}