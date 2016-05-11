package com.kovac.drvar;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class OrderActivity extends FragmentActivity
{	
	Narocilo sorder, aorder;
	String host = "http://kovac.mooo.com";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);
		FillData();
		
		  EditText dateselect = (EditText)findViewById(R.id.textDate);
	        dateselect.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	KliciDDialog();
	            	
	            }
	        });
	        
	        EditText timeselect = (EditText)findViewById(R.id.textTime);
	        timeselect.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	KliciTDialog();
	            }
	        });
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.order, menu);
		return (super.onCreateOptionsMenu(menu));
	}
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	 switch (item.getItemId()) {
	 case R.id.save:
		UpdateOrder();
	 return (true);
	 }
	 return super.onOptionsItemSelected(item);
	 } 
	 
	 public void KliciDDialog()
	    {
	    	DialogFragment newFragment = new DatePickerFragment();
	        newFragment.show(getSupportFragmentManager(), "datePicker");
		
	    }
	    
	    public void KliciTDialog()
	    {
	    	DialogFragment newFragment = new TimePickerFragment();
	        newFragment.show(getSupportFragmentManager(), "timePicker");	
	    }
	    
	    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
	    {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Do something with the time chosen by the user
			String sMinute = "";
			String sHour = "";
			if (minute<10)
				sMinute = "0"+minute;
			else
				sMinute = sMinute.valueOf(minute);
			if(hourOfDay<10)
			{
				sHour="0"+hourOfDay;
			}
			else
			{
				sHour= sHour.valueOf(hourOfDay);
			}
			((EditText) getActivity().findViewById(R.id.textTime)).setText(sHour+":"+sMinute+":00");
		}
		}
	    
	    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
	    {

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState)
			{
				// Use the current date as the default date in the picker
				final Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DAY_OF_MONTH);
				
				// Create a new instance of DatePickerDialog and return it
				return new DatePickerDialog(getActivity(), this, year, month, day);
			}
			
			public void onDateSet(DatePicker view, int year, int month, int day)
			{
				month++;
				String sMonth = "";
				String sDay = "";
				
				if(month<10)
					sMonth="0"+month;
				if (day<10)
					sDay="0"+day;
				((EditText) getActivity().findViewById(R.id.textDate)).setText(year+"-"+sMonth+"-"+sDay); 
			}
		}
			    
			    
		
	 
	 
	 
	 public void UpdateOrder()
	 {
		 OrderDB oDb = new OrderDB(getApplication());
		

		    EditText Customer = (EditText) findViewById(R.id.customerText);
		    EditText editTextAddress=(EditText)findViewById(R.id.adressACText);
	    	EditText Amount = (EditText) findViewById(R.id.amountText);
	    	EditText dateselect = (EditText)findViewById(R.id.textDate);
			EditText timeselect = (EditText)findViewById(R.id.textTime);
	    	
	    	RadioGroup RdGp = (RadioGroup) findViewById(R.id.typeRadio);
	        int selectedId = RdGp.getCheckedRadioButtonId();
	        RadioButton RdBt = (RadioButton) findViewById(selectedId);
	        Spinner dolzina = (Spinner)findViewById(R.id.spinner1);
            Switch status = (Switch)findViewById(R.id.switch1);
	        
	        //INSERT into SQLite database
	        
	    	String space = " ";
	    	String niz = "";
	    	boolean stat =status.isChecked();
	    	if (stat)
	    		niz = "Potrjeno";
	    	else
	    		niz= "Cakanje";
	    	String len = dolzina.getSelectedItem().toString();
	    	len = len.replace("cm", "");
	       Narocilo order = new Narocilo(90,Customer.getText().toString(), //sestava objekta
	    		                           editTextAddress.getText().toString(),
	    		                           dateselect.getText()+space+timeselect.getText().toString(),
	    		                           RdBt.getText().toString(),
	    		                           len,
	    		                           Amount.getText().toString(),
	    		                           niz);
	       aorder=order;
	       new AddToNet().execute();
	       oDb.addOrder(order); //Vpis objekta
	      
	       Toast.makeText(getApplicationContext(), "Naroèilo posodobljeno", Toast.LENGTH_SHORT).show();
	       oDb.deleteOrder(sorder);
	       
	       
	

	 }
	 
	 private class AddToNet extends AsyncTask<Void, Void, Void> {
	
		    
		        @Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		        
		        }
		 
		        @Override
		        protected Void doInBackground(Void... params) {
		        	  
		        		  	OrderDB oDb = new OrderDB(getApplication()); //objekt podatkovne baze
		        			
			                	
			                	 try {
									Jsoup.connect(host+"/farmnet/drvar/vnos.php")
									.data("customer",aorder.getName())
									.data("adress",aorder.getAdress())
									.data("date",aorder.getDate())  
									.data("type",aorder.getType())          //pridobitev podatkov iz objekta
									.data("length",aorder.getLength())
									.data("amount",aorder.getAmount())
									.data("status",aorder.getStatus())
									.header("Content-Type","application/x-www-form-urlencoded;charset=UTF-8") //utf-8 Glava
									.post();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			                	
			                	oDb.DeleteAll();
			                
			                
			                	 return null;
			                
			                 
			            } 
		           
		        }

		     
		    
	 
	 
	 
	 public void FillData()
	 {
		 AutoCompleteTextView editTextAddress;
		 editTextAddress = (AutoCompleteTextView)findViewById(R.id.adressACText);
		 editTextAddress.setAdapter(new AutoCompleteAdapter(this));
		 Bundle b = getIntent().getExtras();
		 int value = b.getInt("id");
		 String id = Integer.toString(value);
		 
		 OrderDB oDb = new OrderDB(getApplication());
		 List<Narocilo> orders = new LinkedList<Narocilo>();
         orders = oDb.GetSingleOrder(id);
         
         Spinner dolzina = (Spinner)findViewById(R.id.spinner1);
 		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.dolzine, android.R.layout.simple_spinner_item);
 		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 		dolzina.setAdapter(adapter);
         
         
         
         for(Narocilo order : orders)
	        {
        	 	   sorder=order;
        	 	   Log.d("Narocilo", order.toString());
	               EditText  narocnik = (EditText)findViewById(R.id.customerText);
	               EditText  naslov = (EditText)findViewById(R.id.adressACText);
	               EditText  datum = (EditText)findViewById(R.id.textDate);
	               EditText  ura = (EditText)findViewById(R.id.textTime);
	               RadioGroup tip = (RadioGroup)findViewById(R.id.typeRadio);
	               RadioButton drva = (RadioButton)findViewById(R.id.radio0);
		           RadioButton paleta = (RadioButton)findViewById(R.id.radio1);   
	               EditText  kolicina = (EditText)findViewById(R.id.amountText);
	               Switch status = (Switch)findViewById(R.id.switch1);
		 

				try {
					SimpleDateFormat raw = new SimpleDateFormat("yyyy-MM-d hh:mm:ss");
			        Date d1 = raw.parse(order.getDate().toString());
			        Log.d("Datum", d1.toString()+"/"+order.getDate().toString());
			        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
			        String sCertDate = date.format(d1);
			        String sCertTime = time.format(d1);
			        datum.setText(sCertDate);
			        ura.setText(sCertTime);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		           
		           
		           narocnik.setText(order.getName());
		           naslov.setText(order.getAdress());
		           if(order.getType().equals("Drva"))
		           {
		        	   drva.setChecked(true);
		        	   paleta.setChecked(false);
		           }
		           else
		           {
		        	   paleta.setChecked(true);
		        	   drva.setChecked(false);
		           }
		    
		           
		           
		           
		          for (int i=0;i<dolzina.getCount();i++)
		          {
		        	  int position;
		        	  if(dolzina.getItemAtPosition(i).toString().equals(order.getLength().toString()))
		        	  {
		        		 position = i;
		        		  i=dolzina.getCount();
		        		  dolzina.setSelection(position);
		        	  }
		        	  
		          }
		          
		          
		           
		           kolicina.setText(order.getAmount());
		           
		           if(order.getStatus().equals("Potrjeno"))
		        	   status.setChecked(true);
		           else
		        	   status.setChecked(false);
		           
	        }
        
	 }
	 
	 

		//Adress AUTO COMPLETE
		private class AutoCompleteAdapter extends ArrayAdapter<Address> implements Filterable {

	        private LayoutInflater mInflater;
	        private Geocoder mGeocoder;
	        private StringBuilder mSb = new StringBuilder();

	        public AutoCompleteAdapter(final Context context) {
	            super(context, -1);
	            mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
	            mGeocoder = new Geocoder(context);
	        }

	        @Override
	        public View getView(final int position, final View convertView, final ViewGroup parent) {
	            final TextView tv;
	            if (convertView != null) {
	                tv = (TextView) convertView;
	            } else {
	                tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
	            }

	            tv.setText(createFormattedAddressFromAddress(getItem(position)));
	            return tv;
	        }

	        private String createFormattedAddressFromAddress(final Address address) {
	            mSb.setLength(0);
	            final int addressLineSize = address.getMaxAddressLineIndex();
	            for (int i = 0; i < addressLineSize; i++) {
	                mSb.append(address.getAddressLine(i));
	                if (i != addressLineSize - 1) {
	                    mSb.append(", ");
	                }
	            }
	            return mSb.toString();
	        }

	        @Override
	        public Filter getFilter() {
	            Filter myFilter = new Filter() {
	                @Override
	                protected FilterResults performFiltering(final CharSequence constraint) {
	                    List<Address> addressList = null;
	                    if (constraint != null) {
	                        try {
	                            addressList = mGeocoder.getFromLocationName((String) constraint, 5);
	                        } catch (IOException e) {
	                        }
	                    }
	                    if (addressList == null) {
	                        addressList = new ArrayList<Address>();
	                    }

	                    final FilterResults filterResults = new FilterResults();
	                    filterResults.values = addressList;
	                    filterResults.count = addressList.size();

	                    return filterResults;
	                }

	                @SuppressWarnings("unchecked")
	                @Override
	                protected void publishResults(final CharSequence contraint, final FilterResults results) {
	                    clear();
	                    for (Address address : (List<Address>) results.values) {
	                        add(address);
	                    }
	                    if (results.count > 0) {
	                        notifyDataSetChanged();
	                    } else {
	                        notifyDataSetInvalidated();
	                    }
	                }

	                @Override
	                public CharSequence convertResultToString(final Object resultValue) {
	                    return resultValue == null ? "" : createFormattedAddressFromAddress((Address) resultValue);
	                }
	            };
	            return myFilter;
			}
		}


		
	 
}
	
   
	
	
                
           
	
	
	

