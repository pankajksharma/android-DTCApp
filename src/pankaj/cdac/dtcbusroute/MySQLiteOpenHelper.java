package pankaj.cdac.dtcbusroute;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MySQLiteOpenHelper extends SQLiteOpenHelper{
	
	public static final String DB_NAME="dtc_initial";
	public static final String TABLE_NAME="route_info";
	public static final String COL_ID="uid";
	public static final String COL_ROUTENO="route_no";
	public static final String COL_SOURCE="source";
	public static final String COL_DESTINATION="destination";
	public static final String COL_ROUTE="route_detail";
	Context context;

	public MySQLiteOpenHelper(Context context){
		super(context, DB_NAME, null, 1);
		this.context=context;
		//Toast.makeText(context, "text", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//db.execSQL("CREATE TABLE hello (name TEXT);");
		//Toast.makeText(context, "text", Toast.LENGTH_SHORT).show();
		db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_ROUTENO+" TEXT, "+COL_SOURCE+" TEXT, "+COL_DESTINATION+" TEXT, "+COL_ROUTE+" TEXT );");
		String[] queries = context.getResources().getStringArray(R.array.insertQueries);
		for(int i=0;i<queries.length;i++){
			db.execSQL(queries[i]);
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}