package com.silvertouch.sliderdemo;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.silvertouch.OnActiveListener;
import com.silvertouch.OnStateChangeListener;
import com.silvertouch.SwipeButton;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

       /* final SwipeButton swipeBtnEnabled = (SwipeButton) findViewById(R.id.swipeBtnEnabled);
//        swipeBtnEnabled.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_button3));
        swipeBtnEnabled.setButtonBackground(ContextCompat.getDrawable
                (MainActivity.this, R.drawable.shape_button3));
        swipeBtnEnabled.setSlidingButtonBackground(ContextCompat.getDrawable(this, R.drawable
                .shape_rounded2_trasparent));

        swipeBtnEnabled.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                if (active) {
                    swipeBtnEnabled.setButtonBackground(ContextCompat.getDrawable
                            (MainActivity.this, R.drawable.shape_button));
                    swipeBtnEnabled.collapseButton();

                } else {
                    swipeBtnEnabled.setButtonBackground(ContextCompat.getDrawable
                            (MainActivity.this, R.drawable.shape_button3));
                }
            }
        });
        //swipeBtnEnabled.setEnabledStateNotAnimated();
        swipeBtnEnabled.setDisabledStateNotAnimated();*/


        final SwipeButton swipeBtnHangUp = (SwipeButton) findViewById(R.id.swipeBtnHangUp);
//        swipeBtnEnabled.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_button3));
        swipeBtnHangUp.setButtonBackground(ContextCompat.getDrawable
                (MainActivity.this, R.drawable.shape_button));
        swipeBtnHangUp.setSlidingButtonBackground(ContextCompat.getDrawable(this, R.drawable
                .shape_rounded2_trasparent));

        swipeBtnHangUp.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                if (active) {
                    /*swipeBtnHangUp.setButtonBackground(ContextCompat.getDrawable
                            (MainActivity.this, R.drawable.shape_button));*/
                    swipeBtnHangUp.collapseButton();

                } else {
                   /* swipeBtnHangUp.setButtonBackground(ContextCompat.getDrawable
                            (MainActivity.this, R.drawable.shape_button3));*/
                }
            }
        });

        swipeBtnHangUp.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                Toast.makeText(MainActivity.this, "Call Accepted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActiveFail() {
                Toast.makeText(MainActivity.this, "Call Rejected", Toast.LENGTH_SHORT).show();
            }
        });
        //swipeBtnEnabled.setEnabledStateNotAnimated();
        swipeBtnHangUp.setDisabledStateNotAnimated();

    }
}
