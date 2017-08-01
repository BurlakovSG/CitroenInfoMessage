package psa.citroenparking;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class InfoMessageService extends Service {
    final String LOG_TAG = "myLogs";

    private Context context;
    private Intent intentRearParking;
    private Intent intentBroadcastParking;
    private Boolean reverse = false;


    @Override
    public void onCreate() {
        this.context = this;
        super.onCreate();
        setFilter();

        intentRearParking = new Intent(context, ParkingActivity.class);
        intentBroadcastParking = new Intent(ParkingActivity.BROADCAST_REAR_PARKING);

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
                    prepare_parking(hexStringToByteArray(value));
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
        startActivity(intentRearParking);
    }

    private void closeRearParking() {
        if (ParkingActivity.activity != null)
            ParkingActivity.activity.finish();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
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
