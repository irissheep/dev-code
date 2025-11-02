import router from '@ohos.router'
import promptAction from '@ohos.promptAction'
import { homeApi } from '../../api/HomeAPI';
import { loginResType } from '../../typeDeclare/responseType.d'

PersistentStorage.PersistProp('token', ''); //存储登陆token
PersistentStorage.PersistProp('userType',''); //存储用户类别
PersistentStorage.PersistProp('account', ''); //存储用户名
PersistentStorage.PersistProp('pwd', ''); //存储密码
PersistentStorage.PersistProp('userId',''); //存储用户id
PersistentStorage.PersistProp('isConnect',false); //存储用户id
@Entry
@Component
struct Login {
  // 定义变量
  @State userType: string = '1'
  @State account: string = ''
  @State password: string = ''

  onPageShow() {
    if (AppStorage.Get('token')) {
      router.replaceUrl({url:'pages/common/index'})
    }
  }

  build() {
    Column() {
      // logo图标
      Image($r("app.media.logo"))
        .width(80)
        .height(80)
        .borderRadius(50)
        .margin({ bottom: 20 })
      // 客户商户
      Row() {
        Text('客户')
          .fontSize(28)
          .borderWidth({ bottom: this.userType == '1' ? 2 : 0, })
          .margin({ right: 50 })
          .onClick(() => {
            this.userType = '1'
          })
        Text('商户')
          .fontSize(28)
          .borderWidth({ bottom: this.userType == '2' ? 2 : 0, })
          .onClick(() => {
            this.userType = '2'
          })
      }.align(Alignment.Center)

      // 登录账户
      Column() {
        Column() {
          Row() {
            // Text('账户')
            TextInput({ placeholder: '请输入账户' })
              .maxLength(20)
              .inputTextStyle()
              .onChange((value) => { //绑定变量值
                this.account = value;
              })
          }

          Row() {
            // Text('密码')
            TextInput({ placeholder: '请输入密码' })
              .type(InputType.Password)
              .maxLength(20)
              .inputTextStyle()
              .onChange((value) => { //绑定变量值
                this.password = value;
              })
              .onSubmit((enterKey) => {
                this.login() //调用登陆逻辑
              })
          }

        }

        Button('登录').width('100%').height(48)
          .onClick(() => {

            this.login() //调用登陆逻辑

          })
      }.width('80%').margin({ top: 50 })

      Text('新用户注册')
        .fontColor(Color.Blue)
        .margin({ top: 20 })
        .visibility(this.userType == '1' ? Visibility.Visible : Visibility.None)
        .onClick(() => {
          router.pushUrl({
            url: 'pages/login/register',
          })
        })
    }
    .padding({ top: 50 })
    .width('100%')
    .height('100%')
    .backgroundColor('#eee')

  }

  // 方法定义
  login() {
    if (this.account.length == 0 || this.password.length == 0) {
      promptAction.showToast({ message: '用户名或者密码不可为空！', duration: 2000 })
      return
    }

    interface paramsType {
      account: string
      password: string
      category: string
    }

    let data: paramsType = {
      account: this.account,
      password: this.password,
      category: this.userType
    }

    // api发请求
    homeApi.doLoginApi(data).then((loginResult: loginResType) => {

      console.info('loginResult' + JSON.stringify(loginResult))
      //登陆成功
      if (loginResult.success) {
        AppStorage.SetOrCreate('userType', loginResult.data.category); //存储用户类别
        AppStorage.SetOrCreate('account', loginResult.data.account); //存储用户信息
        AppStorage.SetOrCreate('pwd',this.password);
        AppStorage.SetOrCreate('userId', loginResult.data.id); //存储用户id
        AppStorage.SetOrCreate('token', loginResult.data.token); //存储登陆token
        // console.log('登陆成功', AppStorage.Get('token'))
        //  跳转到主页
        router.replaceUrl({
          url: 'pages/common/index',
        })
      } else {
        promptAction.showToast({ message: loginResult.msg, duration: 2000 })
      }
    })
      .catch((err: Error) => {
        console.info('login error' + JSON.stringify(err))
        promptAction.showToast({ message: `系统错误，登陆失败(error:${err.message})`, duration: 2000 })
      })

  }
}

// @Extend仅支持定义在全局，不支持在组件内部定义。
@Extend(TextInput)
function inputTextStyle() {
  .backgroundColor(Color.White)
  .height(48)
  .margin({ bottom: 20 })
  .borderRadius(10)
}
