package psa.citroenparking;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class HideNotificationService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.build();
        startForeground(ParkingService.DEFAULT_NOTIFICATION_ID, notification);
        stopForeground(true);
    }
}
