package com.istrong.RNNativeIntent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by mike on 16/4/26.
 */
public class NativeIntentModule extends ReactContextBaseJavaModule {

    private Context context;
    private SharedPreferences preferences;

    public NativeIntentModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext.getBaseContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public String getName() {
        return "NativeIntentModule";
    }

    /**
     * 调用原生组件类
     * @param activityName 调用类名称
     */
    @ReactMethod
    public void openNativeModule(String activityName, String jsonString, Callback successCallback, Callback erroCallback){
        try {
            Activity currentActivity = getCurrentActivity();
            if (null != currentActivity) {
                // 及时云自定义属性，如果AndroidManifest.xml 存在对应配置，打开类名强制替换
                String customWebviewName=getMetaValue(currentActivity,"RNCustomWebViewOpenName");
               if(!TextUtils.isEmpty(customWebviewName)&&activityName.endsWith(".RNWebview")){
                   //如果存在强制替换
                   activityName=customWebviewName;
               }
                Class aimActivity = Class.forName(activityName);
                Intent intent = new Intent(currentActivity,aimActivity);
                intent.putExtra("jsonString", jsonString);

                currentActivity.startActivity(intent);
                successCallback.invoke("success");
            }
        } catch (Exception e) {
            erroCallback.invoke("error");
            throw new JSApplicationIllegalArgumentException(
                    "Could not open the activity : " + e.getMessage());
        }
    }

    @ReactMethod
    public void getDataFromIntent(String name, Promise promise){
        try{
            Activity currentActivity = getCurrentActivity();

            String result = currentActivity.getIntent().getStringExtra(name);
            if (TextUtils.isEmpty(result)){
                result = "{}";
            }
            
            promise.resolve(result);
        }catch (Exception e){
            promise.reject("error");
        }
    }

    @ReactMethod
    public void getStoreForKey(String key, Callback successCallback, Callback errorCallback) {
        Object value = preferences.getAll().get(key);
        if (value != null) {      
          successCallback.invoke(value.toString());
        } else {
          errorCallback.invoke("getStoreForKey error");
        }
    }

    @ReactMethod
    public void setStore(String key, String value, Callback successCallback, Callback errorCallback) {
        SharedPreferences.Editor editor = preferences.edit();
        try{
          if (value == null) {
            editor.remove(key);
          } else {
            editor.putString(key, value);
          }
          editor.commit();
          successCallback.invoke("success");
        } catch(Exception e) {
          e.printStackTrace();
          errorCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void removeStoreForKey(String key, Callback successCallback, Callback errorCallback) {
        SharedPreferences.Editor editor = preferences.edit();
        try{          
          editor.remove(key);
          editor.commit();
          successCallback.invoke("success");
        } catch(Exception e) {
          e.printStackTrace();
          errorCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void openScheme(String urlscheme, Callback successCallback, Callback errorCallback) {
        Uri data = Uri.parse(urlscheme);
        String isNewTask = data.getQueryParameter("isNewTask");
        String actionName = data.getQueryParameter("action");
        Intent intent = null;
        if(isNewTask == null || isNewTask.equals("true")) {
            //保证新启动的APP有单独的堆栈，如果希望新启动的APP和原有APP使用同一个堆栈则去掉该项
            if(actionName != null) {
                intent = new Intent(actionName, data);                
            } else {
                intent = new Intent(Intent.ACTION_VIEW,data);
            }
            
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }else{
            intent = new Intent();
            if(actionName != null) {
                intent.setAction(actionName);
            }
            intent.setData(data);
//            Toast.makeText(getCurrentActivity(), ""+isNewTask, Toast.LENGTH_SHORT).show();
        }

        try {
            Activity currentActivity = getCurrentActivity();
            currentActivity.startActivityForResult(intent, RESULT_OK);
            successCallback.invoke("success");
        } catch (Exception e) {
            e.printStackTrace();
            errorCallback.invoke(e.getMessage());
        }
    }

    /**
     * 获取 AndroidManifest 下 <strong>meta-data</strong> 的值
     *
     * @param context
     * @param metaKey
     * @return
     */
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String value = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                value = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return value;
    }


}
