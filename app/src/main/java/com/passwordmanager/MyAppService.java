package com.passwordmanager;

import android.util.Log;
import com.amplifyframework.core.Amplify;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyAppService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // Register device with Pinpoint
        Amplify.Notifications.Push.registerDevice(
                token,
                () -> Log.i("MyAmplifyApp", "Successfully registered device"),
                error -> Log.e("MyAmplifyApp", "Error registering device", error)
        );
    }
}