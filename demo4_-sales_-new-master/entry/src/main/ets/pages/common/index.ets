


import { home } from '../customer/components/home'
import { notice } from './components/notice'
import { consumeRecords } from './components/consumeRecords'
import { my } from './components/my'
import { merchantHome } from '../merchant/components/merchantHome'
import { statisticalData } from '../merchant/components/statisticalData'
import { goodsManage } from '../merchant/components/goodsManage'
import SocketModel from '../../model/socket'
import router from '@ohos.router'

interface resType {
  createTime: string
  id: string
  msg: string
  title: string
}

@Entry
@Component
struct customer {
  private tabsController: TabsController = new TabsController()
  @State userType: string = ''
  @State pageIndex: number = 0; //页面索引
  @State pageShow: boolean = false
  private socketModel: SocketModel = new SocketModel();
  private context: Context | undefined = undefined;

  onPageShow() {
    console.log('onPageShow')
    this.pageShow = true
    interface nn {
      tabIndex: string
    }
    const params = (router.getParams() || {}) as nn
    this.pageIndex = Number(params.tabIndex || 0)
    this.initNotification()
  }

  onPageHide() {
    this.pageShow = false
  }

  aboutToAppear() {
    this.userType = AppStorage.Get('userType') || '2'
  }

  // 初始化消息
  initNotification() {
    const  a = AppStorage.Get('isConnect')
    console.log('initNotification', a == 'false', a);
    this.socketModel.onConnect()
  }

  build() {
    Column() {
      if (this.userType == '1') {
        Tabs({ barPosition: BarPosition.End, controller: this.tabsController, index: this.pageIndex }) {
          TabContent() {
            home()
          }.tabBar(this.TabBuilder('首页', 0, $r('app.media.home'), $r('app.media.home1')))

          TabContent() {
            notice()
          }.tabBar(this.TabBuilder('消息', 1, $r('app.media.notice'), $r('app.media.notice1')))

          TabContent() {
            my({ pageShow: this.pageShow })
          }.tabBar(this.TabBuilder('我的', 2, $r('app.media.my'), $r('app.media.my1')))

        }
        .barMode(BarMode.Fixed) //不允许滑动
        // .fullScreen() //全屏展示
        .onChange((index) => {
          this.pageIndex = index; //控制换页
        })
      } else {
        Tabs({ barPosition: BarPosition.End, controller: this.tabsController, index: this.pageIndex }) {
          TabContent() {
            merchantHome()
          }.tabBar(this.TabBuilder('首页', 0, $r('app.media.home'), $r('app.media.home1')))

          TabContent() {
            goodsManage({ pageShow: this.pageShow })
          }.tabBar(this.TabBuilder('商品', 1, $r('app.media.shangpin'), $r('app.media.shangpin1')))

          TabContent() {
            consumeRecords({ pageShow: this.pageShow })
          }.tabBar(this.TabBuilder('订单', 2, $r('app.media.dingdan'), $r('app.media.dingdan1')))

          TabContent() {
            statisticalData()
          }.tabBar(this.TabBuilder('数据', 3, $r('app.media.shuju'), $r('app.media.shuju1')))

        }
        .barMode(BarMode.Fixed) //不允许滑动
        // .fullScreen() //全屏展示
        .onChange((index) => {
          this.pageIndex = index; //控制换页
        })
      }
    }

  }

  @Builder
  TabBuilder(title: string, targetIndex: number, normalImg: Resource, selectedImg: Resource,) {
    Column() {
      Image(this.pageIndex === targetIndex ? selectedImg : normalImg)
        .size({ width: 25, height: 25 })
      Text(title)
        .fontColor(this.pageIndex === targetIndex ? '#1698CE' : '#6B6B6B')
    }
    .width('100%')
    .height(50)
    .justifyContent(FlexAlign.Center)
  }
}
