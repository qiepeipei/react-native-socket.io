package com.example.reactnativesocketio;

import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.LongArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by qiepeipei on 16/7/16.
 */
public class SocketModule extends ReactContextBaseJavaModule {

    private Socket mSocket;


    public SocketModule(ReactApplicationContext reactContext){

        super(reactContext);
    }

    @Override
    public String getName() {
        return "SocketIO";
    }

    //初始化方法
    @ReactMethod
    public void initialise(String host,ReadableMap config){
        ReadableMapKeySetIterator iterator = config.keySetIterator();

        //将map转为Options
        IO.Options options = toOptions((ReadableNativeMap) config);

        try {
            this.mSocket = IO.socket(host,options);

        } catch (URISyntaxException e) {

            Log.d("SocketModule","初始化socket错误,详细信息:",e);
        }

    }

    @ReactMethod
    public void on(String event){
        //Log.d("aaaaaaa",event);
        mSocket.on(event, onAnyEventHandler(event));

    }


    @ReactMethod
    public void emit(String event,ReadableMap items){

        HashMap<String,Object> map = this.toHashMap((ReadableNativeMap)items);
        if(map != null){
            mSocket.emit(event,new JSONObject(map));
        }else {
            Log.d("emit","提交数据不能为null");
        }

    }



    @ReactMethod
    public void connect(){

        mSocket.connect();

    }

    @ReactMethod
    public void AllOff(){

        mSocket.off();

    }

    @ReactMethod
    public void off(String event){

        mSocket.off(event);

    }


    @ReactMethod
    public void reconnect() {
        Log.d("reconnect", "reconnect方法java暂不支持");
    }

    @ReactMethod
    public void joinNamespace(String namespace) {
        Log.d("joinNamespace", "joinNamespace方法java暂不支持");
    }

    @ReactMethod
    public void closes() {
        mSocket.disconnect();
    }

    @ReactMethod
    public void leaveNamespace() {
        Log.d("leaveNamespace", "leaveNamespace方法java暂不支持");
    }


    //事件回调
    private Emitter.Listener onAnyEventHandler(final String event){
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                WritableMap params = Arguments.createMap();
                params.putString("name", event);

                WritableArray items = objectsToJSON(args);
                if(items != null){
                    params.putArray("items",items);
                }

                sendEvent(getReactApplicationContext(), "socketEvent", params);

            }
        };

    };


    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    private HashMap<String,Object>toHashMap(ReadableNativeMap map){
        //获取所有key
        ReadableMapKeySetIterator iterator = map.keySetIterator();
        HashMap<String,Object> hashMap = new HashMap<>();

        //遍历所有key
        while(iterator.hasNextKey()){
            String key = iterator.nextKey();
            //获取key对应存放数据类型
            switch(map.getType(key)){

                case Null:
                    hashMap.put(key,null);
                    break;
                case Boolean:
                    hashMap.put(key,map.getBoolean(key));
                    break;
                case Number:
                    hashMap.put(key,map.getDouble(key));
                    break;
                case String:
                    hashMap.put(key,map.getString(key));
                    break;
                case Map:
                    hashMap.put(key,map.getMap(key));
                    break;
                case Array:
                    hashMap.put(key,map.getArray(key));
                    break;
                default:
                    throw new IllegalArgumentException("无法转换对象到指定类型,key:"+key);

            }


        }

        return hashMap;
    }


    private IO.Options toOptions(ReadableNativeMap options){

        ReadableMapKeySetIterator iterator = options.keySetIterator();
        IO.Options opts = new IO.Options();

        while (iterator.hasNextKey()){

            //获取当前key并转换为小写字母
            String key = iterator.nextKey().toLowerCase();

            switch (key) {
                case "force new connection":
                case "forcenew":
                    opts.forceNew = options.getBoolean(key);
                    break;
                case "multiplex":
                    opts.multiplex = options.getBoolean(key);
                    break;
                case "reconnection":
                    opts.reconnection = options.getBoolean(key);
                    break;
                case "connect_timeout":
                    opts.timeout = options.getInt(key);
                    break;
                default:
                    Log.d("SocketOptions","没有找到指定配置项,key:"+key);

            }



        }

        return opts;


    }

    //将objects转为json
    private WritableArray objectsToJSON(Object... args){

        if(args != null && args.length > 0){

            WritableArray items = Arguments.createArray();

            for(Object object : args){

                if(object == null){

                    items.pushNull();

                }else if(object instanceof JSONObject){

                    items.pushMap(jsonObjectToWritableMap((JSONObject)object));

                }else if(object instanceof JSONArray){

                    items.pushArray(jsonArrayToWritableArray((JSONArray)object));

                }else{

                    Log.d("objectsToJSON","无法匹配对象object:" + object);

                    items.pushNull();

                }

            }

            return items;

        }else{

            return null;

        }


    }





    private WritableMap jsonObjectToWritableMap(JSONObject jsonObject){

        WritableMap items = Arguments.createMap();

        Iterator<String> iterator = jsonObject.keys();

        while (iterator.hasNext()){

            String key = iterator.next();

            try {
                //获取key内容
                Object object = jsonObject.get(key);

                if(object == null){
                    //将key内容设置为null
                    items.putNull(key);

                }else if(object instanceof Boolean){

                    items.putBoolean(key,(Boolean)object);

                }else if(object instanceof Integer){

                    items.putInt(key,(Integer)object);

                }else if(object instanceof Double){

                    items.putDouble(key,(Double)object);

                }else if(object instanceof Float){

                    items.putDouble(key,((Float)object).doubleValue());

                }else if(object instanceof Long){

                    items.putDouble(key,((Long)object).doubleValue());

                }else if(object instanceof String){

                    items.putString(key,object.toString());

                }else if(object instanceof JSONObject){

                    items.putMap(key,jsonObjectToWritableMap((JSONObject)object));

                }else if(object instanceof JSONArray){

                    items.putArray(key, jsonArrayToWritableArray((JSONArray) object));

                }else{

                    Log.d("jsonObjectToWritableMap","无法匹配对象key="+key+",value="+object);
                    items.putNull(key);

                }

            } catch (JSONException e) {
                Log.d("jsonObjectToWritableMap","jsonObject获取keys出错,error:"+e);
            }


        }

        return items;

    }


    private WritableArray jsonArrayToWritableArray(JSONArray jsonArray){

        WritableArray items = Arguments.createArray();

        for(int i=0;i<jsonArray.length();i++){

            try {
                Object object = jsonArray.get(i);

                if(object == null){

                    items.pushNull();

                }else if(object instanceof Boolean){

                    items.pushBoolean((Boolean)object);

                }else if(object instanceof Integer){

                    items.pushInt((Integer)object);

                }else if(object instanceof Double){

                    items.pushDouble((Double)object);

                }else if(object instanceof Float){

                    items.pushDouble(((Float)object).doubleValue());

                }else if(object instanceof Long){

                    items.pushDouble(((Long)object).doubleValue());

                }else if(object instanceof String){

                    items.pushString((String) object);

                }else if(object instanceof JSONObject){

                    items.pushMap(jsonObjectToWritableMap((JSONObject)object));

                }else if(object instanceof JSONArray){

                    items.pushArray(jsonArrayToWritableArray((JSONArray)object));

                }else{

                    Log.d("jsonArrayToArrray","无法匹配对象object:" + object);

                    items.pushNull();

                }



            } catch (JSONException e) {
                Log.d("jsonArrayToArrray","jsonArray获取指定元素错误,error:"+e);
            }


        }

        return items;

    }


}
