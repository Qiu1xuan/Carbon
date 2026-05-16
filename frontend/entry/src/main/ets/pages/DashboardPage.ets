import { CarbonService } from '../common/net/CarbonService';
import { OverviewData, StatsData } from '../common/entity/CarbonModels';
import { User } from '../common/entity/User';

@Entry
@Component
struct DashboardPage {
  @State overview: OverviewData = { totalReduction: 0, totalPoints: 0, todayReduction: 0 };
  @State statsList: StatsData[] = [];

  @StorageLink('currentUser') user: User = new User();
  private userId: number = 2;

  aboutToAppear() {
    if (this.user && this.user.id) {
      this.userId = this.user.id;
    }
    this.refresh();
  }

  async refresh() {
    // 获取概览和趋势统计
    this.overview = await CarbonService.getOverview(this.userId);
    this.statsList = await CarbonService.getStats(this.userId, 'week');
  }

  build() {
    // 修复点 1: 将 ScrollView 改为 Scroll
    Scroll() {
      Column({ space: 15 }) {
        Text("碳足迹看板")
          .fontSize(24)
          .fontWeight(FontWeight.Bold)
          .margin({ top: 20, bottom: 10 })

        // 1. 顶部数据概览
        Row({ space: 10 }) {
          this.StatCard("今日减排", this.overview.todayReduction + "g", "#4CAF50")
          this.StatCard("累计减排", (this.overview.totalReduction / 1000).toFixed(1) + "kg", "#2196F3")
          this.StatCard("累计积分", this.overview.totalPoints.toString(), "#FF9800")
        }
        .width('95%')
        .justifyContent(FlexAlign.SpaceAround)

        // 2. 减排趋势图表
        Column() {
          Text("近7天减排趋势 (克)")
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
            .width('100%')
            .margin({ bottom: 20 })

          Row() {
            if (this.statsList.length > 0) {
              // 修复点 2: ForEach 的正确写法
              ForEach(this.statsList, (item: StatsData) => {
                Column() {
                  Rect()
                    .width(15)
                    // 简单比例缩放高度，最大 150
                    .height(Math.min(item.totalValue / 10 + 5, 150))
                    .fill("#81C784")
                    .radius(5)
                  Text(item.dateLabel.length > 5 ? item.dateLabel.substring(5) : item.dateLabel)
                    .fontSize(10)
                    .margin({ top: 5 })
                }
                .layoutWeight(1)
              }, (item: StatsData) => item.dateLabel) // 提供 Key 生成器
            } else {
              Text("暂无统计数据").fontColor("#999").width('100%').textAlign(TextAlign.Center)
            }
          }
          .width('100%')
          .height(200)
          .padding({ bottom: 10 })
          .alignItems(VerticalAlign.Bottom)
        }
        .width('95%')
        .padding(15)
        .backgroundColor(Color.White)
        .borderRadius(12)
        .margin({ top: 10 })

        Button("刷新数据")
          .margin({ top: 20, bottom: 30 })
          .width('80%')
          .onClick(() => {
            this.refresh()
          })
      }
      .width('100%')
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F1F3F5')
  }

  @Builder StatCard(title: string, value: string, color: string) {
    Column() {
      Text(title).fontSize(12).fontColor('#666')
      Text(value).fontSize(16).fontWeight(FontWeight.Bold).fontColor(color).margin({ top: 5 })
    }
    .padding(10)
    .backgroundColor(Color.White)
    .borderRadius(10)
    .width('31%')
  }
}