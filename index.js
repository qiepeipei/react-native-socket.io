'use strict';
import React, {
    DeviceEventEmitter,
    NativeModules,
    Platform,

} from 'react-native';
var sockets = NativeModules.SocketIO;
class Socket {

  constructor (host, config) {


    if(typeof host === 'undefined'){
      throw '连接地址不能为空!';
    }

    if(typeof config === 'undefined'){
      config = {};
    }


    this.sockets = sockets;

    this.isConnected = false;
    this.handlers = {};

    //接收 socketEvent事件
    this.deviceEventSubscription = DeviceEventEmitter.addListener(
      'socketEvent', this._handleEvent.bind(this)
    );

    this.defaultHandlers = {
      connect: () => {
        this.isConnected = true;
      },

      disconnect: () => {
        this.isConnected = false;
      }
    };


    // 初始化socket对象
    this.sockets.initialise(host, config);

  }

  //事件回调
  _handleEvent (event) {

    console.log(event);
    if(this.handlers.hasOwnProperty(event.name)){
      //执行事件回调
      this.handlers[event.name](
          (event.hasOwnProperty('items')) ? event.items : null
      );
    }

  //判断是否连接或断开连接
    if(this.defaultHandlers.hasOwnProperty(event.name)){

      this.defaultHandlers[event.name]();
    }


  }

  connect () {
    this.sockets.connect();
  }

  on (event, handler) {
    if (Platform.OS === 'android') {
      this.handlers[event] = handler;
      this.sockets.on(event);
    }else{
      this.handlers[event] = handler;
    }

  }


  emit (event, data) {
    this.sockets.emit(event, data);
  }
  
  joinNamespace () {
    this.sockets.joinNamespace();
  }

  leaveNamespace () {
    this.sockets.leaveNamespace();
  }

  AllOff () {
    this.sockets.AllOff();
  }

  AllOff (event) {
    this.sockets.off(event);
  }


  close () {
    this.sockets.close();
  }

  reconnect () {
    this.sockets.reconnect();
  }
}

module.exports = Socket;
