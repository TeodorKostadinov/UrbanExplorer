package fmi.tkostadinov.urbanexplorer.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private final Context context;
	//TODO may cause error b-e of package
	private static String DB_PATH = "/data/data/fmi.tkostadinov.urbanexplorer/databases/";
	private static String DB_NAME = "questionsSQL";
	private static int DB_VERSION = 1;
	protected SQLiteDatabase questionsDB; 
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	/**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
    	if(dbExist){
    		//do nothing - database already exist
    	} else {
    		//By calling this method an empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
        	try {
    			copyDataBase();
    		} catch (IOException e) {
        		throw new Error("Error copying database");
        	}
    	}
    }
    
    private boolean checkDataBase(){
    	 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + this.DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
    		//database does't exist yet.
    	}
 
    	if(checkDB != null){
    		checkDB.close();
    	}
 
    	return checkDB != null ? true : false;
    }
    
    private void copyDataBase() throws IOException{
    	 
    	//Open your local db as the input stream
    	InputStream myInput = context.getAssets().open(this.DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + this.DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }
    
    public void openDataBase() throws SQLException{
    	//Open the database
        String myPath = DB_PATH + this.DB_NAME;
    	this.questionsDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
 
    @Override
	public synchronized void close() {
    	    if(this.questionsDB != null)
    		    this.questionsDB.close();
 
    	    super.close();
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
}
