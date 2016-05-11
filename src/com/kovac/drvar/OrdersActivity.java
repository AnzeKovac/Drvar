package com.kovac.drvar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class OrdersActivity extends Activity
{	
	ProgressDialog mProgressDialog;
	ListView OrderList;
	ArrayList<Narocila> narocilo;
	AdapterView.AdapterContextMenuInfo info;
	String choose= "week";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_list);
		GenerateList();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.orders, menu);
		return (super.onCreateOptionsMenu(menu));
	}
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	 switch (item.getItemId()) {
	 case R.id.teden:
		 choose="week";
		 GenerateList();
		 return (true);
	 
	 case R.id.mesec:
		 choose="month";
		 GenerateList();
	 return (true);
	 }
	 return super.onOptionsItemSelected(item);
	 }
	 	 
	 
	 public void GenerateList()
	 {
		 OrderList = (ListView) findViewById(R.id.ListView1);
    	 //narocilo = new ArrayList<Narocilo>();
		 OrderDB oDb = new OrderDB(getApplication());
    	 ArrayList<Narocilo> orders = new ArrayList<Narocilo>();
    	 final ArrayList<Narocilo> ordersf = oDb.getFilterOrders(choose);
 
         orders = oDb.getFilterOrders(choose);
        

        OrderList.setAdapter(new CustomAdapter(orders, getApplicationContext()));
        
        OrderList.setOnItemClickListener(new OnItemClickListener() {
        	 public void onItemClick(AdapterView a, View v,final int position, long id) {
        	       
        	        
        	         Intent intent = new Intent(OrdersActivity.this, OrderActivity.class);
        	         Bundle b = new Bundle();
        	         b.putInt("id", ordersf.get(position).getId()); //Your id
        	         intent.putExtras(b); //Put your id to your next Intent
        	         startActivity(intent);
        	         
        	 }
        	     });
	 }
	 
	 
	public class Narocila {
         String narocnik;
         String datum;
         String naslov;
         String tip;
         String dolzina;
         String kolicina;
         String status;
           
		public String getKolicina() {
			return kolicina;
		}
		public void setKolicina(String kolicina) {
			this.kolicina = kolicina;
		}
		public String getNarocnik() {
			return narocnik;
		}
		public void setNarocnik(String narocnik) {
			this.narocnik = narocnik;
		}
		public String getDatum() {
			return datum;
		}
		public void setDatum(String datum) {
			this.datum = datum;
		}
		public String getNaslov() {
			return naslov;
		}
		public void setNaslov(String naslov) {
			this.naslov = naslov;
		}
		public String getTip() {
			return tip;
		}
		public void setTip(String tip) {
			this.tip = tip;
		}
		public String getDolzina() {
			return dolzina;
		}
		public void setDolzina(String dolzina) {
			this.dolzina = dolzina;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
     }
	
	public class CustomAdapter extends BaseAdapter {
		private ArrayList<Narocilo> _data;
		Context _c;
	    CustomAdapter (ArrayList<Narocilo> data, Context c){
	        _data = data;
	        _c = c;
	    }
	   
	    public int getCount() {
	        // TODO Auto-generated method stub
	        return _data.size();
	    }
	    
	    public Object getItem(int position) {
	        // TODO Auto-generated method stub
	        return _data.get(position);
	    }
	 
	    public long getItemId(int position) {
	        // TODO Auto-generated method stub
	        return position;
	    }
	   
	    public View getView(int position, View convertView, ViewGroup parent) {
	        // TODO Auto-generated method stub
	         View v = convertView;
	         if (v == null)
	         {
	            LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.order_item, null);
	         }
	        
                Narocilo order = _data.get(position);
           
            	   TextView narocnik = (TextView)v.findViewById(R.id.narocnikText);
		           TextView datum = (TextView)v.findViewById(R.id.datumText);
		           TextView naslov = (TextView)v.findViewById(R.id.naslovText);
		           TextView tip = (TextView)v.findViewById(R.id.tipText);
		           TextView dolzina = (TextView)v.findViewById(R.id.dolzinaText);
		           TextView kolicina = (TextView)v.findViewById(R.id.kolicinaText);
		           ImageView status = (ImageView)v.findViewById(R.id.imageView1);
		 

				try {
					SimpleDateFormat raw = new SimpleDateFormat("yyyy-MM-d hh:mm:ss");
			        Date d1 = raw.parse(order.getDate().toString());
			        Log.d("Datum", d1.toString()+"/"+order.getDate().toString());
			        SimpleDateFormat dateFormatYouWant = new SimpleDateFormat("EEE d.MMMM HH:mm");
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
		           
		           if(order.getStatus().equals("Potrjeno"))
		        	   status.setImageResource(R.drawable.ic_navigation_accept);
		           else
		        	   status.setImageResource(R.drawable.ic_action_help);
                 
	        return v;
	}
	}
	
	
	
	
}
