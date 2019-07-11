package com.bytedance.clockapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bytedance.clockapplication.widget.Clock;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private View mRootView;
    private Clock mClockView;
    private MyHandler myHandler;
    private Thread thread;

    public final int MSG_UPDATE = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootView = findViewById(R.id.root);
        mClockView = findViewById(R.id.clock);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClockView.setShowAnalog(!mClockView.isShowAnalog());
            }
        });

        myHandler = new MyHandler(this);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        myHandler.sendEmptyMessage(MSG_UPDATE);
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.interrupted();
                    }

                }
            }
        });

        thread.start();
    }

    @Override
    protected void onDestroy() {
        // myHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private static class MyHandler extends  Handler {

        public final int MSG_UPDATE = 60;

        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_UPDATE:
                        activity.mClockView.updateTime();
                }
            }
        }
    }
}

