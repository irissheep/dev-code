import promptAction from '@ohos.promptAction'
import router from '@ohos.router'
import { homeApi } from '../../../api/HomeAPI'
import { PageParamsType, consumeListItemType, consumeResType } from '../../../typeDeclare/responseType.d'
import { loadStatusCom } from './loadStatusCom'

@Component
export struct consumeRecords {
  @State refresh: boolean = false
  @State pageParams: PageParamsType = {
    size: 10,
    current: 1,
    total: 0
  };
  @State loadStatus: string = 'loadMore'
  @State userType: string = ''
  @State list: consumeListItemType[] = []
  @State keywords: string = ''
  @Prop @Watch('watchPageShow') pageShow: boolean;

  // @Watch 回调
  watchPageShow() {
    this.userType = AppStorage.Get('userType') || ''
    this.pageParams.current = 1
    this.list = []
    this.getList()

  }

  aboutToAppear() {
    this.userType = AppStorage.Get('userType') || ''
    this.pageParams.current = 1
    this.list = []
    this.getList()
  }

  @Builder
  customNav() {
    Row() {
      if (this.userType == '1') {
        Image($r('app.media.back')).width(20).margin({ right: 14 }).onClick(() => {
          // router.back()
          router.replaceUrl({ url: 'pages/common/index', params: { tabIndex: '2' } })
        })
        Text('购买记录').fontSize(19)
      } else {
        Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {

          Text('订单列表').fontSize(19)
          Image($r('app.media.my'))
            .width(20)
            .onClick(() => {
              router.pushUrl({ url: 'pages/merchant/merchantMy' })
            })
        }
      }
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
        Refresh({ refreshing: $$this.refresh, offset: 50 }) {
          Column() {
            if (this.userType == '2') {
              Search({ value: this.keywords, placeholder: '输入关键字搜索' })
                .searchButton('搜索')
                .height(40)
                .placeholderFont({ size: 14, weight: 400 })
                .textFont({ size: 14, weight: 400 })
                .onSubmit((value: string) => {
                  this.keywords = value
                  this.search()
                })
                .onChange((value) => {
                  if (!value) {
                    this.keywords = ''
                    this.search()
                  }
                })
                .margin({ bottom: 20 })
            }
            List({ space: 20, initialIndex: 0 }) {
              ForEach(this.list, (item) => {
                ListItem() {
                  Column() {
                    Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
                      Row() {
                        Text('订单号：' + item.billNo).fontSize(14)
                        Text(item.action == '1' ? '取走' : '退回')
                          .borderWidth(1)
                          .borderRadius(8)
                          .borderColor(Color.Gray)
                          .fontColor(Color.Gray)
                          .padding({ left: 4, right: 4 })
                          .margin({ left: 6 })
                          .fontSize(12)
                      }

                      if (this.userType == '1') {
                        Text('已完成').fontSize(14)
                      } else {
                        Text(item.account || '').fontSize(14)
                      }
                    }.margin({ bottom: 10 })

                    Flex() {
                      Column() {
                        Image(item.productList?.[0].pictureUrl)
                          .width(60)
                          .height(60)
                        // .objectFit(ImageFit.ScaleDown)
                        Row() {
                          Text('x ' + item.totalNumber)
                            .fontColor(Color.White)
                            .fontSize(9)
                        }
                        .position({ x: 46, y: 46 })
                        .backgroundColor('gray')
                        .borderRadius(4)
                        .width(14)
                        .height(14)

                      }.width(60).height(60)

                      Flex({ direction: FlexDirection.Column, justifyContent: FlexAlign.SpaceBetween }) {
                        Flex({ justifyContent: FlexAlign.SpaceBetween }) {
                          Column() {
                            Text(item.productList[0].name || '暂无名称').fontSize(14).margin({ bottom: 8 })
                            Text('￥' + item.totalAmount || '0').fontSize(14)
                          }.flexGrow(1)
                          .align(Alignment.Start)
                          .alignItems(HorizontalAlign.Start)

                          if (this.userType == '2') { ////1 已完成，2 异常退回，3 异常取走"
                            if (item.status == '1') {
                              Text("已完成").fontSize(14)
                            } else if (item.status == '2') {
                              Text("异常退回").fontSize(14)
                            } else if (item.status == '3') {
                              Text("异常取走").fontSize(14)
                            } else {
                              Text("未知")
                            }
                          }
                        }

                        Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.End }) {
                          Text(item.createTime).fontColor(Color.Gray).fontSize(12)
                          if (this.userType == '1') {
                            // Text('余额' + item.balance + '元').fontColor(Color.Gray).fontSize(10)
                            Text('')
                          } else if (this.userType == '2' && item.status != '1') {
                            Button('处置')
                              .fontSize(12)
                              .width(58)
                              .height(22)
                              .onClick(() => {
                                router.pushUrl({
                                  url: 'pages/merchant/abnormalHandling',
                                  params: { id: item.id, from: "billList" }
                                })
                              })
                          }
                        }
                      }
                      .flexGrow(1)
                      .margin({ left: 10 })
                    }.height(60).width('100%')
                  }
                  .backgroundColor('#fff')
                  .width('100%')
                  .padding(10)
                  .borderRadius(4)
                }
              }, (item: object, index: number) => index.toString())
              ListItem() { // 加载更多布局
                loadStatusCom({ loadStatus: this.loadStatus, refresh: $refresh })
              }
            }
            .width('100%')
            .height('100%')
            .listDirection(Axis.Vertical) // 排列方向
            .onReachEnd(() => {
              if (this.loadStatus != 'loadMore') return
              this.pageParams.current++
              this.getList()
            })
          }
          .width('100%')
          .height('100%')
        }
        .onStateChange((refreshStatus: RefreshStatus) => {
          if (refreshStatus == 3) {
            this.pageParams.current = 1
            this.list = []
            this.getList()
          } else if (refreshStatus == 4) {
            this.refresh = false
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

  search() {
    this.pageParams.current = 1
    this.list = []
    this.getList()
  }

  getList() {
    this.loadStatus = 'loading' // 加载中...
    interface paramsType extends PageParamsType {
      userId: string
      keyword: string
    }

    const params: paramsType = {
      size: this.pageParams.size,
      current: this.pageParams.current,
      userId: AppStorage.Get('userId') || '',
      keyword: this.keywords
    }

    homeApi.consumeRecordApi(params).then((res: consumeResType) => {
      console.log('订单列表', res.data.total)
      if (res.success) {
        this.list = this.list.concat(res.data.records)
        if (res.data.total <= this.list.length) {
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