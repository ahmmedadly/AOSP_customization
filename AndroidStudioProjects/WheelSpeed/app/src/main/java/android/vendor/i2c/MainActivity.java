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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Initialize the TextView
        resultTextView = findViewById(R.id.resultTextView);
        seekBar = findViewById(R.id.seekBar);

        // Bind to the AIDL service
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
        } catch (ClassNotFoundException e) {
            Log.e("ServiceBinding", "Class not found: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e("ServiceBinding", "Method not found: " + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("ServiceBinding", "Invocation target exception: " + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("ServiceBinding", "Illegal access exception: " + e.getMessage());
        }

        // Setup button listeners
        setupButtons();

    }
    private void setupButtons() {
        Button WheelButton = findViewById(R.id.WheelButton);


        
        WheelButton.setOnClickListener(v -> {
            Log.d("MainActivity", "Date button clicked");
            if (ii2cService != null) {
                try {
                    int val = ii2cService.getWhealAngle();
                    Log.d("MainActivity", "value = "+val );
                  //  resultTextView.setText("Value = "+val);
                    int mappedValue = (val * 100) / 66;

                    resultTextView.setText("Mapped Value: " + mappedValue);
                    Log.d("SeekBar", "Mapped Value: " + mappedValue);

                    seekBar.setProgress(mappedValue);
                } catch (RemoteException e) {
                    Log.e("MainActivity", "RemoteException: " + e.getMessage());
                    resultTextView.setText("RemoteException getting date");
                }
            } else {
                Log.e("MainActivity", "helloService is null");
                resultTextView.setText("Service not bound");
            }
        });
    }
}