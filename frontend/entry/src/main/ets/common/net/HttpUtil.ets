import http from '@ohos.net.http';
import { Constants } from '../utils/Constants';

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export class HttpUtil {
  static async post<T>(url: string, data: object): Promise<T> {
    let httpRequest = http.createHttp();
    let options: http.HttpRequestOptions = {
      method: http.RequestMethod.POST,
      header: { 'Content-Type': 'application/json' },
      extraData: data,
      connectTimeout: 60000,
      readTimeout: 60000
    };
    try {
      let response = await httpRequest.request(Constants.BASE_URL + url, options);
      httpRequest.destroy();
      if (response.responseCode === 200) {
        let rawData: string;
        if (typeof response.result === 'string') {
          rawData = response.result;
        } else if (response.result instanceof ArrayBuffer) {
          const uint8Array = new Uint8Array(response.result);
          rawData = HttpUtil.decodeUtf8(uint8Array);
        } else {
          throw new Error('未知的响应类型');
        }
        const result: ApiResponse<T> = JSON.parse(rawData);
        if (result.code === 200) {
          return result.data;
        } else {
          throw new Error(result.message || '请求失败');
        }
      } else {
        throw new Error(`HTTP ${response.responseCode}`);
      }
    } catch (err) {
      httpRequest.destroy();
      throw new Error(`请求异常: ${err.message}`);
    }
  }

  static async get<T>(url: string): Promise<T> {
    let httpRequest = http.createHttp();
    let options: http.HttpRequestOptions = {
      method: http.RequestMethod.GET,
      header: { 'Content-Type': 'application/json' },
      connectTimeout: 60000,
      readTimeout: 60000
    };
    try {
      let response = await httpRequest.request(Constants.BASE_URL + url, options);
      httpRequest.destroy();
      if (response.responseCode === 200) {
        let rawData: string;
        if (typeof response.result === 'string') {
          rawData = response.result;
        } else if (response.result instanceof ArrayBuffer) {
          const uint8Array = new Uint8Array(response.result);
          rawData = HttpUtil.decodeUtf8(uint8Array);
        } else {
          throw new Error('未知的响应类型');
        }
        const result: ApiResponse<T> = JSON.parse(rawData);
        if (result.code === 200) {
          return result.data;
        } else {
          throw new Error(result.message || '请求失败');
        }
      } else {
        throw new Error(`HTTP ${response.responseCode}`);
      }
    } catch (err) {
      httpRequest.destroy();
      throw new Error(`请求异常: ${err.message}`);
    }
  }

  private static decodeUtf8(uint8Array: Uint8Array): string {
    let result = '';
    for (let i = 0; i < uint8Array.length; i++) {
      result += String.fromCharCode(uint8Array[i]);
    }
    return result;
  }
}