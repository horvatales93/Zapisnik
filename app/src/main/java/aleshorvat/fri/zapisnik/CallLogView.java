package aleshorvat.fri.zapisnik;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by horva on 1. 08. 2016.
 */
public class CallLogView extends LinearLayout {
    private TextView tvPhoneNumber;
    private TextView tvCallTime;
    private TextView tvPerson;
    private ImageView ivCallIcon;

    public CallLogView(Context context) {
        super(context);
        init(context);
    }

    public CallLogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CallLogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calllog_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tvPhoneNumber = (TextView) findViewById(R.id.tvNumber);
        tvCallTime = (TextView) findViewById(R.id.tvTime);
        tvPerson = (TextView) findViewById(R.id.tvCallerId);
        ivCallIcon = (ImageView) findViewById(R.id.ivIcon);
    }

    public void setPhoneNumber(String phoneNumber){
        tvPhoneNumber = (TextView) findViewById(R.id.tvNumber);
        tvPhoneNumber.setText(phoneNumber);
    }

    public void setCallTime(String callTime){
        tvCallTime = (TextView) findViewById(R.id.tvTime);
        tvCallTime.setText(callTime);
    }

    public void setCallerId(String callerID){
        tvPerson = (TextView) findViewById(R.id.tvCallerId);
        tvPerson.setText(callerID != null ? callerID : "UNKNOWN");
    }

    public void setCallType(int callType){
        ivCallIcon = (ImageView) findViewById(R.id.ivIcon);
        switch(callType){
            case CallLog.CALL_OUTGOING:
                ivCallIcon.setBackgroundResource(R.drawable.outgoing);
                break;
            case CallLog.CALL_INCOMING:
                ivCallIcon.setBackgroundResource(R.drawable.incoming);
                break;
            case CallLog.CALL_UNANSWERED:
                ivCallIcon.setBackgroundResource(R.drawable.missed);
                break;
        }
    }

}
