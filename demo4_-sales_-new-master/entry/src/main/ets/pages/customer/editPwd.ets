import router from '@ohos.router'
import promptAction from '@ohos.promptAction'
import { homeApi } from '../../api/HomeAPI';
import { responseType } from '../../typeDeclare/responseType.d'

@Entry
@Component
struct editPwd {
  @State oldPassword: string = ''
  @State password: string = ''
  @State confirmPwd: string = ''

  @Styles
  sameStyle(){
    .width('100%')
    .height(50)
    .borderWidth({ bottom: 1 })
    .borderColor('gray')
    .padding({ left: 10, right: 10 })
    .backgroundColor('#ffffff')
  }

  @Builder
  customNav() {
    Row() {
      Image($r('app.media.back')).width(20).margin({ right: 14 }).onClick(() => {
        if (AppStorage.Get('userType') == '1') {
          router.replaceUrl({ url: 'pages/common/index', params: { tabIndex: '2' } })
        } else {
          router.back()
        }
      })
      Text('修改密码').fontSize(19)
    }
    .position({ x: 0 })
    .width('100%')
    .padding({ left: 20, right: 20, top: 10, bottom: 10 })
    .height(50)
    .backgroundColor('#f9fafc')
  }

  build() {
    Navigation() {
      Column() {
        Row() {
          Flex({ alignItems: ItemAlign.Center }) {
            Text('*').fontColor(Color.Red)
            Text('原密码').width(100)
            TextInput({ text: this.oldPassword, placeholder: '请输入' })
              .backgroundColor('#fff')
              .type(InputType.Password)
              .onChange((value) => { //绑定变量值
                this.oldPassword = value;
              })
          }
        }.sameStyle()

        Row() {
          Flex({ alignItems: ItemAlign.Center }) {
            Text('*').fontColor(Color.Red)
            Text('新密码').width(100)
            TextInput({ text: this.password, placeholder: '请输入' }).backgroundColor('#fff').type(InputType.Password)
              .onChange((value) => { //绑定变量值
                this.password = value;
              })
          }
        }.sameStyle()

        Row() {
          Flex({ alignItems: ItemAlign.Center }) {
            Text('*').fontColor(Color.Red)
            Text('确认密码').width(100)
            TextInput({ text: this.confirmPwd, placeholder: '请输入' })
              .backgroundColor('#fff')
              .type(InputType.Password)
              .onChange((value) => { //绑定变量值
                this.confirmPwd = value;
              })
          }
        }.sameStyle().margin({ bottom: 20 })

        Button('保存').width(100).onClick(() => {
          if (this.oldPassword.length > 0 && this.password.length > 0 && this.confirmPwd.length > 0) {
            this.edit()
          } else {
            promptAction.showToast({ message: '请填写完整信息！', duration: 2000 })
          }
        })
      }.padding(10)
    }
    .mode(NavigationMode.Auto)
    .title({ builder: this.customNav, height: 50 })
    // .titleMode(NavigationTitleMode.Mini)
    .hideBackButton(true)
    .width('100%')
    .height('100%')
    .backgroundColor('#f4f9fd')

  }

  edit() {
    interface paramsType {
      id: string
      oldPassword: string
      password: string
      confirmPwd: string
    }

    const data: paramsType = {
      id: AppStorage.Get('userId') || '',
      oldPassword: this.oldPassword,
      password: this.password,
      confirmPwd: this.confirmPwd,
    }

    homeApi.editPwdApi(data).then((res: responseType) => {
      promptAction.showToast({ message: res.msg })
      if (res.success) {
        setTimeout(() => {
          AppStorage.SetOrCreate('userType', '1')
          AppStorage.SetOrCreate('account', '')
          AppStorage.SetOrCreate('userId', '')
          AppStorage.SetOrCreate('token', '')
          AppStorage.SetOrCreate('pwd', '')
          router.replaceUrl({
            url: 'pages/login/login'
          })
          router.clear()
        }, 2000)
      }
    })
  }
}