//
//  Socket.swift
//  
//
//  Created by qiepeipei on 16/7/15.
//
//

import Foundation

@objc(SocketIO)
class SocketIO: NSObject {
    
    var socket: SocketIOClient!    //socketIO对象
    var connectionSocket: String!  //配置项
    var bridge: RCTBridge!  //通信对象
    
    
    
    @objc func initWithBridge(_bridge: RCTBridge) {
        self.bridge = _bridge
    }
    
    
    /*
     初始化socket对象
     */
    @objc func initialise(connection: String, config: NSDictionary) -> Void {
        connectionSocket = connection
        
        // 连接socket
        self.socket = SocketIOClient(
            socketURL: NSURL(string: self.connectionSocket)!,
            options:config as? [String : AnyObject]
        )
        
        // 初始化所有事件
        self.onAnyEvent()
    }
    
    /**
     * 设置命名空间
     */
    
    @objc func joinNamespace(nameSpace:String) {
        self.socket.joinNamespace(nameSpace)
    }
    
    /**
     * 退出命名空间
     */
    @objc func leaveNamespace() {
        self.socket.leaveNamespace()
    }
    
    /**
     * 提交数据到服务器
     */
    @objc func emit(event: String, items: AnyObject) -> Void {
        self.socket.emit(event, items)
    }
    
    /**
     * 回调会接收所有事件
     */
    private func onAnyEventHandler (sock: SocketAnyEvent) -> Void {
        //判断接收消息是否带参数
        if let items = sock.items {
            self.bridge.eventDispatcher().sendDeviceEventWithName("socketEvent",
                                                                  body: ["name": sock.event, "items": items])
        } else {
            self.bridge.eventDispatcher().sendDeviceEventWithName("socketEvent",
                                                                  body: ["name": sock.event])
        }
    }
    
    //初始化所有事件
    @objc func onAnyEvent() -> Void {
        self.socket.onAny(self.onAnyEventHandler)
    }
    
    // 连接到socket
    @objc func connect() -> Void {
        self.socket.connect()
    }
    
    // 重新连接到socket
    @objc func reconnect() -> Void {
        self.socket.reconnect()
    }


    @objc func AllOff() -> Void {
        print("AllOff该方法目前ios暂不支持!")
    }

    @objc func off(event:String) -> Void {
        print("AllOff该方法目前ios暂不支持!")
    }
    
    // 关闭socket连接
    @objc func close() -> Void {
        self.socket.disconnect()
    }
}
