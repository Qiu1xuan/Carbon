import router from '@ohos.router';
// 删除了 import { InputType } from '@kit.ArkUI';
import { HttpUtil } from '../common/net/HttpUtil';
import { Constants } from '../common/utils/Constants';
import { User } from '../common/entity/User';

@Entry
@Component
struct Index {
  @State username: string = '';
  @State password: string = '';
  @State message: string = '';

  build() {
    Column({ space: 20 }) {
      Text('碳普惠平台')
        .fontSize(28)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 80 })

      TextInput({ placeholder: '用户名' })
        .onChange((value: string) => { this.username = value })
        .margin({ top: 40 })

      TextInput({ placeholder: '密码' })
        .type(InputType.Password)    // 直接使用，无需 import
        .onChange((value: string) => { this.password = value })

      Button('登录')
        .onClick(() => { this.doLogin() })
        .width('100%')
        .margin({ top: 30 })

      Text(this.message)
        .fontSize(14)
        .fontColor('#FF0000')
        .margin({ top: 10 })
    }
    .width('100%')
    .height('100%')
    .padding(20)
    .justifyContent(FlexAlign.Center)
  }

  async doLogin() {
    // 使用 trim() 去除首尾空格后判断
    const userStr = this.username.trim();
    const pwdStr = this.password.trim();

    if (!userStr || !pwdStr) {
      this.message = '用户名和密码不能为空';
      return;
    }

    try {
      // 解决报错 2：ArkTS 严格模式要求对象必须声明类型，使用 Record 泛型
      let requestBody: Record<string, string> = {
        'username': userStr,
        'password': pwdStr
      };

      // 密码不要拼在 URL 后面，放在 POST 的 body (即第二个参数) 里面
      const url = Constants.URL_LOGIN;

      // 假设你的 HttpUtil.post 第二个参数接收 body 参数
      const userData = await HttpUtil.post<User>(url, requestBody);

      AppStorage.setOrCreate('currentUser', userData);
      this.message = '登录成功！';
      router.replaceUrl({ url: 'pages/MainPage' });
    } catch (err) {
      // 在新版鸿蒙中，err 可能不是 Error 对象，建议用 JSON.stringify 或强制转换
      this.message = err.message || '登录失败，请检查网络';
    }
  }
}