package pankaj.cdac.dtcbusroute;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import static pankaj.cdac.dtcbusroute.MySQLiteOpenHelper.*;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapingActivity extends MapActivity{
	
	MapView map;
	Button btnExit;
	MapController mc;
	SeekBar seek;
	String routeNo;
	MySQLiteOpenHelper helper;
	SQLiteDatabase db;
	ArrayList<String> busRoutes;
	ArrayList<Location> standsLocation;
	String []stands;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.neareststation);
		routeNo = getIntent().getStringExtra("routeNo");
		bindItems();
		getPoints();
		
	}

	private void bindItems() {
		map = (MapView) findViewById(R.id.myMap);
		btnExit = (Button) findViewById(R.id.mapExit);
		seek = (SeekBar) findViewById(R.id.seekRadius);
		mc = map.getController();
		
		map.setBuiltInZoomControls(true);
		map.setSatellite(false);
		
		seek.setVisibility(8);
		
		btnExit.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				MapingActivity.this.finish();
			}
		});
	}
	
	private void getPoints(){
		helper = new MySQLiteOpenHelper(this);
		db = helper.getReadableDatabase();
		Cursor cr1 = db.query(TABLE_NAME, new String[]{COL_ROUTE,COL_DESTINATION}, COL_ROUTENO+" = \'"+routeNo+"\'", null, null, null, null);
		if(!cr1.isAfterLast())
			cr1.moveToFirst();
		busRoutes = new ArrayList<String>();
		
		standsLocation = new ArrayList<Location>();
		busRoutes = new RouteActivity().getListOfStands(cr1.getString(0), cr1.getString(1));
		cr1.close();
		
		stands = getResources().getStringArray(R.array.busStands);
		
		String longs[] = getResources().getStringArray(R.array.longitudes);
		String lats[] = getResources().getStringArray(R.array.latitudes);
		for(int i=0;i<busRoutes.size();i++){
			for(int j=0;j<stands.length;j++){
				//if((busRoutes.get(i)).trim().equalsIgnoreCase(stands[j])){
					Location loc = new Location("");
					loc.setLatitude(Double.parseDouble(lats[j]));
					loc.setLongitude(Double.parseDouble(longs[j]));
					standsLocation.add(loc);
				//}
			}
		}
		
		draw_route();
	//	Toast.makeText(getApplicationContext(), standsLocation.toString(), Toast.LENGTH_LONG).show();
	}

	private void draw_route() {
		//overlay = new MyOverlay(this);
		map.getOverlays().clear();
		MyOverlay routeOverlay = new MyOverlay();
		mc.setZoom(14);
		mc.setCenter(new GeoPoint((int)(standsLocation.get(standsLocation.size()/2).getLatitude()*1E6),(int)(standsLocation.get(standsLocation.size()/2).getLongitude()*1E6)));
		map.getOverlays().add(routeOverlay);

	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
class MyOverlay extends Overlay{
		
		private GeoPoint geoPoint1,geoPoint2;
		
		/*public MyOverlay(Location loc1,Location loc2){
			this.geoPoint1 = new GeoPoint ((int)(loc1.getLatitude()*1E6), (int)(loc1.getLongitude()*1E6));
			this.geoPoint2 = new GeoPoint ((int)(loc2.getLatitude()*1E6), (int)(loc2.getLongitude()*1E6));
		}*/

	    @Override
	    public void draw(Canvas canvas, MapView mapView, boolean shadow) {

	        super.draw(canvas, mapView, shadow);

	        Paint paint=new Paint();
	        //paint.setARGB(0, 0, 15, 0);
	        paint.setStrokeWidth(15);
	        paint.setColor(Color.GREEN);
	        Point point1 = new Point();
	        Point point2 = new Point();
	        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
	        for(int i=0;i<standsLocation.size()-1;i++){
				if(standsLocation.get(i).distanceTo(standsLocation.get(i+1))<3000){
	        		geoPoint1 = new GeoPoint ((int)(standsLocation.get(i).getLatitude()*1E6), (int)(standsLocation.get(i).getLongitude()*1E6));
					mapView.getProjection().toPixels(geoPoint1, point1);
					x1=point1.x;
					y1=point1.y;

					geoPoint2 = new GeoPoint ((int)(standsLocation.get(i+1).getLatitude()*1E6), (int)(standsLocation.get(i+1).getLongitude()*1E6));
					mapView.getProjection().toPixels(geoPoint2, point2);
					x2=point2.x;
					y2=point2.y;
				}
	        }

	        paint.setStrokeWidth(5);
	        try {
				canvas.drawLine(x1, y1, x2, y2, paint);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      /*  Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.present_loc);
			canvas.drawBitmap(bmp, point1.x-25, point2.y-50, null);*/
	    }

	}
}