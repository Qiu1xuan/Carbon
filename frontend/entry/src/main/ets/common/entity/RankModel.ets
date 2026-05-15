// src/main/ets/common/entity/RankModel.ets

export interface UserRankVO {
  rankNum: number;
  userId: number;
  realName: string;
  avatar: string;
  city: string;
  totalReduction: number;
  totalPoints: number;
}

export interface EnterpriseRankVO {
  rankNum: number;
  enterpriseId: number;
  enterpriseName: string;
  totalReduction: number;
}