package psa.citroenparking;

import android.app.ActivityOptions;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ParkingService extends Service {
    final String LOG_TAG = "myLogs";

    public static final int DEFAULT_NOTIFICATION_ID = 101;

    private Intent intentRearParking;
    private Intent intentBroadcastParking;
    private Boolean reverse = false;


    @Override
    public void onCreate() {
        super.onCreate();
        setFilter();

        intentRearParking = new Intent(this, ParkingActivity.class);
        intentBroadcastParking = new Intent(ParkingActivity.BROADCAST_REAR_PARKING);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = builder.build();
        startForeground(DEFAULT_NOTIFICATION_ID, notification);

        Intent hideIntent = new Intent(this, HideNotificationService.class);
        startService(hideIntent);

        Log.d(LOG_TAG, "Service OnCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Service OnDestroy");
        super.onDestroy();
        unregisterReceiver(smReceiver);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final BroadcastReceiver smReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key      = intent.getStringExtra("key");
            String value    = intent.getStringExtra("value");

//            Log.d(LOG_TAG, "prevValue: " + msgHistory[indexID >= 0 ? indexID : 0]);
//            Log.d(LOG_TAG, "key: " + key + " value: " + value);

            switch (key) {
                case "PARK": // parking
                    if ((value.length() == 8) && checkData(value)) {
                        prepare_parking(hexStringToByteArray(value));
                    }
                    break;
            }
        }
    };

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("kg.serial.manager.command_received"); //SM 2
        filter.addAction("kg.delletenebre.serial.NEW_DATA"); //SM 1
        registerReceiver(smReceiver, filter);
    }

    private void showRearParking() {
        intentRearParking.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentRearParking.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intentRearParking.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentRearParking, ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out).toBundle());
    }

    private void closeRearParking() {
//        if (ParkingActivity.activity != null)
//            ParkingActivity.activity.finish();
        sendBroadcast(new Intent(ParkingActivity.CLOSE_ACTIVITY));
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }

    private static boolean checkData(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.digit(string.charAt(i), 16) == -1)
                return false;
        }
        return true;
    }

    private void prepare_parking(byte[] data) {
        if (data[0] != 0) {
            if (!reverse) {
                reverse = true;
                showRearParking();
            }
            else {
                int rear_left   = convertParking(data[1]);
                int rear_center = convertParking(data[2]);
                int rear_right  = convertParking(data[3]);

                intentBroadcastParking.putExtra(ParkingActivity.RL_SENSOR, rear_left);
                intentBroadcastParking.putExtra(ParkingActivity.RLC_SENSOR, rear_center);
                intentBroadcastParking.putExtra(ParkingActivity.RRC_SENSOR, rear_center);
                intentBroadcastParking.putExtra(ParkingActivity.RR_SENSOR, rear_right);
                sendBroadcast(intentBroadcastParking);
            }
        }
        else if (reverse) {
            reverse = false;
            closeRearParking();
        }
    }

    private static int convertParking(int value) {
        switch (value) {
                case 0:
                    return 5;
                case 1:
                    return 4;
                case 2:
                    return 3;
                case 3:
                case 4:
                    return 2;
                case 5:
                case 6:
                    return 1;
                case 7:
                    return 0;
            }
        return 0;
    }

}
