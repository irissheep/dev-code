import webSocket from '@ohos.net.webSocket';
import promptAction from '@ohos.promptAction';
import notificationManager from '@ohos.notificationManager';

// let socket: webSocket.WebSocket = webSocket.createWebSocket() //创建一个WebSocket连接。
let socket = webSocket.createWebSocket();

interface resType {
  createTime: string
  id: string
  msg: string
  title: string
}

export default class socketClass {
  private isConnect: string = 'false'
  private eventHandleBinded: boolean = false
  private ipAddress: string = ''
  // public  pushMsgHandle: (str) => void = undefined;

  constructor() {
    const userId: string = AppStorage.Get('userId') || ''
    this.ipAddress = 'ws://42.193.243.96:9999/websocket/' + userId
    this.isConnect = AppStorage.Get('isConnect') || 'false'
  }

  // 连接
  async onConnect() {
    console.log('连接状态2', AppStorage.Get('isConnect'), this.isConnect)
    if (AppStorage.Get('isConnect') == 'false') {
      socket.connect(this.ipAddress, (err, value) => {
        if (!err) {
          this.isConnect = 'true'
          AppStorage.SetOrCreate('isConnect', 'true')
          console.info(`AppStorage -isConnect 连接成功`, AppStorage.Get('isConnect'));
          this.bindEventHandle()
        } else {
          promptAction.showToast({
            message: '连接失败'
          })
        }
      });
    }
  }

  bindEventHandle() {
    //如果已绑定就退出
    if (this.eventHandleBinded) {
      return
    }
    socket.on('message', (err, str: string) => { //订阅WebSocket的接收到服务器消息事件
      console.info(`接收消息str`, str)
      // str = '{"createTime":"2024-08-12 15:56:21","id":1822904945561772033,"msg":"测试：您的商品：方便面 库存量已不足，请及时补货！","title":"补货预警","userId":"1806202507931783169"}'
      const o = JSON.parse(str)
      //通知Request对象
      let notificationRequest = {
        id: 1,
        content: {
          contentType: notificationManager.ContentType.NOTIFICATION_CONTENT_BASIC_TEXT,
          normal: {
            title: o.title || '消息类型',
            text: o.msg || "消息内容",
            additionalText: ""
          }
        }
      };
      notificationManager.publish(notificationRequest, (err) => {}); //发布通知，仅在模拟器上生效，真机无推送
    })

    socket.on('close', (err, value) => {
      console.log("关闭 连接on close, code is " + value.code + ", reason is " + value.reason);
      this.disConnect()
    });
    socket.on('error', (err) => {
      console.log("错误 连接on error, error:" + JSON.stringify(err));
      this.close()
    });
    this.eventHandleBinded = true
  }

  close() {
    socket.close();
  }
  // 断开连接
  disConnect() {
    if (this.isConnect == 'false') {
      return;
    }
    socket.off('message');
    socket.off('close');
    socket.off('error');
    this.isConnect = 'false'
    console.log('socket关闭成功 连接')
    AppStorage.SetOrCreate('isConnect', 'false');
    this.eventHandleBinded = false
  }


  // 回调函数
  // setCallback(callback): void {
  //   this.pushMsgHandle = callback;
  // }
}