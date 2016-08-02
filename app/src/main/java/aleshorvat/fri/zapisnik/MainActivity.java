package aleshorvat.fri.zapisnik;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    LinearLayout listLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,Manifest.permission.PROCESS_OUTGOING_CALLS}, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        listLayout = (LinearLayout) findViewById(R.id.logList);
        listLayout.removeAllViews();
        buildLogList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildLogList(){
        listLayout = (LinearLayout) findViewById(R.id.logList);

        ZapisnikSQLiteHelper db = new ZapisnikSQLiteHelper(this);
        List<CallLog> logs = db.getAllLogs();
        if(logs.isEmpty()){
            Toast toast = Toast.makeText(MainActivity.this, "Call log empty...you should make a call", Toast.LENGTH_LONG);
            toast.show();
        }
        ListIterator<CallLog> logIterator = logs.listIterator();
        while(logIterator.hasNext()){

            CallLog callLog = logIterator.next();

            final String contact = ZapisnikSQLiteHelper.getContactName(callLog.getNumber());
            final int id = callLog.getCallId() ;

            CallLogView callLogView = new CallLogView(this);
            callLogView.setPhoneNumber(callLog.getNumber());
            callLogView.setCallTime(callLog.getCallTime());
            callLogView.setCallType(callLog.getCallType());
            callLogView.setCallerId(contact);
            callLogView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallLogView clv = (CallLogView)v;
                    Intent details = new Intent(MainActivity.this, DetailsActivity.class);
                    details.setAction("logDetail");
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id+"");
                    bundle.putString("contact", contact != null ? contact : "UNKNOWN");
                    details.putExtras(bundle);
                    startActivity(details);
                }
            });

            listLayout.addView(callLogView);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
