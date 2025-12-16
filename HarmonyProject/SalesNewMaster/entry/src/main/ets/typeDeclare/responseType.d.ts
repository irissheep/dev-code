import { Resource } from 'GlobalResource';

export interface responseType {
  success: boolean,
  msg: string | Resource,
  code?: number
}

// 一键已读
export interface booleanDataRes extends responseType {
  data:boolean
}

export interface PageParamsType {
  size: number
  current: number
  total?: number
}

export interface userType {
  id?: string
  account?: string
  avatar?: string
  name?: string
  userId?: string
  isCertified?: number
}

export interface loadingStatusType {
  loading?: string
  loadMore?: string
  loadFailed?: string
  noMore?: string
}


export interface responseTypeWithData {
  success: boolean,
  msg?: string,
  code?: number,
  data?: object | Array<any>
}

// login 登录接口
export interface loginResponseDataType {
  category: string
  account: string
  id: string
  token: string
}

export interface loginResType extends responseType {
  data: loginResponseDataType,
}


// 注册接口
export interface registerResType extends responseType {
  data: loginResponseDataType,
}

//消费记录/ 账单记录

interface consumeItemType {
  price: string
  pictureUrl: string
  name: string
  number: number
}

export interface consumeListItemType {
  id:string
  billNo: string
  action: string
  account: string
  productList: Array<consumeItemType>
  createTime: string
  balance: string
  status: string
}

interface dataType {
  records: Array<consumeListItemType>,
  total: number
}

export interface consumeResType extends responseType {
  data: dataType
}

//通用-我的
export interface myResType extends responseType {
  data: userType
}

interface balanceType extends responseType {
  balance: string
  id?: string
}

export interface myBalanceResType extends responseType {
  data: balanceType
}

//客户首页商品分页
export interface goodsItemType {
  shelf: string
  name: string
  price: string
  number: number
  pictureUrl: string
  id: string
  cell?: string
  status?: string
}

interface goodsDataType {
  records: goodsItemType[]
  total: number
}

interface goodsResType extends responseType {
  data: goodsDataType
}


// 充值记录接口
export interface recordItemType {
  id: string
  createTime: string
  rechargeAmount: string
}

interface recordDataType {
  records: recordItemType[]
  total: number
}

export interface rechargeResType extends responseType {
  data: recordDataType
}

// 商品名称列表接口
export interface goodsNameResType extends responseType {
  data: Array<string>
}

// 统计数据接口

export interface saleNumberResType extends responseType {
  data: Array<{
    number: number,
    name: string
  }>
}

export interface behaviorResType extends responseType {
  data: Array<{
    successCount: number,
    name: string,
    cancelCount: number
  }>
}

export interface sellTrendResType extends responseType {
  data: Array<{
    number: number,
    saleDate: string,
  }>
}
// 商品库存统计
export interface data4Type{
  "shelf":string,
  "name":string,
  "number":string
}
export interface data4ResType extends responseType {
  data:data4Type[]
}
// 设备管理接口
export
interface deviceItemType {
  id: string
  recentReportTime: string
  status: string
  category: string
  name: string
  deviceNo:string
}

interface deviceDataType {
  records: deviceItemType
  total: number
}

export interface deviceResType extends responseType {
  data: deviceDataType
}


// 销售统计接口
interface SalesType {
  totalAmount: string
  count: string
}

export interface SalesResType extends responseType {
  data: SalesType
}


export interface saleRankType {
  name: string
  price: string
  number: string
}

// 销量排行接口
export interface SaleRankResType extends responseType {
  data: saleRankType[]
}

// 补货预警和设备异常
export interface swiperListType {
  name: string
  number?: string
  shelf?: string
  deviceNo?: string
  status?: string
  recentReportTime?: string
}

export interface warnResType extends responseType {
  data: swiperListType[]
}

interface messageListType {
  "id"?: string
  "title"?: string
  "msg"?: string
  "userId"?: string
  "status"?: number
  "billId"?: string
  "createTime"?: string
  "type"?: string
}

interface msgListType {
  records: Array<messageListType>,
  total: number
}

export interface messageListResType extends responseType {
  data: msgListType
}


// 设备列表接口

export interface deviceListItemType {
  id?: string
  name?: string
  cell?: string
  childrenTreeVO?: {
    id?: string
    name?: string
    cell?: string
  }
}

export interface deviceListResType extends responseType {
  data: deviceListItemType[]
}

// 设备详情接口
export interface deviceDetailItemType {
  id?: string
  name?: string
  deviceId?: string //所在货架
  shelf?: string //商品货架
  price?: number
  number?: number
  thresholdValue?: string
  weight?: string
  pictureUrl?: string
}

export interface deviceDetailResType extends responseType {
  data: deviceDetailItemType
}

export interface userListResType extends responseType {
  data: {
    id: string
    account: string
  }[]
}

// 订单详情接口

interface goodsType {
  id: string
  name: string
  price: string
  pictureUrl: string
  number: string
}

export  interface billDetailInfoType {
  id?: string
  billNo?: string
  status?: string //1 已完成，2 异常退回，3 异常取走，4 已忽略
  productList?: goodsType[]
  createTime?: string
  goodsImg?: string
  pictureUrl?: string
  amount?: number
  totalAmount?:string
  totalNumber?:number
  userId?: string | number // 用户id
}

export interface  billDetailResType  extends responseType{
  data:billDetailInfoType
}