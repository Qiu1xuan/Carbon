import { CarbonService } from '../common/net/CarbonService';
import { CarbonFactor } from '../common/entity/CarbonModels';
import { User } from '../common/entity/User';

@Entry
@Component
struct ReportPage {
  @State factors: CarbonFactor[] = [];
  @State selectedIndex: number = 0;
  @State inputValue: string = "";

  // 从全局存储获取当前登录用户，如果没登录则默认ID为2（开发调试用）
  @StorageLink('currentUser') user: User = new User();
  private userId: number = 2;

  aboutToAppear() {
    // 如果登录了，使用登录用户的ID
    if (this.user && this.user.id) {
      this.userId = this.user.id;
    }
    this.loadFactors();
  }

  async loadFactors() {
    this.factors = await CarbonService.getFactors();
  }

  build() {
    Column({ space: 20 }) {
      Text("低碳行为上报")
        .fontSize(24)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 30, bottom: 10 })

      // --- 修复点 1: 明确 SelectOption 类型 ---
      Select(this.factors.map((item: CarbonFactor): SelectOption => {
        return { value: item.activityName }
      }))
        .selected(this.selectedIndex)
        .value(this.factors.length > 0 ? this.factors[this.selectedIndex].activityName : "加载中...")
        .font({ size: 16 })
        .width('90%')
        .onSelect((index: number) => {
          this.selectedIndex = index
        })

      // 输入数值
      TextInput({ placeholder: "请输入数值（如步数、公里）" })
        .type(InputType.Number)
        .width('90%')
        .height(50)
        .onChange((value: string) => {
          this.inputValue = value
        })

      Button("提交上报")
        .width('90%')
        .height(50)
        .margin({ top: 20 })
        .onClick(async () => {
          if (this.factors.length === 0 || !this.inputValue) {
            AlertDialog.show({ message: "请选择类型并输入数值" });
            return;
          }

          let type = this.factors[this.selectedIndex].activityType;
          let val = parseFloat(this.inputValue);

          // --- 修复点 2: 确保使用定义的 userId ---
          let res = await CarbonService.reportAction(this.userId, type, val);

          if (res.code === 200 && res.data) {
            AlertDialog.show({
              title: '上报成功',
              message: `本次减排：${res.data.reduction}克\n获得积分：${res.data.pointsEarned}`,
              confirm: {
                value: '确定',
                action: () => {
                  // 可以选择清空输入框
                  this.inputValue = "";
                }
              }
            });
          } else {
            AlertDialog.show({ message: "上报失败：" + res.msg });
          }
        })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F1F3F5')
  }
}