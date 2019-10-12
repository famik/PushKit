package com.telpo.pushkit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

public class MiPushReceiver extends PushMessageReceiver {
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        if (MiPushClient.COMMAND_REGISTER.equals(message.getCommand())) {
            PushKit.log("updateToken onResult code: %d", message.getResultCode());
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                List<String> arguments = message.getCommandArguments();
                String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
                PushKit.handleTokenUpdated(context, cmdArg1);
            }
        }
    }


    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        PushKit.log("onReceivePassThroughMessage");
        PushKit.handleMessageReceived(context, message.getContent());
    }

    @Override
    public void onRequirePermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= 23 && context.getApplicationInfo().targetSdkVersion >= 23) {
            PushKit.log("onRequirePermissions is called. need permission: %s", arrayToString(permissions));

            Intent intent = new Intent();
            intent.putExtra("permissions", permissions);
            intent.setComponent(new ComponentName(context.getPackageName(), PermissionActivity.class.getCanonicalName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }
    }

    private static String arrayToString(String[] strings) {
        StringBuilder result = new StringBuilder(" ");
        for (String str : strings) {
            result.append(str).append(" ");
        }
        return result.toString();
    }

}
