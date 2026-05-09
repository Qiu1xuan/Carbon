import router from '@ohos.router';
import { User } from '../common/entity/User';

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
      .tabBar(this.TabBarBuilder('行为上报'))

      TabContent() {
        RankPage()
      }
      .tabBar(this.TabBarBuilder('排行榜'))

      TabContent() {
        ProfilePage()
      }
      .tabBar(this.TabBarBuilder('我的'))
    }
    .barPosition(BarPosition.End)
  }

  @Builder
  TabBarBuilder(title: string) {
    Column() {
      Text(title)
        .fontSize(16)
        .margin({ top: 8, bottom: 8 })
    }
    .width('100%')
    .justifyContent(FlexAlign.Center)
  }
}

// ----- 占位组件 -----
@Component
struct RecordPage {
  build() {
    Column() {
      Text('行为上报页面（待开发）').fontSize(20).margin({ top: 50 })
      Text('组员B：对接核算接口').fontSize(14).fontColor('#666')
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
  }
}

@Component
struct RankPage {
  build() {
    Column() {
      Text('排行榜页面（待开发）').fontSize(20).margin({ top: 50 })
      Text('组员C：对接排行接口').fontSize(14).fontColor('#666')
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
  }
}

@Component
struct ProfilePage {
  @StorageLink('currentUser') user: User = new User();

  build() {
    Column({ space: 20 }) {
      Text('个人中心').fontSize(24).fontWeight(FontWeight.Bold).margin({ top: 40 })
      Text(`用户名: ${this.user.username}`)
      Text(`真实姓名: ${this.user.realName}`)
      Text(`总减排: ${this.user.carbonTotal} 克`)
      Text(`碳积分: ${this.user.points}`)
      Button('退出登录')
        .onClick(() => {
          AppStorage.delete('currentUser');
          router.replaceUrl({ url: 'pages/Index' });
        })
        .width('80%')
        .margin({ top: 40 })
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
  }
}