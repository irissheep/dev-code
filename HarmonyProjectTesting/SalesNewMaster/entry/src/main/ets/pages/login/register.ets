import router from '@ohos.router'
import promptAction from '@ohos.promptAction'
import { homeApi } from '../../api/HomeAPI';
import { registerResType,loginResType} from '../../typeDeclare/responseType.d'
@Entry
@Component
struct Register {
  @State account: string = ''
  @State password: string = ''
  @State confirmPwd: string = ''

  build() {

    Column() {
      Navigation() {
        // logo图标
        Image($r("app.media.logo"))
          .width(80)
          .height(80)
          .borderRadius(50)
          .margin({ bottom: 20 ,top: 20})
        Column() {
          Column() {
            TextInput({ placeholder: '请输入账户' })
              .maxLength(20)
              .inputTextStyle()
              .onChange((value) => { //绑定变量值
                this.account = value;
              })

            TextInput({ placeholder: '请输入密码' })
              .type(InputType.Password)
              .maxLength(20)
              .inputTextStyle()
              .onChange((value) => { //绑定变量值
                this.password = value;
              })
            TextInput({ placeholder: '请输入确认密码' })
              .type(InputType.Password)
              .maxLength(20)
              .inputTextStyle()
              .onChange((value) => { //绑定变量值
                this.confirmPwd = value;
              })


          }

          Button('注册').width('100%').height(48)
            .onClick(() => {
              if (this.account.length > 0 && this.password.length > 0 && this.confirmPwd.length > 0) {
                this.register() //调用注册逻辑
              } else {
                promptAction.showToast({ message: '请完整填写表单！', duration: 2000 })
              }
            })
        }.width('80%').margin({ top: 50 })
      }
      .mode(NavigationMode.Auto)
      .title('用户注册')
      .titleMode(NavigationTitleMode.Mini)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#eee')

  }
  // 注册用户
  register() {
    interface  paramsType{
      account:string
      password: string
      confirmPwd: string
      category:string
    }
    let data:paramsType = {
      account: this.account,
      password: this.password,
      confirmPwd: this.confirmPwd,
      category:'1'
    }



    homeApi.registerApi(data).then((loginResult:registerResType)=>{
      if(loginResult.success){
        promptAction.showToast({message:'注册成功',duration:2000})

        homeApi.doLoginApi(data).then((loginResult:loginResType)=>{
          //登陆成功
          if(loginResult.success){
            AppStorage.SetOrCreate('userType',loginResult.data.category)    //存储用户类别
            AppStorage.SetOrCreate('account',loginResult.data.account)   //存储用户名
            AppStorage.SetOrCreate('pwd',this.password);
            AppStorage.SetOrCreate('userId',loginResult.data.id)
            AppStorage.SetOrCreate('token',loginResult.data.token)//存储登陆token

          }else{
            promptAction.showToast({message:loginResult.msg,duration:2000})
          }
        })
        //  跳转到人脸识别
        setTimeout(()=>{
          router.pushUrl({
            url: 'pages/login/facial',
            params: data
          })
        },1500)
      }else{
        promptAction.showToast({message:loginResult.msg,duration:2000})
      }
    })
      .catch((err:Error)=>{
        console.info('login error'+JSON.stringify(err))
        promptAction.showToast({message:`系统错误，请稍后再试`,duration:2000})
      })

  }
}

@Extend(TextInput) function inputTextStyle() {
  .backgroundColor(Color.White)
  .height(48)
  .margin({ bottom: 20 })
  .borderRadius(10)
}
