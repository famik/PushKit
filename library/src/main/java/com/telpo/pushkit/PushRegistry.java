package com.telpo.pushkit;

import android.content.Context;

public interface PushRegistry {
    void init(Context context);
    void updateToken();
    void reset();
}

