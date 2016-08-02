package aleshorvat.fri.zapisnik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        String id = "0";
        String contact = "UNKNOWN";

        if(intent != null){
            final Bundle bundle = intent.getExtras();
            if(bundle != null){
                id = bundle.getString("id");
                contact = bundle.getString("contact");
            }
        }else{
            Intent returnToMain = new Intent(DetailsActivity.this,MainActivity.class);
            startActivity(returnToMain);
        }

        final ZapisnikSQLiteHelper db = new ZapisnikSQLiteHelper(this);
        final CallLog clog = db.getLog(Integer.parseInt(id));
        final Context myContext = this;

        TextView contactID = (TextView) findViewById(R.id.tvDetailsContact);
        TextView callNumber = (TextView) findViewById(R.id.tvDetailsPhoneNumber);
        TextView callTime = (TextView) findViewById(R.id.tvDetailsCallTime);
        TextView callDuration = (TextView) findViewById(R.id.tvDetailsDuration);
        final EditText callNote = (EditText) findViewById(R.id.etDetailsNote);
        Button save = (Button) findViewById(R.id.btnDetailsSaveNote);

        contactID.setText("Contact : " + contact );
        callNumber.setText("Number : " + clog.getNumber());
        callTime.setText("Time : " + clog.getCallTime());
        if(clog.getCallDuration() < 60){
            callDuration.setText("Duration : " + clog.getCallDuration().toString() + " sec");
        }else{
            callDuration.setText("Duration : " + (int)(clog.getCallDuration()/60) + " min " + clog.getCallDuration()%60 + " sec");
        }
        callNote.setText(clog.getNote());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
                CallLog newLog = new CallLog();
                newLog.setCallNote(callNote.getText().toString());
                newLog.setCallId(clog.getCallId());
                newLog.setTelNumber(clog.getNumber());
                newLog.setCallDateTime(clog.getCallTime());
                newLog.setCallDuration(clog.getCallDuration());
                newLog.setCallType(clog.getCallType());

                int result = db.updateLog(newLog);

                if(result == 1){
                    Toast toast = Toast.makeText(DetailsActivity.this, "Saved sucessfully", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(DetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

}
