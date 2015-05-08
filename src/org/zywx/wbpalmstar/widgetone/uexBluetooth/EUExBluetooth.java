package org.zywx.wbpalmstar.widgetone.uexBluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import java.io.File;
import java.util.List;

/**
 * Created by ylt on 15/3/23.
 */
@SuppressLint("NewApi")
public class EUExBluetooth extends EUExBase {

    private static final int MSG_SEND=32;
    private static final int MSG_SETENABLE=33;
    private static final int MSG_SETVISIBLE=34;

    private BluetoothAdapter mBluetoothAdapter;

    private static final String BUNDLE_DATA = "data";
    public EUExBluetooth(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected boolean clean() {
        return false;
    }

    public void send(String[] params){
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SEND;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void sendMsg(String[] params){
        String json=params[0];
        String type = null;
        String filePath = null;
        String content = null;
        try {
            JSONObject jsonObject=new JSONObject(json);
            type=jsonObject.optString("type");
            filePath=jsonObject.optString("filePath");
            content=jsonObject.optString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(type)){
            errorCallback(0, 0, "error params!");
            return;
        }
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        if ("1".equals(type)){
            shareIntent.setType("text/plain");
            setSharePackage(mContext, shareIntent, "bluetooth");
            shareIntent.putExtra(Intent.EXTRA_TEXT,content);
        }else if ("2".equals(type)){
            shareIntent.setType("*/*");
            setSharePackage(mContext, shareIntent, "bluetooth");
            shareIntent.putExtra(Intent.EXTRA_STREAM,
                     Uri.fromFile(new File(filePath)));
        }
        mContext.startActivity(Intent.createChooser(shareIntent, "select"));
    }

    private void setSharePackage(Context context,Intent intent,String action){
        List<ResolveInfo> resolveInfos=context.getPackageManager().queryIntentActivities(intent, 0);
        if (!resolveInfos.isEmpty()){
            for (ResolveInfo resolveInfo:resolveInfos){
                if (resolveInfo.activityInfo.packageName.toLowerCase().contains(action)||
                        resolveInfo.activityInfo.name.toLowerCase().contains(action)){
                        intent.setPackage(resolveInfo.activityInfo.packageName);
                }
            }
        }
    }

    public void setEnable(String[] params){
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SETENABLE;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    @SuppressLint("NewApi")
    private void setEnableMsg(String[] params){
        String json=params[0];
        String enable = null;
        try {
            JSONObject jsonObject=new JSONObject(json);
            enable=jsonObject.optString("enable");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(enable)){
            errorCallback(0, 0, "error params!");
            return;
        }
        if ("1".equals(enable)){
            mBluetoothAdapter.enable();
        }else{
            mBluetoothAdapter.disable();
        }
    }

    public void setVisible(String[] params){
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SETVISIBLE;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void setVisibleMsg(String[] params){
        String json=params[0];
        String time = null;
        try {
            JSONObject jsonObject=new JSONObject(json);
            time=jsonObject.optString("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(time)){
            errorCallback(0, 0, "error params!");
            return;
        }
        Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enabler.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, Integer.valueOf(time));
        startActivity(enabler);
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {
            case MSG_SEND:
                sendMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SETENABLE:
                setEnableMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SETVISIBLE:
                setVisibleMsg(bundle.getStringArray(BUNDLE_DATA));
        }
        super.onHandleMessage(message);
    }
}
