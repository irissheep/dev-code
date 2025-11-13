import { userListResType, responseType, billDetailInfoType, billDetailResType } from '../../typeDeclare/responseType'
import { merchantAPI } from '../../api/merchantApi'
import router from '@ohos.router'
import promptAction from '@ohos.promptAction'


interface selectType {
  value: string
}

interface userListType {
  id: string
  account: string

}

let storage = LocalStorage.GetShared()

@CustomDialog
struct CustomDialogExample {
  confirm?: (id: userListType) => void
  cancel?: () => void
  searchUser?: (id: string) => void
  controller: CustomDialogController
  @LocalStorageLink('userList') userList: userListType[] = []

  build() {
    Column() {
      Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
        Text('取消').onClick(() => {
          this.cancel()
          this.controller.close()
        }).fontColor('#296DFF')
        Text('选择客户').fontWeight(500).fontSize(18)
        Text('确定').onClick(() => {
          this.controller.close()
        }).fontColor('#296DFF')
      }.margin({ bottom: 10, top: 4 }).padding({ left: 6, right: 6 })

      Search({ placeholder: '输入关键字搜索', controller: new SearchController() })
        .searchButton('搜索')
        .height(30)
        .placeholderFont({ size: 14, weight: 400 })
        .textFont({ size: 14, weight: 400 })
        .onSubmit((value: string) => {
          this.searchUser && this.searchUser(value)
        })
        .onChange((value) => {
          if (!value) {
            this.searchUser && this.searchUser('')
          }
        })

      Column() {
        List({ space: 0, initialIndex: 0 }) {
          ForEach(this.userList, (item: userListType, index: number) => {
            ListItem() {
              Flex({ justifyContent: FlexAlign.SpaceBetween }) {
                Text(item.account)
                Radio({ value: item.id, group: 'userGroup' })
                  .checked(false).onChange((isChecked: boolean) => {
                  if (isChecked) {
                    this.confirm && this.confirm(item)
                  }
                }).height(10)
                  .width(10)
              }.padding(4).borderWidth({ bottom: 1 }).width('100%').borderColor('#f4f6f5')
            }
          }, (item: userListType, index: number) => JSON.stringify(item))
        }
      }.padding({ bottom: 100 })
    }.padding(10).height(300)
  }
}

@Entry(storage)
@Component
struct abnormalHandling {
  @LocalStorageLink('userList') userList: userListType[] = []
  @State radioSelect: string = '0'
  @State selectUserId: string = ''
  @State selectUser: string = ''
  @State selectUserAccount: string = ''
  @State selectList: selectType[] = []
  @State originUserList: userListType[] = []
  @State detailInfo: billDetailInfoType = {
    id: '',
    status: '', //1 已完成，2 异常退回，3 异常取走，4 已忽略
    createTime: '2012-12-12 12:23:12',
    billNo: '',
    amount: 0,
    productList: [],
    totalAmount: '',
    pictureUrl: "'https://img2.baidu.com/it/u=1572233920,3302887590&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500', 'https://img2.baidu.com/it/u=1572233920,3302887590&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500'"
  }
  @State curId: string = ''
  @State from: string = ''
  dialogController: CustomDialogController = new CustomDialogController({

    builder: CustomDialogExample({
      confirm: (item: userListType) => {
        this.selectUserId = item.id
        this.selectUser = item.account
        console.log('[用户选择] 选择的用户ID:', item.id, '类型:', typeof item.id, '账户:', item.account)
      },
      cancel: () => {
        this.selectUserId = ''
        this.selectUser = ''
      },
      searchUser: (keywords) => {
        this.userList = []
        this.userList = this.originUserList.filter(i => i.account.includes(keywords))

        console.log('触发搜索', keywords, JSON.stringify(this.userList))
      },
    }),
    alignment: DialogAlignment.Top
  })

  onPageShow() {
    interface nn {
      id: string
      from: string
    }

    const params = router.getParams() as nn
    this.curId = params.id || ''
    this.from = params.from || 'noticeList'
    this.getUserList()
    this.getDetail()
  }

  @Builder
  customNav() {
    Row() {
      Image($r('app.media.back')).width(20).margin({ right: 14 }).onClick(() => {
        if (this.from == 'noticeList') {
          router.back()
          return
        }
        router.replaceUrl({ url: 'pages/common/index', params: { tabIndex: '2' } })
      })
      Text('异常处置').margin({ right: 16 }).fontSize(19).fontWeight(500)

    }
    .position({ x: 0 })
    .width('100%')
    .padding({ left: 20, right: 20, top: 10, bottom: 10 })
    .height(50)
    .backgroundColor('#f9fafc')
  }

  build() {
    Column() {
      Navigation() {
        Column() {
          Column() {
            Flex({ justifyContent: FlexAlign.SpaceBetween }) {
              Text('订单号：' + this.detailInfo.billNo).fontSize(14)
              Text(this.detailInfo.status == '1' ? '已完成' : 
                   (this.detailInfo.status == '2' ? '异常退回' : 
                    (this.detailInfo.status == '3' ? '异常取走' : 
                     (this.detailInfo.status == '4' ? '已忽略' : '未知'))))
                .fontColor(this.detailInfo.status == '1' ? Color.Black : 
                          (this.detailInfo.status == '4' ? Color.Gray : Color.Red)).fontSize(14)
            }.margin({ bottom: 10 })

            Flex() {
              Column() {
                Image((this.detailInfo?.productList?.[0] || {}).pictureUrl || '')
                  .width(60)
                  .height(60)
                Row() {
                  Text('x ' + this.detailInfo.totalNumber || '')
                    .fontColor(Color.White)
                    .fontSize(9)
                }
                .position({ x: 46, y: 46 })
                .backgroundColor('gray')
                .borderRadius(4)
                .width(14)
                .height(14)

              }.width(60).height(60)

              Flex({
                direction: FlexDirection.Column,
                alignItems: ItemAlign.Start,
                justifyContent: FlexAlign.SpaceBetween
              }) {
                Text((this.detailInfo?.productList?.[0] || {}).name || '').fontSize(14)
                Text(this.detailInfo.totalAmount || '').fontSize(14)
                Text(this.detailInfo.createTime).fontSize(14)
              }.height(60).margin({ left: 10 })
            }
          }
          .width('100%')
          .backgroundColor(Color.White)
          .padding(10)
          .borderWidth(1)
          .borderColor(Color.Gray)
          .borderRadius(4)
          .margin({ bottom: 10 })

          Column() {
            Text('照片:').margin({ bottom: 10 })
            if (this.detailInfo.pictureUrl) {
              List({ space: 0, initialIndex: 0 }) {
                ForEach((this.detailInfo.pictureUrl || '').split(','), (item: string, index: number) => {
                  ListItem() {
                    Image(item).width(60).height(60).margin({ right: 10 })
                  }
                }, (item: object, index: number) => index.toString())
              }.listDirection(Axis.Horizontal).height(60).alignListItem(ListItemAlign.Start)
            } else {
            }
            Text('暂无照片')


          }
          .width('100%')
          .backgroundColor(Color.White)
          .padding(10)
          .borderWidth(1)
          .borderColor(Color.Gray)
          .borderRadius(4)
          .alignItems(HorizontalAlign.Start)
          .margin({ bottom: 10 })


          Column() {
            Flex({ alignItems: ItemAlign.Center }) {
              Text('处置：')

              Radio({ value: '0', group: 'radioGroup' })
                .checked(true)
                .onChange((isChecked: boolean) => {
                  if (isChecked) {
                    this.radioSelect = '0'
                  }
                })
              Text('忽略').margin({ right: 10 })
              Radio({ value: '1', group: 'radioGroup' })
                .checked(false)
                .onChange((isChecked: boolean) => {
                  if (isChecked) {
                    this.radioSelect = '1'
                  }
                })
              Text(this.detailInfo.status == '2' ?'退款':'扣款')
            }

            if (this.radioSelect == '1') {
              Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
                Text('客户:').width(50)
                Text(this.selectUser ? this.selectUser : '请选择').flexGrow(1).onClick(() => {
                  this.dialogController.open()
                })
                Image($r('app.media.right')).width(10)
                // Select(this.selectList)
                //   .selected(-1)
                //   .value('请选择')
                //   .flexGrow(1)
                //   .onSelect((index: number, text?: string | undefined) => {
                //     this.selectUserId = this.originUserList[index].id
                //     this.selectUserAccount = this.originUserList[index].account
                //   })     .flexGrow(1)

              }
            }

          }
          .width('100%')
          .backgroundColor(Color.White)
          .padding(10)
          .borderWidth(1)
          .borderColor(Color.Gray)
          .borderRadius(4)
          .margin({ bottom: 10 })
        }

        Flex({ justifyContent: FlexAlign.Center }) {
          Button('提交').width('80%').onClick(() => {
            console.log('提交按钮点击，客户id:', this.selectUserId, '处理类型:', this.radioSelect)
            this.submit()
          })
        }.position({ y: '90%' })
      }
      .mode(NavigationMode.Auto)
      .title(this.customNav)
      .hideBackButton(true)
      .titleMode(NavigationTitleMode.Mini)
    }.width('100%').height('100%').backgroundColor('#f4f9fd').padding(10)

  }

  getDetail() {
    console.log(' 订单详情resp', this.curId)

    merchantAPI.billDetailApi(this.curId).then((res: billDetailResType) => {
      console.log('订单详情res', JSON.stringify(res))
      if (res.success && res.data) {
        this.detailInfo = res.data as billDetailInfoType
      } else {
        promptAction.showToast({ message: res.msg || '获取订单详情失败' })
      }
    }).catch((err: Error) => {
      console.error('获取订单详情失败:', JSON.stringify(err))
      promptAction.showToast({ message: '获取订单详情失败，请重试' })
    })
  }

  getUserList() {
    merchantAPI.getUserListApi().then((res: userListResType) => {
      this.selectList = res.data.map((i: userListType) => {
        return { value: i.account } as selectType
      })
      this.originUserList = res.data
      this.userList = res.data
      console.log('用户列表res', JSON.stringify(res.data))
      
      // 检测用户ID精度问题
      res.data.forEach((user, index) => {
        if (user.id) {
          const originalId = user.id;
          if (typeof originalId === 'string' && originalId.length >= 15) {
            const numId = Number(originalId);
            const convertedBack = numId.toString();
            if (convertedBack !== originalId) {
              console.error(`[精度丢失检测] 用户索引 ${index}: ID "${originalId}" 转换为number后变为 "${convertedBack}"`);
              console.error(`[精度丢失检测] 建议: 保持ID为字符串格式`);
            }
          }
        }
      });
    })
  }

  // 提交处置
  submit() {
    interface mm {
      id?: string | number,
      userId?: string | number,
      handle?: string,
      billNo?: string,
      amount?: number,
      status?: string
    }

    // 安全转换ID，避免精度丢失
    // 对于大整数ID（>=15位），保持字符串格式，让后端Jackson自动转换为Long
    const safeConvertId = (id: string | number | undefined): string | number | undefined => {
      if (id === null || id === undefined) return undefined;
      if (typeof id === 'number') return id;
      if (typeof id === 'string') {
        // 如果ID长度>=15位，可能超出JavaScript安全整数范围，保持字符串
        if (id.length >= 15) {
          console.log(`[精度保护] ID "${id}" 长度${id.length}位，保持字符串格式`);
          return id;
        }
        // 小ID可以安全转换为number
        const num = Number(id);
        if (!isNaN(num)) {
          return num;
        }
        return id;
      }
      return id;
    };

    // 获取订单的userId，用于忽略操作
    const billUserId: string | number | undefined = this.detailInfo?.userId ? 
      safeConvertId(this.detailInfo.userId) : undefined;
    
    // 获取金额，优先使用amount字段，否则从totalAmount解析
    const amountNum: number = this.detailInfo?.amount ?? parseFloat(this.detailInfo?.totalAmount || '0')

    // 验证必要参数
    if (!this.detailInfo.id) {
      promptAction.showToast({ message: '订单ID不能为空' })
      return
    }
    if (!this.detailInfo.billNo) {
      promptAction.showToast({ message: '订单号不能为空' })
      return
    }
    if (!this.detailInfo.status) {
      promptAction.showToast({ message: '订单状态不能为空' })
      return
    }

    // 当选择处理（扣款/退款）时，必须选择客户
    if (this.radioSelect == '1' && !this.selectUserId) {
      promptAction.showToast({ message: '请选择客户' })
      return
    }

    // 安全转换用户ID，避免精度丢失
    let finalUserId: string | number | undefined = undefined;
    if (this.radioSelect == '1') {
      // 处理操作：使用选择的客户ID
      finalUserId = safeConvertId(this.selectUserId);
      if (!finalUserId) {
        promptAction.showToast({ message: '用户ID无效' })
        return
      }
    } else {
      // 忽略操作：使用订单的userId（如果存在）
      finalUserId = billUserId;
    }

    // 构建请求参数
    const params: mm = {
      id: safeConvertId(this.detailInfo.id),
      userId: finalUserId, // 使用安全转换后的ID
      handle: this.radioSelect, //处理类型 0 忽略 1 处理
      billNo: this.detailInfo.billNo,
      amount: amountNum, //金额
      status: this.detailInfo.status
    }
    console.log('处置账单params', JSON.stringify(params))
    
    // 调用接口并处理响应
    console.log('准备发送请求，参数:', JSON.stringify(params))
    merchantAPI.handleBillApi(params).then((res: responseType) => {
      console.log('处置账单响应:', JSON.stringify(res))
      if (res.success) {
        promptAction.showToast({ message: res.msg || '操作成功' })
        setTimeout(() => {
          router.replaceUrl({ url: 'pages/common/index', params: { tabIndex: '2' } })
        }, 300)
      } else {
        // 显示后端返回的错误信息
        const errorMsg = res.msg || res.code ? `错误代码: ${res.code}, 错误信息: ${res.msg || '操作失败'}` : '操作失败，请重试'
        console.error('处置账单失败:', errorMsg)
        promptAction.showToast({ message: errorMsg })
      }
    }).catch((err: Error) => {
      console.error('处置账单请求异常:', JSON.stringify(err))
      console.error('错误类型:', typeof err)
      console.error('错误消息:', err?.message)
      console.error('错误堆栈:', err?.stack)
      
      // 尝试从错误信息中提取更详细的错误描述
      let errorMsg = '网络请求失败，请检查：\n1. 后端服务是否启动\n2. 网络连接是否正常\n3. 服务器地址是否正确'
      
      if (err) {
        if (err.message) {
          errorMsg = err.message
        } else if ((err as any).code) {
          errorMsg = `错误代码: ${(err as any).code}`
        } else if ((err as any).response) {
          const response = (err as any).response
          if (response.msg) {
            errorMsg = response.msg
          } else if (response.message) {
            errorMsg = response.message
          }
        } else if (typeof err === 'string') {
          errorMsg = err
        }
      }
      
      console.error('最终错误信息:', errorMsg)
      promptAction.showToast({ message: errorMsg, duration: 5000 })
    })
  }
}