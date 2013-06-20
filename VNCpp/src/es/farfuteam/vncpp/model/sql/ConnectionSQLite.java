/*
 	Copyright 2013 Oscar Crespo Salazar
 	Copyright 2013 Gorka Jimeno Garrachon
 	Copyright 2013 Luis Valero Martin
  
	This file is part of VNCpp.

	VNCpp is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	any later version.
	
	VNCpp is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with VNCpp.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.farfuteam.vncpp.model.sql;

import java.util.ArrayList;

import es.farfuteam.vncpp.controller.NewConnectionActivity.QualityArray;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * @class ConnectionSQLite
 * @brief This is the class which creates and controls the data base
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @extends SQLiteOpenHelper
 */
public class ConnectionSQLite extends SQLiteOpenHelper {
	
	/** Database Version */
    private static final int DATABASE_VERSION = 1;
 
    /** Database Name */
    private static final String DATABASE_NAME = "DBConnections";
 
    /** Connection table name */
    private static final String TABLE_CONNECTIONS = "Connections";
 
    /** Connection Table Columns names */
    private static final String KEY_NAME = "NameConnection";
    private static final String KEY_IP = "ip";
    private static final String KEY_PORT = "port";
    private static final String KEY_PSW = "psw";
    private static final String KEY_FAV = "fav";
    private static final String KEY_COLOR = "colorFormat";
        
    /** instance of ConnectionSQLite */
	private static ConnectionSQLite instance = null;
	   
	/**
	 * @brief Gets the ConnectionSQLite instance
	 * @param context The context
	 * @return ConnectionSQLite the instance
	 * @details Gets the ConnectionSQLite instance
	 */
	public static ConnectionSQLite getInstance(Context context) {
	      
		if(instance == null) {
	         instance = new ConnectionSQLite(context);
	    }
	    return instance;
	    
	}    
    
	/**
	 * @brief ConnectionSQLite constructor
	 * @param context The context
	 * @return ConnectionSQLite
	 * @details ConnectionSQLite constructor
	 */
    public ConnectionSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
 
    /**
     * @brief Creates the sqlite table of Connections
     * @param db The Data Base
     * @details Creates the sqlite table of Connections
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_CONNECTIONS + "("
                + KEY_NAME + " TEXT PRIMARY KEY," + KEY_IP + " TEXT,"
                + KEY_PORT + " TEXT," + KEY_PSW + " TEXT," 
                + KEY_FAV + " TEXT," + KEY_COLOR + " TEXT" +")";
        
        db.execSQL(CREATE_USERS_TABLE);        
        
    }

    /**
     * @brief Recreates the sqlite database of Connections
     * @param db The Data Base
     * @param beforeVersion Before version of the table
     * @param newVersion The new version table
     * @details Recreates the sqlite database of Connections
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int beforeVersion, int newVersion) {
        //Se elimina la versión anterior de la tabla,aunque quizas
    	//hubiese que salvar datos anteriores, pero de momento no interesa
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECTIONS);
        
      //Se crea la nueva versión de la tabla
        onCreate(db);        

    }
    
    
  //CONTROL BASE DE DATOS
	
	/**
	 * @brief Creates a new Connection
	 * @param u The new connection
	 */
  	public void newUser(Connection u ) {  		
  		
  		SQLiteDatabase db = this.getWritableDatabase();
  		 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, u.getName());
        values.put(KEY_IP, u.getIP());
        values.put(KEY_PORT, u.getPORT());
        values.put(KEY_PSW, u.getPsw());
        values.put(KEY_FAV, u.isFav());//se crea sin ser favorito
        values.put(KEY_COLOR, u.getColorFormat().toString());
        
        // Inserting Row
        db.insert(TABLE_CONNECTIONS, null, values);
        
        db.close();

      }

  	 /**
  	 * @brief Returns an specific connection
  	 * @param nameID The id connection
  	 * @return Connection with this id
  	 */
      public Connection getUserConnection(String nameID) {

          SQLiteDatabase db = this.getReadableDatabase();
          
          Cursor cursor = db.query(TABLE_CONNECTIONS, new String[] { KEY_NAME,
                  KEY_IP, KEY_PORT, KEY_PSW, KEY_FAV, KEY_COLOR }, KEY_NAME + "=?",
                  new String[] { String.valueOf(nameID) }, null, null, null, null);
          if (cursor != null)
              cursor.moveToFirst();
          
          boolean favs = getBooleanFav(cursor.getInt(4));
          String aux_quality = cursor.getString(5);
          QualityArray quality = QualityArray.valueOf(aux_quality);
          
          Connection u = new Connection(cursor.getString(0),
                  cursor.getString(1), cursor.getString(2),cursor.getString(3),
                  favs,quality);

          db.close();

          return u;

      }
      
      /**
       * @brief Returns true if the connection is favorite
       * @param value The value in the dataBase
       * @return true if the value is 1, false in another case
       * @details Returns true if the connection is favorite
       */
      private boolean getBooleanFav(int value){
    	 if(value == 1){
    		 return true;
    	 }
    	 else{
    		 return false;
    	 }
      }

      /**
       * @brief Returns the list with all the connections
       * @return connections list
       * @details Returns the list with all the connections
       */
      public ArrayList<Connection> getAllUsers() {
          ArrayList<Connection> userList = new ArrayList<Connection>();
          // Select All Query
          String selectQuery = "SELECT  * FROM " + TABLE_CONNECTIONS;
   
          SQLiteDatabase db = this.getWritableDatabase();
          Cursor cursor = db.rawQuery(selectQuery, null);
   
          // looping through all rows and adding to list
          boolean favs;
          if (cursor.moveToFirst()) {
              do {
            	  favs = getBooleanFav(cursor.getInt(4));
            	  
                  Connection user = new Connection();
                  user.setName(cursor.getString(0));
                  user.setIP(cursor.getString(1));
                  user.setPORT(cursor.getString(2));
                  user.setPsw(cursor.getString(3));
                  user.setFav(favs);
                  
                  String aux_quality = cursor.getString(5);
                  QualityArray quality = QualityArray.valueOf(aux_quality);
                  user.setColorFormat(quality);
                  // Adding users to list
                  userList.add(user);
              } while (cursor.moveToNext());
          }
          
          db.close();
          
          // return users list
          return userList;
      }
      
      
      /**
       * @brief Returns the list with all the favorites connections
       * @return favorites connections list
       * @details Returns the list with all the favorites connections
       */
      public ArrayList<Connection> getAllFavUsers() {
          ArrayList<Connection> userList = new ArrayList<Connection>();
          // Select All Query
          String selectQuery = "SELECT  * FROM " + TABLE_CONNECTIONS;
   
          SQLiteDatabase db = this.getWritableDatabase();
          Cursor cursor = db.rawQuery(selectQuery, null);
          boolean favs;
          if (cursor.moveToFirst()) {
              do {
            	  
            	  favs = getBooleanFav(cursor.getInt(4));
            	  
                  Connection user = new Connection();
                  user.setName(cursor.getString(0));
                  user.setIP(cursor.getString(1));
                  user.setPORT(cursor.getString(2));
                  user.setPsw(cursor.getString(3));
                  user.setFav(favs);
                  String aux_quality = cursor.getString(5);
                  QualityArray quality = QualityArray.valueOf(aux_quality);
                  user.setColorFormat(quality);
                  // Adding users to list if is favorite
                  if (user.isFav())
                	  userList.add(user);
              
              } while (cursor.moveToNext());
          }
          
          db.close();
          
          // return users list
          return userList;
      }
      

      /**
       * @brief Updates the connection
       * @param user The connection to update
       * @details Updates the connection
       */
      public void updateUser(Connection user) {
    	      	  
          SQLiteDatabase db = this.getWritableDatabase();
   
          ContentValues values = new ContentValues();
          values.put(KEY_IP, user.getIP());
          values.put(KEY_PORT, user.getPORT());
          values.put(KEY_PSW, user.getPsw());
          values.put(KEY_FAV, user.isFav());
         
          QualityArray quality = user.getColorFormat();
          
          values.put(KEY_COLOR, quality.toString());
            
          // updating row 
          db.update(TABLE_CONNECTIONS, values, KEY_NAME + " = ?",
          new String[] { String.valueOf(user.getName()) });
         
          db.close();
      }
   

      /**
       * @brief Deletes the connection
       * @param user Connection to delete
       * @details Deletes the specific connection
       */
      public void deleteUser(Connection user) {
          SQLiteDatabase db = this.getWritableDatabase();
          db.delete(TABLE_CONNECTIONS, KEY_NAME + " = ?",
                  new String[] { String.valueOf(user.getName()) });
          
          db.close();
      }
   

      /**
       * @brief Returns the number of connections
       * @return number of connections
       */
      public int getUsersCount() {
          String countQuery = "SELECT  * FROM " + TABLE_CONNECTIONS;
          SQLiteDatabase db = this.getReadableDatabase();
          Cursor cursor = db.rawQuery(countQuery, null);
          cursor.close(); 
          
          db.close(); // Closing database connection
          
          // return count
          return cursor.getCount();
      }
      
      /**
       * @brief Searches a connection by the name
       * @param name The name connection
       * @return true if the connection exists, false in another case
       */
      public boolean searchNameConnection(String name){
    	  
          // Select All Connections
          String selectQuery = "SELECT  * FROM " + TABLE_CONNECTIONS;
   
          SQLiteDatabase db = this.getWritableDatabase();
          Cursor cursor = db.rawQuery(selectQuery, null);
   
          // looping through all rows and adding to list
          if (cursor.moveToFirst()) {
              do {
            	  //la columna 0 es el name, y se compara con el parametro
            	  if (cursor.getString(0).equalsIgnoreCase(name)){
            		  return true;
            	  }
              } while (cursor.moveToNext());
          }
          
          db.close(); 
    	  
          return false;
    	  
      }
    
        
}
