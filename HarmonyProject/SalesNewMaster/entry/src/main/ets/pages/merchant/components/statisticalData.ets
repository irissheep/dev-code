import router from '@ohos.router';
import promptAction from '@ohos.promptAction'
import { McBarChart, Options, McLineChart } from '@mcui/mccharts'
import { merchantAPI } from '../../../api/merchantApi'
import dayjs from "dayjs";
import {
  goodsNameResType,
  responseType,
  saleNumberResType,
  behaviorResType,
  sellTrendResType,
  data4ResType,
  data4Type
} from '../../../typeDeclare/responseType'

const today = dayjs().format('YYYY-MM-DD 23:59:59')
const beforeSeven = dayjs().subtract(7, 'day').format('YYYY-MM-DD 00:00:00')
const beforeMonth = dayjs().subtract(30, 'day').format('YYYY-MM-DD 00:00:00')

interface goodsListOptionType {
  value: string
}

interface timeParams {
  startTime: string | undefined
  endTime: string | undefined
}

// 数据统计
@Component
export struct statisticalData {
  selectedStartDate: Date = new Date("2010-1-1")
  selectedEndDate: Date = new Date("2010-1-1")
  @State goodsListOption: goodsListOptionType[] = []
  @State selectType1: string = '7'
  @State selectType2: string = '7'
  @State data4: data4Type[] = []
  private scrollerForScroll: Scroller = new Scroller()
  // 初始化数据
  @State saleNumberOption: Options =new Options( {
    cPaddingL:40,
    color:['#296DFF', '#ff5495fd',],
    xAxis: {
      boundaryGap:true,
      // axisLine: { // 轴线样式
      //   show: true, // 是否显示
      //   lineStyle: {
      //     color: '#ccc',
      //     width: 2
      //   }
      // },
      // axisTick: { // 刻度线配置
      //   show: true, // 是否显示
      //   length: 4, // 刻度的长度
      //   lineStyle: {
      //     color: '#ccc', // 刻度线颜色
      //     width: 2 // 刻度线宽度
      //   }
      // },
      axisLabel: { // x轴文本标签样式
        color: '#666',
        fontSize: 30,
        overflow:'breakAll'
      },
      data: []
    },
    yAxis: {
      name: '',

    },
    legend: {
      show: false
    },
    series: [
      {
        name: '商品销售数量统计',
        barStyle: {
          color: 'red',
        },
        color: 'red',
        data: []
      },
    ],


  })
  @State behaviorOption: Options = new Options({
    color:['#296DFF', '#ff5495fd',],
    cPaddingB: 60,
    xAxis: {
      data: []
    },

    legend: {
      show: true,
      top: '93%'
    },
    series: [
      {
        name: '商品销售数量统计',
        data: []
      }, {
      name: '商品销售数量统计',
      data: []
    },
    ]
  })
  @State trendOption: Options = new Options({
    color:['#296DFF',],
    cPaddingB: 90,
    xAxis: {
      data: []
    },
    legend: {
      show: true,
      top: '84%'
    },
    series: [
      {
        name: '商品销售趋势',
        data: []
      },
    ]
  })
  @State titleList: String[] = ['序号', '货架', '商品名称', '库存数量',]

  @Styles
  sameStyle() {
    .width('96%')
    .backgroundColor('#fff')
    .borderRadius(10)
    .padding(20)
    .margin({ bottom: 20 })
  }

  async aboutToAppear() {
    this.getData1('btn', '7')
    this.getData2('btn', '7')
    this.getData3('')
    this.getData4()


    const res: goodsNameResType = await merchantAPI.getGoodsNameApi()
    const data: string[] = res.data
    const handleData =data.map(i =>({value:i}))  || []
    this.goodsListOption = [{ value: "全部" }, ...handleData]
  }

  @Builder
  customNav() {
    Row() {
      Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
        Text('数据统计').fontSize(19)
        Image($r('app.media.my'))
          .width(20)
          .onClick(() => {
            router.pushUrl({ url: 'pages/merchant/merchantMy' })
          })

      }
    }.position({x:0}).width('100%').padding({ left: 20, right: 20, top: 10, bottom: 10 }).height(50).backgroundColor('#f9fafc')
  }

  build() {
    Navigation() {
      Column() {
        Scroll(this.scrollerForScroll) {
          Column() {
            Column() {
              Flex({ justifyContent: FlexAlign.SpaceBetween }) {
                Text('商品销售数量统计')
                Row() {
                  Image($r('app.media.more')).width(20).margin({ right: 8 }).onClick(() => {
                    this.selectType1 = 'free'
                    this.openTimeRange('1')
                  })
                  Text('近7天')
                    .borderWidth(1)
                    .borderColor(this.selectType1 == '7' ? Color.Blue : '#000')
                    .backgroundColor(this.selectType1 == '7' ? Color.Blue : '#fff')
                    .fontColor(this.selectType1 == '7' ? '#ffff' : '#000')
                    .onClick(() => {
                      this.selectType1 = '7'
                      this.getData1('btn', '7')
                    })
                  Text('近1月')
                    .borderWidth(1)
                    .borderColor(this.selectType1 == '30' ? Color.Blue : '#000')
                    .backgroundColor(this.selectType1 == '30' ? Color.Blue : '#fff')
                    .fontColor(this.selectType1 == '30' ? '#ffff' : '#000')
                    .onClick(() => {
                      this.selectType1 = '30'
                      this.getData1('btn', '30')
                    })
                }
              }.margin({ bottom: 8 })

              Divider().strokeWidth(1)
              McBarChart({
                options: this.saleNumberOption
              }).width('100%').height('100%')
            }.sameStyle().height(400)

            Column() {
              Flex({ justifyContent: FlexAlign.SpaceBetween }) {
                Text('用户选购行为统计')
                Row() {
                  Image($r('app.media.more')).width(20).margin({ right: 8 })
                    .onClick(() => {
                      this.selectType1 = 'free'
                      this.openTimeRange('2')
                    })
                  Text('近7天')
                    .borderWidth(1)
                    .borderColor(this.selectType2 == '7' ? Color.Blue : '#000')
                    .backgroundColor(this.selectType2 == '7' ? Color.Blue : '#fff')
                    .fontColor(this.selectType2 == '7' ? '#ffff' : '#000')
                    .onClick(() => {
                      this.selectType2 = '7'
                      this.getData2('btn', '7')
                    })
                  Text('近1月')
                    .borderWidth(1)
                    .borderColor(this.selectType2 == '30' ? Color.Blue : '#000')
                    .backgroundColor(this.selectType2 == '30' ? Color.Blue : '#fff')
                    .fontColor(this.selectType2 == '30' ? '#ffff' : '#000')
                    .onClick(() => {
                      this.selectType2 = '30'
                      this.getData2('btn', '30')
                    })
                }
              }.margin({ bottom: 8 })

              Divider().strokeWidth(1)
              McBarChart({
                options: this.behaviorOption
              }).width('100%').height('100%')
            }.sameStyle().height(400)

            Column() {
              Flex({ justifyContent: FlexAlign.SpaceBetween }) {
                Text('商品销售趋势')
                Select(this.goodsListOption)
                  .selected(undefined)
                  .value('全部')
                  .font({ size: 16, weight: 500 })
                  .fontColor('#182431')
                  .selectedOptionFont({ size: 16, weight: 400 })
                  .optionFont({ size: 16, weight: 400 })
                  .onSelect((index: number, value: string) => {
                    if (value == '全部') {
                      this.getData3('')
                    } else {
                      this.getData3(value)
                    }


                  })
              }.margin({ bottom: 8 })

              Divider().strokeWidth(1)
              McLineChart({
                options: this.trendOption
              }).width('100%').height('100%')
            }.sameStyle().height(400)



            Column() {
              Flex({justifyContent:FlexAlign.Start}){
                Text('商品库存统计')
              }
              Divider().strokeWidth(1).margin({ bottom: 10 })
              Row() {
                ForEach(this.titleList, (item: string, index: number) => {
                  if (index == 0) {
                    Text(item).height(28).width('13%').textAlign(TextAlign.Center).backgroundColor('#f7f7fa')
                  } else {
                    Text(item)
                      .height(28)
                      .width('29%')
                      .textAlign(TextAlign.Center)
                      .backgroundColor('#f7f7fa')
                      .borderWidth({ left: index == 1 ? 1 : 0, right: (index == 2 || index == 1) ? 1 : 0 })
                      .borderColor('#cccccc')
                  }
                }, (item: string) => item)
              }.borderWidth(1).width('100%').borderColor('#cccccc')

              if (this.data4.length > 0) {
                ForEach(this.data4, (item: data4Type, index: number) => {
                  Row() {
                    Text((index + 1).toString()).height(36).width('13%').textAlign(TextAlign.Center)
                    Text(item.shelf)
                      .height(36)
                      .width('29%')
                      .textAlign(TextAlign.Center)
                      .borderWidth({ left: 1, right: 1 })
                      .borderColor('#cccccc')
                    Text(item.name)
                      .height(36)
                      .width('29%')
                      .textAlign(TextAlign.Center)
                      .borderWidth({ right: 1 })
                      .borderColor('#cccccc')
                    Text(item.number.toString()).height(36).width('29%').textAlign(TextAlign.Center)
                  }.borderWidth({ bottom: 1, left: 1, right: 1 }).width('100%').borderColor('#cccccc')
                }, (item: data4Type) => item.name)
              } else {
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
            }.sameStyle()

          }.padding({ bottom: 20 })
        }
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

  openTimeRange(group: string) {
    let startTime: string | undefined = undefined
    let endTime: string | undefined = undefined
    DatePickerDialog.show({
      start: new Date("2000-1-1"),
      end: new Date("2100-12-31"),
      onAccept: (value: DatePickerResult) => {
        // 通过Date的setFullYear方法设置按下确定按钮时的日期，这样当弹窗再次弹出时显示选中的是上一次确定的日期
        this.selectedStartDate.setFullYear(value?.year, value?.month, value?.day)
        const start:string =  dayjs(this.selectedStartDate).format('YYYY-MM-DD');
        startTime = `${start} 00:00:00`
        console.log('开始时间dayjs',startTime)
        DatePickerDialog.show({
          start: new Date( this.selectedStartDate),
          end: new Date("2100-12-31"),
          selected: new Date(this.selectedStartDate), //selectedStartDate
          onAccept: (endValue: DatePickerResult) => {
            this.selectedEndDate.setFullYear(value?.year, value?.month, value?.day)
            const end:string =  dayjs( this.selectedEndDate).format('YYYY-MM-DD');
            endTime = `${end} 23:59:59`
            console.log('结束时间',endTime)
            if (group == '1') {
              this.getData1('free', startTime + ',' + endTime)
            } else {
              this.getData2('free', startTime + ',' + endTime)
            }
          }
        })
      }
    })
  }

  getData1(type: string, data: string) {
    const params: timeParams = { startTime: '', endTime: '' }
    if (type == 'btn') {
      params.endTime = today
      if (data == '7') {
        params.startTime = beforeSeven
      } else {
        params.startTime = beforeMonth
      }
    } else {
      // 日期选择器的时间
      params.startTime = data.split(',')[0]
      params.endTime = data.split(',')[1]
    }
    console.log('params11', JSON.stringify(params))
    merchantAPI.saleNumberApi(params).then((res: saleNumberResType) => {
      if (res.success) {
        console.log('data1', JSON.stringify(res.data))
        this.saleNumberOption.setVal({
          xAxis: {
            data: res.data.map(i => i.name) || []
          },
          series: [
            {
              name: '商品销售数量统计',
              data: res.data.map(i => i.number) || []
            }
          ]
        })
      }
    })
  }

  //
  getData2(type: string, data: string) {
    const params: timeParams = { startTime: '', endTime: '' }
    if (type == 'btn') {
      params.endTime = today
      if (data == '7') {
        params.startTime = beforeSeven
      } else {
        params.startTime = beforeMonth
      }
    } else {
      // 日期选择器的时间
      // 日期选择器的时间
      params.startTime = data.split(',')[0]
      params.endTime = data.split(',')[1]
    }
    merchantAPI.behaviorApi(params).then((res: behaviorResType) => {
      if (res.success) {
        console.log('data2', JSON.stringify(res))
        this.behaviorOption.xAxis.data = res.data.map(i => i.name) || []

        this.behaviorOption.setVal({
          // xAxis: {
          //   data: res.data.map(i => i.name)
          // },
          series: [
            {
              name: '购买成功',
              data: res.data.map(i => i.successCount) || []
            },
            {
              name: '购买取消',
              data: res.data.map(i => i.cancelCount) || []
            }
          ]
        })
      }
    })
  }

  getData3(value: string) {
    interface par {
      name: string
    }

    const data: par = { name: value ? value : '' }
    merchantAPI.sellTrendApi(data).then((res: sellTrendResType) => {
      if (res.success) {
        this.trendOption.xAxis.data = res.data.map(i => i.saleDate.toString()) || []
        // this.trendOption.series[0].data =res.data.map(i => i.number)
        this.trendOption.setVal({
          // xAxis: {
          //   data: res.data.map(i => i.saleDate.toString())
          //   // data:['2011','2011','2011','2011-04-08']
          // },
          series: [
            {
              name: '商品销售趋势',
              data: res.data.map(i => i.number)|| []
              // data:[100,300,1000,2222]
            }
          ]
        })
      }
    })
  }

  // 商品库存统计
  getData4() {
    merchantAPI.inventoryApi().then((res: data4ResType) => {
      if (res.success) {
        this.data4 = res.data
        console.log('商品库存统计data4', JSON.stringify(res))

      }
    })
  }
}