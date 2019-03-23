package swd.affiliatemarketing.hotpromotion.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import swd.affiliatemarketing.hotpromotion.MainActivity;
import swd.affiliatemarketing.hotpromotion.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private NotificationManager notifManager;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("Hot promotion", "Firebase message: received message from "+remoteMessage.getFrom());
//        super.onMessageReceived(remoteMessage);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            showPromotionCodeTrackingNotification(remoteMessage.getData().get("promotionCode"), remoteMessage.getData().get("timeOfUsing"));
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("Message: ", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void showPromotionCodeTrackingNotification(String promotionCode, String time) {
        String date = time.substring(0,10);
        String hour = time.substring(11,19);
        time = hour + " " + date;
        String message = "Promotion code: " + promotionCode+" is used\n"+"At " + time;
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        if (notifManager == null) {
            notifManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        final int NOTIFY_ID = 0; // ID of notification
        String id = getApplicationContext().getString(R.string.default_notification_channel_id); // default_channel_id
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, "promotionCode", importance);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
            }
            notificationBuilder = new NotificationCompat.Builder(this, id)
                    .setContentTitle("Promotion code used")
                    .setSmallIcon(R.drawable.sale)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setChannelId(id);
        } else {
             notificationBuilder = new NotificationCompat.Builder(this, id)
                    .setContentTitle("Promotion code used")
                    .setSmallIcon(R.drawable.sale)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }

        notifManager.notify(NOTIFY_ID /* ID of notification */, notificationBuilder.build());
    }
}
