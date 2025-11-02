import promptAction from '@ohos.promptAction'
import { PageParamsType } from '../../../typeDeclare/responseType.d'
import { loadStatusCom } from './loadStatusCom'
import router from '@ohos.router'
import { homeApi } from '../../../api/HomeAPI'
import { messageListResType, booleanDataRes, messageListType } from '../../../typeDeclare/responseType'

interface statusParamsType {
  userId: string
  status: number
  id?: string
}

@CustomDialog
struct CustomDialogExample {
  cancel?: () => void
  confirm?: () => void
  controller: CustomDialogController
  userType?:string

  build() {
    Column() {
      Text(this.userType=='1'?'是否清空':'是否清除全部未读').fontSize(20).margin({ top: 20, bottom: 10 })
      Flex({ justifyContent: FlexAlign.SpaceAround }) {
        Button('取消')
          .onClick(() => {
            this.controller.close()
            if (this.cancel) {
              this.cancel()
            }
          }).backgroundColor(0xffffff).fontColor(Color.Black)
        Button('确认')
          .onClick(() => {
            this.controller.close()
            if (this.confirm) {
              this.confirm()
            }
          }).backgroundColor(0xffffff).fontColor(Color.Red)
      }.margin({ bottom: 10 })
    }
  }
}

@Component
export struct notice {
  @State refresh: boolean = false
  @State noticeList: messageListType[] = []
  @State loadStatus: string = 'loadMore'
  @State userType: string = AppStorage.Get('userType') || ''
  @State pageParams: PageParamsType = {
    size: 10,
    current: 1,
    total: 0
  };

  aboutToAppear() {
    // 调接口获取商品列表
    this.pageParams.current = 1
    this.noticeList = []
    this.getList()
  }

  @Builder
  customNav() {
    Row() {
      if (this.userType == '2') {
        Image($r('app.media.back')).width(20).margin({ right: 14 }).onClick(() => {
          router.back()
        })
      }
      Text('消息').margin({ right: 16 }).fontSize(19).fontWeight(500)
      Row() {
        Image($r('app.media.clear')).width(14).height(16).margin({ right: 6 }).onClick(() => {
          // 调用方法一键已读
          // this.tagRead()
          this.dialogController.open()
        })
        Text(this.userType == '1'?'清空':'一键已读').fontSize(12)
      }
      .onClick(() => {
        // 调用方法一键已读
        this.dialogController.open()
      })
    }.width('100%').padding({ left: 20, right: 20, top: 10, bottom: 10 }).height(50).backgroundColor('#f9fafc').position({x:0})
  }

  build() {
    Navigation() {
      Column() {
        Refresh({ refreshing: $$this.refresh, offset: 50 }) {
          Column() {
            // 将listScroller用于初始化List组件的scroller参数，完成listScroller与列表的绑定。
            List({ space: 10, initialIndex: 0 }) {
              ForEach(this.noticeList, (item: messageListType) => {
                ListItem() {
                  Column() {
                    Flex({ justifyContent: FlexAlign.SpaceBetween }) {
                      Row() {
                        Text(item.title).fontWeight(500).margin({ right: 8 })
                      }

                      Text(item.createTime)
                        .fontSize(10)
                        .fontColor(Color.Gray)
                    }.margin({ bottom: 10 })

                    Flex({ justifyContent: FlexAlign.SpaceBetween }) {
                      Text(item.msg).textOverflow({ overflow: TextOverflow.None })
                        .maxLines(2)
                        .fontSize(12)
                        .fontColor(Color.Gray)
                      if (this.userType == '2') {
                        if (item.type && item.type != '3') {
                          Image($r('app.media.youjiantou')).width(10)
                        }
                      }
                    }

                  }
                  .backgroundColor('#fff')
                  .width('100%')
                  .padding(8)
                  .borderRadius(4)
                  .margin({ bottom: 10 })
                  .opacity(item.status ? 0.7 : 1)
                  .onClick(() => {
                    // 商户跳转
                    if (this.userType == '2') {
                      if (item.type) {
                        this.navigateTo(item)
                      }
                    }
                  })
                }
              }, (item: messageListType, index: number) => index.toString())
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
      .onStateChange((refreshStatus: RefreshStatus) => {
        if (refreshStatus == 3) {
          this.pageParams.current = 1
          this.noticeList = []
          this.getList()
        } else if (refreshStatus == 4) {
          this.refresh = false
        }
      })
      }
    }
    .mode(NavigationMode.Auto)
    .titleMode(NavigationTitleMode.Mini)
    .hideBackButton(true)
    .title({ builder: this.customNav, height: 50 })
    .width('100%')
    .height('100%')
    .backgroundColor('#f4f9fd')

  }

  navigateTo(item: messageListType) {
    // 不同状态跳转不同页面
    console.log('不同状态跳转不同页面', JSON.stringify(item))
    // type 0 补货预警 1 异常取走 2异常退回 3 已完成
    // 补货预警点击跳转商品列表
    if (item.type == '0') {
      // 跳转商品列表
      router.replaceUrl({ url: 'pages/common/index', params: { tabIndex: '1' } })
    } else if (item.type == '1' || item.type == '2') {
      // 跳转账单处置页面
      router.pushUrl({ url: 'pages/merchant/abnormalHandling', params: { id: item.billId,from:"noticeList" } })
    }

    const pp: statusParamsType = {
      id: item.id,
      userId: AppStorage.Get('userId') || '',
      status: 1
    }
    // 设置单个为已读
    homeApi.msgStatusApi(pp).then((res:booleanDataRes) => {
      if (res.data) {
        this.pageParams.current = 1
        this.getList()
      }
    })
  }

  dialogController: CustomDialogController = new CustomDialogController({
    builder: CustomDialogExample({
      cancel: () => {
      },
      confirm: () => {
        if(this.userType=='1'){
          this.clearAllNotice()
        }else{
          this.tagRead()
        }

      },
      userType:this.userType
    }),
  })
  clearAllNotice(){
    console.log('清空消息')
    homeApi.clearMsgApi(AppStorage.Get('userId') || '')
      .then((res: booleanDataRes) => {
        console.log('清空消息', JSON.stringify(res))
        if (res.success ) {
          promptAction.showToast({ message: '已清空' });
          this.pageParams.current = 1
          this.noticeList =[]
          this.getList()
        }else{
          promptAction.showToast({ message:res.msg });
        }
      })
      .catch(() => {
        console.log('err')
      })
  }
  // 一键已读
  tagRead() {
    console.log('一键已读')
    homeApi.clearAllMsgApi(AppStorage.Get('userId') || '')
      .then((res: booleanDataRes) => {
        console.log('res一键已读', JSON.stringify(res))

        if (res.success ) {
          promptAction.showToast({ message: '操作成功' });
          this.pageParams.current = 1
          this.noticeList =[]
          this.getList()
        }else{
          promptAction.showToast({ message:res.msg });
        }
      })
      .catch(() => {
        console.log('err')
      })

  }

  getList() {
    this.loadStatus = 'loading'

    interface paramsType {
      size: number
      current: number,
      userId?: string
    }

    const params: paramsType = {
      size: this.pageParams.size,
      current: this.pageParams.current,
      userId: AppStorage.Get('userId') || ''
    }

    homeApi.messageListApi(params)
      .then((res: messageListResType) => {
        if (res.success) {
          console.log('消息列表res', JSON.stringify(res.data.total));
          this.noticeList = this.noticeList.concat(res.data.records)
          // const unread = this.noticeList.filter(i => i.status == 0) ///0未读1已读
          // this.isHasUnRead = unread.length > 0 ? true : false
          if (res.data.total <= this.noticeList.length) {
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
      .catch(() => {
        this.loadStatus = 'noMore'
        this.refresh = false
      })


  }
}