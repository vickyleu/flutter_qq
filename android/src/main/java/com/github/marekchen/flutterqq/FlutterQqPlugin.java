package com.github.marekchen.flutterqq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;


public final class FlutterQqPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {
    private MethodChannel mMethodChannel;
    private ActivityPluginBinding mActivityBinder;
    private boolean isLogin;
    private static Tencent mTencent;

    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.mMethodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_qq");
        MethodChannel var10000 = this.mMethodChannel;
        if (var10000 != null) {
            var10000.setMethodCallHandler((MethodCallHandler)this);
        }

    }

    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        MethodChannel var10000 = this.mMethodChannel;
        if (var10000 != null) {
            var10000.setMethodCallHandler((MethodCallHandler)null);
        }

        this.mMethodChannel = (MethodChannel)null;
    }

    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        this.mActivityBinder = activityPluginBinding;
    }

    public void onDetachedFromActivityForConfigChanges() {
        this.mActivityBinder = (ActivityPluginBinding)null;
    }

    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
        this.onAttachedToActivity(activityPluginBinding);
    }

    public void onDetachedFromActivity() {
        this.onDetachedFromActivityForConfigChanges();
    }

    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        FlutterQqPlugin.OneListener listener = new FlutterQqPlugin.OneListener();
        ActivityPluginBinding var10000 = this.mActivityBinder;
        if (var10000 != null) {
            var10000.addActivityResultListener((ActivityResultListener)listener);
        }

        String var5 = call.method;
        if (var5 != null) {
            String var4 = var5;
            switch(var4.hashCode()) {
                case -1869931517:
                    if (var4.equals("registerQQ")) {
                        this.registerQQ(call, result);
                    }
                    break;
                case -1585587965:
                    if (var4.equals("shareToQzone")) {
                        this.isLogin = false;
                        listener.setResult$flutter_qq_debug(result);
                        this.doShareToQzone(call, listener);
                    }
                    break;
                case -1582030246:
                    if (var4.equals("shareToQQ")) {
                        this.isLogin = false;
                        listener.setResult$flutter_qq_debug(result);
                        this.doShareToQQ(call, listener);
                    }
                    break;
                case 103149417:
                    if (var4.equals("login")) {
                        this.isLogin = true;
                        listener.setResult$flutter_qq_debug(result);
                        this.login(call, listener);
                    }
                    break;
                case 693216720:
                    if (var4.equals("isQQInstalled")) {
                        this.isQQInstalled(call, result);
                    }
            }
        }

    }

    private final void registerQQ(MethodCall call, Result result) {
        String mAppid = (String)call.argument("appId");
        ActivityPluginBinding var10001 = this.mActivityBinder;
        if (var10001 != null) {
            Activity var4 = var10001.getActivity();
            if (var4 != null) {
                mTencent = Tencent.createInstance(mAppid, (Context)var4);
                result.success(true);
                return;
            }
        }

    }

    private final void isQQInstalled(MethodCall call, Result result) {
        Tencent var10001 = mTencent;
        Boolean var3;
        if (var10001 != null) {
            ActivityPluginBinding var10002 = this.mActivityBinder;
            if (var10002 == null) {
                return;
            }

            Activity var4 = var10002.getActivity();
            if (var4 == null) {
                return;
            }

            var3 = var10001.isQQInstalled((Context)var4);
        } else {
            var3 = null;
        }

        result.success(var3);
    }

    private final void login(MethodCall call, FlutterQqPlugin.OneListener listener) {
        String scopes = (String)call.argument("scopes");
        Tencent var10000 = mTencent;
        if (var10000 != null) {
            ActivityPluginBinding var10001 = this.mActivityBinder;
            if (var10001 == null) {
                return;
            }

            Activity var4 = var10001.getActivity();
            if (var4 == null) {
                return;
            }

            String var10002 = scopes;
            if (scopes == null) {
                var10002 = "get_simple_userinfo";
            }

            var10000.login(var4, var10002, (IUiListener)listener);
        }

    }

    private final void doShareToQQ(MethodCall call, final FlutterQqPlugin.OneListener listener) {
        final Bundle params;
        Integer shareType;
        byte var5;
        label39: {
            params = new Bundle();
            shareType = (Integer)call.argument("shareType");
            Log.i("FlutterQqPlugin", "arguments:" + call.arguments);
            var5 = 5;
            if (shareType != null) {
                if (shareType == var5) {
                    break label39;
                }
            }

            params.putString("title", (String)call.argument("title"));
            params.putString("targetUrl", (String)call.argument("targetUrl"));
            params.putString("summary", (String)call.argument("summary"));
        }

        label34: {
            var5 = 5;
            if (shareType != null) {
                if (shareType == var5) {
                    params.putString("imageLocalUrl", (String)call.argument("imageLocalUrl"));
                    break label34;
                }
            }

            params.putString("imageUrl", (String)call.argument("imageUrl"));
        }

        params.putString("appName", (String)call.argument("appName"));
        params.putInt("req_type", shareType != null ? shareType : 0);
        Integer var10002 = (Integer)call.argument("qzoneFlag");
        if (var10002 == null) {
            var10002 = 0;
        }

        params.putInt("cflag", var10002);
        var5 = 2;
        if (shareType != null) {
            if (shareType == var5) {
                params.putString("audio_url", (String)call.argument("audioUrl"));
            }
        }

        params.putString("share_to_qq_ark_info", (String)call.argument("ark"));
        Log.i("FlutterQqPlugin", "params:" + params);
        (new Handler(Looper.getMainLooper())).post((Runnable)(new Runnable() {
            public final void run() {
                Tencent var10000 = FlutterQqPlugin.mTencent;
                if (var10000 != null) {
                    ActivityPluginBinding var10001 = FlutterQqPlugin.this.mActivityBinder;
                    if (var10001 == null) {
                        return;
                    }

                    Activity var1 = var10001.getActivity();
                    if (var1 == null) {
                        return;
                    }

                    var10000.shareToQQ(var1, params, (IUiListener)listener);
                }

            }
        }));
    }

    private final void doShareToQzone(MethodCall call, final FlutterQqPlugin.OneListener listener) {
        final Bundle params = new Bundle();
        Integer shareType = (Integer)call.argument("shareType");
        Log.i("FlutterQqPlugin", "arguments:" + call.arguments);
        params.putInt("req_type", shareType != null ? shareType : 0);
        params.putString("title", (String)call.argument("title"));
        params.putString("summary", (String)call.argument("summary"));
        params.putString("targetUrl", (String)call.argument("targetUrl"));
        ArrayList list = new ArrayList();
        String var10001 = (String)call.argument("imageUrl");
        if (var10001 != null) {
            list.add(var10001);
            params.putStringArrayList("imageUrl", list);
            params.putString("videoPath", (String)call.argument("videoPath"));
            Bundle bundle2 = new Bundle();
            bundle2.putString("hulian_extra_scene", (String)call.argument("scene"));
            bundle2.putString("hulian_call_back", (String)call.argument("hulian_call_back"));
            params.putBundle("extMap", bundle2);
            Log.i("FlutterQqPlugin", "params:" + params);
            byte var7 = 1;
            if (shareType != null) {
                if (shareType == var7) {
                    (new Handler(Looper.getMainLooper())).post((Runnable)(new Runnable() {
                        public final void run() {
                            Tencent var10000 = FlutterQqPlugin.mTencent;
                            if (var10000 != null) {
                                ActivityPluginBinding var10001 = FlutterQqPlugin.this.mActivityBinder;
                                if (var10001 == null) {
                                    return;
                                }

                                Activity var1 = var10001.getActivity();
                                if (var1 == null) {
                                    return;
                                }

                                var10000.shareToQzone(var1, params, (IUiListener)listener);
                            }

                        }
                    }));
                    return;
                }
            }

            (new Handler(Looper.getMainLooper())).post((Runnable)(new Runnable() {
                public final void run() {
                    Tencent var10000 = FlutterQqPlugin.mTencent;
                    if (var10000 != null) {
                        ActivityPluginBinding var10001 = FlutterQqPlugin.this.mActivityBinder;
                        if (var10001 == null) {
                            return;
                        }

                        Activity var1 = var10001.getActivity();
                        if (var1 == null) {
                            return;
                        }

                        var10000.publishToQzone(var1, params, (IUiListener)listener);
                    }

                }
            }));
        }
    }

    // $FF: synthetic method
    public static final void access$setLogin$p(FlutterQqPlugin $this, boolean var1) {
        $this.isLogin = var1;
    }

    // $FF: synthetic method
    public static final void access$setMTencent$cp(Tencent var0) {
        mTencent = var0;
    }

    // $FF: synthetic method
    public static final void access$setMActivityBinder$p(FlutterQqPlugin $this, ActivityPluginBinding var1) {
        $this.mActivityBinder = var1;
    }

    private final class OneListener implements IUiListener, ActivityResultListener {
        private Result result;

        public final void setResult$flutter_qq_debug(@NonNull Result result) {
            this.result = result;
        }

        public void onComplete(@Nullable Object response) {
            Log.i("FlutterQqPlugin", String.valueOf(response));
            HashMap re = new HashMap();
            Result var7;
            if (!FlutterQqPlugin.this.isLogin) {
                ((Map)re).put("Code", 0);
                ((Map)re).put("Message", String.valueOf(response));
                var7 = this.result;
                if (var7 != null) {
                    var7.success(re);
                }

            } else if (response == null) {
                ((Map)re).put("Code", 1);
                ((Map)re).put("Message", "response is empty");
                var7 = this.result;
                if (var7 != null) {
                    var7.success(re);
                }

            } else {
                Object var10000 = response;
                if (!(response instanceof JSONObject)) {
                    var10000 = null;
                }

                JSONObject jsonResponse = (JSONObject)var10000;
                if (jsonResponse != null && jsonResponse.length() != 0) {
                    HashMap resp = new HashMap();

                    String var10002;
                    Map var8;
                    try {
                        Log.i("FlutterQqPlugin", resp.toString());
                        var8 = (Map)resp;
                        var10002 = jsonResponse.getString("openid");
                        var8.put("openid", var10002);
                        var8 = (Map)resp;
                        var10002 = jsonResponse.getString("access_token");
                        var8.put("accessToken", var10002);
                        ((Map)resp).put("expiresAt", jsonResponse.getLong("expires_time"));
                        ((Map)re).put("Code", 0);
                        ((Map)re).put("Message", "ok");
                        ((Map)re).put("Response", resp);
                        var7 = this.result;
                        if (var7 != null) {
                            var7.success(re);
                        }

                    } catch (Exception var6) {
                        ((Map)re).put("Code", 1);
                        var8 = (Map)re;
                        var10002 = var6.getLocalizedMessage();
                        var8.put("Message", var10002);
                        var7 = this.result;
                        if (var7 != null) {
                            var7.success(re);
                        }

                    }
                } else {
                    ((Map)re).put("Code", 1);
                    ((Map)re).put("Message", "response is empty");
                    var7 = this.result;
                    if (var7 != null) {
                        var7.success(re);
                    }

                }
            }
        }

        public void onError(@Nullable UiError uiError) {
            Log.w("FlutterQqPlugin", "errorCode:" + (uiError != null ? uiError.errorCode : null) + ";errorMessage:" + (uiError != null ? uiError.errorMessage : null));
            HashMap re = new HashMap();
            ((Map)re).put("Code", 1);
            ((Map)re).put("Message", "errorCode:" + (uiError != null ? uiError.errorCode : null) + ";errorMessage:" + (uiError != null ? uiError.errorMessage : null));
            Result var10000 = this.result;
            if (var10000 != null) {
                var10000.success(re);
            }

        }

        public void onCancel() {
            Log.w("FlutterQqPlugin", "error:cancel");
            HashMap re = new HashMap();
            ((Map)re).put("Code", 2);
            ((Map)re).put("Message", "cancel");
            Result var10000 = this.result;
            if (var10000 != null) {
                var10000.success(re);
            }

        }

        public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if (requestCode != 11101 && requestCode != 10103 && requestCode != 10104 && requestCode != 10102) {
                return false;
            } else {
                Tencent.onActivityResultData(requestCode, resultCode, data, (IUiListener)this);
                return true;
            }
        }

        public OneListener() {
        }
    }


}
