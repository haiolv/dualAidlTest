package com.dual.toolkit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import attv.toolkit.burn.BurnSn;

public class MainActivity extends Activity {

    Button btn_burn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.id.activity_main);
        setContentView(R.layout.activity_main);
        btn_burn = findViewById(R.id.burn);
        btn_burn.setOnClickListener(new BurnOnClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private class BurnOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.d("MainActivity", "BurnOnClickListener click");
            BurnSn bsn = new BurnSn(MainActivity.this, "android.intent.action.BURN");
//            bsn.beginBurnSn();
        }
    }
}
