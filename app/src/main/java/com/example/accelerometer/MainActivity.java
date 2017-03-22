package com.example.accelerometer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {


    private SimulationView mSimulationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSimulationView = new SimulationView(this);
        setContentView(mSimulationView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimulationView.startSimulation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSimulationView.stopSimulation();
    }

}
