import {loadingStatusKeyMap} from  '../../../utils/config.data'

@Component
export struct loadStatusCom{
  @Prop loadStatus: string;
  @Link refresh: boolean;
  build(){
    Column(){
      Flex({ justifyContent: FlexAlign.Center, alignItems: ItemAlign.Center }) {
        if (this.loadStatus =="loading") {
          LoadingProgress().width(26).height(26)
          Text('加载中...').margin({ left: 7 }).fontColor(Color.Grey)
        } else {
          Text(loadingStatusKeyMap[this.loadStatus])
            .fontColor(Color.Grey)
            .onClick(() => {
              if(this.loadStatus =="loadFailed"){
                // this.loadStatus ='loadMore'
                this.refresh = true // 回调父组件
              }
            })
        }
      }.width('100%').height(50)
    }.width('100%').height(50)
  }
}