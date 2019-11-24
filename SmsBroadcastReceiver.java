package com.example.android.smsproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by android on 4/20/17.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        NotificationManager notification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            if(bundle != null){
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for(int i = 0; i < pdusObj.length; i++){
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = smsMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = smsMessage.getDisplayMessageBody();

                    Intent startIntent = new Intent(context, MainActivity.class);

                    startIntent.putExtra("number", senderNum);
                    startIntent.putExtra("message", message);

                    PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), startIntent, 0);

                    Notification smsNotification = new Notification.Builder(context)
                            .setContentTitle("Nova poruka od: " + phoneNumber)
                            .setSmallIcon(R.drawable.sms_image)
                            .addAction(R.mipmap.ic_launcher, "Obrisi", pIntent)
                            .setAutoCancel(true)
                            .setContentIntent(pIntent).build();

                    notification.notify(0, smsNotification);
                }
            }
        } catch (Exception e){
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }

    }

}
