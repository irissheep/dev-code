import HttpUtils from '../utils/HttpUtils'
import {goodsResType} from '../typeDeclare/responseType'

class CustomerAPI {
  // 商品分页
  productPageApi(params: object): Promise<goodsResType> {
    return HttpUtils.getInstance().get('/product/page', params);
  }
  // // 商品列表  /product/list
  // productListApi(params: object): Promise<void> {
  //   return HttpUtils.getInstance().get('/product/page', params);
  // }
// 商品详情
//   productDetailApi(params: object): Promise<void> {
//     return HttpUtils.getInstance().get('/product/detail', params);
//   }
  // 新增修改商品
  // submitProductApi(data: object): Promise<void> {
  //   return HttpUtils.getInstance().post('/product/submit', data);
  // }

}


export let customerAPI = new CustomerAPI();