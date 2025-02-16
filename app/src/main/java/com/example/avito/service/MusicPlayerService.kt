package com.example.avito.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.avito.MainActivity
import com.example.avito.R
import com.example.avito.player.PlayerViewModel
import org.koin.core.component.KoinComponent


class MusicPlayerService : Service(), KoinComponent {
    private var isPlaying = true
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()

    //    Инициализация менеджера уведомлений
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    //    Создание канала уведомлений
        createNotificationChannel()

    //    Регистрация BroadcastReceiver для обработки кнопок управления воспроизведением
        registerMediaReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_SERVICE" -> {
                val trackId = intent.getLongExtra("trackId", -1)
                updateNotification(trackId)
            }

            "PLAY_PAUSE" -> handlePlayPause()
            "NEXT" -> handleNext()
            "PREV" -> handlePrevious()
            "STOP" -> stopSelf()
        }
        return START_STICKY
    }

    //    Переключает состояние воспроизведения и обновляет уведомление.
    private fun handlePlayPause() {
        PlayerViewModel.instance?.togglePlayPause()
        isPlaying = !isPlaying
        updateNotification(PlayerViewModel.instance?.currentTrackId?.value ?: -1)
    }

    //    Воспроизводит следующий трек.
    private fun handleNext() {
        PlayerViewModel.instance?.playNextTrack(applicationContext)
    }

    //    Воспроизводит предыдущий трек.
    private fun handlePrevious() {
        PlayerViewModel.instance?.playBackTrack(applicationContext)
    }

    //    Создание канала уведомлений для управления воспроизведением.
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }


    //    Создает уведомление с кнопками управления музыкой.
    private fun buildNotification(trackId: Long): Notification {
        val track = PlayerViewModel.instance?.trackList?.value?.find { it.id == trackId }

        val playPauseIcon = if (isPlaying)
            android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play

    //    PendingIntent для открытия главного экрана приложения
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

    //    Интенты для кнопок управления воспроизведением
        val playPauseIntent = Intent(this, MusicPlayerService::class.java)
            .setAction("PLAY_PAUSE")
        val nextIntent = Intent(this, MusicPlayerService::class.java)
            .setAction("NEXT")
        val prevIntent = Intent(this, MusicPlayerService::class.java)
            .setAction("PREV")

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(track?.titleTrack ?: "Unknown")
            .setContentText(track?.artistTrack ?: "Unknown")
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_icon_music))
            .setSmallIcon(R.drawable.ic_icon_music)
            .setContentIntent(pendingIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_previous,
                    "Previous",
                    PendingIntent.getService(
                        this,
                        0,
                        prevIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )
            .addAction(
                NotificationCompat.Action(
                    playPauseIcon,
                    "Play/Pause",
                    PendingIntent.getService(
                        this,
                        0,
                        playPauseIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_next,
                    "Next",
                    PendingIntent.getService(
                        this,
                        0,
                        nextIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    //    Обновление уведомления с актуальной информацией о треке.
    @SuppressLint("ForegroundServiceType")
    private fun updateNotification(trackId: Long) {
        val notification = buildNotification(trackId)
        startForeground(NOTIFICATION_ID, notification)
    }

    //    Регистрация BroadcastReceiver для обработки медиакнопок.
    private fun registerMediaReceiver() {
        mediaReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    "PLAY_PAUSE" -> handlePlayPause()
                    "NEXT" -> handleNext()
                    "PREV" -> handlePrevious()
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction("PLAY_PAUSE")
            addAction("NEXT")
            addAction("PREV")
        }
        registerReceiver(mediaReceiver, filter)
    }

    //    Очищение ресурсов при уничтожении сервиса.
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mediaReceiver)
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "music_player_channel"
        const val NOTIFICATION_ID = 101
    }
}

