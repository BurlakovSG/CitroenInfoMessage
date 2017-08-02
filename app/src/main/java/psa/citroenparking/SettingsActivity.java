package psa.citroenparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button btnStart = (Button) findViewById(R.id.button_start);
        final Button btnStop = (Button) findViewById(R.id.button_stop);

        startService(
                new Intent(SettingsActivity.this, ParkingService.class));
        finish();

        // запуск службы
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // используем явный вызов службы
                startService(
                        new Intent(SettingsActivity.this, ParkingService.class));
                finish();
            }
        });

        // остановка службы
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(
                        new Intent(SettingsActivity.this, ParkingService.class));
            }
        });
    }
}
