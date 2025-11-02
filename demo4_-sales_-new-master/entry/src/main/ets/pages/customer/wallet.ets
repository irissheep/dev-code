// 账户余额
import router from '@ohos.router';
import promptAction from '@ohos.promptAction'
import { homeApi } from '../../api/HomeAPI';
import  {myBalanceResType,responseType} from '../../typeDeclare/responseType.d'
interface  userType{
  userId:string,
  rechargeAmount?:string
}
let userId:string = AppStorage.Get('userId') || ''

@Entry
@Component
struct wallet {
  @State balance: string = '0'
  @State accountId: string = ''
  @State money: string = ''

  onPageShow() {
    this.getBalance()
  }

  @Styles sameStyle(){
    .padding(8)
    .width('96%')
    .backgroundColor('#fff')
    .margin({ bottom: 14 })
  }

  @Builder customNav() {
    Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
      Row(){
        Image($r('app.media.back')).width(20).margin({right:14}).onClick(()=>{
          //router.back()
          router.replaceUrl({url:'pages/common/index',params:{tabIndex:'2'}})
        })
        Text('余额充值').fontSize(19).fontWeight(500)
      }
      Text('充值记录')
        .onClick(() => {
          router.pushUrl({
            url: 'pages/customer/rechargeRecords',
            params: { accountId: this.accountId }
          })
        })
     }.position({x:0}).width('100%').padding({ left: 20, right: 20, top: 10, bottom: 10 }).height(50).backgroundColor('#f9fafc')

}

  build() {
    Column() {
      Navigation() {
        Row() {
          Flex({ justifyContent: FlexAlign.SpaceBetween }) {
            Text('我的余额')
            Text('￥' + this.balance)
          }
        }
        .sameStyle()

        Column() {
          Text('充值金额').margin({ bottom: 10 })
          Row() {
            Text('￥')
            Row() {
              TextInput({ text: this.money, placeholder: '请输入' })
                .type(InputType.Number)
                .backgroundColor('#fff')
                .width(300).onChange((value: string) => {
                this.money = value
              })
            }.width(300).borderWidth({ bottom: 1 })

          }.width('100%')
        }
        .alignItems(HorizontalAlign.Start)
        .sameStyle()

        Button('确认充值').onClick(() => {
          if (this.money) {
            this.recharge()
          }
        })
      }
      .title({ builder: this.customNav, height: 50 })
      .mode(NavigationMode.Auto)
      .titleMode(NavigationTitleMode.Mini)
      .hideBackButton(true)
    }.width('100%').height('100%').backgroundColor('#f4f9fd')
  }
  // 充值
  recharge() {

    const data: userType={
      userId, rechargeAmount: this.money
    }
    homeApi.rechargeApi(data).then((res:responseType) => {
      console.log('res',JSON.stringify(res))
      if (res.success) {
        promptAction.showToast({
          message: '充值成功',
          duration: 2000,
          bottom: 100
        });
        this.getBalance()
        setTimeout(() => {
          // router.back()

          router.replaceUrl({url:'pages/common/index',params:{tabIndex:'2'}})
        }, 2000)
      }
    })
  }
  getBalance() {
    const data: userType={
      userId,
    }
    homeApi.getBalanceApi(data).then((res:myBalanceResType) => {
      if(res.success){
        this.balance =  res.data.balance || '0'
        this.accountId = res.data.id || ''
      }
    })
  }
}