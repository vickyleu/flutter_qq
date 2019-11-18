package com.github.marekchen.flutterqq

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log

import com.tencent.connect.common.Constants
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzonePublish
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError

import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry

/**
 * FlutterQqPlugin
 */
class FlutterQqPlugin : FlutterPlugin, ActivityAware, MethodCallHandler {

    private var mMethodChannel: MethodChannel? = null
    private var mActivityBinder: ActivityPluginBinding? = null
    private var isLogin: Boolean = false

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        mMethodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_qq")
        mMethodChannel?.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        mMethodChannel?.setMethodCallHandler(null)
        mMethodChannel = null
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        mActivityBinder = activityPluginBinding
    }

    override fun onDetachedFromActivityForConfigChanges() {
        mActivityBinder = null
    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        onAttachedToActivity(activityPluginBinding)
    }

    override fun onDetachedFromActivity() {
        onDetachedFromActivityForConfigChanges()
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        val listener = OneListener()
        mActivityBinder?.addActivityResultListener(listener)
        when (call.method) {
            "registerQQ" -> registerQQ(call, result)
            "isQQInstalled" -> isQQInstalled(call, result)
            "login" -> {
                isLogin = true
                listener.setResult(result)
                login(call, listener)
            }
            "shareToQQ" -> {
                isLogin = false
                listener.setResult(result)
                doShareToQQ(call, listener)
            }
            "shareToQzone" -> {
                isLogin = false
                listener.setResult(result)
                doShareToQzone(call, listener)
            }
        }
    }

    private fun registerQQ(call: MethodCall, result: Result) {
        val mAppid = call.argument<String>("appId")
        mTencent = Tencent.createInstance(mAppid, mActivityBinder?.activity?:return)
        result.success(true)
    }

    private fun isQQInstalled(call: MethodCall, result: Result) {
        result.success(mTencent?.isQQInstalled(mActivityBinder?.activity?:return))
    }

    private fun login(call: MethodCall, listener: OneListener) {
        val scopes = call.argument<Any>("scopes") as String?
        mTencent?.login(mActivityBinder?.activity?:return, scopes ?: "get_simple_userinfo", listener)
    }

    private fun doShareToQQ(call: MethodCall, listener: OneListener) {
        val params = Bundle()
        val shareType = call.argument<Int>("shareType")
        Log.i("FlutterQqPlugin", "arguments:" + call.arguments)
        if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
            params.putString(QQShare.SHARE_TO_QQ_TITLE, call.argument<String>("title"))
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, call.argument<String>("targetUrl"))
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, call.argument<String>("summary"))
        }
        if (shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, call.argument<String>("imageLocalUrl"))
        } else {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, call.argument<String>("imageUrl"))
        }
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, call.argument<String>("appName"))
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType?:0)
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, (call.argument<Int>("qzoneFlag")?:0))
        if (shareType == QQShare.SHARE_TO_QQ_TYPE_AUDIO) {
            params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, call.argument<String>("audioUrl"))
        }
        params.putString(QQShare.SHARE_TO_QQ_ARK_INFO, call.argument<String>("ark"))
        Log.i("FlutterQqPlugin", "params:$params")
        Handler(Looper.getMainLooper()).post { mTencent?.shareToQQ(mActivityBinder?.activity?:return@post, params, listener) }
    }

    private fun doShareToQzone(call: MethodCall, listener: OneListener) {
        val params = Bundle()
        val shareType = call.argument<Int>("shareType")
        Log.i("FlutterQqPlugin", "arguments:" + call.arguments)
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType?:0)
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, call.argument<String>("title"))
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, call.argument<String>("summary") )
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, call.argument<String>("targetUrl"))
        val list = ArrayList<String>()
        list.add(call.argument("imageUrl")?:return)
        params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, list)
        //params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, (ArrayList<String>) call.argument("imageUrls"));
        params.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, call.argument<String>("videoPath"))
        val bundle2 = Bundle()
        bundle2.putString(QzonePublish.HULIAN_EXTRA_SCENE, call.argument<String>("scene"))
        bundle2.putString(QzonePublish.HULIAN_CALL_BACK, call.argument<String>("hulian_call_back"))
        params.putBundle(QzonePublish.PUBLISH_TO_QZONE_EXTMAP, bundle2)
        Log.i("FlutterQqPlugin", "params:$params")
        if (shareType == QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT) {
            Handler(Looper.getMainLooper()).post { mTencent?.shareToQzone(mActivityBinder?.activity?:return@post, params, listener) }
        } else {
            Handler(Looper.getMainLooper()).post { mTencent?.publishToQzone(mActivityBinder?.activity?:return@post, params, listener) }
        }
    }

    private inner class OneListener : IUiListener, PluginRegistry.ActivityResultListener {

        private var result: Result? = null

        internal fun setResult(result: Result) {
            this.result = result
        }

        override fun onComplete(response: Any?) {
            Log.i("FlutterQqPlugin", response.toString())
            val re = HashMap<String, Any>()
            if (isLogin) {
                if (null == response) {
                    re["Code"] = 1
                    re["Message"] = "response is empty"
                    result?.success(re)
                    return
                }
                val jsonResponse = response as? JSONObject
                if (null == jsonResponse || jsonResponse.length() == 0) {
                    re["Code"] = 1
                    re["Message"] = "response is empty"
                    result?.success(re)
                    return
                }
                val resp = HashMap<String, Any>()
                try {
                    Log.i("FlutterQqPlugin", resp.toString())
                    resp["openid"] = jsonResponse.getString(Constants.PARAM_OPEN_ID)
                    resp["accessToken"] = jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN)
                    resp["expiresAt"] = jsonResponse.getLong(Constants.PARAM_EXPIRES_TIME)
                    // resp.put("appId", jsonResponse.getString(Constants.PARAM_APP_ID));
                    re["Code"] = 0
                    re["Message"] = "ok"
                    re["Response"] = resp
                    result?.success(re)
                    return
                } catch (e: Exception) {
                    re["Code"] = 1
                    re["Message"] = e.localizedMessage
                    result?.success(re)
                    return
                }

            }
            re["Code"] = 0
            re["Message"] = response.toString()
            result?.success(re)
        }

        override fun onError(uiError: UiError?) {
            Log.w("FlutterQqPlugin", "errorCode:${uiError?.errorCode};errorMessage:${uiError?.errorMessage}")
            val re = HashMap<String, Any>()
            re["Code"] = 1
            re["Message"] = "errorCode:${uiError?.errorCode};errorMessage:${uiError?.errorMessage}"
            result?.success(re)
        }

        override fun onCancel() {
            Log.w("FlutterQqPlugin", "error:cancel")
            val re = HashMap<String, Any>()
            re["Code"] = 2
            re["Message"] = "cancel"
            result?.success(re)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
            if (requestCode == Constants.REQUEST_LOGIN ||
                    requestCode == Constants.REQUEST_QQ_SHARE ||
                    requestCode == Constants.REQUEST_QZONE_SHARE ||
                    requestCode == Constants.REQUEST_APPBAR) {
                Tencent.onActivityResultData(requestCode, resultCode, data, this)
                return true
            }
            return false
        }

    }

    companion object {
        private var mTencent: Tencent? = null
    }


}
