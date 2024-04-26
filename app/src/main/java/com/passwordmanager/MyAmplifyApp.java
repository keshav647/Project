package com.passwordmanager;

import android.app.Application;
import android.util.Log;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.pushnotifications.pinpoint.AWSPinpointPushNotificationsPlugin;

public class MyAmplifyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // Add these lines to add the AWSCognitoAuthPlugin and AWSPinpointPushNotificationsPlugin plugins
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSPinpointPushNotificationsPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
    }

}
