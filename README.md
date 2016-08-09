# react-native-socket.io
#### react-native-socket.io使用原生socket.io库进行封装,同时支持ios和android平台
### 导出方法
- `connect` - 提交到socket  支持android ios
- `on` - 添加事件到socket  支持android ios
    - `@param` - String - 事件名称
    - `@param` - Function - 回调函数
- `emit` - 发送数据给服务器  支持android ios
    - `@param` - String - 事件名称
    - `@param` - Anything - 要传递的数据
- `close` - 断开socket连接  支持android ios
- `reconnect` - 重新连接socket  支持ios
- `joinNamespace` - 加入到命名空间  支持ios
	 - `@param` - String - 命名空间名称
- `leaveNamespace` - 取消命名空间  支持ios
- `off` - 取消某个事件监控  支持android
	- `@param` - String - 事件名称
- `AllOff` - 取消所有事件监控  支持android

## npm install react-native-socket-io

### 使用实例
    var SocketIO = require('react-native-socket-io');

    componentDidMount(){

        // 配置项
        var socketConfig = {};
        //连接socket
        var socket = new SocketIO('http://zhg.zhuyousoft.com:2120', socketConfig);


        socket.on('connect', () => {
          console.log('连接成功');
        });

        // 接收来自服务器的事件监听
        socket.on('someEvent', (data) => {
          console.log('Some event was called, check out this data: ', data);
        });

        // 提交
        socket.connect();

        // // 发送数据给服务器
        // socket.emit('helloWorld', {some: 'data'});
        //
        // // 断开socket连接
        // socket.close();

    }



###android配置
1. 设置 `android/setting.gradle`

    ```
    ...
	include ':reactnativesocketio'
	project(':reactnativesocketio').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-socket-io/android/reactnativesocketio')

    ```

2. 设置 `android/app/build.gradle`

    ```
    ...
    dependencies {
        ...
        compile project(':reactnativesocketio')
    }
    ```
    
3. 注册模块 (到 MainApplication.java)

    ```
    import com.example.reactnativesocketio.SocketPackage;  // <--- 导入

    public class MainApplication extends Application implements ReactApplication {
      ......

        @Override
    	protected List<ReactPackage> getPackages() {
      		return Arrays.<ReactPackage>asList(
          			new MainReactPackage(),
          			new SocketPackage()      //<--- 添加
      		);
    	} 

      ......

    }
    ```

###ios配置
####打开该目录 .../node_modules/react-native-socket-io/ios
![Mou icon1](/assets/a1.png)

   ```
   ../node_modules/react-native-socket-io/ios/RNSocket/SocketBridge.h
   
   ```
![Mou icon1](/assets/a2.png)






