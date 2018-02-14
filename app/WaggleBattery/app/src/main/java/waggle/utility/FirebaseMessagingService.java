package waggle.utility;

/**
 * Created by SeungSoo on 2018-02-01.
 * copied from https://cosmosjs.blog.me/220739141098
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import waggle.wagglebattery.Splashscreen;
import waggle.wagglebattery.activity.MainActivity;
import waggle.wagglebattery.R;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // 앱이 실행중일때 push알림을 받은 경우 아래 함수를 호출
        sendNotification(remoteMessage.getData().get("waggleId"), remoteMessage.getData().get("remainBattery"));

    }

    private void sendNotification(String waggleId, String remainBattery) {
        Intent intent = new Intent(this, Splashscreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("waggleId", waggleId);
        intent.putExtra("remainBattery", remainBattery);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String contentText = waggleId+" has only "+remainBattery+"%!!!";

        // Notification Layout
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.img_icon)
                .setContentTitle("Warning")
                .setContentText(contentText)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}


