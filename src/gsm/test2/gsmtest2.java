package gsm.test2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class gsmtest2 extends Activity {
	  /** Called when the activity is first created. */
	 private LocationManager lm;
	 private LocationListener locationListener;
	 TextView latitude , longitude, signal, Altitude,Cell;
	 float params[] = {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};
	 int ncellid, ncellpwr;
	 final NeighboringCellInfo strength = new NeighboringCellInfo();
	 GsmCellLocation location;
	 int cellID, lac;
	 int neighborcellpwr = 0;
	 Writer output = null;
	 String Readings;
	 final File file = new File("/sdcard/file.txt");
	 //FileWriter fw;
	 
	 
   @Override
   public void onCreate(Bundle savedInstanceState) {
	   filecreate();
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
       //signal=new TextView(this);
       latitude = (EditText) findViewById(R.id.Lat);
       longitude = (EditText) findViewById(R.id.Long);
       Altitude = (EditText) findViewById(R.id.Alt);
       signal = (EditText) findViewById(R.id.Strength);
       Cell = (EditText) findViewById(R.id.CellID);
  	 
     /* Supposed to work on 2.2 but the class doesent get recognised on 1.6 :(
       if(TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED)    
       {     textTotalRxBytes.setText("TotalRxBytes: " + "UNSUPPORTED!");    }    
       else{     textTotalRxBytes.setText("TotalRxBytes: " + String.valueOf(TrafficStats.getTotalRxBytes()));
       }
      */
      
       
    
       final TelephonyManager SignalManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

       PhoneStateListener signalListener=new PhoneStateListener()
       {
       public void onSignalStrengthChanged(int asu)
       {
       Log.e("onSignalStrengthChanged: "+asu," hello");
       neighborcellpwr = strength.getRssi(); 
       //List cellinfo = SignalManager.getNeighboringCellInfo();
     

       
       params[4]= -113 + (2*asu);
       location = (GsmCellLocation) SignalManager.getCellLocation();
       cellID = location.getCid();
       lac = location.getLac();
       params[5] = (float)cellID;
       params[6] = (float)lac;
       
       Toast.makeText(getBaseContext(), "Neighbor Cell "+ neighborcellpwr , Toast.LENGTH_SHORT).show();
       SetValues();
       
               try
               {
            	   Readings = gsmtest2.arrayToString(params, " ") ;
                   Log.e("Info", Readings);
                   
            	   output.append(Readings+"\n");
            	   Log.e("Info", "Readings Recorded");
            	   output.flush();
                   //output.close();
                  
               }
               catch (IOException e)
               {
                       Log.e("Error", e.toString());
               }
               
              
      
       
       }

       
       };
       SignalManager.listen(signalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
      
     //---use the LocationManager class to obtain GPS locations---
       lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);    
       
       locationListener = new MyLocationListener();
       
       lm.requestLocationUpdates(
           LocationManager.GPS_PROVIDER, 
           0, 
           0, 
           locationListener);   
       
       
       
   }
   
   private class MyLocationListener implements LocationListener 
   {
       @Override
       public void onLocationChanged(Location loc) {
           if (loc != null) {
               //Toast.makeText(getBaseContext(), "Location changed : Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        	   params[0] = (float)loc.getLatitude();
        	   params[1] = (float)loc.getLongitude();
        	   params[2] = (float)loc.getAltitude();
        	   params[3] = (float)loc.getTime();
           }
       }

       @Override
       public void onProviderDisabled(String provider) {
           // TODO Auto-generated method stub
       }

       @Override
       public void onProviderEnabled(String provider) {
           // TODO Auto-generated method stub
       }

       @Override
       public void onStatusChanged(String provider, int status, 
           Bundle extras) {
           // TODO Auto-generated method stub
       }
   }
   
   public void SetValues()
   {
	   latitude.setText("Latitude = "+params[0]);
	   longitude.setText("Longitude = "+params[1]);
	   Altitude.setText("Altitude = "+params[2] + " m");
	   signal.setText("Signal Strength = "+params[4]+ " db");
	   Cell.setText("Cell ID = "+ params[5]);
   }  
   public static String arrayToString(float[] a , String separator) {
	    String result = new String();
	    if (a.length > 0) {
	        result = (""+a[0]);
	        for (int i=1; i<a.length; i++) {
	            result= result + (separator);
	            result= result + a[i];
	        }
	        //result = result + "\n";
	    }
	    return result.toString();
	}
      
   public void filecreate()
   {
   try
   {
	   
           file.createNewFile();
          // fw = ;
           output = new BufferedWriter(new FileWriter(file), 8*1024);
           Log.e("Info", "File Created! and written");
           
   }
   catch (IOException e)
   {
           Log.e("Error", e.toString());
   }
	 }
   
}