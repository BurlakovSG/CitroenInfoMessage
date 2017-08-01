package psa.citroeninfomessage;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import static java.lang.Math.round;

public class InfoMessageService extends Service {
    final String LOG_TAG = "myLogs";

    private Context context;
    private Intent intentInfo, intentRearParking;
    private Intent intentBroadcastParking;
    private String prevValue = "";
    private String prevKey = "";
    private int tempCoolant = 0;
    private int tempOut = 0;
    private Boolean reverse = false;
    private long tsReverse = 0;
    private ArrayList<String> ID;
    private String msgHistory[];


    @Override
    public void onCreate() {
        this.context = this;
        super.onCreate();
        setFilter();

        intentInfo = new Intent(context, InfoActivity.class);
        intentRearParking = new Intent(context, ParkingActivity.class);
        intentBroadcastParking = new Intent(ParkingActivity.BROADCAST_REAR_PARKING);

        ID = new ArrayList<String>() {{
            add("1A1");
            add("0E1");
//            add("0F6");
        }};

        msgHistory = new String[ID.size()];

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
            int indexID     = ID.indexOf(key);

//            Log.d(LOG_TAG, "prevValue: " + msgHistory[indexID >= 0 ? indexID : 0]);
//            Log.d(LOG_TAG, "key: " + key + " value: " + value);

            if (indexID >= 0) {
                if (indexID == 2 || !value.equals(msgHistory[indexID])) {
                    msgHistory[indexID] = value;

                    switch (key) {
                        case "1A1":
                            prepare_1a1(hexStringToByteArray(value));
                            break;

                        case "0F6":
                            prepare_0f6(hexStringToByteArray(value));
                            break;

                        case "0E1": // parking
//                            if (reverse)
                                prepare_0e1(hexStringToByteArray(value));
//                            break;
                    }
                }
            }
        }
    };

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("kg.serial.manager.command_received"); //SM 2
        filter.addAction("kg.delletenebre.serial.NEW_DATA"); //SM 1
        registerReceiver(smReceiver, filter);
    }

    private void prepare_1a1(byte[] data) {
        showInfo("Test message");
    }

    private void showInfo(String text) {
        intentInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentInfo.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intentInfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentInfo.putExtra("text", text);
        startActivity(intentInfo);
    }

    private void closeInfo() {
        if (InfoActivity.activity != null )
            InfoActivity.activity.finish();
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

    private void prepare_0f6(byte[] data){
        tempCoolant = data[1] - 39;
        tempOut = (int) round(data[6]/2.0 - 39.5);

        if ((data[7]&0x80) != 0) {
            if (!reverse) {
                if (tsReverse == 0) {
                    tsReverse = System.currentTimeMillis();
                }
                else if (System.currentTimeMillis() - tsReverse > 1000){
                    reverse = true;
                    showRearParking();
                }
            }
        }
        else if (tsReverse != 0) {
            reverse = false;
            tsReverse = 0;
            closeRearParking();
        }
    }

    private void prepare_0e1(byte[] data) {
        if ((data[5]&0x02) != 0) {
            if (!reverse) {
                reverse = true;
                showRearParking();
            }
            else {
                int rear_left   = convertParking((data[3]>>5) & 0x07);
                int rear_center = convertParking((data[3]>>2) & 0x07);
                int rear_right  = convertParking((data[4]>>5) & 0x07);

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
//        int rear_left   = convertParking((data[3]>>5) & 0x07);
//        int rear_center = convertParking((data[3]>>2) & 0x07);
//        int rear_right  = convertParking((data[4]>>5) & 0x07);
//
////        Log.d(LOG_TAG, "rl: " + data[3] + " rc: " + data[3] + " rr: " + data[4]);
////        Log.d(LOG_TAG, "rl: " + ((data[3]>>5) & 0x07) + " rc: " + ((data[3]>>2) & 0x07) + " rr: " + ((data[4]>>5) & 0x07));
//        Log.d(LOG_TAG, "rl: " + rear_left + " rc: " + rear_center + " rr: " + rear_right);
//
//        intentBroadcastParking.putExtra(ParkingActivity.RL_SENSOR, rear_left);
//        intentBroadcastParking.putExtra(ParkingActivity.RLC_SENSOR, rear_center);
//        intentBroadcastParking.putExtra(ParkingActivity.RRC_SENSOR, rear_center);
//        intentBroadcastParking.putExtra(ParkingActivity.RR_SENSOR, rear_right);
//        sendBroadcast(intentBroadcastParking);
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
