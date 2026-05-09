export class Constants {
  // 模拟器访问主机后端：10.0.2.2
  // 如果你用的是真机，改成电脑的局域网IP（如 192.168.x.x）
  static readonly BASE_URL: string = 'http://10.0.2.2:8080/api';
  static readonly URL_LOGIN: string = '/auth/login';
}