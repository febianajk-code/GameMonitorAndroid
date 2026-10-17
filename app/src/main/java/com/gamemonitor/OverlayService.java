
package com.gamemonitor;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private TextView panel;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateStats();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager)
                getSystemService(WINDOW_SERVICE);

        panel = new TextView(this);
        panel.setTextColor(Color.WHITE);
        panel.setTextSize(14);
        panel.setPadding(20, 16, 20, 16);
        panel.setBackgroundColor(Color.rgb(30, 35, 45));

        WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
                );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 20;
        params.y = 100;

        panel.setOnTouchListener(new View.OnTouchListener() {
            private int startX, startY;
            private float touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = params.x;
                        startY = params.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = startX
                                + (int) (event.getRawX() - touchX);
                        params.y = startY
                                + (int) (event.getRawY() - touchY);
                        windowManager.updateViewLayout(panel, params);
                        return true;
                }
                return false;
            }
        });

        windowManager.addView(panel, params);
        handler.post(updater);
    }

    private void updateStats() {
        ActivityManager am = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo mi =
                new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        long total = mi.totalMem / (1024 * 1024);
        long available = mi.availMem / (1024 * 1024);
        long used = total - available;

        panel.setText(
            "GAME MONITOR\n" +
            "RAM perangkat: " + used + " / " + total + " MB\n" +
            "FPS game: N/A\n" +
            "GPU usage: N/A"
        );
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(updater);

        if (panel != null && windowManager != null) {
            windowManager.removeView(panel);
            panel = null;
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
