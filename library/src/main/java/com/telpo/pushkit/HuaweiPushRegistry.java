package com.telpo.pushkit;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;

public class HuaweiPushRegistry implements PushRegistry {
    private long lastGetTokenTime;

    @Override
    public void init(Context context) {
        HMSAgent.init((Application) context.getApplicationContext());
    }

    @Override
    public void updateToken() {
        if (lastGetTokenTime == 0 || SystemClock.elapsedRealtime() - lastGetTokenTime > 3600 * 1000) {
            HMSAgent.Push.getToken(new GetTokenHandler() {
                @Override
                public void onResult(int code) {
                    PushKit.log("updateToken onResult code: %d", code);
                    if (code == 0) {
                        lastGetTokenTime = SystemClock.elapsedRealtime() ;
                    }
                }
            });
        }
    }

    @Override
    public void reset() {
        lastGetTokenTime = 0;
    }
}
