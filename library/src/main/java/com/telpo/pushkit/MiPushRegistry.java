package com.telpo.pushkit;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

public class MiPushRegistry implements PushRegistry {
    private String regId;
    private Context context;

    @Override
    public void init(Context context) {
        this.context = context.getApplicationContext();
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                PushKit.log(content, t);
            }

            @Override
            public void log(String content) {
                PushKit.log(content);
            }
        };
        Logger.setLogger(context, newLogger);
    }

    @Override
    public void updateToken() {
        if (regId == null) {
            regId = MiPushClient.getRegId(context);
            if (regId != null) {
                PushKit.handleTokenUpdated(context, regId);
            } else {
                registerMiPush(context);
            }
        }
    }

    @Override
    public void reset() {
        regId = null;
    }

    public void unregister() {
        reset();
        MiPushClient.unregisterPush(context);
    }

    private static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return ai.metaData.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    static void registerMiPush(Context context) {
        String appid = getMetaData(context, "com.xiaomi.push.appid");
        String appkey = getMetaData(context, "com.xiaomi.push.appkey");
        PushKit.log("registerMiPush appid:%s; appkey:%s", appid, appkey);
        MiPushClient.registerPush(context, appid, appkey);
    }
}
