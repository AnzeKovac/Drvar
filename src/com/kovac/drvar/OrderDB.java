package com.kovac.drvar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.InputFilter.LengthFilter;
import android.util.Log;

public class OrderDB extends SQLiteOpenHelper {

// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "OrdersDB";
   
	public OrderDB(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);	
}

	@Override
	public void onCreate(SQLiteDatabase db) {
	// SQL statement to create book table
	String CREATE_ORDER_TABLE = "CREATE TABLE orders ( " +
	                "id INTEGER PRIMARY KEY, " +
	"CustomerName TEXT, "+
	"Adress TEXT, "+
	"OrderDate TEXT, "+
	"Type TEXT, "+
	"Length TEXT, "+
	"Amount TEXT, "+
	"Status TEXT )";
	
	// create books table
	db.execSQL(CREATE_ORDER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	// Drop older books table if existed
	        db.execSQL("DROP TABLE IF EXISTS orders");
	        
	        // create fresh books table
	        this.onCreate(db);
	}
//---------------------------------------------------------------------
   
/**
* CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
*/

// Books table name
    private static final String TABLE_ORDERS = "orders";
    
    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "CustomerName";
    private static final String KEY_ADRESS = "Adress";
    private static final String KEY_DATE = "OrderDate";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_LENGTH = "Length";
    private static final String KEY_AMOUNT = "Amount";
    private static final String KEY_STATUS = "Status";
    
    private static final String[] COLUMNS = {KEY_ID,KEY_NAME,KEY_ADRESS,KEY_DATE,KEY_TYPE,KEY_LENGTH,KEY_AMOUNT,KEY_STATUS};
    
	public void addOrder(Narocilo order){
	Log.d("Dodajanje naroèila", order.getStatus().toString());
	// 1. get reference to writable DB
	SQLiteDatabase db = this.getWritableDatabase();
	
	// 2. create ContentValues to add key "column"/value
	        ContentValues values = new ContentValues();
	        values.put(KEY_ID, order.getId());
	        values.put(KEY_NAME,  order.getName());
	        values.put(KEY_ADRESS, order.getAdress());
	        values.put(KEY_DATE, order.getDate());
	        values.put(KEY_TYPE, order.getType());
	        values.put(KEY_LENGTH, order.getLength());
	        values.put(KEY_AMOUNT, order.getAmount());
	        values.put(KEY_STATUS, order.getStatus());
	        Log.d("Vrednosti" ,values.toString());
	 
	        // 3. insert
	        db.insert(TABLE_ORDERS, null, values); 
	        
	        // 4. close
	        db.close();
	}

	public Narocilo getOrder(int id){
	
	// 1. get reference to readable DB
	SQLiteDatabase db = this.getReadableDatabase();
	
	// 2. build query
	        Cursor cursor =
	         db.query(TABLE_ORDERS, // a. table
	         COLUMNS, // b. column names
	         " id = ?", // c. selections
	                new String[] { String.valueOf(id) }, // d. selections args
	                null, // e. group by
	                null, // f. having
	                null, // g. order by
	                null); // h. limit
	        
	        // 3. if we got results get the first one
	        if (cursor != null)
	            cursor.moveToFirst();
	 
	        // 4. build book object
	        Narocilo order = new Narocilo();
	        order.setId(Integer.parseInt(cursor.getString(0)));
	        order.setName(cursor.getString(1));
	        order.setAdress(cursor.getString(2));
	        order.setDate(cursor.getString(3));
	        order.setType(cursor.getString(4));
	        order.setLength(cursor.getString(5));
	        order.setAmount(cursor.getString(6));
	        order.setStatus(cursor.getString(7));
	
	Log.d("Pridobi knjigo:("+id+")", order.toString());
	
	        // 5. return book
	        return order;
	}

	// Get All Books
	    public List<Narocilo> getAllOrders() {
	    	
	    	List<Narocilo> orders = new LinkedList<Narocilo>();
	
	        // 1. build the query
	        String query = "SELECT * FROM " + TABLE_ORDERS;
	 
	     // 2. get reference to writable DB
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(query, null);
	 
	        // 3. go over each row, build book and add it to list
	        Narocilo order = null;
	        if (cursor.moveToFirst()) {
	            do {
	             order = new Narocilo();
	                order.setId(Integer.parseInt(cursor.getString(0)));
	    	        order.setName(cursor.getString(1));
	    	        order.setAdress(cursor.getString(2));
	    	        order.setDate(cursor.getString(3));
	    	        order.setType(cursor.getString(4));
	    	        order.setLength(cursor.getString(5));
	    	        order.setAmount(cursor.getString(6));
	    	        order.setStatus(cursor.getString(7));
	
	                // Add book to books
	               orders.add(order);
	            } while (cursor.moveToNext());
	        }
	        
	        Log.d("Vsa narocila :", orders.toString());
	
	        // return books
	        return orders;
	    }

	// Updating single book
	    public int updateOrder(Narocilo order) {
	
	     // 1. get reference to writable DB
	        SQLiteDatabase db = this.getWritableDatabase();
	 
	// 2. create ContentValues to add key "column"/value
	        ContentValues values = new ContentValues();
	        values.put(KEY_NAME,  order.getName());
	        values.put(KEY_ADRESS, order.getAdress());
	        values.put(KEY_DATE, order.getDate());
	        values.put(KEY_TYPE, order.getType());
	        values.put(KEY_LENGTH, order.getLength());
	        values.put(KEY_AMOUNT, order.getAmount());
	        values.put(KEY_STATUS, order.getStatus());
	 
	        // 3. updating row
	        int i = db.update(TABLE_ORDERS, //table
	         values, // column/value
	         KEY_ID+" = ?", // selections
	                new String[] { String.valueOf(order.getId()) }); //selection args
	        
	        // 4. close
	        db.close();
	        
	        return i;
	    }

    // Deleting single book
    public void deleteOrder(Narocilo order) {

     // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        
        // 2. delete
        db.delete(TABLE_ORDERS,
         KEY_ID+" = ?",
                new String[] { String.valueOf(order.getId()) });
        
        // 3. close
        db.close();
        
        Log.d("Brisanje knjige", order.toString());

    }
    
    public long count() {
    	SQLiteDatabase db = this.getWritableDatabase();
        return DatabaseUtils.queryNumEntries(db,"orders");
    }
    
    public String getID()
    {
    String niz = "";	
    	 // 1. build the query
        String query = "SELECT id FROM " + TABLE_ORDERS;
 
     // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
    	        
    	       
    	 try 
    	 {
    		 // 3. if we got results get the first one
 	        if (cursor != null)
 	            cursor.moveToFirst();
 	        do 
 	        {
 	        	niz += cursor.getString(0)+",";
	        } while (cursor.moveToNext());
    		 
    	 } 
    	 catch (Exception e)
    	 {
    		 niz = "0";
    	 }
    	      
    	 Log.d("ID-ji", niz);
    	       
    	
    	
    	
    	        // 5. return book
    	        return niz;
    }
    
    
    public List<Narocilo> getSpecOrders(String[] lines) {
    	
    	List<Narocilo> orders = new LinkedList<Narocilo>();
    	String raw ;
    	String sqlite = this.getID();
    	String elements = "";
    	for (int i = 0; i<lines.length;i++)
    	{
    		if(lines[i]==null)
    		{
    			Log.d("null", "vrednost elementa je NULLL");
    			break;
    		}
    		else
    		{
    			elements = elements+lines[i]+",";
    		}
    		
		}
    	if (elements.endsWith(",")) {
    		  elements = elements.substring(0, elements.length() - 1);
    		}
    	 Log.d("Elementi", elements);
    	
    
    	
    	
    	
        // 1. build the query
        String query = "SELECT * FROM " + TABLE_ORDERS + " WHERE ID IN ("+elements+")";
 
     // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, build book and add it to list
        Narocilo order = null;
        if (cursor.moveToFirst()) {
            do {
             order = new Narocilo();
                order.setId(Integer.parseInt(cursor.getString(0)));
    	        order.setName(cursor.getString(1));
    	        order.setAdress(cursor.getString(2));
    	        order.setDate(cursor.getString(3));
    	        order.setType(cursor.getString(4));
    	        order.setLength(cursor.getString(5));
    	        order.setAmount(cursor.getString(6));
    	        order.setStatus(cursor.getString(7));

                // Add book to books
               orders.add(order);
            } while (cursor.moveToNext());
        }
        
        Log.d("Specifièna naroèila :", orders.toString());

        // return books
        return orders;
    }
    
 public ArrayList<Narocilo> getFilterOrders(String type) {
    	
    	ArrayList<Narocilo> orders = new ArrayList<Narocilo>();
    	String range = "";
    	if(type.equals("week"))
    		range="+7 day";
    	else
    		range="+1 month";
    
    	
    	
    	Log.d("DNEVI", range);
        // 1. build the query;
        String query = "SELECT * FROM " + TABLE_ORDERS + " WHERE OrderDate >= (Select datetime('now')as '' ) AND OrderDate <=(SELECT datetime('now','"+range+"')as '') ORDER BY OrderDate";
 
     // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, build book and add it to list
        Narocilo order = null;
        if (cursor.moveToFirst()) {
            do {
             order = new Narocilo();
                order.setId(Integer.parseInt(cursor.getString(0)));
    	        order.setName(cursor.getString(1));
    	        order.setAdress(cursor.getString(2));
    	        order.setDate(cursor.getString(3));
    	        order.setType(cursor.getString(4));
    	        order.setLength(cursor.getString(5));
    	        order.setAmount(cursor.getString(6));
    	        order.setStatus(cursor.getString(7));

                // Add book to books
               orders.add(order);
            } while (cursor.moveToNext());
        }
        
        Log.d("Narocila po datumu:", orders.toString());

        // return books
        return orders;
    }
 
 public List<Narocilo> GetNextOrder() {
 	
 	List<Narocilo> orders = new LinkedList<Narocilo>();
 

 	
     // 1. build the query;
     String query = "SELECT * FROM " + TABLE_ORDERS + " WHERE OrderDate >= (Select datetime('now')as '' ) ORDER BY OrderDate LIMIT 1";

  // 2. get reference to writable DB
     SQLiteDatabase db = this.getWritableDatabase();
     Cursor cursor = db.rawQuery(query, null);

     // 3. go over each row, build book and add it to list
     Narocilo order = null;
     if (cursor.moveToFirst()) {
         do {
          order = new Narocilo();
             order.setId(Integer.parseInt(cursor.getString(0)));
 	        order.setName(cursor.getString(1));
 	        order.setAdress(cursor.getString(2));
 	        order.setDate(cursor.getString(3));
 	        order.setType(cursor.getString(4));
 	        order.setLength(cursor.getString(5));
 	        order.setAmount(cursor.getString(6));
 	        order.setStatus(cursor.getString(7));

             // Add book to books
            orders.add(order);
         } while (cursor.moveToNext());
     }
     
     Log.d("Naslednje narocilo:", orders.toString());

     // return books
     return orders;
 }
 
 	
 public void DeleteAll()
 {
	 String query = "DELETE FROM "+TABLE_ORDERS;

	  // 2. get reference to writable DB
	     SQLiteDatabase db = this.getWritableDatabase();
	     db.rawQuery(query, null);
 }
 
 public List<Narocilo> GetSingleOrder(String id) {
	 	
	 	List<Narocilo> orders = new LinkedList<Narocilo>();
	 

	 	
	     // 1. build the query;
	     String query = "SELECT * FROM " + TABLE_ORDERS + " WHERE id="+id;

	  // 2. get reference to writable DB
	     SQLiteDatabase db = this.getWritableDatabase();
	     Cursor cursor = db.rawQuery(query, null);

	     // 3. go over each row, build book and add it to list
	     Narocilo order = null;
	     if (cursor.moveToFirst()) {
	         do {
	          order = new Narocilo();
	             order.setId(Integer.parseInt(cursor.getString(0)));
	 	        order.setName(cursor.getString(1));
	 	        order.setAdress(cursor.getString(2));
	 	        order.setDate(cursor.getString(3));
	 	        order.setType(cursor.getString(4));
	 	        order.setLength(cursor.getString(5));
	 	        order.setAmount(cursor.getString(6));
	 	        order.setStatus(cursor.getString(7));

	             // Add book to books
	            orders.add(order);
	         } while (cursor.moveToNext());
	     }
	     
	     Log.d("PosameznoNarocilo:", orders.toString());

	     // return books
	     return orders;
	 }


	

}