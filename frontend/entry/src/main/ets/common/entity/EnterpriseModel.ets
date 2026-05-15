// src/main/ets/common/entity/EnterpriseModel.ets
import { User } from './User'; // 假设你截图里的 User.ets 已经定义了基本用户字段

export interface Enterprise {
  id: number;
  name: string;
  code: string;
  city: string;
  industry: string;
  carbonQuota: number; // 碳配额
  carbonUsed: number;  // 已使用
  userList: User[];    // 组织内的员工动态列表
}