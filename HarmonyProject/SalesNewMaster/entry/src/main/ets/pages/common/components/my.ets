import router from '@ohos.router';
import promptAction from '@ohos.promptAction'
import { homeApi } from '../../../api/HomeAPI';
import { userType, myBalanceResType, myResType } from '../../../typeDeclare/responseType.d'
import SocketModel from '../../../model/socket'

interface listType {
  title: string,
  path: string
  icon: Resource
}

interface cardListItemType {
  title: string
  icon: string
  path: string
}

interface paramsType {
  id: string
}


let list: Array<listType> = [
  {
    title: '身份识别',
    path: 'pages/login/facial',
    icon: $r('app.media.listCard')
  },
  {
    title: '购买记录',
    path: 'pages/customer/expendRecords',
    icon: $r('app.media.listRecords')
  },
  {
    title: '账户余额',
    path: 'pages/customer/wallet',
    icon: $r('app.media.listWallet')
  },
  {
    title: '修改密码',
    path: "pages/customer/editPwd",
    icon: $r('app.media.listEditPwd')
  },
]

@Component
export struct my {
  @State userId: string = AppStorage.Get('userId') || ''
  @State userType: string = '1'
  @State cardList: listType[] = []
  @State balance: string = '0'
  @State accountId: string = ''
  @State userInfo: userType = {
    id: '',
    account: '',
    avatar: '',
    name: '',
    isCertified: 0
  }
  @Prop @Watch('watchPageShow') pageShow: boolean;

  // @Watch 回调
  watchPageShow() {
    console.log('watchPageShow', this.pageShow)
    this.getUserInfo()
    this.getBalance()

  }

  aboutToAppear() {

    console.log('z组件', this.pageShow)
    this.getUserInfo()
    this.getBalance()
    this.userType = AppStorage.Get('userType') || ''
    if (this.userType == '1') {
      this.cardList = list
    } else {
      this.cardList = [
        {
          title: '设备管理',
          path: "pages/merchant/deviceManage",
          icon: $r('app.media.shebei')
        },
        {
          title: '通知消息',
          path: "pages/merchant/merchantNotice",
          icon: $r('app.media.notice1')
        },
        {
          title: '修改密码',
          path: "pages/customer/editPwd",
          icon: $r('app.media.listEditPwd')
        }
      ]
    }
  }

  @Builder
  customNav() {
    Row() {
      if (this.userType == '2') {
        Image($r('app.media.back')).width(20).margin({ right: 14 }).onClick(() => {
          router.back()
        })
      }
      Text('个人中心').fontSize(19)
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
        Flex({ alignItems: ItemAlign.Center }) {
          Image(this.userInfo.avatar || $r('app.media.my')).width(80)
            .height(80)
            .borderRadius(50)
            .onClick(() => {
              router.pushUrl({ url: 'pages/customer/editAvatar', })
            })
          Image($r('app.media.edit1')).width(20).position({ x: 70, y: 58 }).onClick(() => {
            router.pushUrl({ url: 'pages/customer/editAvatar', })
          })
          Column() {
            Text(this.userInfo.account || '-').textAlign(TextAlign.Start)
          }
          .alignItems(HorizontalAlign.Start)
          .margin({ left: 20 })
        }.width('100%').backgroundColor('#ffffff').padding(10).margin({ bottom: 20 })

        // 列表
        List() {
          ForEach(this.cardList, (item: cardListItemType) => {
            ListItem() {
              Row() {
                Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
                  Row() {
                    Image(item.icon).width(26).margin({ right: 6 })
                    Text(item.title)
                  }

                  Row() {
                    if (item.title == '身份识别') {
                      Text(this.userInfo.isCertified == 1 ? '已认证' : '未认证')
                    }
                    if (item.title == '账户余额') {
                      Text('￥' + this.balance)
                    }
                    if (item.title == '身份识别') {
                      if (this.userInfo.isCertified != 1) {
                        Image($r('app.media.youjiantou')).width(20)
                      }
                    } else {
                      Image($r('app.media.youjiantou')).width(20)
                    }
                  }
                }
              }
              .width('100%')
              .height(50)
              .borderWidth({ bottom: 1 })
              .borderColor('gray')
              .padding({ left: 10, right: 10 })
              .backgroundColor('#ffffff')
              .onClick(() => {
                if (item.title == '身份识别' && this.userInfo.isCertified == 1) {
                  return
                }
                router.pushUrl({ url: item.path, })
              })
            }
          }, (item: object, index: number) => '' + index)
        }

        Button('退出登录').width(200).margin({ top: 20 }).onClick(() => {
          this.logout()
        })
      }
    }
    .title({ builder: this.customNav, height: 50 })
    .mode(NavigationMode.Auto)
    .titleMode(NavigationTitleMode.Mini)
    .hideBackButton(true)
    .width('100%')
    .height('100%')
    .backgroundColor('#f4f9fd')
  }

  getUserInfo() {

    const data: paramsType = { id: this.userId }
    console.log('请求参数data', JSON.stringify(data))
    homeApi.getUserInfoApi(data).then((res: myResType) => {
      if (res.success) {
        this.userInfo = res.data
        this.userInfo.isCertified = res.data.isCertified || 0
        this.userInfo.avatar = res.data.avatar || ''
        AppStorage.SetOrCreate('userInfo', JSON.stringify(res.data))
      }
      console.log('用户信息my页面', JSON.stringify(res.data))
    })
  }

  getBalance() {

    const data = { userId: this.userId }
    homeApi.getBalanceApi(data).then((res: myBalanceResType) => {
      if (res.success) {
        this.balance = res.data.balance || '0'
        // console.log('余额',JSON.stringify(res) )
      }
    })
  }

  async logout() {
    const data: paramsType = { id: this.userId }
    const res: object = await homeApi.loginOutApi(data)
    const socketModel: SocketModel = new SocketModel();
    // if (res.success) {
    // 退出登录，清除缓存
    AppStorage.SetOrCreate('userType', '1')
    AppStorage.SetOrCreate('account', '')
    AppStorage.SetOrCreate('userId', '')
    AppStorage.SetOrCreate('token', '')
    AppStorage.SetOrCreate('pwd', '')
    const isc = AppStorage.Get('isConnect');
    console.log('isc', isc);
    if(AppStorage.Get('isConnect') == 'true'){
      socketModel.close()
    }
    // AppStorage.SetOrCreate('userInfo', '')
    router.replaceUrl({ url: 'pages/login/login' })
    router.clear()
    // } else {
    //   promptAction.showToast({ message: '退出失败，请重试', duration: 2000 })
    // }
  }

}