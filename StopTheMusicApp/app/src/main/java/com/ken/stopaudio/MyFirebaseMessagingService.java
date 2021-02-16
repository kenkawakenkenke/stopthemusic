package com.ken.stopaudio;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

  private static final String TAG = "zzz";

  private final Handler handler = new Handler();

  public static  void killAudio(Context context, Handler handler) {

//    Handler handler = new Handler();
    OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {

      @Override
      public void onAudioFocusChange(int i) {
        Log.w("zzz", "onAudioFocusChange "+i);
      }
    };

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
      AudioAttributes playbackAttributes = new AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_GAME)
          .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
          .build();
      AudioFocusRequest focusRequest = null;
      focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
          .setAudioAttributes(playbackAttributes)
          .setAcceptsDelayedFocusGain(true)
          .setOnAudioFocusChangeListener(afChangeListener, handler)
          .build();
      final Object focusLock = new Object();

      boolean playbackDelayed = false;
      boolean playbackNowAuthorized = false;

// requesting audio focus and processing the response
      int res = audioManager.requestAudioFocus(focusRequest);
      synchronized (focusLock) {
        if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
          playbackNowAuthorized = false;
          Log.w("zzz", "request failed");
        } else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
          playbackNowAuthorized = true;
          Log.w("zzz", "request granted");
//            playbackNow();
        } else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
          playbackDelayed = true;
          Log.w("zzz", "request delayed");
          playbackNowAuthorized = false;
        }
      }
    }
  }

  /**
   * Called when message is received.
   *
   * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
   */
  // [START receive_message]
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    // [START_EXCLUDE]
    // There are two types of messages data messages and notification messages. Data messages
    // are handled
    // here in onMessageReceived whether the app is in the foreground or background. Data
    // messages are the type
    // traditionally used with GCM. Notification messages are only received here in
    // onMessageReceived when the app
    // is in the foreground. When the app is in the background an automatically generated
    // notification is displayed.
    // When the user taps on the notification they are returned to the app. Messages
    // containing both notification
    // and data payloads are treated as notification messages. The Firebase console always
    // sends notification
    // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
    // [END_EXCLUDE]

    // TODO(developer): Handle FCM messages here.
    // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
    Log.d(TAG, "From: " + remoteMessage.getFrom());
    killAudio(this,handler);

    // Check if message contains a data payload.
    if (remoteMessage.getData().size() > 0) {
      Log.d(TAG, "Message data payload: " + remoteMessage.getData());

      if (/* Check if data needs to be processed by long running job */ true) {
        // For long-running tasks (10 seconds or more) use WorkManager.
        scheduleJob();
      } else {
        // Handle message within 10 seconds
        handleNow();
      }

    }

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.
  }
  // [END receive_message]


  // [START on_new_token]
  /**
   * There are two scenarios when onNewToken is called:
   * 1) When a new token is generated on initial app startup
   * 2) Whenever an existing token is changed
   * Under #2, there are three scenarios when the existing token is changed:
   * A) App is restored to a new device
   * B) User uninstalls/reinstalls the app
   * C) User clears app data
   */
  @Override
  public void onNewToken(String token) {
    Log.d(TAG, "Refreshed token: " + token);

    // If you want to send messages to this application instance or
    // manage this apps subscriptions on the server side, send the
    // FCM registration token to your app server.
    sendRegistrationToServer(token);
  }
  // [END on_new_token]

  /**
   * Schedule async work using WorkManager.
   */
  private void scheduleJob() {
    // [START dispatch_job]
    OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
        .build();
    WorkManager.getInstance().beginWith(work).enqueue();
    // [END dispatch_job]
  }

  public static class MyWorker extends ListenableWorker {

    /**
     * @param appContext The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public MyWorker(@NonNull Context appContext,
        @NonNull WorkerParameters workerParams) {
      super(appContext, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
      return null;
    }
  }

  /**
   * Handle time allotted to BroadcastReceivers.
   */
  private void handleNow() {
    Log.d(TAG, "Short lived task is done.");
  }

  /**
   * Persist token to third-party servers.
   *
   * Modify this method to associate the user's FCM registration token with any
   * server-side account maintained by your application.
   *
   * @param token The new token.
   */
  private void sendRegistrationToServer(String token) {
    // TODO: Implement this method to send token to your app server.
  }

  /**
   * Create and show a simple notification containing the received FCM message.
   *
   * @param messageBody FCM message body received.
   */
  private void sendNotification(String messageBody) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
        PendingIntent.FLAG_ONE_SHOT);

    String channelId = "the channel id";
    // getString(R.string.default_notification_channel_id);
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_stat_ic_notification)
//            .setContentTitle(getString(R.string.fcm_message))
            .setContentTitle("Some notification title")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(channelId,
          "Channel human readable title",
          NotificationManager.IMPORTANCE_DEFAULT);
      notificationManager.createNotificationChannel(channel);
    }

    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
  }
}

