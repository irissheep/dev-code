import {loadingStatusType} from '../typeDeclare/responseType.d'

export const loadingStatusKeyMap:loadingStatusType = {
  loading:'加载中',
  loadMore: '加载更多',
  loadFailed :'加载失败，请重试',
  noMore:'没有更多了'
}

// 后端基础地址（默认本地 9999，可在运行期通过 AppStorage.SetOrCreate('BASE_URL', 'http://your-host:9999') 覆盖）
export const DEFAULT_BASE_URL: string = 'http://localhost:9999'
