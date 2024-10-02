package com.example.emicalculator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

// Implicit intent which MainActivity calls to calculate the EMI
public class CalculatorService extends Service {

    // Creating binder given to clients
    private final IBinder binder = new LocalBinder();
    public class LocalBinder extends Binder {
        CalculatorService getService() {
            // Allow clients to call CalculatorService's methods
            return CalculatorService.this;
        }
    }

    // Establish communication channel between service and client
    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    // Method to calculate EMI
    public double calculateEMI(long p, long d, float i, int t) {
        p = p - d; // principle subtracted by down payment
        i = i / (12 * 100); // per month interest rate
        return (p * i * Math.pow((1 + i), t)) / (Math.pow((1 + i), t) - 1);
    }
}