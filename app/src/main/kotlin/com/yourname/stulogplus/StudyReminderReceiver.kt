package com.yourname.stulogplus

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object StudyReminderScheduler {
    const val PREFS = "stulog_app_settings"
    const val CHANNEL_ID = "study_reminders"
    const val ACTION_REMINDER = "com.yourname.stulogplus.STUDY_REMINDER"
    private const val REQUEST_CODE = 8127

    fun configure(context: Context, hour: Int, minute: Int, enabled: Boolean) {
        val h = hour.coerceIn(0, 23)
        val m = minute.coerceIn(0, 59)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("enabled", enabled).putInt("hour", h).putInt("minute", m).apply()
        if (enabled) schedule(context, h, m) else cancel(context)
    }

    fun scheduleFromPreferences(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean("enabled", false)) schedule(context, prefs.getInt("hour", 19), prefs.getInt("minute", 0))
    }

    private fun pendingIntent(context: Context, flags: Int): PendingIntent {
        val intent = Intent(context, StudyReminderReceiver::class.java).setAction(ACTION_REMINDER)
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun schedule(context: Context, hour: Int, minute: Int) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0); if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1) }
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT))
    }

    private fun cancel(context: Context) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pendingIntent(context, PendingIntent.FLAG_NO_CREATE))
    }

    fun showNotification(context: Context) {
        createChannel(context)
        val openApp = Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP }
        val contentIntent = PendingIntent.getActivity(context, 8128, openApp, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val builder = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(context, CHANNEL_ID) else Notification.Builder(context)
        val notification = builder.setSmallIcon(android.R.drawable.ic_menu_my_calendar).setContentTitle("すたろぐ：今日の勉強").setContentText("まずは5分だけ。今日の自分を少し進めよう 🐈").setContentIntent(contentIntent).setAutoCancel(true).setCategory(Notification.CATEGORY_REMINDER).setPriority(Notification.PRIORITY_DEFAULT).build()
        try { (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(8129, notification) } catch (_: SecurityException) { }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "勉強リマインダー", NotificationManager.IMPORTANCE_DEFAULT).apply { description = "毎日の勉強を始めるきっかけを知らせます" })
        }
    }
}

class StudyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) { if (intent?.action == StudyReminderScheduler.ACTION_REMINDER) StudyReminderScheduler.showNotification(context) }
}

class StudyBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) { if (intent?.action == Intent.ACTION_BOOT_COMPLETED) StudyReminderScheduler.scheduleFromPreferences(context) }
}
