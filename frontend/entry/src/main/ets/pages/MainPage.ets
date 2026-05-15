import router from '@ohos.router';
import { User } from '../common/entity/User';
// 👇 引入之前写好的两个真实页面组件
import { RankPage } from './RankPage';
import { MyOrgPage } from './MyOrgPage';

@Entry
@Component
struct MainPage {
  @State currentIndex: number = 0;
  private controller: TabsController = new TabsController();

  build() {
    Tabs({ index: this.currentIndex, controller: this.controller }) {
      TabContent() {
        RecordPage()
      }
      .tabBar(this.TabBarBuilder('行为上报', 0))

      TabContent() {
        RankPage() // 👈 使用真实的排行榜页面
      }
      .tabBar(this.TabBarBuilder('排行榜', 1))

      TabContent() {
        MyOrgPage() // 👈 使用真实的我的组织页面
      }
      .tabBar(this.TabBarBuilder('我的组织', 2))

      TabContent() {
        ProfilePage()
      }
      .tabBar(this.TabBarBuilder('我的', 3))
    }
    .barPosition(BarPosition.End)
    .onChange((index: number) => {
      this.currentIndex = index; // 监听滑动，更新高亮状态
    })
  }

  // 动态构建 Tab，加入选中状态的高亮颜色
  @Builder
  TabBarBuilder(title: string, targetIndex: number) {
    Column() {
      Text(title)
        .fontSize(this.currentIndex === targetIndex ? 16 : 14)
        .fontColor(this.currentIndex === targetIndex ? '#00B075' : '#999999')
        .fontWeight(this.currentIndex === targetIndex ? FontWeight.Bold : FontWeight.Normal)
        .margin({ top: 8, bottom: 8 })
    }
    .width('100%')
    .justifyContent(FlexAlign.Center)
  }
}

// ----- 占位组件：行为上报 -----
@Component
struct RecordPage {
  build() {
    Column() {
      Text('行为上报页面（待开发）').fontSize(20).margin({ top: 50 })
      Text('组员B：对接核算接口').fontSize(14).fontColor('#666').margin({ top: 10 })
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
  }
}

// ----- 个人中心页面 -----
@Component
struct ProfilePage {
  @StorageLink('currentUser') user: User = new User();

  build() {
    Column({ space: 20 }) {
      Image('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png').width(80).height(80).borderRadius(40).margin({ top: 60 })
      Text('个人中心').fontSize(24).fontWeight(FontWeight.Bold)

      Column({ space: 10 }) {
        Text(`用户名: ${this.user.username || '未登录'}`).fontSize(16)
        Text(`真实姓名: ${this.user.realName || '未设置'}`).fontSize(16)
        Text(`所属城市: ${this.user.city || '未知'}`).fontSize(16)
        Text(`总减排量: ${this.user.carbonTotal || 0} 克`).fontSize(16).fontColor('#00B075')
        Text(`当前碳积分: ${this.user.points || 0}`).fontSize(16).fontColor('#FF6B00')
      }
      .alignItems(HorizontalAlign.Start)
      .width('70%')
      .padding(20)
      .backgroundColor('#FFFFFF')
      .borderRadius(10)
      .shadow({ radius: 10, color: 'rgba(0,0,0,0.05)' })
      .margin({ top: 20 })

      Button('退出登录')
        .onClick(() => {
          AppStorage.delete('currentUser');
          router.replaceUrl({ url: 'pages/Index' });
        })
        .backgroundColor('#FF4D4F')
        .width('80%')
        .margin({ top: 40 })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }
}