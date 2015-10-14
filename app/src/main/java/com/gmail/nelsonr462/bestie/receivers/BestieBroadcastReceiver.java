package com.gmail.nelsonr462.bestie.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;

import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.events.BestieReadyEvent;
import com.gmail.nelsonr462.bestie.ui.MainActivity;
import com.parse.ParseConfig;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;


public class BestieBroadcastReceiver extends ParsePushBroadcastReceiver {
    public static final String PARSE_DATA_KEY = "com.parse.Data";
    public static final String TAG = BestieBroadcastReceiver.class.getSimpleName();
    private ParseConfig mParseConfig;


    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mParseConfig = ParseConfig.getCurrentConfig();

        JSONObject data = getDataFromIntent(intent);

        String text = "";

        try {
            text = data.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define intent

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("activeTab", 2);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent notifyPIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Bestie");
        builder.setContentText(text);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(notifyPIntent);
        builder.setAutoCancel(true);

        notificationManager.notify(TAG, 0, builder.build());

        // Check if app is active

        if (ParseConstants.isAppActive) {
            EventBus.getDefault().post(new BestieReadyEvent());
            vibrator.vibrate(100);
            return;
        }

        vibrator.vibrate(200);

    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        // Define intent

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("activeTab", 2);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainIntent);

    }

    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
        } catch (JSONException e) {
            // Json was not readable...
        }
        return data;
    }
}
