package psa.citroenparking;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ParkingActivity extends Activity {
    public final static String BROADCAST_REAR_PARKING = "psa.citroenparking.BROADCAST_REAR_PARKING";
    public final static String CLOSE_ACTIVITY = "psa.citroenparking.CLOSE_ACTIVITY";
    public final static String RL_SENSOR = "rl_sensor";
    public final static String RLC_SENSOR = "rlc_sensor";
    public final static String RRC_SENSOR = "rrc_sensor";
    public final static String RR_SENSOR = "rr_sensor";

    BroadcastReceiver br;

    private ImageView   ivRL_01,
                        ivRL_02,
                        ivRL_03,
                        ivRL_04,
                        ivRL_05,
                        ivRLC_01,
                        ivRLC_02,
                        ivRLC_03,
                        ivRLC_04,
                        ivRLC_05,
                        ivRRC_01,
                        ivRRC_02,
                        ivRRC_03,
                        ivRRC_04,
                        ivRRC_05,
                        ivRR_01,
                        ivRR_02,
                        ivRR_03,
                        ivRR_04,
                        ivRR_05;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        ivRL_01 = (ImageView) findViewById(R.id.ivRL_01);
        ivRL_02 = (ImageView) findViewById(R.id.ivRL_02);
        ivRL_03 = (ImageView) findViewById(R.id.ivRL_03);
        ivRL_04 = (ImageView) findViewById(R.id.ivRL_04);
        ivRL_05 = (ImageView) findViewById(R.id.ivRL_05);

        ivRLC_01 = (ImageView) findViewById(R.id.ivRLC_01);
        ivRLC_02 = (ImageView) findViewById(R.id.ivRLC_02);
        ivRLC_03 = (ImageView) findViewById(R.id.ivRLC_03);
        ivRLC_04 = (ImageView) findViewById(R.id.ivRLC_04);
        ivRLC_05 = (ImageView) findViewById(R.id.ivRLC_05);

        ivRRC_01 = (ImageView) findViewById(R.id.ivRRC_01);
        ivRRC_02 = (ImageView) findViewById(R.id.ivRRC_02);
        ivRRC_03 = (ImageView) findViewById(R.id.ivRRC_03);
        ivRRC_04 = (ImageView) findViewById(R.id.ivRRC_04);
        ivRRC_05 = (ImageView) findViewById(R.id.ivRRC_05);

        ivRR_01 = (ImageView) findViewById(R.id.ivRR_01);
        ivRR_02 = (ImageView) findViewById(R.id.ivRR_02);
        ivRR_03 = (ImageView) findViewById(R.id.ivRR_03);
        ivRR_04 = (ImageView) findViewById(R.id.ivRR_04);
        ivRR_05 = (ImageView) findViewById(R.id.ivRR_05);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BROADCAST_REAR_PARKING:
                        changeLines( intent.getIntExtra(RL_SENSOR, 0),
                                     intent.getIntExtra(RLC_SENSOR, 0),
                                     intent.getIntExtra(RRC_SENSOR, 0),
                                     intent.getIntExtra(RR_SENSOR, 0) );
                        break;

                    case CLOSE_ACTIVITY:
                        finish();
                }


            }
        };

        final Animation carAnimation = AnimationUtils.loadAnimation(this, R.anim.car_animation);
        carAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        final Animation linesAnimation = AnimationUtils.loadAnimation(this, R.anim.lines_animation);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.clLines);

        ImageView ivCar = (ImageView) findViewById(R.id.ivCar);
        ivCar.startAnimation(carAnimation);
        layout.startAnimation(linesAnimation);
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_REAR_PARKING);
        filter.addAction(CLOSE_ACTIVITY);
        registerReceiver(br, filter);
        super.onResume();
    }

    protected void changeLines(int rl, int rlc, int rrc, int rr) {
        // Rear left sensor
        if (rl >= 1)
            ivRL_01.setVisibility(View.VISIBLE);
        else
            ivRL_01.setVisibility(View.INVISIBLE);

        if (rl >= 2)
            ivRL_02.setVisibility(View.VISIBLE);
        else
            ivRL_02.setVisibility(View.INVISIBLE);

        if (rl >= 3)
            ivRL_03.setVisibility(View.VISIBLE);
        else
            ivRL_03.setVisibility(View.INVISIBLE);

        if (rl >= 4)
            ivRL_04.setVisibility(View.VISIBLE);
        else
            ivRL_04.setVisibility(View.INVISIBLE);

        if (rl >= 5)
            ivRL_05.setVisibility(View.VISIBLE);
        else
            ivRL_05.setVisibility(View.INVISIBLE);

        // Rear left-center sensor
        if (rlc >= 1)
            ivRLC_01.setVisibility(View.VISIBLE);
        else
            ivRLC_01.setVisibility(View.INVISIBLE);

        if (rlc >= 2)
            ivRLC_02.setVisibility(View.VISIBLE);
        else
            ivRLC_02.setVisibility(View.INVISIBLE);

        if (rlc >= 3)
            ivRLC_03.setVisibility(View.VISIBLE);
        else
            ivRLC_03.setVisibility(View.INVISIBLE);

        if (rlc >= 4)
            ivRLC_04.setVisibility(View.VISIBLE);
        else
            ivRLC_04.setVisibility(View.INVISIBLE);

        if (rlc >= 5)
            ivRLC_05.setVisibility(View.VISIBLE);
        else
            ivRLC_05.setVisibility(View.INVISIBLE);

        // Rear right-center sensor
        if (rrc >= 1)
            ivRRC_01.setVisibility(View.VISIBLE);
        else
            ivRRC_01.setVisibility(View.INVISIBLE);

        if (rrc >= 2)
            ivRRC_02.setVisibility(View.VISIBLE);
        else
            ivRRC_02.setVisibility(View.INVISIBLE);

        if (rrc >= 3)
            ivRRC_03.setVisibility(View.VISIBLE);
        else
            ivRRC_03.setVisibility(View.INVISIBLE);

        if (rrc >= 4)
            ivRRC_04.setVisibility(View.VISIBLE);
        else
            ivRRC_04.setVisibility(View.INVISIBLE);

        if (rrc >= 5)
            ivRRC_05.setVisibility(View.VISIBLE);
        else
            ivRRC_05.setVisibility(View.INVISIBLE);

        // Rear right sensor
        if (rr >= 1)
            ivRR_01.setVisibility(View.VISIBLE);
        else
            ivRR_01.setVisibility(View.INVISIBLE);

        if (rr >= 2)
            ivRR_02.setVisibility(View.VISIBLE);
        else
            ivRR_02.setVisibility(View.INVISIBLE);

        if (rr >= 3)
            ivRR_03.setVisibility(View.VISIBLE);
        else
            ivRR_03.setVisibility(View.INVISIBLE);

        if (rr >= 4)
            ivRR_04.setVisibility(View.VISIBLE);
        else
            ivRR_04.setVisibility(View.INVISIBLE);

        if (rr >= 5)
            ivRR_05.setVisibility(View.VISIBLE);
        else
            ivRR_05.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(br);
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
