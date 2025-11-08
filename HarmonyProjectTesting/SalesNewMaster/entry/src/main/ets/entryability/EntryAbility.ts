import UIAbility from '@ohos.app.ability.UIAbility';
import hilog from '@ohos.hilog';
import window from '@ohos.window';
import SocketModel from '../model/socket'


export default class EntryAbility extends UIAbility {
  onCreate(want, launchParam) {
    const socketModel: SocketModel = new SocketModel();
    if(AppStorage.Get('isConnect')){socketModel.disConnect()}
    AppStorage.SetOrCreate('isConnect', 'false')
    
    // 配置后端服务器地址
    // 如果AppStorage中没有BASE_URL，则使用默认值
    // 本地开发：'http://localhost:9999' 或 'http://10.0.2.2:9999' (Android模拟器)
    // 远程服务器：'http://42.193.243.96:9999'
    // 真机连接：使用电脑IP，例如 'http://192.168.1.100:9999'
    if (!AppStorage.Get('BASE_URL')) {
      // 配置后端服务器地址
      // 注意：
      // - Android模拟器：使用 http://10.0.2.2:9999（当前配置）
      // - 本地浏览器调试：使用 http://localhost:9999
      // - 真机调试：使用电脑IP地址，例如 http://192.168.1.100:9999（需要替换为你的实际IP）
      // - 远程服务器：使用 http://42.193.243.96:9999
      
      // 当前配置：Android模拟器（10.0.2.2是Android模拟器访问宿主机的特殊IP）
      AppStorage.SetOrCreate('BASE_URL', 'http://10.0.2.2:9999')
      
      // 如果需要使用其他配置，请注释掉上面这行，取消注释下面对应的配置：
      // 本地浏览器调试：
      // AppStorage.SetOrCreate('BASE_URL', 'http://localhost:9999')
      // 真机调试（替换为你的电脑IP）：
      // AppStorage.SetOrCreate('BASE_URL', 'http://192.168.1.100:9999')
      // 远程服务器：
      // AppStorage.SetOrCreate('BASE_URL', 'http://42.193.243.96:9999')
      
      console.log('已设置默认后端地址（Android模拟器）:', AppStorage.Get('BASE_URL'))
    } else {
      console.log('使用已配置的后端地址:', AppStorage.Get('BASE_URL'))
    }
    
    console.log('应用创建')

    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onCreate');
  }

  onDestroy() {

    console.log('应用销毁')
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onDestroy');
  }

  onWindowStageCreate(windowStage: window.WindowStage) {
    console.log('应用 onWindowStageCreate')
    // Main window is created, set main page for this ability
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onWindowStageCreate');

    windowStage.loadContent('pages/login/login', (err, data) => {
      if (err.code) {
        hilog.error(0x0000, 'testTag', 'Failed to load the content. Cause: %{public}s', JSON.stringify(err) ?? '');
        return;
      }


      console.log('应用 实现对页面的加载')
      hilog.info(0x0000, 'testTag', 'Succeeded in loading the content. Data: %{public}s', JSON.stringify(data) ?? '');
    });
  }

  onWindowStageDestroy() {

    console.log('实例销毁之前',AppStorage.Get('isConnect'))

    // Main window is destroyed, release UI related resources
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onWindowStageDestroy');
  }

  onForeground() {
    console.log('应用进入前台')
    // Ability has brought to foreground
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onForeground');

  }

  onBackground() {
    console.log('应用进入后台')
    // Ability has back to background
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onBackground');
  }
}
