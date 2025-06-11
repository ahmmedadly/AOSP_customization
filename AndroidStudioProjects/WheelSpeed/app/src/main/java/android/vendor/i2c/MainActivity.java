package android.vendor.i2c;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    II2cService ii2cService;
    private SeekBar seekBar;
    private TextView resultTextView;

    private volatile boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        seekBar = findViewById(R.id.seekBar);

        bindToService();

        // Start update thread
        startValueUpdater();
    }

    private void bindToService() {
        try {
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);

            if (getServiceMethod != null) {
                Object result = getServiceMethod.invoke(null, "android.vendor.i2c.II2cService/default");

                if (result != null) {
                    IBinder binder = (IBinder) result;
                    ii2cService = II2cService.Stub.asInterface(binder);
                    Log.d("ServiceBinding", "Successfully bound to ii2cService!");
                } else {
                    Log.e("ServiceBinding", "Failed to get ii2cService binder.");
                }
            }
        } catch (Exception e) {
            Log.e("ServiceBinding", "Error binding service: " + e.getMessage());
        }
    }

    private void startValueUpdater() {
        new Thread(() -> {
            while (isRunning) {
                if (ii2cService != null) {
                    try {
                        int val = ii2cService.getWhealAngle();
                        int mappedValue = (val * 100) / 66;

                        runOnUiThread(() -> {
                            resultTextView.setText("Mapped Value: " + mappedValue);
                            seekBar.setProgress(mappedValue);
                        });
                    } catch (RemoteException e) {
                        Log.e("I2C", "RemoteException: " + e.getMessage());
                    }
                }

                try {
                    Thread.sleep(500); // wait 1 second
                } catch (InterruptedException e) {
                    Log.e("Thread", "Interrupted: " + e.getMessage());
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false; // Stop thread when activity is destroyed
    }
}
