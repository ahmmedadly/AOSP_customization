package android.vendor.test;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private IHello helloService;
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
                Object result = getServiceMethod.invoke(null, "android.vendor.test.IHello/default");

                if (result != null) {
                    IBinder binder = (IBinder) result;
                    helloService = IHello.Stub.asInterface(binder);
                    Log.d("ServiceBinding", "Successfully bound to helloService!");
                } else {
                    Log.e("ServiceBinding", "Failed to get service binder.");
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
        Button dateBtn = findViewById(R.id.dateButton);
        Button timeBtn = findViewById(R.id.timeButton);

        dateBtn.setOnClickListener(v -> {
            Log.d("MainActivity", "Date button clicked");
            if (helloService != null) {
                try {
                    String date = helloService.getDate();
                    Log.d("MainActivity", "Date from service: " + date);
                    resultTextView.setText(date);
                } catch (RemoteException e) {
                    Log.e("MainActivity", "RemoteException: " + e.getMessage());
                    resultTextView.setText("RemoteException getting date");
                }
            } else {
                Log.e("MainActivity", "helloService is null");
                resultTextView.setText("Service not bound");
            }
        });

        timeBtn.setOnClickListener(v -> {
            Log.d("MainActivity", "Time button clicked");
            if (helloService != null) {
                try {
                    String time = helloService.getTime();
                    Log.d("MainActivity", "Time from service: " + time);
                    resultTextView.setText(time);
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