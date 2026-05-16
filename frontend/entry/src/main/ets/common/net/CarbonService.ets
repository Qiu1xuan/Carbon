import http from '@ohos.net.http';
import { ApiResult, CarbonFactor, CarbonRecord, OverviewData, StatsData } from '../entity/CarbonModels';

// 请改为你电脑的局域网IP（不要用localhost）
const BASE_URL = 'http://192.168.31.42:8080/api';

export class CarbonService {

  // 1. 获取可用行为因子
  static async getFactors(): Promise<CarbonFactor[]> {
    let httpRequest = http.createHttp();
    let response = await httpRequest.request(`${BASE_URL}/factor/factors`);
    let res: ApiResult<CarbonFactor[]> = JSON.parse(response.result.toString());
    return res.data;
  }

  // 2. 行为上报
  static async reportAction(userId: number, type: string, value: number): Promise<ApiResult<CarbonRecord>> {
    let httpRequest = http.createHttp();
    let response = await httpRequest.request(`${BASE_URL}/record/report`, {
      method: http.RequestMethod.POST,
      header: { 'Content-Type': 'application/json' },
      extraData: { "userId": userId, "type": type, "value": value }
    });
    return JSON.parse(response.result.toString());
  }

  // 3. 获取看板数据
  static async getOverview(userId: number): Promise<OverviewData> {
    let httpRequest = http.createHttp();
    let response = await httpRequest.request(`${BASE_URL}/record/overview/${userId}`);
    let res: ApiResult<OverviewData> = JSON.parse(response.result.toString());
    return res.data;
  }

  // 4. 获取趋势图表数据
  static async getStats(userId: number, range: string): Promise<StatsData[]> {
    let httpRequest = http.createHttp();
    let response = await httpRequest.request(`${BASE_URL}/record/stats?userId=${userId}&range=${range}`);
    let res: ApiResult<StatsData[]> = JSON.parse(response.result.toString());
    return res.data;
  }
}