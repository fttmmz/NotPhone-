package com.example.notphone.logic;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.notphone.R;
import com.example.notphone.ui.system.HomeActivity;

public final class NotificationHelper {
    private static final String CHANNEL_ID = "notphone_main";
    private NotificationHelper() {}

    // Call once from Home. On Android 13+ this shows the system "Allow notifications?" pop-up.
    public static void requestPermissionIfNeeded(Activity activity) {
        if (!hasPermission(activity)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, Keys.REQ_NOTIFICATIONS);
        }
    }

    // Old version, still works for B and C: pass string resource ids
    public static void show(Context context, int titleRes, int textRes) {
        show(context, context.getString(titleRes), context.getString(textRes));
    }

    // New version: pass ready-made strings
    public static void show(Context context, String title, String text) {
        if (!hasPermission(context)) {
            Toast.makeText(context, title + ": " + text, Toast.LENGTH_LONG).show();
            return;
        }
        createChannel(context);

        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pending)
                .build();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), notification);
    }

    private static boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true;
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, context.getString(R.string.system_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}