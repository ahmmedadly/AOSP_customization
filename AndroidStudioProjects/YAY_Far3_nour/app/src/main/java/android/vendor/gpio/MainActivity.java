package android.vendor.gpio;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private IGpioService gpioService;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the TextView
        resultTextView = findViewById(R.id.resultTextView);

        // Bind to the AIDL service
        try {
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);

            if (getServiceMethod != null) {
                Object result = getServiceMethod.invoke(null, "android.vendor.gpio.IGpioService/default");

                if (result != null) {
                    IBinder binder = (IBinder) result;
                    gpioService = IGpioService.Stub.asInterface(binder);
                    Log.d("ServiceBinding", "Successfully bound to gpioService!");
                } else {
                    Log.e("ServiceBinding", "Failed to get gpioService binder.");
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
        Button EtfeneeeButton = findViewById(R.id.EtfeneeeButton);
        Button Wal3neeeButton = findViewById(R.id.Wal3neeeButton);

        Wal3neeeButton.setOnClickListener(v -> {
            Log.d("MainActivity", "Date button clicked");
            if (gpioService != null) {
                try {
                    gpioService.setGpioState(17,true);
                    if (gpioService.getGpioState(17)){
                        Log.d("MainActivity", "Led is ON " );
                        resultTextView.setText("Led is ON");
                    }else{
                        Log.d("MainActivity", "Led is OFF " );
                        resultTextView.setText("Led is OFF");
                    }

                } catch (RemoteException e) {
                    Log.e("MainActivity", "RemoteException: " + e.getMessage());
                    resultTextView.setText("RemoteException getting date");
                }
            } else {
                Log.e("MainActivity", "helloService is null");
                resultTextView.setText("Service not bound");
            }
        });

        EtfeneeeButton.setOnClickListener(v -> {
            Log.d("MainActivity", "Time button clicked");
            if (gpioService != null) {
                try {
                    gpioService.setGpioState(17,false);
                    if (gpioService.getGpioState(17)){
                        Log.d("MainActivity", "Led is ON " );
                        resultTextView.setText("Led is ON");
                    }else{
                        Log.d("MainActivity", "Led is OFF " );
                        resultTextView.setText("Led is OFF");
                    }
                } catch (RemoteException e) {
                    Log.e("MainActivity", "RemoteException: " + e.getMessage());
                    resultTextView.setText("RemoteException getting time");
                }
            } else {
                Log.e("MainActivity", "helloService is null");
                resultTextView.setText("Service not bound");
            }
        });
    }
}