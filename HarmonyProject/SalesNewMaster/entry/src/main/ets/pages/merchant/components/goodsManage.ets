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
  @State replenishMap: { [key: string]: number } = {}
  @State submitLoading: boolean = false
  controller: SearchController = new SearchController()
  private listScroller: Scroller = new Scroller();
  @Prop @Watch('watchPageShow') pageShow: boolean;
  // @Prop @Watch('watchPageHidden') pageHidden: boolean;

  // @Watch 回调
  watchPageShow() {
    this.pageParams.current = 1
    this.list = []
    this.clearReplenishSelection()
    this.goodsStatus = true
    this.getList()
    console.log('触发pageShow', this.pageShow)
  }

  aboutToAppear() {
    this.pageParams.current = 1
    this.list = []
    this.clearReplenishSelection()
    this.goodsStatus = true
    this.getList()
  }

  @Builder
  customNav() {
    Row() {
      Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
        Button(this.goodsStatus ? '开始补货' : `完成补货(${this.getSelectedGoodsCount()})`)
          .height(20)
          .width(140)
          .backgroundColor(this.goodsStatus ? '#fff' : '#007dfe')
          .fontColor(this.goodsStatus ? '#007dfe' : '#fff')
          .borderWidth(1)
          .borderColor('#007dfe')
          .enabled(this.goodsStatus ? !this.submitLoading : (this.getSelectedGoodsCount() > 0 && !this.submitLoading))
          .opacity(!this.goodsStatus && this.getSelectedGoodsCount() === 0 ? 0.6 : 1)
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
                      Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
                        Row().alignItems(VerticalAlign.Center) {
                          Image(item.pictureUrl).width(60).height(60).borderRadius(4).margin({ right: 10 })
                          Column() {
                            Text(item.name).fontSize(14)
                            Text(item.shelf + '-' + item.cell).fontSize(14)
                            Text('库存：' + item.number).fontSize(14)
                          }
                        }
                        Column().alignItems(HorizontalAlign.End) {
                          Row() {
                            Text(this.goodsStatus ? (item.status || '') : (this.isGoodsSelected(item.id) ? '已选择' : (item.status || '')))
                              .fontSize(14)
                              .fontColor(this.goodsStatus ? 'red' : (this.isGoodsSelected(item.id) ? '#007dfe' : 'red'))
                            Image($r('app.media.youjiantou')).width(14)
                          }
                          Text('￥' + item.price).fontSize(14)
                        }
                      }
                      .height(60)

                      if (!this.goodsStatus) {
                        Row({ space: 12 }) {
                          Button('-')
                            .width(32)
                            .height(32)
                            .fontColor('#fff')
                            .borderRadius(16)
                            .backgroundColor('#007dfe')
                            .enabled(this.getQuantity(item.id) > 0 && !this.submitLoading)
                            .opacity(this.getQuantity(item.id) > 0 ? 1 : 0.4)
                            .onClick(() => {
                              this.adjustQuantity(item, -1)
                            })
                          Text(this.getQuantity(item.id).toString())
                            .fontSize(16)
                          Button('+')
                            .width(32)
                            .height(32)
                            .fontColor('#fff')
                            .borderRadius(16)
                            .backgroundColor('#007dfe')
                            .enabled(!this.submitLoading)
                            .onClick(() => {
                              this.adjustQuantity(item, 1)
                            })
                          Text(`本次补货：${this.getQuantity(item.id)}件`).fontSize(14).fontColor('#666')
                        }
                        .margin({ top: 12 })
                      }
                    }
                    .backgroundColor(this.isGoodsSelected(item.id) ? '#e6f2ff' : '#fff')
                    .width('100%')
                    .padding(10)
                    .borderRadius(4)
                    .onClick(() => {
                      if (this.goodsStatus) {
                        router.pushUrl({ url: 'pages/merchant/goodsDetail', params: { mode: 'edit', id: item.id } })
                      }
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

  isGoodsSelected(goodsId: string): boolean {
    return this.getQuantity(goodsId) > 0
  }

  getQuantity(goodsId: string): number {
    return this.replenishMap[goodsId] || 0
  }

  adjustQuantity(item: goodsItemType, delta: number) {
    if (this.goodsStatus) {
      return
    }
    const current = this.getQuantity(item.id)
    const next = current + delta
    const nextMap: { [key: string]: number } = { ...this.replenishMap }
    if (next <= 0) {
      delete nextMap[item.id]
    } else {
      nextMap[item.id] = next
    }
    this.replenishMap = nextMap
  }

  getSelectedGoodsCount(): number {
    return Object.keys(this.replenishMap).filter((id: string) => this.replenishMap[id] > 0).length
  }

  clearReplenishSelection() {
    this.replenishMap = {}
  }

  buildReplenishPayload(): Array<{ productId: string | number, quantity: number }> {
    return Object.keys(this.replenishMap)
      .filter((id: string) => this.replenishMap[id] > 0)
      .map((id: string) => {
        // 保持ID为字符串，避免大整数精度丢失
        // JavaScript的Number类型只能安全表示-2^53到2^53之间的整数（约16位）
        // 商品ID是bigint类型（19位），超过安全范围，所以保持字符串格式
        // 后端Jackson会自动将字符串转换为Long类型
        const MAX_SAFE_INTEGER_LENGTH = 15 // Number.MAX_SAFE_INTEGER = 9007199254740991 (16位)
        const productId = id.length >= MAX_SAFE_INTEGER_LENGTH ? id : Number(id)
        console.log(`商品ID处理: 原始=${id}, 长度=${id.length}, 转换后=${productId}, 类型=${typeof productId}`)
        return {
          productId: productId,
          quantity: this.replenishMap[id]
        }
      })
      .filter(item => (item.productId !== null && item.productId !== undefined) && item.quantity > 0)
  }

  async editGoods() {
    if (this.submitLoading) {
      return
    }

    if (this.goodsStatus) {
      await this.startReplenishment()
      return
    }

    if (this.getSelectedGoodsCount() === 0) {
      promptAction.showToast({
        message: '请选择需要补货的商品并设置数量',
      });
      return
    }

    await this.finishReplenishment()
  }

  async startReplenishment() {
    this.submitLoading = true
    this.clearReplenishSelection()
    try {
      interface paramsType {
        status: string
      }
      const res: responseType = await merchantAPI.supplyGoodsStartApi({ status: '1' } as paramsType)
      if (res.success) {
        promptAction.showToast({
          message: '补货模式已开启，请设置补货数量',
        })
        this.goodsStatus = false
      } else {
        const msg = typeof res.msg === 'string' ? res.msg : '补货模式开启失败'
        promptAction.showToast({ message: msg })
      }
    } catch (err) {
      console.error('开启补货失败', JSON.stringify(err))
      promptAction.showToast({ message: '开启补货失败，请稍后重试' })
    } finally {
      this.submitLoading = false
    }
  }

  async finishReplenishment() {
    this.submitLoading = true
    try {
      const items = this.buildReplenishPayload()
      console.log('补货数据准备:', JSON.stringify(items))
      if (!items.length) {
        promptAction.showToast({ message: '请至少为一个商品设置补货数量' })
        this.submitLoading = false
        return
      }
      // 构造两种兼容的参数，避免后端字段不一致导致不生效
      const compatiblePayload: any = {
        status: '2',
        items,
        productList: items.map(({ productId, quantity }) => ({ id: productId, number: quantity }))
      }
      console.log('发送补货请求:', JSON.stringify(compatiblePayload))
      const res: responseType = await merchantAPI.supplyGoodsFinishApi(compatiblePayload)
      console.log('补货请求响应:', JSON.stringify(res))
      if (res.success) {
        // 乐观更新本地列表，立即看到库存变化
        const addMap = new Map<string | number, number>()
        items.forEach(it => addMap.set(it.productId, it.quantity))
        this.list = this.list.map(g => {
          // 使用字符串ID进行匹配，避免精度问题
          const inc = addMap.get(g.id) || addMap.get(Number(g.id)) || 0
          return { ...g, number: (Number(g.number) || 0) + inc }
        })

        promptAction.showToast({ message: '补货完成，库存已更新' })
        this.goodsStatus = true
        this.clearReplenishSelection()
        // 重新拉取一次，确保与后端最终一致
        this.pageParams.current = 1
        this.list = []
        this.getList()
      } else {
        const msg = typeof res.msg === 'string' ? res.msg : '结束补货失败'
        promptAction.showToast({ message: msg })
      }
    } catch (err) {
      console.error('补货错误', JSON.stringify(err))
      promptAction.showToast({ message: '补货失败，请稍后重试' })
    } finally {
      this.submitLoading = false
    }
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
        if (!this.goodsStatus && this.getSelectedGoodsCount()) {
          const validIds = new Set(this.list.map(goods => goods.id))
          const nextMap: { [key: string]: number } = {}
          let changed = false
          Object.keys(this.replenishMap).forEach((id: string) => {
            if (validIds.has(id)) {
              nextMap[id] = this.replenishMap[id]
            } else {
              changed = true
            }
          })
          if (changed) {
            this.replenishMap = nextMap
          }
        }
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