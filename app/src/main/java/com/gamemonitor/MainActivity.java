
package com.gamemonitor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView title = new TextView(this);
        title.setText("GAME MONITOR");
        title.setTextSize(26);
        layout.addView(title);

        TextView info = new TextView(this);
        info.setText(
            "RAM: tersedia setelah overlay aktif\n" +
            "FPS: perlu metode pengukuran tambahan\n" +
            "GPU: belum tersedia"
        );
        info.setTextSize(16);
        layout.addView(info);

        Button button = new Button(this);
        button.setText("Aktifkan panel melayang");
        layout.addView(button);

        button.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent settings = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
                );
                startActivity(settings);
            } else {
                startService(new Intent(this, OverlayService.class));
            }
        });

        setContentView(layout);
    }
}
