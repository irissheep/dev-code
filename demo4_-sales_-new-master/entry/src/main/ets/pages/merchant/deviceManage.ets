import promptAction from '@ohos.promptAction'
import { merchantAPI } from '../../api/merchantApi'
import { PageParamsType, deviceItemType, deviceResType } from '../../typeDeclare/responseType.d'
import { loadStatusCom } from '../common/components/loadStatusCom'
import router from '@ohos.router'

@Entry
@Component
struct deviceManage {
  @State refresh: boolean = false
  @State pageParams: PageParamsType = {
    current: 1,
    size: 10,
    total: 0,
  };
  @State isHasUnRead: boolean = false
  @State list: object[] = []
  @State loadStatus: string = 'loadMore'
  @State keywords: string = ''
  controller: SearchController = new SearchController()
  private listScroller: Scroller = new Scroller();

  onPageShow() {
    this.pageParams.current = 1
    this.list = []
    this.getList()
  }

  @Builder
  customNav() {
    Row() {
      Image($r('app.media.back')).width(20).margin({ right: 14 }).onClick(() => {

        router.back()
      })
      Text('设备管理').fontSize(19)
    }.position({x:0}).width('100%').padding({ left: 20, right: 20, top: 10, bottom: 10 }).height(50).backgroundColor('#f9fafc')
  }

  build() {
    Column() {
      Navigation() {
        Column() {
          Search({ value: this.keywords, placeholder: '输入关键字搜索', controller: this.controller })
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

          List({ space: 20, scroller: this.listScroller }) {
            ForEach(this.list, (item: deviceItemType,) => {
              ListItem() {
                Column() {
                  Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
                    Column() {
                      Flex({
                        justifyContent: FlexAlign.SpaceBetween,
                        direction: FlexDirection.Column,
                        alignItems: ItemAlign.Start
                      }) {
                        Text('设备类别：' + item.category)
                        Text('设备名称：' + item.name)
                        Flex({alignItems:ItemAlign.Center}) {
                          Text('设备编号：').width(120)
                          Text(`${item.deviceNo}`)
                            .textOverflow({ overflow: TextOverflow.Ellipsis })
                            .maxLines(1)
                            .flexGrow(1)
                        }
                        Text('最近上报：' + item.recentReportTime)
                      }
                    }

                    Column() {
                      Flex({
                        justifyContent: FlexAlign.SpaceBetween,
                        direction: FlexDirection.Column,
                        alignItems: ItemAlign.End
                      }) {
                        Row() {
                          Text(Number(item.status) ? '在线' : '离线')
                            .fontColor(Number(item.status) ? 'green' : 'red')
                        }
                      }
                    }.width(80)
                  }.height(80)
                }
                .backgroundColor('#fff')
                .width('100%')
                .padding(8)
                .borderRadius(4)
              }
            }, (item: deviceItemType, index: number) => index.toString())
            ListItem() { // 加载更多布局
              loadStatusCom({ loadStatus: this.loadStatus, refresh: $refresh })
            }
          }.onReachEnd(() => {
            if (this.loadStatus != 'loadMore') return
            this.pageParams.current++
            this.getList()
          })

        }

        // Text(loadingStatusKeyMap[this.loadStatus]).margin({ top: 10 })
      }
      .mode(NavigationMode.Auto)
      // .title('设备管理')
      .title({ builder: this.customNav, height: 50 })
      .hideBackButton(true)
      .titleMode(NavigationTitleMode.Mini)
    }.width('100%').height('100%').backgroundColor('#f4f9fd').padding(10)

  }

  search() {
    this.pageParams.current = 1
    this.list = []
    this.getList()
  }

  getList() {
    this.loadStatus = 'loading' // 加载中...

    interface paramsType extends PageParamsType {
      keyword: string
    }

    const params: paramsType = {
      size: this.pageParams.size,
      current: this.pageParams.current,
      keyword: this.keywords
    }

    merchantAPI.deviceManageApi(params).then((res: deviceResType) => {
      // console.log('s设备管理',JSON.stringify(res.data))
      if (res.success) {
        this.list = this.list.concat(res.data.records)
        if (this.list.length >= res.data.total) {
          this.loadStatus = 'noMore'
        } else {
          this.loadStatus = 'loadMore'
        }
      } else {
        this.loadStatus = 'loadFailed'
        promptAction.showToast({ message: res.msg })
      }
      this.refresh = false
    })
      .catch((err: Error) => {
        console.log('err', err)
      })
  }
}