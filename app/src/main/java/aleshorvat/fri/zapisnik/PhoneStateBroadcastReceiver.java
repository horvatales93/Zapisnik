package aleshorvat.fri.zapisnik;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by horva on 29. 07. 2016.
 *
 * Recieves broadcast that is sent when PhoneState is changed,
 * determines type of call and saves data to DB
 */
public class PhoneStateBroadcastReceiver extends BroadcastReceiver{

    private static final String TAG = "CPSListener";
    private static int prev_phone_state = TelephonyManager.CALL_STATE_IDLE;
    private static Context myContext;
    private static String number;

    //This method is triggered when phone state is changed
    @Override
    public void onReceive(Context context, Intent intent) {
        String phoneState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        myContext = context;
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            number = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        }else{
            String incomingNum = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(incomingNum != null){
                number = incomingNum;
            }
        }

        if(phoneState != null){
            int state = 0;
            if(phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }else if(phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }else if(phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            onCallStateChanged(state, number);
        }
    }

        private static Date start;
        private static Date end;
        private static boolean outgoingCall;

        //Recieves phone state and incoming number and determines in what state the phone is :
        //{Ringing/Calling/Call answered/Call ended/Call unanswered}
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state){
                //Ringing
                case TelephonyManager.CALL_STATE_RINGING:
                    outgoingCall = false;
                    prev_phone_state = state;
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    start = Calendar.getInstance().getTime();
                    //Calling
                    if(prev_phone_state == TelephonyManager.CALL_STATE_IDLE){
                        outgoingCall = true;
                        prev_phone_state = state;
                    //Call answered
                    }else if(prev_phone_state == TelephonyManager.CALL_STATE_RINGING){
                        prev_phone_state = state;
                    }
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    //Call ended
                    if(prev_phone_state == TelephonyManager.CALL_STATE_OFFHOOK){
                        end = Calendar.getInstance().getTime();
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        String stringStart = df.format(start);
                        Long duration = (end.getTime() - start.getTime())/1000;
                        int callType = outgoingCall ? 0 : 1;
                        CallLog callLog = new CallLog(incomingNumber,stringStart,duration,callType);
                        //save to db
                        ZapisnikSQLiteHelper db = new ZapisnikSQLiteHelper(myContext);
                        db.addLog(callLog);

                        //get last call id from db
                        String lastId = db.getLastCallID();
                        String contact = ZapisnikSQLiteHelper.getContactName(callLog.getNumber());

                        //start activity to input call note
                        Intent i = new Intent();
                        i.setClassName("aleshorvat.fri.zapisnik", "aleshorvat.fri.zapisnik.DetailsActivity");
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setAction("logDetail");
                        Bundle bundle = new Bundle();
                        bundle.putString("id", lastId);
                        bundle.putString("contact", contact != null ? contact : "UNKNOWN");
                        i.putExtras(bundle);
                        myContext.startActivity(i);

                        prev_phone_state = state;
                    //Call missed
                    }else if(prev_phone_state == TelephonyManager.CALL_STATE_RINGING){
                        end = Calendar.getInstance().getTime();
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        String stringEnd = df.format(end);
                        CallLog callLog = new CallLog(incomingNumber,stringEnd,(long)0,2);
                        //save to db
                        ZapisnikSQLiteHelper db = new ZapisnikSQLiteHelper(myContext);
                        db.addLog(callLog);
                        prev_phone_state = state;
                    }
                    break;
            }

        }

}
