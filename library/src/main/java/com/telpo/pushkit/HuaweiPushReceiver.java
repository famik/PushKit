package com.telpo.pushkit;

import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;

public class HuaweiPushReceiver extends PushReceiver {
    @Override
    public void onToken(Context context, String token, Bundle extras) {
        //String belongId = extras.getString("belongId");
        PushKit.handleTokenUpdated(context, token);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            //开发者可以自己解析消息内容，然后做相应的处理
            String content = new String(msg, "UTF-8");
            PushKit.handleMessageReceived(context, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
