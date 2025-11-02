import common from '@ohos.app.ability.common';
import axios, { AxiosError, AxiosProgressEvent, AxiosResponse, FormData, InternalAxiosRequestConfig } from '@ohos/axios'
import fs from '@ohos.file.fs';
import util from '@ohos.util';

interface pathType {
  filePath: string
}

interface dataType {
  data: pathType
}


interface path {
  filePath: string
}

interface bodyType {
  code: string
  success: string
  data: path
}

interface resType {
  body: bodyType
}

export function copyFile(fileUri: string, context: common.UIAbilityContext, preName = '') {
  // 获取应用文件路径
  let cacheDir = context.cacheDir;
  console.log('cacheDir', cacheDir); //  =>应用文件目录filesDir  /data/storage/el2/base/haps/entry/cache
  //文件名称
  const fileName = preName + Date.now() + '.jpg'
  //计划复制到的目标路径
  let realUri = context.cacheDir + "/" + fileName

  //复制选择的文件到沙箱cache文件夹
  try {
    let file = fs.openSync(fileUri);
    fs.copyFileSync(file.fd, realUri)
  } catch (err) {
    console.info('err:' + JSON.stringify(err));
  }

  return fileName
}


export function toBase64(fileUri) {
    try {
      const file = fs.openSync(fileUri)
      console.log('file', JSON.stringify(file))
      // 同步获取文件的详细信息
      const stat = fs.statSync(file.fd)
      // 创建缓冲区存储读取的文件流
      const buffer = new ArrayBuffer(stat.size)
      const num = fs.readSync(file.fd, buffer)
      console.log('转化为64长度', JSON.stringify(num))
      // 开始同步读取文件流到缓冲区
      fs.readSync(file.fd, buffer)
      // 转成base64编码的字符串
      const helper = new util.Base64Helper()
      const imgList = helper.encodeToStringSync(new Uint8Array(buffer))
      return imgList

    } catch (err) {
      return 'err'
    }
}
