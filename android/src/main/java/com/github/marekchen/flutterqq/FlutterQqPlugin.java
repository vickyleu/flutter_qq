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
        MethodChannel methodChannel = this.mMethodChannel;
        if (methodChannel != null) {
            methodChannel.setMethodCallHandler((MethodCallHandler)this);
        }

    }

    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        MethodChannel methodChannel = this.mMethodChannel;
        if (methodChannel != null) {
            methodChannel.setMethodCallHandler((MethodCallHandler)null);
        }

        this.mMethodChannel = (MethodChannel)null;
    }

    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        this.mActivityBinder = activityPluginBinding;
        if (this.mActivityBinder != null) {
            this.mActivityBinder.addActivityResultListener(listener);
        }
    }

    public void onDetachedFromActivityForConfigChanges() {
        if (this.mActivityBinder != null) {
            this.mActivityBinder.removeActivityResultListener(listener);
        }
        this.mActivityBinder = null;
    }

    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
        this.onAttachedToActivity(activityPluginBinding);
    }

    public void onDetachedFromActivity() {
        this.onDetachedFromActivityForConfigChanges();
    }

    FlutterQqPlugin.OneListener listener = new FlutterQqPlugin.OneListener();

    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        String method1 = call.method;
        if (method1 != null) {
            String method = method1;
            switch(method.hashCode()) {
                case -1869931517:
                    if (method.equals("registerQQ")) {
                        this.registerQQ(call, result);
                    }
                    break;
                case -1585587965:
                    if (method.equals("shareToQzone")) {
                        this.isLogin = false;
                        listener.setResult$flutter_qq_debug(result);
                        this.doShareToQzone(call, listener);
                    }
                    break;
                case -1582030246:
                    if (method.equals("shareToQQ")) {
                        this.isLogin = false;
                        listener.setResult$flutter_qq_debug(result);
                        this.doShareToQQ(call, listener);
                    }
                    break;
                case 103149417:
                    if (method.equals("login")) {
                        this.isLogin = true;
                        listener.setResult$flutter_qq_debug(result);
                        this.login(call, listener);
                    }
                    break;
                case 693216720:
                    if (method.equals("isQQInstalled")) {
                        this.isQQInstalled(call, result);
                    }
            }
        }

    }

    private final void registerQQ(MethodCall call, Result result) {
        String mAppid = (String)call.argument("appId");
        ActivityPluginBinding activityPluginBinding = this.mActivityBinder;
        if (activityPluginBinding != null) {
            Activity activity = activityPluginBinding.getActivity();
            if (activity != null) {
                mTencent = Tencent.createInstance(mAppid, (Context)activity);
                result.success(true);
                return;
            }
        }

    }

    private final void isQQInstalled(MethodCall call, Result result) {
        Tencent tencent = mTencent;
        Boolean isInstalled;
        if (tencent != null) {
            ActivityPluginBinding activityPluginBinding = this.mActivityBinder;
            if (activityPluginBinding == null) {
                return;
            }

            Activity activity = activityPluginBinding.getActivity();
            if (activity == null) {
                return;
            }

            isInstalled = tencent.isQQInstalled((Context)activity);
        } else {
            isInstalled = null;
        }

        result.success(isInstalled);
    }

    private final void login(MethodCall call, FlutterQqPlugin.OneListener listener) {
        String scopes = (String)call.argument("scopes");
        Tencent tencent = mTencent;
        if (tencent != null) {
            ActivityPluginBinding activityPluginBinding = this.mActivityBinder;
            if (activityPluginBinding == null) {
                return;
            }

            Activity activity = activityPluginBinding.getActivity();
            if (activity == null) {
                return;
            }

            String user_info = scopes;
            if (scopes == null) {
                user_info = "get_simple_userinfo";
            }

            tencent.login(activity, user_info, (IUiListener)listener);
        }

    }

    private final void doShareToQQ(MethodCall call, final FlutterQqPlugin.OneListener listener) {
        final Bundle bundle;
        Integer shareType;
        byte type;
        label39: {
            bundle = new Bundle();
            shareType = (Integer)call.argument("shareType");
            Log.i("FlutterQqPlugin", "arguments:" + call.arguments);
            type = 5;
            if (shareType != null) {
                if (shareType == type) {
                    break label39;
                }
            }

            bundle.putString("title", (String)call.argument("title"));
            bundle.putString("targetUrl", (String)call.argument("targetUrl"));
            bundle.putString("summary", (String)call.argument("summary"));
        }

        label34: {
            type = 5;
            if (shareType != null) {
                if (shareType == type) {
                    bundle.putString("imageLocalUrl", (String)call.argument("imageLocalUrl"));
                    break label34;
                }
            }

            bundle.putString("imageUrl", (String)call.argument("imageUrl"));
        }

        bundle.putString("appName", (String)call.argument("appName"));
        bundle.putInt("req_type", shareType != null ? shareType : 0);
        Integer qzoneFlag = (Integer)call.argument("qzoneFlag");
        if (qzoneFlag == null) {
            qzoneFlag = 0;
        }

        bundle.putInt("cflag", qzoneFlag);
        type = 2;
        if (shareType != null) {
            if (shareType == type) {
                bundle.putString("audio_url", (String)call.argument("audioUrl"));
            }
        }

        bundle.putString("share_to_qq_ark_info", (String)call.argument("ark"));
        Log.i("FlutterQqPlugin", "params:" + bundle);
        (new Handler(Looper.getMainLooper())).post((Runnable)(new Runnable() {
            public final void run() {
                Tencent tencent = FlutterQqPlugin.mTencent;
                if (tencent != null) {
                    ActivityPluginBinding activityPluginBinding = FlutterQqPlugin.this.mActivityBinder;
                    if (activityPluginBinding == null) {
                        return;
                    }

                    Activity activity = activityPluginBinding.getActivity();
                    if (activity == null) {
                        return;
                    }

                    tencent.shareToQQ(activity, bundle, (IUiListener)listener);
                }

            }
        }));
    }

    private final void doShareToQzone(MethodCall call, final FlutterQqPlugin.OneListener listener) {
        final Bundle bundle = new Bundle();
        Integer shareType = (Integer)call.argument("shareType");
        Log.i("FlutterQqPlugin", "arguments:" + call.arguments);
        bundle.putInt("req_type", shareType != null ? shareType : 0);
        bundle.putString("title", (String)call.argument("title"));
        bundle.putString("summary", (String)call.argument("summary"));
        bundle.putString("targetUrl", (String)call.argument("targetUrl"));
        ArrayList list = new ArrayList();
        String imageUrl = (String)call.argument("imageUrl");
        if (imageUrl != null) {
            list.add(imageUrl);
            bundle.putStringArrayList("imageUrl", list);
            bundle.putString("videoPath", (String)call.argument("videoPath"));
            Bundle bundle2 = new Bundle();
            bundle2.putString("hulian_extra_scene", (String)call.argument("scene"));
            bundle2.putString("hulian_call_back", (String)call.argument("hulian_call_back"));
            bundle.putBundle("extMap", bundle2);
            Log.i("FlutterQqPlugin", "params:" + bundle);
            byte var7 = 1;
            if (shareType != null) {
                if (shareType == var7) {
                    (new Handler(Looper.getMainLooper())).post((Runnable)(new Runnable() {
                        public final void run() {
                            Tencent tencent = FlutterQqPlugin.mTencent;
                            if (tencent != null) {
                                ActivityPluginBinding activityPluginBinding = FlutterQqPlugin.this.mActivityBinder;
                                if (activityPluginBinding == null) {
                                    return;
                                }

                                Activity var1 = activityPluginBinding.getActivity();
                                if (var1 == null) {
                                    return;
                                }

                                tencent.shareToQzone(var1, bundle, (IUiListener)listener);
                            }

                        }
                    }));
                    return;
                }
            }

            (new Handler(Looper.getMainLooper())).post((Runnable)(new Runnable() {
                public final void run() {
                    Tencent tencent = FlutterQqPlugin.mTencent;
                    if (tencent != null) {
                        ActivityPluginBinding activityPluginBinding = FlutterQqPlugin.this.mActivityBinder;
                        if (activityPluginBinding == null) {
                            return;
                        }

                        Activity activity = activityPluginBinding.getActivity();
                        if (activity == null) {
                            return;
                        }

                        tencent.publishToQzone(activity, bundle, (IUiListener)listener);
                    }

                }
            }));
        }
    }


    private final class OneListener implements IUiListener, ActivityResultListener {
        private Result result;

        public final void setResult$flutter_qq_debug(@NonNull Result result) {
            this.result = result;
        }

        public void onComplete(@Nullable Object response) {
            Log.i("FlutterQqPlugin", String.valueOf(response));

            HashMap hashMap = new HashMap();
            Result result;
            if (!FlutterQqPlugin.this.isLogin) {
                ((Map)hashMap).put("Code", 0);
                ((Map)hashMap).put("Message", String.valueOf(response));
                result = this.result;
                if (result != null) {
                    result.success(hashMap);
                }

            } else if (response == null) {
                ((Map)hashMap).put("Code", 1);
                ((Map)hashMap).put("Message", "response is empty");
                result = this.result;
                if (result != null) {
                    result.success(hashMap);
                }

            } else {
                Object response1 = response;
                if (!(response instanceof JSONObject)) {
                    response1 = null;
                }

                JSONObject jsonResponse = (JSONObject)response1;
                if (jsonResponse != null && jsonResponse.length() != 0) {
                    HashMap resp = new HashMap();

                    String string;
                    Map map;
                    try {
                        Log.i("FlutterQqPlugin", resp.toString());
                        map = (Map)resp;
                        string = jsonResponse.getString("openid");
                        map.put("openid", string);
                        map = (Map)resp;
                        string = jsonResponse.getString("access_token");
                        map.put("accessToken", string);
                        ((Map)resp).put("expiresAt", jsonResponse.getLong("expires_time"));
                        ((Map)hashMap).put("Code", 0);
                        ((Map)hashMap).put("Message", "ok");
                        ((Map)hashMap).put("Response", resp);
                        result = this.result;
                        if (result != null) {
                            result.success(hashMap);
                        }

                    } catch (Exception e) {
                        ((Map)hashMap).put("Code", 1);
                        map = (Map)hashMap;
                        string = e.getLocalizedMessage();
                        map.put("Message", string);
                        result = this.result;
                        if (result != null) {
                            result.success(hashMap);
                        }

                    }
                } else {
                    ((Map)hashMap).put("Code", 1);
                    ((Map)hashMap).put("Message", "response is empty");
                    result = this.result;
                    if (result != null) {
                        result.success(hashMap);
                    }

                }
            }
        }

        public void onError(@Nullable UiError uiError) {
            Log.w("FlutterQqPlugin", "errorCode:" + (uiError != null ? uiError.errorCode : null) + ";errorMessage:" + (uiError != null ? uiError.errorMessage : null));
            HashMap hashMap = new HashMap();
            ((Map)hashMap).put("Code", 1);
            ((Map)hashMap).put("Message", "errorCode:" + (uiError != null ? uiError.errorCode : null) + ";errorMessage:" + (uiError != null ? uiError.errorMessage : null));
            Result result = this.result;
            if (result != null) {
                result.success(hashMap);
            }

        }

        public void onCancel() {
            Log.w("FlutterQqPlugin", "error:cancel");
            HashMap hashMap = new HashMap();
            ((Map)hashMap).put("Code", 2);
            ((Map)hashMap).put("Message", "cancel");
            Result result = this.result;
            if (result != null) {
                result.success(hashMap);
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
