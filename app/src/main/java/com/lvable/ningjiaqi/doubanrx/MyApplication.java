package com.lvable.ningjiaqi.doubanrx;

import android.app.Application;
import android.os.Debug;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by ningjiaqi on 16/4/29.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
       // LeakCanary.install(this);
    }
}
