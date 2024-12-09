# Weather Forecast App

一个简洁美观的天气预报应用，提供实时天气、24小时预报、7天预报等功能。

## 功能特点

- 实时天气信息展示
- 24小时天气预报
- 7天天气预报
- 空气质量指数
- 多城市管理
- 城市搜索
- GPS定位
- 自动更新天气
- 主题切换
- 下拉刷新

## 技术特点

- MVVM架构
- Android Jetpack组件
- Room数据库
- Retrofit网络请求
- LiveData数据观察
- ViewBinding视图绑定
- 位置服务
- 数据缓存优化

## 系统要求

- Android 8.0 (API 26) 及以上
- 需要网络连接
- 需要位置权限（可选）

## 安装说明

1. 从Release页面下载最新版本APK
2. 在Android设备上安装APK
3. 授予应用所需权限

## 使用说明

### 首次使用

1. 启动应用后，会请求位置权限（可选）
2. 允许权限后，应用会自动定位并显示当前城市天气
3. 如果不允许位置权限，可以手动搜索并添加城市

### 添加城市

1. 点击右上角菜单按钮
2. 选择"城市管理"
3. 点击"+"按钮
4. 在搜索框中输入城市名称
5. 从搜索结果中选择需要的城市

### 切换城市

1. 在主页面左右滑动切换已添加的城市
2. 或点击城市名称进入城市列表选择

### 刷新天气

- 下拉刷新当前城市天气信息
- 天气信息会自动更新（间隔5分钟）

### 主题切换

1. 点击右上角菜单按钮
2. 选择"设置"
3. 在主题设置中选择喜欢的主题

## API说明

本应用使用和风天气API，免费版每天调用限制1000次。主要使用以下API：

- 城市信息搜索
- 实时天气
- 24小时预报
- 7天预报
- 空气质量

## 开发环境

- Android Studio Hedgehog | 2023.1.1
- Gradle 8.0
- JDK 11
- Kotlin 1.9.0

## 项目结构

```
app/src/main/java/com/example/myapplication24/
├── MainActivity.java              # 主活动
├── WeatherApplication.java        # 应用入口
├── adapter/                       # 适配器
├── api/                          # API相关
├── cache/                        # 缓存管理
├── db/                           # 数据库
├── model/                        # 数据模型
├── repository/                   # 数据仓库
├── service/                      # 服务
├── utils/                        # 工具类
└── viewmodel/                    # 视图模型
```

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 版本历史

- 2.0.0 (2024-01-10)
  - 代码文档完善
  - 缓存机制优化
  - 性能优化和稳定性提升
  - 用户体验改进
  
- 1.0.0 (2024-01-09)
  - 首次发布
  - 基本天气功能
  - 多城市管理
  - 自动定位

## 作者

- [Your Name] - *Initial work*

## 许可证

本项目基于 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 致谢

- [和风天气](https://dev.qweather.com/) - 提供天气API
- [Material Design](https://material.io/design) - UI设计参考
- [Android Jetpack](https://developer.android.com/jetpack) - 开发组件支持 