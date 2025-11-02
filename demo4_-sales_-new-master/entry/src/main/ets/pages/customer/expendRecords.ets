import { consumeRecords } from '../common/components/consumeRecords'

@Entry
@Component
struct expendRecords {
  @State pageShow: boolean = false

  onPageShow() {
    this.pageShow = true
  }

  onPageHide() {
    this.pageShow = false
  }

  build() {
    Column() {
      consumeRecords({ pageShow: this.pageShow })
    }
  }
}