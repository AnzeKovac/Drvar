package com.kovac.drvar;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Profile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity
{	
	
	ProgressDialog mProgressDialog;
	int CountSQLite;
	String CountMySQL = "";
	boolean pause = false;
	String dbID="";
	String host = "http://kovac.mooo.com";
	 String APP_KEY = "ZakljucnaTest";

	boolean first = true;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		OrderDB oDb = new OrderDB(getApplication());
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 LinearLayout PreberiBtn = (LinearLayout) findViewById(R.id.PreberiBtn);
		 LinearLayout DodajBtn = (LinearLayout) findViewById(R.id.AddBtn);
		 SharedPreferences.Editor editor = getSharedPreferences("Drvar",MODE_PRIVATE).edit();
		 editor.putString("host", "http://www.kovac.mooo.com");
		 
		
		 
		 //LinearLayout Ime = (LinearLayout)findViewById(R.id.Ime);
		 isNetworkAvailable();
		 PreberiBtn.setOnClickListener(new OnClickListener() {
	            public void onClick(View arg0) {
	                // Execute Title AsyncTask
	            	Intent i = new Intent(getApplicationContext(), OrdersActivity.class);
	            	startActivity(i);
	            	
	            }
	        });
		 
		 DodajBtn.setOnClickListener(new OnClickListener() {
	            public void onClick(View arg0) {
	                // Execute Title AsyncTask
	            
	                Intent i = new Intent(getApplicationContext(), AddOrderActivity.class);
	            	startActivity(i);
	            }
	        });
		  setHomePage();
		
		 
		
		
		 
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.main, menu);
		return (super.onCreateOptionsMenu(menu));

	}
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	 switch (item.getItemId()) {
	 case R.id.refresh:
		 isNetworkAvailable();
	 return (true);
	 
	  
	 }
	 return super.onOptionsItemSelected(item);
	 } 
	 
	 
	 public void onResume()
	 {
		    super.onResume();  // Always call the superclass method first
		    if (pause==true)
		    {
		   	isNetworkAvailable();
		    OrderDB oDb = new OrderDB(getApplication());
			CountSQLite = (int) oDb.count();
	
			pause=false;
		    }
		}
	 public void onPause()
	 {
		 super.onPause();
		 pause=true;
	 }
	 
	 
	 public void setHomePage()
	 {
		 
	  TextView Date = (TextView)findViewById(R.id.TextView01);
	  TextView ime = (TextView)findViewById(R.id.UserText);
	  
	  Calendar cal = Calendar.getInstance();
	  Date.setText(new SimpleDateFormat("d.MMMM yyyy").format(cal.getTime()));
	 
	  /*final String[] SELF_PROJECTION = new String[] { Phone._ID, Phone.DISPLAY_NAME, };
	  Cursor cursor = this.getContentResolver().query( Profile.CONTENT_URI, SELF_PROJECTION, null, null, null);
	  cursor.moveToFirst();*/ //TESTNO
	  ime.setText("Anže Kovaè");
	  NextOrder();
	  
	 }
	 
	 
	 public void ForceUpdate()
	 {
		 SharedPreferences prefs = getSharedPreferences("Drvar",MODE_PRIVATE);
			boolean connection = prefs.getBoolean("connection", false);
			if(connection)
			{
				 OrderDB oDb = new OrderDB(getApplication());
				 oDb.DeleteAll();
				 new FillFromNet();
			}
	 }
	 
	 public void isNetworkAvailable()
	 {
		    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    boolean con = activeNetworkInfo != null && activeNetworkInfo.isConnected();
		    SharedPreferences.Editor editor = getSharedPreferences("Drvar",MODE_PRIVATE).edit();

		    
			if(con)
			{   
				ImageView status = (ImageView) findViewById(R.id.imageView1);
				status.setImageResource(R.drawable.ic_device_access_storage_1);
				SyncDB();	
				new Prestej().execute();
				CountFill();
				NextOrder();
				editor.putBoolean("connection",true);
				editor.commit();
			}
			else
			{
				
				ImageView status = (ImageView)findViewById(R.id.imageView1);
			    status.setImageResource(R.drawable.ic_device_access_storage__dark1);
				CountFill();
				editor.putBoolean("connection", false);
				editor.commit();
			}
			
	}
	 
	public void NextOrder()
	{
		 
		  OrderDB oDb = new OrderDB(getApplication());
		  List<Narocilo> orders = new LinkedList<Narocilo>();
		        
	    	orders = oDb.GetNextOrder();
	    	
	        for(Narocilo order : orders)
	        {
	               TextView narocnik = (TextView)findViewById(R.id.naslednjiNarocnik);
		           TextView datum = (TextView)findViewById(R.id.datumText);
		           TextView naslov = (TextView)findViewById(R.id.naslovText);
		           TextView tip = (TextView)findViewById(R.id.tipText);
		           TextView dolzina = (TextView)findViewById(R.id.dolzinaText);
		           TextView kolicina = (TextView)findViewById(R.id.kolicinaText);
		           ImageView status = (ImageView)findViewById(R.id.imageView1);
		 

				try {
					SimpleDateFormat raw = new SimpleDateFormat("yyyy-MM-d hh:mm:ss");
			        Date d1 = raw.parse(order.getDate().toString());
			        
			        SimpleDateFormat dateFormatYouWant = new SimpleDateFormat("EEEE d.MMMM HH:mm");
			        String sCertDate = dateFormatYouWant.format(d1);
			        datum.setText(sCertDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		           
		           
		           narocnik.setText(order.getName());
		           naslov.setText(order.getAdress());
		           tip.setText(order.getType());
		           dolzina.setText(order.getLength()+" cm");
		           
		           if(order.getType().equals("Drva"))
		        	   kolicina.setText(order.getAmount()+"m");
		           else
		        	   kolicina.setText(order.getAmount()+"x paleta");
		           
	        }
		  
	}
	
	public void SyncDB()
	{
		if(first)
		{
			new FillFromNet().execute();
			first=false;
		}
		else
		{
			new SyncFromLocal().execute();
		
			new SyncFromNet().execute();
		}
		
	    new Prestej().execute();
		
		setHomePage();
	
	}
	 
	
	public void CountFill()
	 {
		
		 OrderDB oDb = new OrderDB(getApplication());
		 CountSQLite = (int) oDb.count();
		 SharedPreferences.Editor editor = getSharedPreferences("Drvar",MODE_PRIVATE).edit();
		 editor.putString("SQLiteCount",String.valueOf(CountSQLite));
		 editor.commit();
		 
		SharedPreferences prefs = getSharedPreferences("Drvar",MODE_PRIVATE); 
		String SQLite = prefs.getString("SQLiteCount", null);
		String MySQL = prefs.getString("MySQLCount",  "0");
		 
		TextView rowCount = (TextView) findViewById(R.id.StatusText);
		rowCount.setText("SQLite/MySQL: "+SQLite+"/"+MySQL);
		
		if(Integer.parseInt(SQLite)==Integer.parseInt(MySQL))
		{
			rowCount.setText("Baza je sinhronizirana");
		}
		else
		{
			rowCount.setText("SQLite/MySQL: "+SQLite+"/"+MySQL);
		}
		NextOrder();
 
	 }
	 
	
	 private class Prestej extends AsyncTask<Void, Void, Void>
	 {
		    Document document;
		    Elements vsebina;
		   
		        @Override
		        protected void onPreExecute() {
		            super.onPreExecute(); //POPRAVI NA KROGEC V ABAR
		           
					
		        }
		 
		        @Override
		        protected Void doInBackground(Void... params)
		        {
		        	  try {
			                document = Jsoup.connect(host+"/farmnet/drvar/countOrders.php").post();
			               
			                vsebina = document.select("p");
			                
			                for(Element p : vsebina  )
				        	 {
			                     CountMySQL = p.text(); 
				             }
			                 
			            } catch (IOException e) {
			            	SharedPreferences prefs = getSharedPreferences("Drvar",MODE_PRIVATE); 
			        		CountMySQL = prefs.getString("MySQLCount",  "0");
			               Log.d("Napaka",e.toString());
			            }
		            return null;
		        }
		 
		        @Override
		        protected void onPostExecute(Void result)
		        {
		        	
		        	SharedPreferences.Editor editor = getSharedPreferences("Drvar",MODE_PRIVATE).edit();
					editor.putString("MySQLCount",CountMySQL);
					editor.commit();
					CountFill();
					
		          
		        }
	}
	 
	 private class SyncFromNet extends AsyncTask<Void, Void, Void> {
		    Document document;          
		    Elements vsebina;
		    String lines[] = new String[1000];
		    int i = 0;
		    LayoutInflater inflater = (LayoutInflater) getApplication()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    ImageView iv = (ImageView) inflater.inflate(R.layout.actionb_refresh,null);
		    
		        @Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		            
		           
					

		        }
		 
		        @Override
		        protected Void doInBackground(Void... params) {
		        	  try {
		        		  OrderDB oDb = new OrderDB(getApplication());
		        			String niz = oDb.getID();
		        			
			                document = Jsoup.connect(host+"/farmnet/drvar/sync.php")
			                		.data("field",niz)
			    	                .post();
			               
			                vsebina = document.select("p");
			                
			                for(Element p : vsebina  )
				        	 {
			                     lines[i++]= p.text(); 
				             }
			                 
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
		            return null;
		        }
		 
		        @Override
		        protected void onPostExecute(Void result) {
		           
		             OrderDB oDb = new OrderDB(getApplication());
		        	 for (int j = 0;j<i;j+=8)
		        	 {

		    			 Narocilo order = new Narocilo(
		    					 		Integer.parseInt(lines[j].toString()), //id   
		    					 		lines[j+1].toString(),   //narocnik
		    					 		lines[j+2].toString(), //naslov
		    					 		lines[j+3].toString(), //datum
		    					 		lines[j+4].toString(), //Tip
		    					 		lines[j+5].toString(), //dolzina
		    					 		lines[j+6].toString(), //kolicina
		    					 		lines[j+7].toString());//status
		    						oDb.addOrder(order);   //Vpis objekta
		             }
		        	 new SyncFromLocal().execute();
		            CountFill();
		        }
		    }
	
	 
	 private class SyncFromLocal extends AsyncTask<Void, Void, Void> {
		    Document ID;
		    Elements IDS;
		    String lines[] = new String[100];  //variables
		    int i = 0;
		  
		    List<Narocilo> orders = new LinkedList<Narocilo>();  //orders list
	        

		    
		        @Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		          /*  mProgressDialog = new ProgressDialog(MainActivity.this);
		            mProgressDialog.setTitle("FarmNet");
		            mProgressDialog.setMessage("Nalaganje podatkov...");             ------>>> REFRESH ICON ACTION BAR
		          
		            mProgressDialog.show();*/
		        }
		 
		        @Override
		        protected Void doInBackground(Void... params) {
		        	  try {
		        		  	OrderDB oDb = new OrderDB(getApplication()); //objekt podatkovne baze
		        			String niz = oDb.getID();                    //pridobitev vseh unikatnih ID-jev  za upload
			                ID = Jsoup.connect(host+"/farmnet/drvar/syncID.php")  
			                		.data("field",niz)   
			                		.post();                       //analiza ID-jev in priprava podatkov na strežniku
			                IDS = ID.select("p");
			            
		
			                for(Element p : IDS  )
				        	 {
			                    lines[i++]= p.text();             //branje rezultata
				             }
			                
			            	 orders = oDb.getSpecOrders(lines);  //pridobitev specifiènih naroèil glede na rezultat strežnika
			            	 
			                for(Narocilo order : orders)  //Vsako notsync naroèilo se prenese na strežnik
			                {
			                	
			                	 Jsoup.connect(host+"/farmnet/drvar/vnos.php")
			 	                .data("customer",order.getName())
			 	                .data("adress",order.getAdress())
			 	                .data("date",order.getDate())  
			 	                .data("type",order.getType())          //pridobitev podatkov iz objekta
			 	                .data("length",order.getLength())
			 	                .data("amount",order.getAmount())
			 	                .data("status", order.getStatus())
			 	                .header("Content-Type","application/x-www-form-urlencoded;charset=UTF-8") //utf-8 Glava
			 	                .cookie("key", APP_KEY) //seja - kljuè
			 	                .post();
			                	oDb.deleteOrder(order); //brisanje lokalnega naroèila -> pridobitev istega naroèila iz strežnika z unikatnim ID-jem
			                	
			                }
			                
			                
			                
			                 
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
		            return null;
		        }
		 
		        @Override
		        protected void onPostExecute(Void result) {

			    CountFill(); //osvežitev podatkov o vnosih
		       
		        }
		    }
	 
	 private class FillFromNet extends AsyncTask<Void, Void, Void> {
		    Document document;
		    Elements vsebina;
		    String lines[] = new String[1000];
		    int i = 0;
		    LayoutInflater inflater = (LayoutInflater) getApplication()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    ImageView iv = (ImageView) inflater.inflate(R.layout.actionb_refresh,null);
		    
		        @Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		            
		            Log.d("Refresh", "Refreshing and rotating and stuff.");
					

		        }
		 
		        @Override
		        protected Void doInBackground(Void... params) {
		        	  try {
		        		  OrderDB oDb = new OrderDB(getApplication());
		        			String niz = oDb.getID();
		        			Log.d("NIZ IDjev", niz);
			                document = Jsoup.connect(host+"/farmnet/drvar/sync.php")
			                		.data("field",niz)
			    	                .post();
			               
			                vsebina = document.select("p");
			                
			                for(Element p : vsebina  )
				        	 {
			                     lines[i++]= p.text(); 
				             }
			                 
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
		            return null;
		        }
		 
		        @Override
		        protected void onPostExecute(Void result) {
		           
		             OrderDB oDb = new OrderDB(getApplication());
		        	 for (int j = 0;j<i;j+=8)
		        	 {

		    			 Narocilo order = new Narocilo(
		    					 		Integer.parseInt(lines[j].toString()), //id   
		    					 		lines[j+1].toString(),   //narocnik
		    					 		lines[j+2].toString(), //naslov
		    					 		lines[j+3].toString(), //datum
		    					 		lines[j+4].toString(), //Tip
		    					 		lines[j+5].toString(), //dolzina
		    					 		lines[j+6].toString(), //kolicina
		    					 		lines[j+7].toString());//status
		    						oDb.addOrder(order);   //Vpis objekta
		             }
		        	 new SyncFromLocal().execute();
		            CountFill();
		        }
		    }
	 
	 
	
		
	
	
	
}
