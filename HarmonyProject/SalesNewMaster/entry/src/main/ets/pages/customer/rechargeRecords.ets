import promptAction from '@ohos.promptAction'
import { homeApi } from '../../api/HomeAPI'
import router from '@ohos.router';
import { PageParamsType, recordItemType, rechargeResType } from '../../typeDeclare/responseType.d'
import { loadStatusCom } from '../common/components/loadStatusCom'


interface paramsType {
  accountId: string
}

@Entry
@Component
struct recharge {
  @State refresh: boolean = false
  @State accountId: string = (router.getParams() as paramsType).accountId
  @State recordsList: object[] = []
  @State loadStatus: string = 'loadMore'
  @State pageParams: PageParamsType = {
    current: 1,
    size: 20,
    total: 0,
  };
  controller?: CustomDialogController
  private listScroller: Scroller = new Scroller();

  aboutToAppear() {
    // 调接口获取商品列表
    this.pageParams.current = 1
    this.recordsList = []
    this.getList()
  }

  build() {
    Column() {
      Navigation() {
        Column() {
          List({ space: 0, scroller: this.listScroller }) {
            ForEach(this.recordsList, (item: recordItemType) => {
              ListItem() {
                Column() {
                  Flex({ justifyContent: FlexAlign.SpaceBetween }) {
                    Text(item.createTime)
                    Text('+'+item.rechargeAmount)
                  }
                }
                .backgroundColor('#fff')
                .width('100%')
                .padding(10)
                .borderRadius(4)

              }.borderWidth({ bottom: 1 }).borderColor('gray')
            }, (item: recordItemType) => item.id)
            ListItem() { // 加载更多布局
              loadStatusCom({ loadStatus: this.loadStatus, refresh: $refresh })
            }
          }.width('100%')
          .height('100%')
          .onReachEnd(() => {
            if (this.loadStatus != 'loadMore') return
            this.pageParams.current++
            this.getList()
          })
        }

        .padding({ left: 10, right: 10 })
      }
      .mode(NavigationMode.Auto)
      .title('充值记录')
      .titleMode(NavigationTitleMode.Mini)
    }.width('100%').height('100%').backgroundColor('#f4f9fd')

  }

  getList() {
    this.loadStatus = 'loading'
    interface paramsType extends PageParamsType {
      accountId: string
    }

    const params: paramsType = {
      size: this.pageParams.size,
      current: this.pageParams.current,
      accountId: this.accountId
    }
    console.log('accountId',JSON.stringify(params))
    homeApi.rechargeRecordApi(params).then((res: rechargeResType) => {
      if (res.success) {
        this.recordsList = this.recordsList.concat(res.data.records)

        console.log('充值记录total',res.data.total, this.recordsList.length)
        if (res.data.total <= this.recordsList.length) {
          this.loadStatus = 'noMore'
        } else {
          this.loadStatus = 'loadMore'
        }
      } else {
        this.loadStatus = 'loadFailed'
        promptAction.showToast({
          duration: 3000,
          message: res.msg
        })
      }
      this.refresh = false

    })
  }
}