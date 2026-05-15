import { EnterpriseRankVO, UserRankVO } from '../common/entity/RankModel';
import { User } from '../common/entity/User';
import { HttpUtil } from '../common/net/HttpUtil';
import { promptAction } from '@kit.ArkUI';

@Component
export struct RankPage {
  // 从 AppStorage 中自动获取并双向绑定当前登录用户的信息
  @StorageLink('currentUser') user: User = new User();

  @State personalList: UserRankVO[] = [];
  @State enterpriseList: EnterpriseRankVO[] = [];
  @State personalTab: string = 'global'; // global | city | org
  @State isLoading: boolean = false;

  aboutToAppear() {
    this.loadPersonalData('global');
    this.loadEnterpriseData();
  }

  // 加载个人排行榜，根据用户的真实城市和归属企业动态加载
  async loadPersonalData(type: string) {
    this.isLoading = true;
    this.personalTab = type;
    try {
      let url = '/rank/personal/global';

      if (type === 'city') {
        let targetCity = this.user.city ? this.user.city : '上海';
        // 必须用 encodeURIComponent 对中文进行 URL 编码！
        url = `/rank/personal/city?city=${encodeURIComponent(targetCity)}`;
      }

      if (type === 'org') {
        // 防止 orgId 为 undefined 时，拼接出 ?orgId=undefined 导致后端 Long 类型转换失败(也会报 400)
        let orgId = this.user.orgId ? this.user.orgId : 1;
        url = `/rank/personal/enterprise?orgId=${orgId}`;
      }

      this.personalList = await HttpUtil.get<UserRankVO[]>(url);
    } catch (e) {
      let err = e as Error;
      promptAction.showToast({ message: `加载失败: ${err.message}` });
    } finally {
      this.isLoading = false;
    }
  }

  // 加载全平台企业排行榜
  async loadEnterpriseData() {
    try {
      this.enterpriseList = await HttpUtil.get<EnterpriseRankVO[]>('/rank/enterprise/global');
    } catch (e) {
      console.error('加载企业排行失败', e);
    }
  }

  build() {
    Column() {
      Text('碳普惠风云榜')
        .fontSize(22).fontWeight(FontWeight.Bold).fontColor('#fff')
        .width('100%').height(60).backgroundColor('#00B075')
        .textAlign(TextAlign.Center)

      Tabs({ barPosition: BarPosition.Start }) {
        // --- 选项卡 1：个人排行 ---
        TabContent() {
          Column() {
            // 维度切换按钮
            Row() {
              Button('全平台').onClick(() => this.loadPersonalData('global'))
                .backgroundColor(this.personalTab === 'global' ? '#00B075' : '#eee')
                .fontColor(this.personalTab === 'global' ? '#fff' : '#333').layoutWeight(1).margin(5)
              Button('同城榜').onClick(() => this.loadPersonalData('city'))
                .backgroundColor(this.personalTab === 'city' ? '#00B075' : '#eee')
                .fontColor(this.personalTab === 'city' ? '#fff' : '#333').layoutWeight(1).margin(5)
              Button('企业内').onClick(() => this.loadPersonalData('org'))
                .backgroundColor(this.personalTab === 'org' ? '#00B075' : '#eee')
                .fontColor(this.personalTab === 'org' ? '#fff' : '#333').layoutWeight(1).margin(5)
            }.width('100%').padding(10)

            if (this.isLoading) {
              LoadingProgress().width(50).height(50).margin({ top: 50 }).color('#00B075')
            } else {
              List() {
                ForEach(this.personalList, (item: UserRankVO) => {
                  ListItem() {
                    Row() {
                      // 排名名次
                      Text(`${item.rankNum}`)
                        .fontSize(18)
                        .fontWeight(FontWeight.Bold)
                        .fontColor(item.rankNum <= 3 ? '#FF6B00' : '#333')
                        .width(40)

                      // 名字与减排量
                      Column() {
                        Text(item.realName || '匿名').fontSize(16).fontWeight(FontWeight.Medium)
                        Text(`减排 ${item.totalReduction}g`).fontSize(12).fontColor('#00B075').margin({ top: 4 })
                      }.alignItems(HorizontalAlign.Start).layoutWeight(1)

                      // 积分
                      Text(`${item.totalPoints} 积分`).fontSize(16).fontColor('#FF6B00').fontWeight(FontWeight.Bold)
                    }.width('100%').padding(15).border({ width: { bottom: 1 }, color: '#eee' })
                  }
                }, (item: UserRankVO) => item.userId.toString() + '_' + item.rankNum)
              }.width('100%')
              .height('100%')
              .layoutWeight(1)
            }
          }.width('100%').height('100%')
        }.tabBar('个人积分榜')

        // --- 选项卡 2：企业排行 ---
        TabContent() {
          List() {
            ForEach(this.enterpriseList, (item: EnterpriseRankVO) => {
              ListItem() {
                Row() {
                  Text(`${item.rankNum}`)
                    .fontSize(18)
                    .fontWeight(FontWeight.Bold)
                    .fontColor(item.rankNum <= 3 ? '#FF6B00' : '#333')
                    .width(40)

                  Text(item.enterpriseName).fontSize(16).layoutWeight(1)
                  Text(`${item.totalReduction}g 减排`).fontSize(16).fontColor('#00B075').fontWeight(FontWeight.Bold)
                }.width('100%').padding(15).border({ width: { bottom: 1 }, color: '#eee' })
              }
            }, (item: EnterpriseRankVO) => item.enterpriseId.toString())
          }.width('100%').height('100%')
        }.tabBar('企业减排榜')
      }
      .barMode(BarMode.Fixed)
      .layoutWeight(1)
    }.width('100%').height('100%')
  }
}