import router from '@ohos.router';
import promptAction from '@ohos.promptAction'
import prompt from '@ohos.promptAction';
import common from '@ohos.app.ability.common';
import abilityAccessCtrl from '@ohos.abilityAccessCtrl';
import axios, { AxiosError, AxiosProgressEvent, AxiosResponse, FormData, InternalAxiosRequestConfig } from '@ohos/axios'
import fs from '@ohos.file.fs';
import { copyFile,toBase64 } from '../../utils/index'
import { myResType } from '../../typeDeclare/responseType.d'
import { homeApi } from '../../api/HomeAPI'
import AlbumModel from '../../model/AlbumModel'

enum CameraMode {
  modePhoto = 0, // 拍照模式
  modeVideo = 1 // 录像模式
};

const TAG: string = '[CameraPage]';

let context: Context = getContext(this) as common.UIAbilityContext;

enum status {
  no1 = '未上传',
  no2 = '展示照片',
  no3 = '审核中',
  no4 = '通过',
  no5 = '未通过'
}

@Entry
@Component
struct facial {
  @State curStatus: string = status.no1
  @State progress: number = 0
  @State imageThumbnail: string = '';
  @State currentModel: number = CameraMode.modePhoto;
  private albumModel: AlbumModel = new AlbumModel();

  @Styles
  samePositionStyle(){
    .position({ x: '50%', y: 100 })
    .translate({ x: '-50%' })
  }

  onPageShow() {
    this.init()
  }

  async init() {
    let atManager: abilityAccessCtrl.AtManager = abilityAccessCtrl.createAtManager();
    await atManager.requestPermissionsFromUser(context, [
      'ohos.permission.READ_MEDIA',
      'ohos.permission.WRITE_MEDIA'
    ], (err, data) => {
      if (err) {
        console.log(`requestPermissionsFromUser fail, err->${JSON.stringify(err)}`);
      } else {
        // console.info('这是data:' + JSON.stringify(data));
        // console.info('data permissions:' + data.permissions);
        // console.info('data authResults:' + data.authResults);

      }
    })

  }

  @Builder
  customNav() {
    Row() {
      Flex({ justifyContent: FlexAlign.SpaceBetween }) {
        Row() {
          Image($r('app.media.back')).width(20).margin({ right: 14 }).onClick(() => {
            this.curStatus = status.no1
            router.replaceUrl({ url: 'pages/common/index', params: { tabIndex: '2 ' } })
          })
          Text("人脸识别认证").fontSize(19)
        }

        if (!AppStorage.Get('token')) {
          Text('跳过').onClick(() => {
            router.pushUrl({ url: 'pages/common/index' })
          })
        }
      }
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
        Stack() {
          Column() {
            Column() {
              Button('选择人脸图片').onClick(() => {
                this.albumModel.selectPicture()
                this.albumModel.setCallback((str: string) => {
                  // console.log('选择图片后的回调',str) //datashare:///media/image/151
                  this.imageThumbnail = str
                  this.curStatus = status.no2
                })
              })
              if (this.imageThumbnail && this.curStatus == status.no2) {
                Image(this.imageThumbnail)
                  .width(200)
                  .height(200)
                  .borderRadius(100)
                  .offset({ x: 0, y: 30 })
                Button('提交').onClick(() => {
                  const context = getContext(this) as common.UIAbilityContext;
                  const fileName = copyFile(this.imageThumbnail, context, 'faceImg')
                  this.faceIdentify(fileName)
                }).offset({ x: 0, y: 50 })
              }
            }

            if (this.curStatus == status.no3) {
              Column() {
                Progress({ value: 0, total: 100, type: ProgressType.Ring })
                  .width(50)
                  .color(Color.Blue)
                  .style({ strokeWidth: 6 })
                  .margin({ bottom: 16 })
                Text('识别中').fontSize(18).fontWeight(500)
              }.samePositionStyle()
            }
            if (this.curStatus == status.no5) {
              Column() {
                Image($r('app.media.failed')).width(50).height(50).margin({ bottom: 16 })
                Text('未通过').fontSize(18).fontWeight(500)
              }.samePositionStyle()
            }
            if (this.curStatus == status.no4) {
              Column() {
                Image($r('app.media.success')).width(50).height(50).margin({ bottom: 16 })
                Text('已通过').fontSize(18).fontWeight(500)
              }.samePositionStyle()
            }
          }.width('100%').height('100%').padding({ top: 80 })
        }
        .backgroundColor('#f2f2f2')
        .width('100%')
        .height('100%')
      }
      .width('100%')
      .height('100%')
    }
    .title({ builder: this.customNav, height: 50 })
    .mode(NavigationMode.Auto)
    .titleMode(NavigationTitleMode.Mini)
    .width('100%')
    .height('100%')
    .backgroundColor('#f4f9fd')
  }

  // 提交人脸数据
  faceIdentify(fileName: string) {
    const userId: string = AppStorage.Get('userId') || ''
    try{
      const base = toBase64(context.cacheDir + "/" + fileName)
      if(base=='err') return
      homeApi.uploadFacialApi({
        image: base,
        userId,
      }).then((res: any) => {
        console.log('res===',JSON.stringify(res))
        if (res.data == '是') {
          this.curStatus = status.no4 //通过
          setTimeout(() => {
            router.replaceUrl({ url: 'pages/common/index', params: { tabIndex: '2' } })
          }, 1000)
        } else {
          this.curStatus = status.no5 //未通过
        }
        this.updateUserInfo()
      })
    } catch (e){
      console.error(e)
    }


    // let formData = new FormData();
    // formData.append('image', 'internal://cache/' + fileName);
    // axios.post<string, AxiosResponse<string>, FormData>(`http://42.193.243.96:9999/images/recognizeImage?userId=${userId}`, formData, {
    //   headers: {
    //     'Content-Type': 'multipart/form-data',
    //     'token': AppStorage.Get('token')
    //   },
    //   context,
    //   onUploadProgress: (progressEvent: AxiosProgressEvent): void => {
    //     this.progress = progressEvent.loaded
    //   },
    // }).then((res: AxiosResponse) => {
    //   console.log('人脸识别res', JSON.stringify(res))
    //   if (res.data.data == '是') {
    //     this.curStatus = status.no4 //通过
    //     setTimeout(() => {
    //       router.replaceUrl({ url: 'pages/common/index', params: { tabIndex: '2' } })
    //     }, 300)
    //   } else {
    //     this.curStatus = status.no5 //未通过
    //   }
    //
    //   this.updateUserInfo()
    //   console.log('res', JSON.stringify(res))
    // }).catch((error: AxiosError) => {
    //   console.error("error:" + JSON.stringify(error));
    //
    // })

  }

  updateUserInfo() {
    interface paramsType {
      id: string
    }

    let userId: string = AppStorage.Get('userId') || ''
    const data: paramsType = { id: userId }
    homeApi.getUserInfoApi(data).then((res: myResType) => {
      if (res.success) {
        AppStorage.SetOrCreate('userInfo', JSON.stringify(res.data))
      }
      console.log('用户信息res.data', JSON.stringify(res.data))
    })
  }
}