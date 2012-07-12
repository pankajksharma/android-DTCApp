package pankaj.cdac.dtcbusroute;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static pankaj.cdac.dtcbusroute.MySQLiteOpenHelper.*;


public class StandsActivity extends Activity{
	
	String [] busStands;
	ArrayList<String> busRoutes;
	Cursor cr;
	ExpandableListView listRoutes;
	AutoCompleteTextView actvStands;
	Button btnSubmit, btnClear;
	MySQLiteOpenHelper myHelper;
	SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.standsactivity);
		
		bindItems();
		addAdapterToACTV();
		myListener();
	}
	private void bindItems() {
		actvStands = (AutoCompleteTextView) findViewById(R.id.actvStands);
		btnSubmit = (Button) findViewById(R.id.btnStandSubmit);
		btnClear = (Button) findViewById(R.id.btnClear);
		listRoutes = (ExpandableListView) findViewById(R.id.listRoutes);
	}

	
	private void addAdapterToACTV() {
		
		busStands = getResources().getStringArray(R.array.busStands);
		actvStands.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, busStands));
		
	}		


	private void myListener() {
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), actvStands.getText().toString(), Toast.LENGTH_LONG).show();
				listRoutes.setVisibility(1);
				getRoutes(actvStands.getText().toString());
			}
			
		});
		
		btnClear.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), actvStands.getText().toString(), Toast.LENGTH_LONG).show();
				listRoutes.setVisibility(4);
				actvStands.setText("");
			}
			
		});

	}
	private void getRoutes(String busStand) {
		myHelper = new MySQLiteOpenHelper(this);
		try {
			db = myHelper.getReadableDatabase();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), busStand, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		cr = db.rawQuery("select "+COL_ROUTENO+" from "+TABLE_NAME+" where "+COL_ROUTE+" like '%"+busStand+"%';", null);
		
		busRoutes = new ArrayList<String>();
		
		if(!cr.isAfterLast())
			cr.moveToFirst();

		listRoutes.setAdapter(new MyAdapter());
		//cr.close();
	}
	

	public void imgClickListener(View view){		
		if(view.getId()==R.id.imgMap)
		{			
			Intent mapingIntent = new Intent();
			mapingIntent.setClassName("pankaj.cdac.dtcbusroute", "pankaj.cdac.dtcbusroute.MapingActivity");
			mapingIntent.putExtra("routeNo", view.getTag().toString().substring(3));
			startActivity(mapingIntent);
			/*Cursor cr1 = db.query(TABLE_NAME, new String[]{COL_ROUTE,COL_DESTINATION}, COL_ROUTENO+" = \'"+view.getTag().toString().substring(3)+"\'", null, null, null, null);
			if(!cr1.isAfterLast())
				cr1.moveToFirst();
			busRoutes = new RouteActivity().getListOfStands(cr1.getString(0), cr1.getString(1));
			cr1.close();
			MapView mapView = new MapView(this, "0psMIPLlN5rLrpxVLuXV-iH3-F1Q1BJen_DO90g");
			//mapView = (MapView) findViewById(R.id.myMap);
			AlertDialog.Builder overlay = new AlertDialog.Builder(mapView.getContext());
			/*LayoutInflater inflater = getLayoutInflater();
			View mapView = inflater.inflate(R.layout.neareststation, null,true);
			overlay.setView(mapView);
			AlertDialog dialog = overlay.create();
			dialog.show();
			//overlay = inflater.inflate(R.layout.neareststation, root)
			Toast.makeText(getApplicationContext(), view.getTag().toString(), Toast.LENGTH_SHORT).show();*/
		}
		else{			
			AlertDialog.Builder overlay = new AlertDialog.Builder(this);
			Cursor cr1 = db.query(TABLE_NAME, new String[]{COL_ROUTE,COL_DESTINATION}, COL_ROUTENO+" = \'"+view.getTag().toString().substring(4)+"\'", null, null, null, null);
			if(!cr1.isAfterLast())
				cr1.moveToFirst();
			busRoutes = new RouteActivity().getListOfStands(cr1.getString(0), cr1.getString(1));
			cr1.close();
			
			overlay.setTitle("Stands on Route "+view.getTag().toString().substring(4)+" :");
			overlay.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, busRoutes), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					actvStands.setText(busRoutes.get(which).trim());
					getRoutes(busRoutes.get(which).trim());
				}
			});			
			AlertDialog dialog = overlay.create();
			dialog.getListView().setBackgroundColor(Color.BLACK);
			dialog.show();
		}
		
	}

	public class MyAdapter extends BaseExpandableListAdapter{
		
		ColorStateList clr;

		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv = null;
            tv = new TextView(StandsActivity.this);
            cr.moveToPosition(groupPosition);
            String busRoute = cr.getString(0);
            Cursor cr2 = db.query(TABLE_NAME, new String[]{COL_ROUTE}, COL_ROUTENO+" = '"+busRoute+"'", null, null, null, null);
            if(!cr2.isAfterLast())
            	cr2.moveToFirst();
            tv.setText(cr2.getString(0)); 
            cr2.close();
            tv.setTextColor(clr);
            tv.setPadding(30, 0, 0, 0);
            return tv;
		}

		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return 1;
		}

		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		public int getGroupCount() {
			// TODO Auto-generated method stub
			return cr.getCount();  
		}

		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			/*TextView tv = null;
            tv = new TextView(StandsActivity.this);
            cr.moveToPosition(groupPosition);
            String busRoute = cr.getString(0);
            tv.setText(busRoute);
            tv.setPadding(60, 10, 0, 10);
            return tv;*/
			/*ListView lv = new ListView(StandsActivity.this);
			lv.setAdapter(new MyCustumAdapter(StandsActivity.this,groupPosition));
			lv.setPadding(60, 10, 0, 10);
			return lv;*/
			//LinearLayout ll = new LinearLayout(StandsActivity.this);
			View ll = new View(StandsActivity.this); 
			LayoutInflater myInflater = getLayoutInflater();
			ll = myInflater.inflate(R.layout.listelement, parent,false);			
			//ll = (View) findViewById(R.layout.listelement);
			ll.setPadding(60, 10, 0, 10);
			TextView txtRoute =(TextView)ll.findViewById(R.id.txtRouteNo);
			ImageView mapImg = (ImageView)ll.findViewById(R.id.imgMap);
			ImageView listImg = (ImageView)ll.findViewById(R.id.imgList);			
			cr.moveToPosition(groupPosition);
			txtRoute.setText(cr.getString(0));
			clr = txtRoute.getTextColors();
			mapImg.setTag("map"+cr.getString(0));
			listImg.setTag("list"+cr.getString(0));
			return ll;
		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
}