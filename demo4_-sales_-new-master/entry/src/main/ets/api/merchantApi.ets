import HttpUtils from '../utils/HttpUtils'
import {
  deviceResType,
  responseType,
  goodsNameResType,
  saleNumberResType,
  behaviorResType,
  sellTrendResType,
  SaleRankResType,
  SalesResType,
  warnResType,
  deviceListResType,
  deviceDetailResType,
  userListResType,
  billDetailResType,
  data4ResType
} from '../typeDeclare/responseType'

class MerchantAPI {
  // 设备分页
  deviceManageApi(params: object): Promise<deviceResType> {
    return HttpUtils.getInstance().get('/device/page', params);
  }

  // 商品列表名称
  getGoodsNameApi(): Promise<goodsNameResType> {
    return HttpUtils.getInstance().get('/product/listName',{});
  }

  // 商品补货
  supplyGoodsApi(params: object): Promise<responseType> {
    return HttpUtils.getInstance().get('/product/replenishment', params);
  }

  // 商品详情
  goodsDetailApi(params: object): Promise<deviceDetailResType> {
    return HttpUtils.getInstance().get('/product/detail', params);
  }

  //设备列表 包含货架和格子两级
  shelfListLApi(params?: object): Promise<deviceListResType> {
    return HttpUtils.getInstance().get('/device/list', params);
  }

  // 新增或者修改商品
  submitGoodsApi(data: object): Promise<responseType> {
    return HttpUtils.getInstance().post('/product/submit', data);
  }

  // 商品销售数量统计
  saleNumberApi(params: object): Promise<saleNumberResType> {
    return HttpUtils.getInstance().get('/statistics/numberCount', params);
  }

  //用户选购行为统计
  behaviorApi(params: object): Promise<behaviorResType> {
    return HttpUtils.getInstance().get('/statistics/purchaseBehavior', params);
  }

  //商品销售趋势
  sellTrendApi(params: object): Promise<sellTrendResType> {
    return HttpUtils.getInstance().get('/statistics/saleTrend', params);
  }

  //商品库存统计
  inventoryApi(): Promise<data4ResType> {
    return HttpUtils.getInstance().get('/statistics/inventory',{});
  }

  // 销售统计
  salesApi(): Promise<SalesResType> {
    return HttpUtils.getInstance().get('/statistics/saleCount',{});
  }

  // 销量排行
  SaleRankApi(params: object): Promise<SaleRankResType> {
    return HttpUtils.getInstance().get('/statistics/SaleRank', params);
  }

  // 补货预警
  replenishWarnApi(): Promise<warnResType> {
    return HttpUtils.getInstance().get('/product/replenishWarn');
  }

  //设备异常
  deviceWarnApi(): Promise<warnResType> {
    return HttpUtils.getInstance().get('/device/deviceWarn',{});
  }

  // 账户列表
  getUserListApi(): Promise<userListResType> {
    return HttpUtils.getInstance().get('/blade-system/user/getUserList');
  }

  // 处置账单
  handleBillApi(data: object): Promise<responseType> {
    return HttpUtils.getInstance().post('/bill/handleBill',data);
  }

  // 账单详情
  billDetailApi(id: string): Promise<billDetailResType> {
    return HttpUtils.getInstance().get(`/bill/detail?id=${id}`);
  }
}


export let merchantAPI = new MerchantAPI();