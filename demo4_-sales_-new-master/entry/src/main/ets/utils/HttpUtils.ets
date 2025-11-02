import http from '@ohos.net.http';
import router from '@ohos.router';
import promptAction from '@ohos.promptAction';

//let BASE_URL = 'http://localhost:8000';
let BASE_URL = 'http://42.193.243.96:9999'
const TAG: string = '[HttpUtils]'


export default class HttpUtils {
  private static instance: HttpUtils | null = null;
  private url: string;
  private extraData?: string | Object;
  private options: http.HttpRequestOptions;
  private header = {
    "Content-Type": 'application/json',
    token: '2'
  }
  private context = getContext(this);

  constructor() {
    this.url = '';
    this.options = {
      method: http.RequestMethod.GET,
      header: {
        'Content-Type': 'application/json',
      },
      readTimeout: 60000,
      connectTimeout: 60000
    }
  }

  static getInstance(): HttpUtils {
    if (HttpUtils.instance == null) {
      HttpUtils.instance = new HttpUtils();
    }
    return HttpUtils.instance;
  }

  setReadTimeout(readTimeout: number) {
    this.options.readTimeout = readTimeout;
  }

  setConnectTimeout(connectTimeout: number) {
    this.options.connectTimeout = connectTimeout;
  }

  setHeader(header) {
    this.options.header = header;
  }

  setUrl(url: string) {
    this.url = url;
  }

  setExtraData(extraData: string | Object | ArrayBuffer | undefined) {
    this.options.extraData = extraData;
  }

  setOptions(option: Object) {
    this.options = option;
  }

  setMethod(method: string) {

    switch (method) {
      case 'GET':
        this.options.method = http.RequestMethod.GET;
        break;
      case 'POST':
        this.options.method = http.RequestMethod.POST;
        break;
      case 'PUT':
        this.options.method = http.RequestMethod.PUT;
        break;
      case 'DELETE':
        this.options.method = http.RequestMethod.DELETE;
        break;
      default:
        this.options.method = http.RequestMethod.GET;
        break;
    }

  }

  sendRequest<T>(url: string): Promise<T> {
    console.log('url', url)
    let token: string | undefined = AppStorage.Get('token') || undefined
    this.setHeader({
      "Content-Type": 'application/json',
      token,
    })

    let requestUrl = BASE_URL + url
    let httpRequest = http.createHttp();
    if (this.options.method == http.RequestMethod.GET) {
      if (this.options.extraData && Object.keys(this.options.extraData).length) {
        requestUrl += "?" + Object.keys(this.options.extraData).map(key => {
          if (this.options.extraData[key]) {
            return `${key}=${encodeURI(this.options.extraData[key])}` // a=1 =>
          }
          return ""
        }).join('&') //['a=1','b=2','c=3']
      }
      this.options.extraData={}
      console.log('urlStr', JSON.stringify(this.options.extraData), url, requestUrl)
    }
    return new Promise((resolve, reject) => {
      try {
        httpRequest.request(requestUrl, this.options).then((data) => {

          const response = JSON.parse(`${data.result}`)
          if (response.code == 400 && response.msg == '请登录') {
            promptAction.showToast({
              message: response.msg
            })
            this.backToLogin()
            return
          } else if (response.code === 401) {
            //401 token超时
            //删除持久化数据token
            this.backToLogin()
            promptAction.showToast({
              message: 'token超时！'
            })
            return
          }
          return resolve(response)
        }).catch((err: Error) => {
          console.info('error:' + JSON.stringify(err));
          return reject(err)
        })
      } catch (err) {
        return reject(err)
      }

    })
  }

  get<T>(url: string, requestData?: string | Object | ArrayBuffer | undefined): Promise<T> {
    this.setMethod('GET');

    this.setExtraData(requestData);

    return this.sendRequest(url);
  }

  post<T>(url: string, requestData?: string | Object | ArrayBuffer | undefined): Promise<T> {
    this.setMethod('POST');
    this.setExtraData(requestData);
    return this.sendRequest(url);
  }

  put<T>(url: string, requestData?: string | Object | ArrayBuffer | undefined): Promise<T> {
    this.setMethod('PUT');
    this.setExtraData(requestData);
    return this.sendRequest(url);
  }

  delete<T>(url: string, requestData?: string | Object | ArrayBuffer | undefined): Promise<T> {
    this.setMethod('DELETE');
    this.setExtraData(requestData);
    return this.sendRequest(url);
  }

  backToLogin() {
    AppStorage.SetOrCreate('userType', '1')
    AppStorage.SetOrCreate('account', '')
    AppStorage.SetOrCreate('userId', '')
    AppStorage.SetOrCreate('token', '')
    AppStorage.SetOrCreate('pwd', '')
    router.replaceUrl({
      url: 'pages/login/login'
    })
    router.clear()
  }
}



