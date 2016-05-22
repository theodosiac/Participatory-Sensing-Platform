package com.acme.proximity;

import java.util.ArrayList;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Activator implements BundleActivator,SensorEventListener {

	ArrayList<Float> SensorData=new ArrayList<Float>();
	
	private static BundleContext context;

	//String message = "not received anything from sensor yet";

	private SensorManager sensorManager;
	Sensor sensor;
	static BundleContext getContext() {
		return context;
	}
	
	public void start(BundleContext bundleContext) throws Exception{

		try {
		
		Activator.context = bundleContext;
		
		ServiceReference<Context> ref = bundleContext.getServiceReference(Context.class);
		final Context ctx = bundleContext.getService(ref);

		if(ctx!=null)//found 
		{
			sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
			if(sensorManager.getSensorList(Sensor.TYPE_PROXIMITY).size()!=0)
			{
				sensor = sensorManager.getSensorList(Sensor.TYPE_PROXIMITY).get(0);
				//message = "Sensor found";
				
				bundleContext.registerService(String.class,  "Proximity Sensor found" , null);
				
				sensorManager.registerListener(this,sensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
			else
			{
				//message = "Sorry!Sensor not found";
				bundleContext.registerService(String.class, "Sorry!Sensor not found" , null);
			
			// Create the text view
			//TextView textView = new TextView(this);
			//textView.setTextSize(20);
			//textView.setText(message);
			// Set the text view as the activity layout
			//setContentView(textView);
			}
			
			//Intent intent = new Intent(ctx, SensingGyroscope.class);

			bundleContext.registerService(String.class, "BUNDLE: context found!!!" , null);
			//String pkgName = View_Patient_File_Activity.class.getPackage().getName();
			//String clssName = View_Patient_File_Activity.class.getName();
			//intent.setClassName(pkgName, clssName);

			// You may add the NEW_TASK flag
			//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Important: do not use startActivity(Context, Class) version because it will fail to resolve the activity
			//ctx.startActivity(intent);
		}else
		{
			 bundleContext.registerService(String.class, "BUNDLE: context not found " , null);
			
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		bundleContext.registerService(String.class.getName(), e.getMessage() , null);
	}
		
		
		try {
			SensorData.add((float) 0) ;
			bundleContext.registerService(ArrayList.class.getName(), SensorData , null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			bundleContext.registerService(String.class.getName(), "bundle registration failed" , null);
		}
		
		//Intent intent = new Intent(ctx, SensingGyroscope.class);
		//String pkgName = View_Patient_File_Activity.class.getPackage().getName();
		//String clssName = View_Patient_File_Activity.class.getName();
		//intent.setClassName(pkgName, clssName);

		// You may add the NEW_TASK flag
		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Important: do not use startActivity(Context, Class) version because it will fail to resolve the activity
		//ctx.startActivity(intent);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	int counter =0;
	public void onSensorChanged(SensorEvent event) {
		// Create the text view
		//TextView textView = new TextView(this);
		//textView.setTextSize(15);
		//else it will output the Roll, Pitch and Yawn values
		
//		bufferData.set(0, "Orientation X (Roll) :"+ Float.toString(event.values[2]) +"\n"+
//		"Orientation Y (Pitch) :"+ Float.toString(event.values[1]) +"\n"+
//		"Orientation Z (Yaw) :"+ Float.toString(event.values[0]));
		
		SensorData.set(0, event.values[0]);
		
		/*if(counter <4)
		{
			getContext().registerService(String.class, message , null);
			counter++;
	
		}*/
	}
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		
		//do nothing
		}

}
