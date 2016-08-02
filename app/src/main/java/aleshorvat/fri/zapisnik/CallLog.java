package aleshorvat.fri.zapisnik;

import java.util.GregorianCalendar;

/**
 * Created by horva on 31. 07. 2016.
 */
public class CallLog {

    public static final int CALL_OUTGOING = 0;
    public static final int CALL_INCOMING = 1;
    public static final int CALL_UNANSWERED = 2;

    private int callId;
    //incoming/outgoing telephone number
    private String telNumber;
    //date and time when call was made
    private String callDateTime;
    //call duration in seconds
    private Long callDuration;
    //0 - outgoing call, 1 - incoming call, 2 - unanswered call
    private int callType;
    //call note
    private String callNote;

    public CallLog(String number, String callTime, Long duration, int type){
        telNumber = number;
        callDateTime = callTime;
        callDuration = duration;
        callType = type;
    }

    public CallLog(){

    }

    public String getNumber(){
        return telNumber;
    }

    public String getCallTime(){
        return callDateTime;
    }

    public Long getCallDuration(){
        return callDuration;
    }

    public int getCallType(){
        return callType;
    }

    public String getNote(){
        return callNote;
    }

    public int getCallId(){
        return callId;
    }

    public void setTelNumber(String number){
        telNumber = number;
    }

    public void setCallDateTime(String callTime){
        callDateTime = callTime;
    }

    public void setCallDuration(Long duration){
        callDuration = duration;
    }

    public void setCallType(int type){
        callType = type;
    }

    public void setCallNote(String note){
        callNote = note;
    }

    public void setCallId(int id){
        callId = id;
    }

    @Override
    public String toString() {
        return callId + " " + telNumber + " " + callDateTime + " " + callDuration + " " + callType;
    }
}
