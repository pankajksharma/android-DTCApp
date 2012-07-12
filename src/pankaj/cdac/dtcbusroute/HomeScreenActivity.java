package pankaj.cdac.dtcbusroute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreenActivity extends Activity {
	
	Button btnRoutesInfo, btnStandsBuses, btnRoutesFinder, btnStandsFinder;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bindItems();
    }

	private void bindItems() {
		btnRoutesInfo = (Button) findViewById(R.id.btnFindRouteInfo);
		btnStandsBuses = (Button) findViewById(R.id.btnFindBuses);
		btnRoutesFinder = (Button) findViewById(R.id.btnFindRouteBetween2);
		btnStandsFinder = (Button) findViewById(R.id.btnFindBusStands);
		btnRoutesInfo.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent activityOpener = new Intent();
				activityOpener.setClassName("pankaj.cdac.dtcbusroute", "pankaj.cdac.dtcbusroute.RouteActivity");
				startActivity(activityOpener);
			}
		});
		
		btnStandsBuses.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent activityOpener = new Intent();
				activityOpener.setClassName("pankaj.cdac.dtcbusroute", "pankaj.cdac.dtcbusroute.StandsActivity");
				startActivity(activityOpener);
			}
		});
		
		btnRoutesFinder.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent activityOpener = new Intent();
				activityOpener.setClassName("pankaj.cdac.dtcbusroute", "pankaj.cdac.dtcbusroute.RouteBetween");
				startActivity(activityOpener);
			}
		});
		
		btnStandsFinder.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent activityOpener = new Intent();
				activityOpener.setClassName("pankaj.cdac.dtcbusroute", "pankaj.cdac.dtcbusroute.NearestStand");
				startActivity(activityOpener);
			}
		});
	}
}