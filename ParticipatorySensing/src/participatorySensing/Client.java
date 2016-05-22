package participatorySensing;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.osgi.framework.InvalidSyntaxException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
 class ReturnData {
	  String errorCode;
	  String bundleList;
	  String bundlePath;
	  String action;
	  ReturnData()
	  {		  
		   errorCode="";
		   bundleList="";
		   bundlePath="";
		   action="";
		   }
	}
@SuppressLint("ResourceAsColor")
public class Client extends AsyncTask<String, String, ReturnData>{


	public Timer timer;
      String errorResponse="";
	static MainActivity mainActivity= null;
	
	 String bundlePath="";
	
	public Client (MainActivity mainActivity) {
		this.mainActivity=mainActivity;
	}	
	
	
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	static void getData(MainActivity mainActivity, List<NameValuePair> nameValuePairs)
	{
		for (org.osgi.framework.Bundle bundle :  mainActivity.m_felix.getBundleContext().getBundles()) 
		{ 
			if(bundle.getBundleId()==0) //exclude felix bundle
				continue;

			if(bundle.getState()!=org.osgi.framework.Bundle.ACTIVE) //exclude if bundle is not started
				continue;


			try {		
				for (org.osgi.framework.ServiceReference b :  bundle.getBundleContext().getAllServiceReferences(ArrayList.class.getName() , null)) 
				{
					if (b!=null&&b.getBundle().getBundleId()==bundle.getBundleId())//valid reference and corresponds to current bundle
					{			
						try {
							ArrayList<Float> sensorData = (ArrayList<Float> )bundle.getBundleContext().getService(b);
							String str="";
							for(int i=0;i<sensorData.size();i++)
								str   +=Float.toString(sensorData.get(i)) +"\n";
							if(!str.equals(""))
							{
								nameValuePairs.add(new BasicNameValuePair(bundle.getSymbolicName(), str));

							}
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
						
					}

				}
				
			} catch (InvalidSyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

	}
	}
	
	
	 void sendDataPost(final String[] uri )
	{

		timer = new Timer();
		final HttpPost httpPost = new HttpPost(uri[1]);

		httpPost.setHeader("Content-Type", "text/plain; charset=utf-8");
		httpPost.setHeader("Expect", "100-continue");
		timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
				  
		    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    		getData(mainActivity,nameValuePairs);
		    		if(!nameValuePairs.isEmpty())
		    		{
			    		try {
							httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
				    	    HttpClient httpClient = new DefaultHttpClient();
				    	    HttpResponse resp =  httpClient.execute(httpPost);
				            ByteArrayOutputStream out = new ByteArrayOutputStream();
				    		resp.getEntity().writeTo(out);
				    		out.close();
						}
			    		catch (IOException e){
			    			errorResponse = "Client error: "+e.getMessage();
						}
		    		}
			  }
			}, 1000, 1000);
		    	

	}
	

	
	public ReturnData getBundle(String[] uri)
	{
    	ReturnData returnData = new ReturnData();
	    returnData.bundlePath=uri[2];	    
	    
		HttpPost httpPost = new HttpPost(uri[1]);

		httpPost.setHeader("Content-Type", "text/plain; charset=utf-8");
		httpPost.setHeader("Expect", "100-continue");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("DownloadBundle", uri[3]));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpPost);
	        InputStream in = response.getEntity().getContent();

	        FileOutputStream fos = new FileOutputStream(new File(uri[2]));

	        byte[] buffer = new byte[4096];
	        int length; 
	        while((length = in.read(buffer)) > 0) {
	            fos.write(buffer, 0, length);
	        }
		} catch (IOException e1) {
	        returnData.errorCode = "Download Bundle, malformed URL: "+e1.getMessage();
		}
    	return returnData;
	}
	
	public ReturnData getList(String[] uri)
	{
	    	ReturnData returnData = new ReturnData();	    	
    		HttpGet httpGet = new HttpGet(uri[1]);

	    	httpGet.setHeader("Content-Type", "text/plain; charset=utf-8");
	    	httpGet.setHeader("Expect", "100-continue");
	    	HttpResponse resp = null;
	    	try {
	    	    HttpClient httpClient = new DefaultHttpClient();
	    	    resp = httpClient.execute(httpGet);
	    	} catch (ClientProtocolException e) {
	    		returnData.errorCode = "Client error1: "+e.getMessage();
	    	} catch (IOException e) {
	    		returnData.errorCode = "Client error2: "+e.getMessage();
	    	}
	    	if (resp != null) {	    		
            ByteArrayOutputStream out = new ByteArrayOutputStream();
	    		
		    	try {           
		    		resp.getEntity().writeTo(out);
		    		out.close();
		    		returnData.bundleList=out.toString();
		    		
				} catch (ParseException e) {
					e.printStackTrace();
					returnData.errorCode = "Client error4: "+e.getMessage();
				} catch (IOException e) {
					returnData.errorCode = "Client error3: "+e.getMessage();
				}
	    	} else {
	    	}
	    	return returnData;
	}
	
	

	//@Override
	protected ReturnData doInBackground(String... uri){
		ReturnData returnData=null;
		if(uri[0].equals("DownloadList"))
			returnData=getList(uri) ;
		if(uri[0].equals("DownloadBundle"))
			returnData= getBundle(uri) ;
		if(uri[0].equals("sendData"))
			sendDataPost(uri) ;		
		if(returnData!=null)
			returnData.action=uri[0];
		return returnData;
	}
	
	
	protected void onPostExecute(ReturnData returnData) {

    	if(returnData==null)
    		return;

    	if(!returnData.errorCode.equals(""))
    	{
			mainActivity.appendLog(returnData.errorCode);
			mainActivity.listProcessing--;
			mainActivity.startButton.setBackgroundColor(mainActivity.getResources().getColor(android.R.color.holo_red_light));
			if(returnData.action.equals("DownloadList"))    		
			{
				mainActivity.uninstallBundles();
			}
			
    		return;
    	}
		if(returnData.action.equals("DownloadList"))    		
		{
			mainActivity.bundleManager( returnData.bundleList,this.mainActivity);
			mainActivity.startButton.setBackgroundColor(mainActivity.getResources().getColor(android.R.color.holo_green_light));

		}
		if(returnData.action.equals("DownloadBundle")) 
		{
			mainActivity.appendLog(returnData.bundlePath+ " downloaded.");
			mainActivity.installBundle( returnData.bundlePath,this.mainActivity);
			mainActivity.startButton.setBackgroundColor(mainActivity.getResources().getColor(android.R.color.holo_green_light));
			
		}

    }
}
