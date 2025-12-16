import  {my} from '../common/components/my'

@Entry
@Component
struct merchantMy{
  @State pageShow: boolean = false

  onPageShow() {
    this.pageShow = true
  }

  onPageHide() {
    this.pageShow = false
  }

  build(){
    Column(){
      my({ pageShow: this.pageShow })
    }

  }

}