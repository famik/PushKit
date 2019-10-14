package com.telpo.pushkit;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * <strong>Android PushKit</strong>
 * <p>Just support huawei and xiaomi
 *
 * @author gfm
 * @see <a href="https://github.com/famik/pushkit">README.md</a>
 */
public class PushKit {
    private static final String TAG = PushKit.class.getSimpleName();

    /**
     * PushKit Delegate 接口.
     */
    public interface Delegate {
        /**
         * 当调用 {@link PushKit#updateToken} 获取token成功后回调。
         * <p>
         * 应用需要在该方法回调时上报token到应用自己的服务器。
         *
         * @param context {@link Context}
         * @param token 获取到的推送token (小米推送称为 regId)
         */
        void onPushTokenUpdated(Context context, String token);


        /**
         * 当收到透传消息后回调
         *
         * @param context {@link Context}
         * @param msg 消息文本
         */
        void onPushMessageReceived(Context context, String msg);
    }


    /**
     * 初始化 PushKit
     * <p>
     * 简单工厂模式初始化，根据手机厂商使用相应的推送平台。
     * <p>
     * 目前只支持华为和小米，如果不是华为手机则使用小米推送。
     * @param context Application Context
     * <p>
     * 如果 context 同时是 {@link Delegate}, 初始化会自动调用 setDelegate
     *
     * @see #setDelegate
     */
    public static void init(Context context) {
        switch (Build.MANUFACTURER.toLowerCase()) {
            case "huawei":
                pushRegistry = new HuaweiPushRegistry();
                break;
            default:
                pushRegistry = new MiPushRegistry();
        }
        pushRegistry.init(context);
        handler = new Handler(Looper.getMainLooper());
        if (context instanceof Delegate) {
            setDelegate((Delegate) context);
        }
    }


    /**
     * 设置 Delegate 回调接口
     *
     * @param delegate {@link Delegate}
     */
    public static void setDelegate(Delegate delegate) {
        PushKit.delegate = delegate;
    }


    /**
     * 更新token (小米推送称为 regId)
     * <p></p>
     * <strong>Note:</strong>
     * <ol>
     * <li>
     *     在 PushKit 初始化之后，必须调用一次 updateToken 来获取token
     *     (通过 {@link Delegate#onPushTokenUpdated} 来接收)
     * </li>
     *
     * <li>
     *     由于华为推送平台的Token会过期和失效，所以必须定时调用一下 updateToken 来更新token
 *     </li>
     * <li>
     *     重复调用 updateToken 不一定都会回调 onPushTokenUpdated,
     *     小米推送仅在第一次获取token成功时回调, 除非调用了 {@link #resetRegistry} 再更新token
     *  </li>
     * </ol>
     * @see Delegate#onPushTokenUpdated
     */
    public static void updateToken() {
        if (pushRegistry != null) {
            pushRegistry.updateToken();
        }
    }


    /**
     * 重置推送注册
     *
     * @see #updateToken
     */
    public static void resetRegistry() {
        if (pushRegistry != null) {
            pushRegistry.reset();
        }
    }

    private static Handler handler;
    private static Delegate delegate;
    private static PushRegistry pushRegistry;

    static void handleTokenUpdated(final Context context, final String token) {
        if (delegate == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (delegate != null) {
                    delegate.onPushTokenUpdated(context, token);
                }
            }
        });
    }

    static void handleMessageReceived(final Context context, final String msg) {
        if (delegate == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (delegate != null) {
                    delegate.onPushMessageReceived(context, msg);
                }
            }
        });
    }

    static void log(String content, Object... args) {
        //if (Log.isLoggable(TAG, Log.DEBUG)) {
            if (args.length > 0) {
                content = String.format(content, args);
            }
            Log.d(TAG, content);
        //}
    }

    static void log(String content, Throwable t) {
        //if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, content, t);
        //}
    }
}
