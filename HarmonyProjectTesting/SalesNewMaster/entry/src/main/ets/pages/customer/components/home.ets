import { customerAPI } from '../../../api/customerApi'
import { PageParamsType, goodsItemType, goodsResType } from '../../../typeDeclare/responseType.d'
import promptAction from '@ohos.promptAction'
import { loadStatusCom } from '../../common/components/loadStatusCom'


@Component
export struct home {
  @State refresh: boolean = false
  // @Link装饰的变量与其父组件中的数据源共享相同的值。
  @State goodsList: goodsItemType[] = []
  @State loadStatus: string = 'loadMore'
  @State pageParams: PageParamsType = {
    size: 10,
    current: 1,
    total: 0
  };
  scroller: Scroller = new Scroller()

  @Builder
  customNav() {
    Row() {
      Text('首页').fontSize(19)
    }
    .width('100%')
    .padding({ left: 20, right: 20, top: 10, bottom: 10 })
    .height(50)
    .backgroundColor('#f9fafc')
    .position({ x: 0 })
  }

  aboutToAppear() {
    // 调接口获取商品列表
    this.pageParams.current = 1
    this.goodsList = []
    this.getGoodsList()
  }

  // aboutToDisappear：在自定义组件析构销毁之前执行。不允许在aboutToDisappear函数中改变状态变量，特别是@Link变量的修改可能会导致应用程序行为不稳定。
  build() {
    Navigation() {
      Column() {
        Column() {
          List({ space: 20, initialIndex: 0 }) {
            ForEach(this.goodsList, (item: goodsItemType, index) => {
              ListItem() {
                Column() {
                  Text(item.shelf)
                    .position({ x: 0, y: 0 })
                    .zIndex(9)
                    .backgroundColor('#FA2A2D')
                    .borderRadius(10)
                    .fontColor('#fff')
                    .padding({ top: 2, bottom: 2, left: 6, right: 6 })
                    .fontSize(9)
                  Image(item.pictureUrl).width('100%').height('75%').borderRadius({ topLeft: 10, topRight: 10 })
                  Column() {
                    Text(item.name).textAlign(TextAlign.Start).width('100%')
                    Flex({ justifyContent: FlexAlign.SpaceBetween }) {
                      Text('￥' + item.price).fontColor(Color.Red)
                      Text('库存:' + item.number).fontColor(Color.Orange)
                    }.width('100%').margin({ top: 6 })
                  }.padding(8)
                }.backgroundColor(Color.White).borderRadius(10).height(220).width('90%')

              }
            }, (item: goodsItemType, index: number) => index.toString())


              ListItem() {
                if ((this.goodsList.length) % 2 == 0) {
                Column() {
                }.width('90%').height(220)
              }
            }
            ListItem() { // 加载更多布局
              loadStatusCom({ loadStatus: this.loadStatus, refresh: $refresh })
            }.width(this.goodsList.length>0?'200%':'100%')
          }
          .width("100%")
          .height('100%')
          .lanes(2)
          .alignListItem(ListItemAlign.Center)
          .onReachEnd(() => {
            if (this.loadStatus != 'loadMore') return
            this.pageParams.current++
            this.getGoodsList()
          })
        }
      }.height('100%')
    }
    .mode(NavigationMode.Auto)
    .title({ builder: this.customNav, height: 50 })
    .titleMode(NavigationTitleMode.Mini)
    .hideBackButton(true)
    .width('100%')
    .height('100%')
    .backgroundColor('#f4f9fd')
  }

  getGoodsList() {
    this.loadStatus = 'loading'
    const params: PageParamsType = {
      size: this.pageParams.size,
      current: this.pageParams.current
    }

    customerAPI.productPageApi(params).then((res: goodsResType) => {
      if (res.success) {
        // this.goodsList =[]
        this.goodsList = this.goodsList.concat(res.data.records)
        if (res.data.total <= this.goodsList.length) {
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