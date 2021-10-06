package com.wildanimalphoto.photoeditor;

import android.app.Application;

import com.facebook.ads.AudienceNetworkAds;
import com.onesignal.OneSignal;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        if (AudienceNetworkAds.isInAdsProcess(this)) {
            return;
        }
        AudienceNetworkAds.initialize(this);
    }
}