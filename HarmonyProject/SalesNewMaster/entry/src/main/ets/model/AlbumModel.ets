import picker from '@ohos.file.picker';
import promptAction from '@ohos.promptAction'
import fs from '@ohos.file.fs';
import buffer from '@ohos.buffer';


export default class AlbumClass {
  private pushMsgHandle: (str) => void = undefined;

  // 打开相册选择图片
  async selectPicture() {
    try {
      let photoSelectOptions = new picker.PhotoSelectOptions();
      photoSelectOptions.MIMEType = picker.PhotoViewMIMETypes.IMAGE_TYPE;
      photoSelectOptions.maxSelectNumber = 1;
      let photoPicker = new picker.PhotoViewPicker();
      const photoSelectResult = await photoPicker.select(photoSelectOptions)
      if(!photoSelectResult){ return}

      if (this.pushMsgHandle) {
        setTimeout(()=>{
          this.pushMsgHandle(photoSelectResult.photoUris[0]);
        }, 100)
      }
    } catch (error) {
      let err = error
      console.error('PhotoViewPicker failed with err: ' + JSON.stringify(err));
    }

  }
  // 保存图片
  saveToAlbum(fileName, filePath) {
    console.log('保存图片model')
    try {
      let PhotoSaveOptions = new picker.PhotoSaveOptions();
      PhotoSaveOptions.newFileNames = [fileName];
      let photoPicker = new picker.PhotoViewPicker();
      console.log('PhotoSaveOptions', JSON.stringify(PhotoSaveOptions))
      photoPicker.save(PhotoSaveOptions).then((PhotoSaveResult: Array<string>) => {

        console.log('保存成功 ' + JSON.stringify(PhotoSaveResult));
        if (!PhotoSaveResult) {
          return
        }
        let url = PhotoSaveResult[0]
        setTimeout(() => {
          let srcFile = fs.openSync(filePath, fs.OpenMode.READ_WRITE);
          let targetFile = fs.openSync(url, fs.OpenMode.WRITE_ONLY)
          fs.copyFileSync(srcFile.fd, targetFile.fd)
          fs.closeSync(srcFile);
          fs.closeSync(targetFile)
          promptAction.showToast({
            message: '保存成功'
          })
        }, 1000)
      }).catch((err) => {
        console.log('保存err: ' + JSON.stringify(err));
        if(err==1001){}
        promptAction.showToast({
          message: '文件已存在'
        })
      });
    } catch (error) {
      let err = error
      console.log('图片PhotoViewPicker err: ' + JSON.stringify(err));
    }
  }
  // 选择图片回调函数
  setCallback(callback) {
    this.pushMsgHandle = callback;
  }
}