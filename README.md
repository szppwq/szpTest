# szpTest
这是一个新手学习Android的各种项目仓库
PhoneSocketTest CarSocketTest 是用于学习Socket传输协议的项目，分为手机和车机两个客户端
Mp3ReadTest 是用于学习 如何解析MP3文件的 ID3 标签信息的项目
高德地图注意事项：
1.导航SDK的使用 ：高德地图当前版本有个隐私合规检查，需要在实例化AMapNavi或者使用任何与AMapNavi相关的接口或者方法前（最好在app启动时调用一次，防止异步获取，软件崩溃），先去调用updatePrivacyShow，updatePrivacyAgree
NaviSetting.updatePrivacyShow(context, true, true);
NaviSetting.updatePrivacyAgree(context, true);
具体信息请参考：https://lbs.amap.com/api/android-navi-sdk/guide/create-project/configuration-considerations

Socket 注意事项：
1. 连接 ：

有关于服务的注意事项：
1.服务的启动：服务启动需要两个条件：1.清单文件注册，2.Intent 启动 根据Android版本不同，启动方式不同，需要判断
2.服务的保护：系统在内存不足时会杀死进程，为了保证服务尽量不被杀死，需要提高服务进程的优先级,提高优先级的方式：1.开启前台服务：即设置app的系统通知，状态栏通知（这个需要在清单文件添加权限） 2.暂未研究