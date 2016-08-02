package aleshorvat.fri.zapisnik;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by horva on 31. 07. 2016.
 */
public class ZapisnikSQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "zapisnikDB";

    private static final String TABLE_ZAPISNIKI = "zapisniki";
    private static final String KEY_ID = "id";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_CALL_TIME = "callTime";
    private static final String KEY_CALL_DURATION = "callDuration";
    private static final String KEY_CALL_TYPE = "callType";
    private static final String KEY_CALL_NOTE = "callNote";
    private static final String[] COLUMNS = {KEY_ID,KEY_PHONE_NUMBER,KEY_CALL_TIME,KEY_CALL_DURATION,KEY_CALL_TYPE, KEY_CALL_NOTE};
    static Context myContext;

    public ZapisnikSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CALL_LOG_TABLE = "CREATE TABLE zapisniki ( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " phoneNumber TEXT, callTime TEXT, callDuration INTEGER, callType INTEGER, callNote TEXT )";
        db.execSQL(CREATE_CALL_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS zapisniki");
        this.onCreate(db);
    }

    public void addLog(CallLog callLog){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_PHONE_NUMBER, callLog.getNumber());
        values.put(KEY_CALL_TIME, callLog.getCallTime().toString());
        values.put(KEY_CALL_DURATION, callLog.getCallDuration());
        values.put(KEY_CALL_TYPE, callLog.getCallType());
        values.put(KEY_CALL_NOTE, "");

        // 3. insert
        db.insert(TABLE_ZAPISNIKI, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();

        Log.i("Saved : ",callLog.toString());
    }

    public CallLog getLog(int id){
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_ZAPISNIKI, // a. table
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

        // 4. build log object
        CallLog callLog = new CallLog();
        callLog.setCallId(Integer.parseInt(cursor.getString(0)));
        callLog.setTelNumber(cursor.getString(1));
        callLog.setCallDateTime(cursor.getString(2));
        callLog.setCallDuration(Long.parseLong(cursor.getString(3)));
        callLog.setCallType(Integer.parseInt(cursor.getString(4)));
        callLog.setCallNote(cursor.getString(5));

        //log
        Log.i("getBook(" + id + ")", callLog.toString());

        return callLog;
    }

    public List<CallLog> getAllLogs(){
        List<CallLog> logs = new LinkedList<CallLog>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ZAPISNIKI + " ORDER BY callTime DESC";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build log and add it to list
        CallLog callLog = null;
        if (cursor.moveToFirst()) {
            do {
                callLog = new CallLog();
                callLog.setCallId(Integer.parseInt(cursor.getString(0)));
                callLog.setTelNumber(cursor.getString(1));
                callLog.setCallDateTime(cursor.getString(2));
                callLog.setCallDuration(Long.parseLong(cursor.getString(3)));
                callLog.setCallType(Integer.parseInt(cursor.getString(4)));
                callLog.setCallNote(cursor.getString(5));

                // Add log to logs
                logs.add(callLog);
            } while (cursor.moveToNext());
        }

        Log.i("getAllLogs()", logs.toString());

        return logs;
    }

    public int updateLog(CallLog callLog) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_PHONE_NUMBER, callLog.getNumber());
        values.put(KEY_CALL_TIME, callLog.getCallTime().toString());
        values.put(KEY_CALL_DURATION, callLog.getCallDuration());
        values.put(KEY_CALL_TYPE, callLog.getCallType());
        values.put(KEY_CALL_NOTE, callLog.getNote());

        // 3. updating row
        int i = db.update(TABLE_ZAPISNIKI, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(callLog.getCallId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deleteLog(CallLog callLog) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_ZAPISNIKI, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(callLog.getCallId())}); //selections args

        // 3. close
        db.close();

        //log
        Log.i("deleteLog", callLog.toString());

    }

    public String getLastCallID(){
        // 1. build the query
        String query = "SELECT  id FROM " + TABLE_ZAPISNIKI + " ORDER BY id DESC LIMIT 1";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        String lastId = "0";
        // 3. go over each row, build log and add it to list
        CallLog callLog = null;
        if (cursor.moveToFirst()) {
                lastId = cursor.getString(0);
        }

        // 4. close
        db.close();

        return lastId;
    }

    public static String getContactName(String phoneNumber){
        ContentResolver cr = myContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

}
