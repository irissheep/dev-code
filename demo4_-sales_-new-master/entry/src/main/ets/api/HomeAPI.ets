import HttpUtils from '../utils/HttpUtils';
import {
  booleanDataRes,
  rechargeResType,
  responseType,
  loginResType,
  registerResType,
  consumeResType,
  myResType,
  myBalanceResType,
  messageListResType
} from '../typeDeclare/responseType'

class HomeAPI {
  // 登录
  doLoginApi(data: any): Promise<any>{
    return HttpUtils.getInstance().post('/blade-system/user/login', data);
  }

  // 用户注册
  registerApi(data: object): Promise<registerResType> {
    return HttpUtils.getInstance().post('/blade-system/user/register', data);
  }

  // 退出登录
  loginOutApi(params: object): Promise<object> {
    return HttpUtils.getInstance().get('/blade-system/user/logout', params);
  }

  // 用户详情
  getUserInfoApi(params: object): Promise<myResType> {
    return HttpUtils.getInstance().get('/blade-system/user/userInfo', params);
  }

  //修改信息
  // editUserInfoApi(data?: object): Promise<T> {
  //   return HttpUtils.getInstance().post('/blade-system/user/edit', data);
  // }
  // 账户余额
  getBalanceApi(params: object): Promise<myBalanceResType> {
    return HttpUtils.getInstance().get('/account/getBalance', params);
  }

  // 账户充值
  rechargeApi(data: object): Promise<responseType> {
    return HttpUtils.getInstance().post('/account/add', data);
  }

  // 充值记录
  rechargeRecordApi(params): Promise<rechargeResType> {
    return HttpUtils.getInstance().get(`/account/log`, params);
  }

  // 修改密码
  editPwdApi(data: object): Promise<responseType> {
    return HttpUtils.getInstance().post('/blade-system/user/editPwd', data);
  }

  // 消费/账单记录 分页
  consumeRecordApi(params: object): Promise<consumeResType> {
    return HttpUtils.getInstance().get('/bill/page', params);
  }

  // 消息列表
  messageListApi(params: object): Promise<messageListResType> {
    return HttpUtils.getInstance().get('/bill/messageList', params);
  }

  // 一键已读
  clearAllMsgApi(userId:string): Promise<booleanDataRes> {
    return HttpUtils.getInstance().post(`/bill/editAllStatus?userId=${userId}&status=1`,{});
  }
 // 清空消息
  clearMsgApi(userId:string): Promise<booleanDataRes> {
    return HttpUtils.getInstance().get(`/bill/clear`,{userId});
  }
  // 编辑单个消息状态
  msgStatusApi(data: object): Promise<booleanDataRes> {
    return HttpUtils.getInstance().post('/bill/editStatus', data);
  }

  // 编辑头像
  editAvatarApi(data: object): Promise<responseType> {
    return HttpUtils.getInstance().post('/blade-system/user/edit', data);
  }
  downloadAvatarApi(fileName): Promise<responseType> {
    return HttpUtils.getInstance().get(`/images/download`,{fileName,type:1});
  }
  uploadApi(data): Promise<responseType> {
    return HttpUtils.getInstance().post(`/images/uploadBase64`,data);
  }
  //人脸识别
  uploadFacialApi(data): Promise<responseType> {
    return HttpUtils.getInstance().post(`/images/recognizeImageBase64`,data);
  }

}

export let homeApi = new HomeAPI();

