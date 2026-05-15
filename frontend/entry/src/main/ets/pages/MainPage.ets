import router from '@ohos.router';
import { User } from '../common/entity/User';
import { HttpUtil } from '../common/net/HttpUtil';

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

// ----- 行为上报占位组件 -----
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

// ----- 排行榜占位组件 -----
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

// ----- 🌟 个人中心与 AI 对话组件 🌟 -----
@Component
struct ProfilePage {
  @StorageLink('currentUser') user: User = new User();

  // AI 相关状态变量
  @State aiQuestion: string = '我这个月骑行了20km，用电100度，请给我一些低碳建议。';
  @State aiAnswer: string = '';
  @State isThinking: boolean = false;

  build() {
    Scroll() {
      Column({ space: 20 }) {
        // --- 1. 个人信息模块 ---
        Column({ space: 10 }) {
          Text('个人中心').fontSize(24).fontWeight(FontWeight.Bold).margin({ top: 20 })
          Text(`用户名: ${this.user.username}`)
          Text(`真实姓名: ${this.user.realName}`)
          Text(`总减排: ${this.user.carbonTotal} 克`)
          Text(`碳积分: ${this.user.points}`)
        }
        .width('90%')
        .padding(20)
        .backgroundColor('#F5F5F5')
        .borderRadius(10)

        // --- 2. AI 智能顾问模块 ---
        Column({ space: 15 }) {
          Text('🤖 AI 绿色智能顾问').fontSize(18).fontWeight(FontWeight.Bold).width('100%')

          TextArea({ text: this.aiQuestion, placeholder: '输入您的低碳疑问或数据...' })
            .height(80)
            .onChange((value: string) => { this.aiQuestion = value })
            .backgroundColor('#FFFFFF')

          // 发送按钮
          Button(this.isThinking ? 'AI 正在思考中...' : '向 AI 提问')
            .width('100%')
            .backgroundColor(this.isThinking ? '#999999' : '#007DFF')
            .enabled(!this.isThinking)
            .onClick(() => { this.askAi() })

          // 回复展示区
          if (this.aiAnswer) {
            Text(this.aiAnswer)
              .width('100%')
              .padding(15)
              .backgroundColor('#E8F5E9')
              .borderRadius(8)
              .fontColor('#2E7D32')
              .fontSize(14)
              .lineHeight(20)
          }
        }
        .width('90%')
        .padding(20)
        .backgroundColor('#F0F4F8')
        .borderRadius(10)

        // --- 3. 退出登录 ---
        Button('退出登录')
          .type(ButtonType.Capsule)
          .fontColor(Color.Red)
          .backgroundColor('#FFEBEE')
          .onClick(() => {
            AppStorage.delete('currentUser');
            router.replaceUrl({ url: 'pages/Index' });
          })
          .width('60%')
          .margin({ bottom: 40 })
      }
      .width('100%')
    }
    .width('100%')
    .height('100%')
  }

  // 触发请求后端 AI 接口
  async askAi() {
    if (!this.aiQuestion) return;
    this.isThinking = true;
    this.aiAnswer = '';

    try {
      // 组装请求体，发送给 Spring Boot
      let reqBody: Record<string, string> = { "content": this.aiQuestion };
      let response: string = await HttpUtil.post<string>('/ai/advise', reqBody);
      this.aiAnswer = response;
    } catch (err) {
      this.aiAnswer = "抱歉，请求失败：" + (err.message || '网络错误');
    } finally {
      this.isThinking = false;
    }
  }
}