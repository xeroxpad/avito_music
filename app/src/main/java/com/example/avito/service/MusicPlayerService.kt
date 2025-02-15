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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.runtime.getValue
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media.AudioManagerCompat.requestAudioFocus
import com.example.avito.MainActivity
import com.example.avito.R
import com.example.avito.data.model.Track
import com.example.avito.data.model.TrackCard
import com.example.avito.player.PlayerViewModel
import com.example.avito.viewmodel.DownloadedTracksViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URL


class MusicPlayerService : Service(), KoinComponent {
    private var isPlaying = true
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
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

    private fun handlePlayPause() {
        PlayerViewModel.instance?.togglePlayPause()
        isPlaying = !isPlaying
        updateNotification(PlayerViewModel.instance?.currentTrackId?.value ?: -1)
    }

    private fun handleNext() {
        PlayerViewModel.instance?.playNextTrack(applicationContext)
    }

    private fun handlePrevious() {
        PlayerViewModel.instance?.playBackTrack(applicationContext)
    }

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

    private fun buildNotification(trackId: Long): Notification {
        val track = PlayerViewModel.instance?.trackList?.value?.find { it.id == trackId }

        val playPauseIcon = if (isPlaying)
            android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

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

    @SuppressLint("ForegroundServiceType")
    private fun updateNotification(trackId: Long) {
        val notification = buildNotification(trackId)
        startForeground(NOTIFICATION_ID, notification)
    }

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

