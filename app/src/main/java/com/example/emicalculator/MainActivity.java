package com.example.emicalculator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Implicit intent for the calculator
    private CalculatorService emiService;
    private boolean isBound = false;

    // Variable creation
    EditText textPrincipal, textDownPayment, textInterestRate, textTenure;
    Button buttonCalculate;
    TextView viewEMIResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements into each variable
        textPrincipal = findViewById(R.id.textPrincipal);
        textDownPayment = findViewById(R.id.textDownPayment);
        textInterestRate = findViewById(R.id.textInterestRate);
        textTenure = findViewById(R.id.textTenure);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        viewEMIResult = findViewById(R.id.viewEMIResult);

        // Button onClick listener to calculate EMI using implicit intents
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    calculateEMI();
                } else {
                    Toast.makeText(MainActivity.this, "Service not bound", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Binding to the CalculatorService
        Intent intent = new Intent(this, CalculatorService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    // Defines callbacks for service binding, passed to bindService()
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Bound to LocalService, cast the IBinder to get LocalService instance
            CalculatorService.LocalBinder binder = (CalculatorService.LocalBinder) service;
            emiService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    // Method which sends info the the calculator and displays the result from EMICalculator
    private void calculateEMI() {
        try {
            long principal = Long.parseLong(textPrincipal.getText().toString());
            long downPayment = Long.parseLong(textDownPayment.getText().toString());
            float interestRate = Float.parseFloat(textInterestRate.getText().toString());
            int tenure = Integer.parseInt(textTenure.getText().toString());

            double emi = emiService.calculateEMI(principal, downPayment, interestRate, tenure);
            viewEMIResult.setText(String.format("EMI: %.2f", emi));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid inputs", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}