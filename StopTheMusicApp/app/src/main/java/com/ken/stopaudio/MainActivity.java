package com.ken.stopaudio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

  public static  void killAudio(Context context) {

    Handler handler = new Handler();
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        killAudio(MainActivity.this);

        // Fetch firebase messaging token.
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
              @Override
              public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                  Log.w("zzz", "Fetching FCM registration token failed", task.getException());
                  return;
                }

                // Get new FCM registration token
                String token = task.getResult();

                // Log and toast
                String msg = "Token:"+token;
                Log.d("zzz", msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
              }
            });
//
//        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//            .setAction("Action", null).show();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}