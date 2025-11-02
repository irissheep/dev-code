import promptAction from '@ohos.promptAction'
import router from '@ohos.router';
import { customerAPI } from '../../../api/customerApi'
import { merchantAPI } from '../../../api/merchantApi'
import { PageParamsType, responseType, goodsItemType, goodsResType } from '../../../typeDeclare/responseType.d'
import { loadStatusCom } from '../../common/components/loadStatusCom'


@Component
export struct goodsManage {
  @State refresh: boolean = false
  @State pageParams: PageParamsType = {
    size: 10,
    current: 1,
    total: 0,
  };
  @State goodsStatus: boolean = true
  @State isHasUnRead: boolean = false
  @State list: goodsItemType[] = []
  @State loadStatus: string = 'loadMore'
  @State keywords: string = ''
  controller: SearchController = new SearchController()
  private listScroller: Scroller = new Scroller();
  @Prop @Watch('watchPageShow') pageShow: boolean;
  // @Prop @Watch('watchPageHidden') pageHidden: boolean;

  // @Watch 回调
  watchPageShow() {
    this.pageParams.current = 1
    this.list = []
    this.getList()

    if (this.goodsStatus == false) {
      this.editGoods()
    }
    console.log('触发pageShow', this.pageShow)
  }

  aboutToAppear() {
    if (this.goodsStatus == false) {
      this.editGoods()
    }
    this.pageParams.current = 1
    this.list = []
    this.getList()
  }

  @Builder
  customNav() {
    Row() {
      Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
        Button(this.goodsStatus ? '开始补货' : '完成补货')
          .height(20)
          .width(74)
          .backgroundColor(this.goodsStatus ? '#fff' : '#007dfe')
          .fontColor(this.goodsStatus ? '#007dfe' : '#fff')
          .borderWidth(1)
          .borderColor('#007dfe')
          .onClick(() => {
            this.editGoods()
          })
        Text('商品列表').fontSize(19)
        Image($r('app.media.my'))
          .width(20)
          .onClick(() => {
            router.pushUrl({ url: 'pages/merchant/merchantMy' })
          })

      }.width('100%')
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

              // 将listScroller用于初始化List组件的scroller参数，完成listScroller与列表的绑定。
              List({ space: 20, initialIndex: 0 }) {
                ForEach(this.list, (item: goodsItemType) => {
                  ListItem() {
                    Column() {
                      Flex({ alignItems: ItemAlign.Center }) {
                        Image(item.pictureUrl).width(60).height(60).margin({ right: 10 })
                        Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
                          Column() {
                            Flex({
                              justifyContent: FlexAlign.SpaceBetween,
                              direction: FlexDirection.Column,
                              alignItems: ItemAlign.Start
                            }) {
                              Text(item.name).fontSize(14)
                              Text(item.shelf + '-' + item.cell).fontSize(14)
                              Text('库存：' + item.number).fontSize(14)
                            }
                          }

                          Column() {
                            Flex({
                              justifyContent: FlexAlign.SpaceBetween,
                              direction: FlexDirection.Column,
                              alignItems: ItemAlign.End
                            }) {
                              Row() {
                                // 判断是否在补货
                                Text(item.status).fontSize(14)
                                  .fontColor(this.goodsStatus ? 'red' : '#007dfe')
                                Image($r('app.media.youjiantou')).width(14)
                              }

                              Text('￥' + item.price).fontSize(14)

                            }
                          }
                        }.height(60)
                      }
                    }
                    .backgroundColor('#fff')
                    .width('100%')
                    .padding(10)
                    .borderRadius(4)
                    .onClick(() => {
                      router.pushUrl({ url: 'pages/merchant/goodsDetail', params: { mode: 'edit', id: item.id } })
                    })
                  }
                }, (item: goodsItemType, index: number) => index.toString())
                ListItem() { // 加载更多布局
                  loadStatusCom({ loadStatus: this.loadStatus, refresh: $refresh })
                }
              }.onReachEnd(() => {
                if (this.loadStatus != 'loadMore') return
                this.pageParams.current++
                this.getList()
              })

            }.padding(10)

            // 添加商品按钮
            Image($r('app.media.add')).width(50).height(50).position({ x: '80%', y: '80%' }).onClick(() => {
              router.pushUrl({ url: 'pages/merchant/goodsDetail', params: { mode: 'add' } })
            })
          }
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
      }
    }
    .mode(NavigationMode.Auto)
    .title({ builder: this.customNav, height: 50 })
    .titleMode(NavigationTitleMode.Mini)
    .hideBackButton(true)
    .width('100%')
    .height('100%')
    .backgroundColor('#f4f9fd')

  }

  editGoods() {
    // 1 开始补货 2 完成补货
    interface paramsType {
      status: string
    }

    console.log('补货')
    const data: paramsType = {
      status: this.goodsStatus ? '1' : '2'
    }
    merchantAPI.supplyGoodsApi(data).then((res: responseType) => {
      if (res.success) {
        promptAction.showToast({
          message: this.goodsStatus ? '补货中...' : '操作成功',
        });
        // if (this.goodsStatus) {
        this.pageParams.current = 1
        this.list = []
        this.getList()
        // }

        this.goodsStatus = !this.goodsStatus
      }
    })

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

    console.log('商品列表params===>', JSON.stringify(params))
    customerAPI.productPageApi(params).then((res: goodsResType) => {
      console.log('商品列表222===>', res.data.records.length, JSON.stringify(res))
      if (res.success) {
        this.list = this.list.concat(res.data.records)
        if (res.data.total <= this.list.length) {
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
  }
}