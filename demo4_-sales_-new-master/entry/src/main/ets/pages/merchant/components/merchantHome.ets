import router from '@ohos.router';
import promptAction from '@ohos.promptAction'
import { merchantAPI } from '../../../api/merchantApi'
import {
  SalesResType,
  saleRankType,
  SaleRankResType,
  swiperListType,
  warnResType
} from '../../../typeDeclare/responseType'


@Component
export struct merchantHome {
  private swiperController: SwiperController = new SwiperController()
  @State replenishList: swiperListType[] = []
  @State deviceWarnList: swiperListType[] = [
    // {
    //   name: 'string',
    //   number: 'string',
    //   shelf: 'string',
    //   deviceNo: 'kixzybvctfbj-1800739805722574925_smart_warehouse',
    //   status: 'string',
    //   recentReportTime: 'string'
    // }
  ]
  @State titleList: String[] = ['序号', '商品名称', '单价', '销量',]
  @State count: string = '0'
  @State totalAmount: string = '0' //销售额
  @State condition: string = 'desc'
  @State saleSort: saleRankType[] = []
  private scrollerForScroll: Scroller = new Scroller()

  @Styles
  titleSameStyle(){
    .width('100%')
    .margin({ bottom: 12 })
    .borderWidth({ left: 2 })
    .padding({ left: 6 })
    .borderColor(Color.Orange)
    // .fontWeight(600)
  }

  @Styles
  cardSameStyle(){
    .backgroundColor(Color.White)
    .borderRadius(6)
    .padding(10)
    .width('100%')
    .margin({ bottom: 14 })
    .height(100)
  }

  @Builder
  empty() {
    Text('暂无信息')
      .textAlign(TextAlign.Center)
      .fontColor(Color.Gray)
      .fontWeight(700)
      .fontSize(24)
      .width('100%')
      .height(80)
      .borderRadius(6)
      .borderColor('#f4f9fd')
  }

  @Builder
  customNav() {
    Row() {
      Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
        Text('首页').fontSize(19)
        Image($r('app.media.my'))
          .width(20)
          .onClick(() => {
            router.pushUrl({ url: 'pages/merchant/merchantMy' })
          })
      }.width('100%')
    }.position({x:0}).width('100%').height(50).backgroundColor('#f9fafc').padding({ left: 20, right: 20, top: 10, bottom: 10 })
  }

  aboutToAppear() {
    this.getInfo()
    this.getSaleSort()
  }

  build() {
    Navigation() {
      Scroll(this.scrollerForScroll) {
        Column() {
          Column() {
            Text('销售统计').titleSameStyle().fontWeight(600)
            Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
              Flex() {
                Image($r('app.media.app_icon')).width(50).height(50).borderRadius(50).margin({ right: 10 })
                Flex({ direction: FlexDirection.Column, justifyContent: FlexAlign.SpaceBetween }) {
                  Text('订单数量')
                  Text(this.count)
                }.height(50)
              }

              Flex() {
                Image($r('app.media.app_icon')).width(50).height(50).borderRadius(50).margin({ right: 10 })
                Flex({ direction: FlexDirection.Column, justifyContent: FlexAlign.SpaceBetween }) {
                  Text('销售额')
                  Text(this.totalAmount)
                }.height(50)
              }
            }
          }.cardSameStyle()


          Column() {
            Flex({ alignItems: ItemAlign.Center, justifyContent: FlexAlign.SpaceBetween }) {
              Text('销量排行').titleSameStyle().fontWeight(600).width('70%')
              Select([{ value: "top3", }, { value: "bottom3" }])
                .selected(0)
                .value('top3')
                .onSelect((index: number, text?: string | undefined) => {
                  this.condition = text == 'top3' ? 'desc' : 'asc'
                  this.getSaleSort()
                })
            }

            Row() {
              ForEach(this.titleList, (item: string) => {
                Text(item).height(28).width('25%').textAlign(TextAlign.Center)
              }, (item: string) => item)
            }

            if (this.saleSort.length > 0) {

              ForEach(this.saleSort, (item: saleRankType, index: number) => {
                Row() {
                  Text((index + 1).toString()).height(36).width('25%').textAlign(TextAlign.Center)
                  Text(item.name).height(36).width('25%').textAlign(TextAlign.Center)
                  Text(item.price).height(36).width('25%').textAlign(TextAlign.Center)
                  Text(item.number.toString()).height(36).width('25%').textAlign(TextAlign.Center)
                }
              }, (item: saleRankType) => item.name)
            } else {
              this.empty()
            }
          }.cardSameStyle().height(200)

          Column() {
            Text(`补货预警（${this.replenishList.length || 0}）`).titleSameStyle().fontWeight(600)
            if (this.replenishList.length > 0) {
              Swiper(this.swiperController) {
                ForEach(this.replenishList, (item: swiperListType) => {
                  Column() {
                    Flex({ justifyContent: FlexAlign.Start, direction: FlexDirection.Column }) {
                      Text(item.name).height(24).fontWeight(500)
                      Text('所在货架：' + item.shelf).height(24).fontSize(14).fontColor(Color.Gray)
                      Text('剩余库存：' + item.number).height(24).fontSize(14).fontColor(Color.Gray)
                    }
                    .width('90%')
                    .height(80)
                    .padding({ left: 10 })
                    .borderWidth(1)
                    .borderRadius(6)
                    .borderColor('#f4f9fd')
                  }.width('100%')
                }, (item: swiperListType, index: number) => index.toString())
              }
              .index(1)
              .autoPlay(true)
              .interval(4000)
              .loop(true)
              .duration(1000)
              .indicator(true)
              .itemSpace(10)
              .indicatorStyle({
                bottom:4
              }) .height(110)
              // .displayArrow({ // 设置导航点箭头样式
              //   showBackground: false,
              //   arrowSize: 26,
              //   arrowColor: Color.Black
              // }, false) 1
            } else {
              this.empty()
            }
          }.cardSameStyle().height(140)

          Column() {
            Text(`设备异常（${this.deviceWarnList.length || 0}）`).titleSameStyle().fontWeight(600)
            if (this.deviceWarnList.length > 0) {
              Swiper(this.swiperController) {
                ForEach(this.deviceWarnList, (item: swiperListType) => {
                  Column() {
                    Flex({ justifyContent: FlexAlign.Start, direction: FlexDirection.Column }) {
                      Flex({ justifyContent: FlexAlign.SpaceBetween }) {
                        Text(item.name).height(24).fontSize(14)
                        // 设备状态 0 离线 1 在线
                        Text(Number(item.status) ? '在线' : '离线')
                          .height(24)
                          .fontColor(Number(item.status) ? Color.Green : Color.Red)
                          .fontSize(14)
                      }

                      Flex({alignItems:ItemAlign.Center}) {
                        Text('设备编号：').width(100).fontSize(14)
                        Text(`${item.deviceNo}`)
                          .height(24)
                          .fontSize(14)
                          .textOverflow({ overflow: TextOverflow.Ellipsis })
                          .maxLines(1)
                          .flexGrow(1)
                      }


                      Text('最近上报：' + item.recentReportTime).height(24).fontSize(14)
                    }
                    .width('90%')
                    .height(80)
                    .padding({ left: 10, right: 10 })
                    .borderWidth(1)
                    .borderRadius(6)
                    .borderColor('#f4f9fd')
                  }.width('100%')
                }, (item: swiperListType, index: number) => index.toString())
              } .height(110)
              .index(1)
              .autoPlay(true)
              .interval(4000)
              .loop(true)
              .duration(1000)
              .indicator(true)
              .itemSpace(10)
              .indicatorStyle({
                bottom:4
              })
              // .displayArrow({ // 设置导航点箭头样式
              //   showBackground: false,
              //   arrowSize: 26,
              //   arrowColor: Color.Black
              // }, false) 1
            } else {
              this.empty()
            }

          }.cardSameStyle().height(140)

        }.padding(10)
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

  getSaleSort() {
    //销量排行
    merchantAPI.SaleRankApi({ condition: this.condition }).then((res: SaleRankResType) => {
      console.log('销量排行', this.condition , JSON.stringify(res),)
      if (res.success) {
        this.saleSort = []
        this.saleSort = res.data
        this.saleSort.forEach((item, index) => {
          this.saleSort.splice(index, 1, this.saleSort[index]);
        });
      }
    })
  }

  async getInfo() {
    // 销售统计
    merchantAPI.salesApi().then((res: SalesResType) => {
      if (res.success) {
        console.log('销售统计', JSON.stringify(res.data))
        this.totalAmount = res.data.totalAmount
        this.count = res.data.count
      }
    })

    //补货预警
    merchantAPI.replenishWarnApi().then((res: warnResType) => {
      if (res.success) {
        console.log('补货预警', JSON.stringify(res.data))
        this.replenishList = res.data
      }
    })
    //设备异常
    merchantAPI.deviceWarnApi().then((res: warnResType) => {
      if (res.success) {
        console.log('设备异常', JSON.stringify(res.data))
        this.deviceWarnList = res.data
      }
    })
  }
}