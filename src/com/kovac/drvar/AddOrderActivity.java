package com.kovac.drvar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.Duration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter.LengthFilter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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


public class AddOrderActivity extends FragmentActivity 
{	
	
	ProgressDialog mProgressDialog;
	Spinner spinner;
    AutoCompleteTextView editTextAddress;
    TextView textDate;
    Button dateButton;
	
	private RadioGroup RdGp;
    private RadioButton RdBt; 

 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_order);
		
		//Init variables
		
		
		
		//Function Calls
		PopulateLength();
		FillDates();
		
		//Spinners
		editTextAddress = (AutoCompleteTextView)findViewById(R.id.adressACText);
		editTextAddress.setAdapter(new AutoCompleteAdapter(this));
		
		//Add order
		Button AddOrder = (Button) findViewById(R.id.addButton);
		AddOrder.setOnClickListener(new OnClickListener() {
	    public void onClick(View arg0) {
	    			SharedPreferences prefs = getSharedPreferences("Drvar",MODE_PRIVATE);
	    			boolean connection = prefs.getBoolean("connection", false);
	    			Log.d("povezava", String.valueOf(connection));
	    			
	    			if(connection==true)
	    			{
	    				Toast toast = Toast.makeText(getApplicationContext(),"Dodajanje zapisa v lokalno bazo.", Toast.LENGTH_LONG);
	    				toast.show(); 
	    				AddToSQlite();
	    			}
	    			else
	    			{
	    				Toast toast = Toast.makeText(getApplicationContext(),"Dodajanje zapisa v lokalno bazo.", Toast.LENGTH_LONG);
	    				toast.show();
	    				AddToSQlite();
	    			}
	            }
	        });	

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
	
	public void AddToSQlite()
	{
		EditText Customer = (EditText) findViewById(R.id.customerText);
    	EditText Amount = (EditText) findViewById(R.id.amountText);
    	EditText dateselect = (EditText)findViewById(R.id.textDate);
		  	EditText timeselect = (EditText)findViewById(R.id.textTime);
    	
    	RdGp = (RadioGroup) findViewById(R.id.typeRadio);
        int selectedId = RdGp.getCheckedRadioButtonId();
        RdBt = (RadioButton) findViewById(selectedId);
        
        //INSERT into SQLite database
    	OrderDB oDb = new OrderDB(getApplication());
        
    	String space = " ";
    	String dolzina = spinner.getSelectedItem().toString();
    	dolzina = dolzina.replace("cm", "");
       Narocilo order = new Narocilo(90,Customer.getText().toString(), //sestava objekta
    		                           editTextAddress.getText().toString(),
    		                           dateselect.getText()+space+timeselect.getText().toString(),
    		                           RdBt.getText().toString(),
    		                           dolzina,
    		                           Amount.getText().toString(),
    		                           "Potrjeno");
       oDb.addOrder(order); //Vpis objekta
        
	}
	
	public void FillDates()
	{
		  EditText dateselect = (EditText)findViewById(R.id.textDate);
		  EditText timeselect = (EditText)findViewById(R.id.textTime);
		  
		  final Calendar c = Calendar.getInstance();
		  int hour = c.get(Calendar.HOUR_OF_DAY);
		  int minute = c.get(Calendar.MINUTE);
		  int second = c.get(Calendar.SECOND);
		  int year = c.get(Calendar.YEAR);
		  int month = c.get(Calendar.MONTH)+1;
		  int day = c.get(Calendar.DAY_OF_MONTH);
		  if (minute<10)
		  {
			  timeselect.setText(hour+":0"+minute+":"+second);
		  }
		  else
		  {
			  timeselect.setText(hour+":"+minute+":"+second);
		  }
		  if(month<10)
		  {
			  dateselect.setText(year+"-0"+month+"-"+day); 
			  if(day<10)
			  {
				  dateselect.setText(year+"-0"+month+"-0"+day); 
			  }
		  }
		  else if(day<10)
		  {
			  dateselect.setText(year+"-"+month+"-0"+day); 
		  }
		
		  else
		  {
			  dateselect.setText(year+"-"+month+"-"+day); 
		  }
		 
		
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


	
	//Populating Length Spinner
	public void PopulateLength()
	{
		spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.dolzine, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	//Adding order to database
	private class Dodaj extends AsyncTask<Void, Void, Void> {
	    Document document;
	    Elements vsebina;
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            mProgressDialog = new ProgressDialog(AddOrderActivity.this);
	            mProgressDialog.setTitle("FarmNet");
	            mProgressDialog.setMessage("Vstavljanje...");
	            mProgressDialog.setIndeterminate(false);
	            mProgressDialog.show();
	        }
	 
	        @Override
	        protected Void doInBackground(Void... params) {
	            try {
	            	
	            	 
	            	EditText Customer = (EditText) findViewById(R.id.customerText);
	            	EditText Amount = (EditText) findViewById(R.id.amountText);
	            	EditText dateselect = (EditText)findViewById(R.id.textDate);
	       		  	EditText timeselect = (EditText)findViewById(R.id.textTime);
	            	
	            	RdGp = (RadioGroup) findViewById(R.id.typeRadio);
	                int selectedId = RdGp.getCheckedRadioButtonId();
	                RdBt = (RadioButton) findViewById(selectedId);
	                Switch status = (Switch)findViewById(R.id.switch1);
	               
	                
	                //INSERT into MySQL Server
	                document = Jsoup.connect("http://kovac.mooo.com/farmnet/drvar/vnos.php")
	                .data("customer",Customer.getText().toString())
	                .data("adress",editTextAddress.getText().toString())
	                .data("date",dateselect.getText().toString()+" "+timeselect.getText().toString())
	                .data("type",RdBt.getText().toString())
	                .data("length",spinner.getSelectedItem().toString())
	                .data("amount",Amount.getText().toString())
	                .data("status",status.getTextOn().toString())
	                .header("Content-Type","application/x-www-form-urlencoded;charset=UTF-8")
	                .post();
	                vsebina = document.select("p");
	                
	             
	                
	                
	                
	                
	                
	                 
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            return null;
	        }
	 
	        @Override
	        protected void onPostExecute(Void result) {
	        	 mProgressDialog.dismiss();
	        	 for(Element p : vsebina  )
	        	 {
	        		 if(p.text().toString().equals("Zapis dodan osveževanje..."));
	        		 Toast.makeText(AddOrderActivity.this,"Zapis dodan.",Toast.LENGTH_LONG).show();
	             }
	        	 
	        }
	     
		}
	
}
