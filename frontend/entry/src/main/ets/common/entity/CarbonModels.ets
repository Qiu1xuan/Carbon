// 行为类型实体
export interface CarbonFactor {
  id: number;
  activityType: string;
  activityName: string;
  unitDesc: string;
}

// 首页看板概览实体
export interface OverviewData {
  totalReduction: number;
  totalPoints: number;
  todayReduction: number;
}

// 图表统计实体
export interface StatsData {
  dateLabel: string;
  totalValue: number;
}

// 统一的返回结果包装类（对接后端的 Result）
export interface ApiResult<T> {
  code: number;
  msg: string;
  data: T;
}

export interface CarbonRecord {
  id: number;
  userId: number;
  activityType: string;
  activityValue: number;
  reduction: number;
  pointsEarned: number;
  recordTime: string;
  factorVersion: string;
  factorValue: number;
  merkleHash: string;
  verifyStatus: number;
  remark: string;
}