package com.example.myCourse.UI;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.myCourse.R;

public class MyReceiver extends BroadcastReceiver {
    static int notificationID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        String channel_id = "task_alerts";

        String message = intent.getStringExtra("key");
        if (message == null || message.isEmpty()) {
            message = "You have a new task alert.";
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        createNotificationChannel(context, channel_id);
        Notification notification = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.baseline_calendar_month_24)
                .setContentTitle("Task2 Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++, notification);
    }

    private void createNotificationChannel(Context context, String channelID) {
        CharSequence name = "Assessment and Course Alerts";
        String description = "Notifies when an Assessment or Course starts or ends.";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
