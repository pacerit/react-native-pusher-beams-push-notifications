
package com.b8ne.RNPusherPushNotifications;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Callback;

import java.util.Set;

// SEE: https://docs.pusher.com/beams/reference/android

public class RNPusherPushNotificationsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
    private PusherWrapper pusher;

    public RNPusherPushNotificationsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    private final LifecycleEventListener lifecycleEventListener = new LifecycleEventListener() {

        @Override
        public void onHostResume() {
            pusher.onResume(getCurrentActivity());
        }

        @Override
        public void onHostDestroy() {
            pusher.onDestroy(getCurrentActivity());
        }

        @Override
        public void onHostPause() {
            pusher.onPause(getCurrentActivity());
        }
    };

    @Override
    public String getName() {
        return "RNPusherPushNotifications";
    }

    @ReactMethod
    public void setAppKey(String appKey) {
        this.pusher = new PusherWrapper(appKey, this.reactContext);
        reactContext.addLifecycleEventListener(lifecycleEventListener);
    }

    @ReactMethod
    public void clearAllState() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (checkPusherInstance()) {
                    pusher.clearAllState();
                }
            }
        });
    }

    @ReactMethod
    public void subscribe(final String interest, final Callback errorCallback, final Callback successCallback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (checkPusherInstance()) {
                    pusher.subscribe(interest, errorCallback, successCallback);
                }
            }
        });
    }

    @ReactMethod
        public void setSubscriptions(final Set<String> interests, final Callback errorCallback, final Callback successCallback) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    pusher.setSubscriptions(interests, errorCallback, successCallback);
                }
            });
        }

    @ReactMethod
    public void unsubscribe(final String interest, final Callback errorCallback, final Callback successCallback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (checkPusherInstance()) {
                    pusher.unsubscribe(interest, errorCallback, successCallback);
                }
            }
        });
    }

    @ReactMethod
    public void unsubscribeAll(final Callback errorCallback, final Callback successCallback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (checkPusherInstance()) {
                    pusher.unsubscribeAll(errorCallback, successCallback);
                }
            }
        });
    }

    @ReactMethod
    public void getSubscriptions( final Callback subscriptionCallback, final Callback errorCallback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (checkPusherInstance()) {
                    pusher.getSubscriptions(subscriptionCallback, errorCallback);
                }
            }
        });
    }

    @ReactMethod
    public void setUserId(final String userId, final String token, final Callback errorCallback, final Callback successCallback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (checkPusherInstance()) {
                    pusher.setUserId(userId, token, errorCallback, successCallback);
                }
            }
        });
    }

    @ReactMethod
    public void setOnSubscriptionsChangedListener(final Callback subscriptionChangedListener) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (checkPusherInstance()) {
                    pusher.setOnSubscriptionsChangedListener(subscriptionChangedListener);
                }
            }
        });
    }

    public boolean checkPusherInstance() {
        if (pusher == null) {
            Log.d("PUSHER_PUSH_MODULE", "Pusher instance not set. setAppKey() function must be called before");
            System.out.print("Pusher instance not set. setAppKey() function must be called before");
            return false;
        }

        return true;
    }
}
