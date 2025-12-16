import router from '@ohos.router';
import promptAction from '@ohos.promptAction'
import picker from '@ohos.file.picker';
import common from '@ohos.app.ability.common';
import fs from '@ohos.file.fs';
import request from '@ohos.request';
import axios, { AxiosError, AxiosProgressEvent, AxiosResponse, FormData } from '@ohos/axios'
import abilityAccessCtrl from '@ohos.abilityAccessCtrl';
import { userType, myResType } from '../../typeDeclare/responseType'
import buffer from '@ohos.buffer';
import { copyFile, toBase64 } from '../../utils/index'
import { homeApi } from '../../api/HomeAPI';
import AlbumModel from '../../model/AlbumModel'
import util from '@ohos.util';


const context = getContext(this) as common.UIAbilityContext;

@Entry
@Component
struct avatar {
  private select: number = 0
  @State avatar: string = ''
  private options: string[] = ['从手机相册上传', '保存图片',]
  private albumModel: AlbumModel = new AlbumModel();

  async onPageShow() {
    const userInfo: userType = JSON.parse(AppStorage.Get('userInfo')) || {}
    this.avatar = userInfo.avatar || ''
    let atManager = abilityAccessCtrl.createAtManager();
    await atManager.requestPermissionsFromUser(context, [
      "ohos.permission.WRITE_MEDIA",
      "ohos.permission.READ_MEDIA",
    // "ohos.permission.READ_IMAGEVIDEO",
    // "ohos.permission.WRITE_IMAGEVIDEO"
    ], (err, data) => {
      if (err) {
        console.log(`requestPermissionsFromUser fail, err->${JSON.stringify(err)}`);
      } else {
        console.info('这是data:' + JSON.stringify(data));
        console.info('data permissions:' + data.permissions); //0已授权
        console.info('data authResults:' + data.authResults);
      }
    })

  }

  @Builder
  customNav() {
    Row() {
      Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
        Row() {

          Image($r('app.media.back')).width(20).margin({ right: 14 }).onClick(() => {
            if (AppStorage.Get('userType') == '1') {
              router.replaceUrl({ url: 'pages/common/index', params: { tabIndex: '2' } })
            } else {
              router.back()
            }
          })
          Text('个人头像').fontSize(19)
        }

        Image($r('app.media.more')).width(30).onClick(() => {
          // 唤起弹窗
          TextPickerDialog.show({
            range: this.options,
            selected: this.select,
            onAccept: (value: TextPickerResult) => {
              // 设置select为按下确定按钮时候的选中项index，这样当弹窗再次弹出时显示选中的是上一次确定的选项
              this.select = value.index || 0
              console.info("TextPickerDialog:onAccept()" + JSON.stringify(value))
              if (value.index == 0) this.openAlbum() //手机相册上传
              if (value.index == 1) this.saveImg() //保存图片
            },
          })
        })
      }
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
          Image(this.avatar ? this.avatar : $r('app.media.my'))
            .width('100%')
            .backgroundColor('#fff')
            .position({ y: '48%' })
            .translate({ y: '-50%' })
        }.backgroundColor('#000').width('100%').height('100%')
      }
      .title({ builder: this.customNav, height: 50 })
      .mode(NavigationMode.Auto)
      .titleMode(NavigationTitleMode.Mini)
      .hideBackButton(true)
    }.width('100%').height('100%')
  }

  // 保存图片到本地
  saveImg() {
    if (!this.avatar) {
      promptAction.showToast({ message: '暂无图片' })
      return
    }
    const fileName = this.avatar.split("?")[1].split("&")[0].split("=")[1]
    // 获取应用文件路径
    let filesPath = context.filesDir + fileName;
    //文件如果已经存在，就删除
    if (fs.accessSync(filesPath)) {
      console.log('文件已经存在')
      fs.unlink(filesPath).then(() => {
        console.info("删除成功");
      }).catch((err) => {
        console.error("删除失败 file failed with error message: " + err.message + ", error code: " + err.code);
      });
    }

    try {
      request.downloadFile(context, {
        url: this.avatar,
        filePath: filesPath,
      }).then((downloadTask: request.DownloadTask) => {
        downloadTask.on('progress', (receivedSize, totalSize) => {
          console.info('下载进度', "download receivedSize:" + receivedSize + " totalSize:" + totalSize);
        });
        downloadTask.on('complete', () => {
          console.info('下载完成', filesPath); ///data/storage/el2/base/haps/entry/filesedd268e02df84910ba85d9ff54668cdb.jpg
          this.albumModel.saveToAlbum(fileName, filesPath)
        })
      }).catch((err) => {
        console.error(`下载失败1 code is ${err.code}, message is ${err.message}`);
      });
    } catch (error) {
      let err = error;
      promptAction.showToast({ message: err.message })
      console.error(`下载失败2, code is ${err.code}, message is ${err.message}`);
    }
  }


  // 打开相册
  async openAlbum() {
    this.albumModel.setCallback((str: string) => {
      // console.log('选择图片后的回调',str) //datashare:///media/image/151
      this.uploadImg(str)
    })
    this.albumModel.selectPicture()
    setTimeout(() => {
    }, 300)

  }

  async uploadImg(fileUri: string) {
    try{
      const base = toBase64(fileUri)
      if(base=='err') return
      homeApi.uploadApi({
        image: base,
        name: Date.now() + '.jpg',
        type: '1'
      }).then((res: any) => {
        const path = res.data.filePath
        const params = { id: AppStorage.Get('userId') || '', avatar: path }
        // console.info("编辑头像参数", JSON.stringify(params));
        homeApi.editAvatarApi(params).then(res => {
          // console.log('头像上传', JSON.stringify(res))
          if (res.success) {
            promptAction.showToast({ message: '上传成功' })
            this.avatar = path
            this.getUserInfo()
          }
        })
      })
    } catch (e){
      console.error(e)
    }




    // const fileName = copyFile(fileUri, context, 'avatar')
    // // 第三方库axios请求
    // let formData = new FormData();
    // formData.append('image', 'internal://cache/' + fileName);
    // formData.append('type', '1');
    // console.log('上传图片路径','internal://cache/' + fileName)
    // axios.post<string, AxiosResponse<string>, FormData>('http://42.193.243.96:9999/images/upload', formData, {
    //   headers: {
    //     'Content-Type': 'multipart/form-data',
    //     'token': AppStorage.Get('token')
    //   },
    //   context: getContext(this),
    //   onUploadProgress: (progressEvent: AxiosProgressEvent): void => {
    //     console.info('上传进度', progressEvent && progressEvent.loaded && progressEvent.total ? Math.ceil(progressEvent.loaded / progressEvent.total * 100) + '%' : '0%');
    //   },
    // })
    //   .then((res1: AxiosResponse) => {
    //     const data = res1
    //     console.log("上传结果data",  JSON.stringify(data));
    //     // const path = data.data.filePath
    //     // const params = { id: AppStorage.Get('userId') || '', avatar: path }
    //     // console.info("编辑头像参数", JSON.stringify(params));
    //     // homeApi.editAvatarApi(params).then(res => {
    //     //   console.log('头像上传', JSON.stringify(res))
    //     //   if (res.success) {
    //     //     promptAction.showToast({ message: '上传成功' })
    //     //     this.avatar = path
    //     //     this.getUserInfo()
    //     //   }
    //     // })
    //
    //   }).catch((error) => {
    //   promptAction.showToast({ message: `上传失败${error.message}` })
    //   console.error("上传失败error:" + JSON.stringify(error));
    // })

    // // 官方上传方法
    // let uploadTask: request.UploadTask;
    // let uploadConfig: request.UploadConfig = {
    //   url: 'http://42.193.243.96:9999/images/upload', //需要手动替换为真实服务器地址
    //   header: { 'Content-Type': 'multipart/form-data', token: AppStorage.Get('token') },
    //   method: "POST",
    //   files: [{ filename: fileName, name: "image", uri: 'internal://cache/' + fileName, type: "jpg" }],
    //   data: [
    //     { name: 'type', value: '1' },
    //     { name: 'image', value: 'internal://cache/' + fileName }
    //   ],
    // };
    //
    // try {
    //   request.uploadFile(context, uploadConfig).then((data: request.UploadTask) => {
    //     uploadTask = data;
    //     uploadTask.on('progress', (uploadedSize: number, totalSize: number) => {
    //       console.log('上传进度1', uploadedSize, totalSize)
    //     })
    //     uploadTask.on('headerReceive', (header: object) => {
    //       console.log('上传头picRes', JSON.stringify(header))
    //     })
    //     uploadTask.on("complete", (taskStates) => {
    //       console.log('上传头成功taskStates', JSON.stringify(taskStates)) //path":"avatar1726810072220.jpg"
    //     })
    //   }).catch((err) => {
    //     console.error(`上传失败Failed to request the upload. Code: ${err.code}, message: ${err.message}`);
    //   });
    // } catch (err) {
    //   console.error(`上传失败Failed to request the upload. err: ${JSON.stringify(err)}`);
    // }
  }

  getUserInfo() {
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