package utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.owner.saloon.craftystudio.saloonowner.FullDetailActivity;
import app.owner.saloon.craftystudio.saloonowner.MainActivity;
import app.owner.saloon.craftystudio.saloonowner.R;

/**
 * Created by bunny on 07/07/17.
 */

public class FireBasePushNotificationService extends FirebaseMessagingService {
    String saloonUID, orderID;

    Intent intent;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            saloonUID = remoteMessage.getData().get("saloon");
            orderID = remoteMessage.getData().get("order");




            intent = new Intent(this, FullDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.putExtra("SaloonUID" ,saloonUID );
            intent.putExtra("OrderID" , orderID);


            showNotification(remoteMessage.getData().get("notificationT"), remoteMessage.getData().get("notificationB"));

        }

        // Check if message contains a notification payload.
        /*if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
        }*/
    }

    private void showNotification(String title, String author) {



        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle( title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(author)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(124 /* ID of notification */, notificationBuilder.build());
    }
}
