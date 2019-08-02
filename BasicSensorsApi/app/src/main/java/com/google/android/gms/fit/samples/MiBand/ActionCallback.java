package com.google.android.gms.fit.samples.MiBand;

public interface ActionCallback {
    public void onSuccess(Object data);

    public void onFail(int errorCode, String msg);
}
