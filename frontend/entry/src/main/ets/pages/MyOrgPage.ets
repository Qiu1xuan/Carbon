import { Enterprise } from '../common/entity/EnterpriseModel';
import { User } from '../common/entity/User';
import { HttpUtil } from '../common/net/HttpUtil';
import { promptAction } from '@kit.ArkUI';

@Component
export struct MyOrgPage {
  // 从 AppStorage 中自动获取并双向绑定当前登录用户的信息
  @StorageLink('currentUser') user: User = new User();

  @State enterprise: Enterprise | null = null;
  @State isLoading: boolean = true;

  aboutToAppear() {
    this.loadOrgData();
  }

  async loadOrgData() {
    try {
      // 改为判断 !== 0 （因为我们在 User.ets 里把默认值设为了 0）
      if (!this.user.orgId || this.user.orgId === 0) {
        this.isLoading = false;
        return;
      }
      this.enterprise = await HttpUtil.get<Enterprise>(`/enterprise/detail?enterpriseId=${this.user.orgId}`);
    } catch (err) {
      let e = err as Error; // 👈 明确类型
      promptAction.showToast({ message: `获取组织失败: ${e.message}` });
    } finally {
      this.isLoading = false;
    }
  }

  build() {
    Column() {
      // 头部
      Text('我的组织')
        .fontSize(22).fontWeight(FontWeight.Bold).fontColor('#fff')
        .width('100%').height(60).backgroundColor('#00B075')
        .textAlign(TextAlign.Center)

      if (this.isLoading) {
        LoadingProgress().width(50).color('#00B075').margin({ top: 50 })
      } else if (this.enterprise) {
        // --- 1. 企业荣誉与配额卡片 ---
        Column() {
          Text(this.enterprise.name).fontSize(20).fontWeight(FontWeight.Bold).margin({ bottom: 10 })
          Text(`行业: ${this.enterprise.industry} | 城市: ${this.enterprise.city}`).fontSize(14).fontColor('#666').margin({ bottom: 20 })

          // 碳配额进度计算
          Row() {
            Text('企业碳配额消耗:').fontSize(14).fontColor('#333')
            Blank()
            Text(`${this.enterprise.carbonUsed} / ${this.enterprise.carbonQuota} g`).fontSize(14).fontColor('#00B075')
          }.width('100%').margin({ bottom: 8 })

          // 进度条组件
          Progress({
            value: this.enterprise.carbonUsed,
            total: this.enterprise.carbonQuota,
            type: ProgressType.Linear
          })
            .width('100%').color('#FF6B00').backgroundColor('#E8E8E8')

        }
        .width('90%').padding(20).margin({ top: 15, bottom: 15 })
        .backgroundColor('#fff').borderRadius(12)
        .shadow({ radius: 10, color: 'rgba(0,0,0,0.05)' })

        // --- 2. 组织内部减排动态 (员工列表) ---
        Column() {
          Text('组织低碳动态与成员')
            .fontSize(16).fontWeight(FontWeight.Bold)
            .width('100%').padding({ left: 20, bottom: 10 })

          List() {
            ForEach(this.enterprise.userList, (member: User) => {
              ListItem() {
                Row() {
                  // 这里我帮你把之前报错的 $r('app.media.icon') 替换为了一个占位图标，防止编译不通过
                  // 建议以后你的 assets 里准备好默认头像
                  Image('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png').width(40).height(40).borderRadius(20).margin({ right: 10 })

                  Column() {
                    Text(member.realName || member.username).fontSize(16).fontWeight(FontWeight.Medium)
                    Text(`为企业贡献减排量: ${member.carbonTotal || 0}g`).fontSize(12).fontColor('#00B075').margin({ top: 4 })
                  }.alignItems(HorizontalAlign.Start).layoutWeight(1)

                  // 荣誉标签 (如果员工贡献很大，给个荣誉标识)
                  if ((member.carbonTotal || 0) > 60000) {
                    Text('环保先锋')
                      .fontSize(10)
                      .fontColor('#fff')
                      .backgroundColor('#FFD700')
                      .padding(4)
                      .borderRadius(4)
                  }
                }
                .width('100%').padding(15).backgroundColor('#fff').border({ width: { bottom: 1 }, color: '#F5F5F5' })
              }
            }, (member: User) => member.id.toString())
          }.layoutWeight(1)
        }
        .layoutWeight(1).width('100%')

      } else {
        // 如果用户未绑定任何企业，显示此页面
        Column() {
          Text('🏢')
            .fontSize(60)
            .margin({ top: 100, bottom: 20 })
          Text('您当前还未加入任何企业哦')
            .fontSize(16)
            .fontColor('#999')
        }.width('100%').alignItems(HorizontalAlign.Center)
      }
    }
    .width('100%').height('100%').backgroundColor('#F5F5F5')
  }
}