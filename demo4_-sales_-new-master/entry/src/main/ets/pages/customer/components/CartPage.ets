import { CartItem, removeFromCart, updateCartItemCount, getCartTotal } from '../../../utils/CartData';
import router from '@ohos.router';
import promptAction from '@ohos.promptAction';

@Entry
@Component
struct CartPage {
  @StorageLink('cartList') cartList: CartItem[] = [];

  build() {
    Column() {
      // 返回按钮和标题
      Row() {
        Button('返回')
          .onClick(() => router.back()) // 必须有onClick事件
          .backgroundColor('#fff')
        Text('我的购物车')
          .fontSize(20)
          .margin({ left: 20 })
      }
      .padding(15)
      .width('100%')

      // 购物车为空时显示
      if (this.cartList.length === 0) {
        Column() {
          Text('购物车是空的，去加商品吧~')
            .margin({ top: 50 })
          // 新增一个“去首页”按钮，必须有onClick
          Button('去首页')
            .margin({ top: 20 })
            .onClick(() => router.back()) // 点击返回首页
        }
      } else {
        // 商品列表
        List() {
          ForEach(this.cartList, (item: CartItem) => {
            ListItem() {
              Row() {
                Image(item.image).width(60).height(60)
                Column() {
                  Text(item.name)
                  Text('￥' + item.price)
                }.margin({ left: 10 })
                Row() {
                  // 减号按钮：必须有onClick
                  Button('-')
                    .width(30)
                    .height(30)
                    .onClick(() => updateCartItemCount(item.id, item.count - 1))
                  Text(item.count.toString()).width(30).textAlign(TextAlign.Center)
                  // 加号按钮：必须有onClick
                  Button('+')
                    .width(30)
                    .height(30)
                    .onClick(() => updateCartItemCount(item.id, item.count + 1))
                }.margin({ left: 20 })
                // 删除按钮：必须有onClick
                Button('删除')
                  .backgroundColor('gray')
                  .margin({ left: 20 })
                  .onClick(() => removeFromCart(item.id))
              }.padding(10)
            }
          })
        }

        // 合计
        Text('合计：￥' + getCartTotal().toFixed(2))
          .fontSize(16)
          .fontColor('red')
          .margin({ top: 20 })
      }
    }.width('100%').height('100%')
  }
}