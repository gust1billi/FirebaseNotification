package com.example.firebasenotificationactivity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button tokenBtn;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if ( result ) {
                                Utilities.showToast(MainActivity.this,
                                        "Notification Perms Granted");
                            } else Utilities.showToast(MainActivity.this,
                                    "FCM cannot post Noficiations without Perms");
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ERROR STATEMENT FROM GOOGLE SERVICE VERSION 4.4.2
        // FirebaseApp.initializeApp( MainActivity.this );

        firstInitProject();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            String channelID = "Default FCM Channel ID";
            String channelName = "Notification Channel Name";

            NotificationManager notifManager = getSystemService(NotificationManager.class);
            notifManager.createNotificationChannel(
                    new NotificationChannel(channelID, channelName,
                            NotificationManager.IMPORTANCE_LOW) );

        }

        tokenBtn = findViewById( R.id.token_button );
        tokenBtn.setOnClickListener(view ->
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(Task<String> task) {
                                if (!task.isSuccessful()){
                                    Log.e(TAG, "Fetching FCM Regis Token Fail", task.getException());
                                } else {
                                    String token = task.getResult();

                                    String msg = "FCM registration Token: " + token;
                                    Log.e(TAG, msg);
                                    Utilities.showToast(MainActivity.this, msg);
                                }
                            }
                        }));

        askNotificationPermission();
    }

    private void askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void firstInitProject() {
        Utilities.showToast(MainActivity.this, "Hello World!");
    }
}