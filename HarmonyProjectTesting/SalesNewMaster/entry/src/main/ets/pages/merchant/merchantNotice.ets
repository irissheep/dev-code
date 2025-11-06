import  {notice} from '../common/components/notice'

@Entry
@Component
struct merchantNotice{
  @State pageShow: boolean = false

  onPageShow() {
    this.pageShow = true
  }

  onPageHide() {
    this.pageShow = false
  }

  build(){
    Column(){
      notice()
    }

  }

}