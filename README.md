# szpTest
这是一个新手学习Android的各种项目仓库
PhoneSocketTest CarSocketTest 是用于学习Socket传输协议的项目，分为手机和车机两个客户端
Mp3ReadTest 是用于学习 如何解析MP3文件的 ID3 标签信息的项目
高德地图注意事项：
1.导航SDK的使用 ：高德地图当前版本有个隐私合规检查，需要在实例化AMapNavi或者使用任何与AMapNavi相关的接口或者方法前（最好在app启动时调用一次，防止异步获取，软件崩溃），先去调用updatePrivacyShow，updatePrivacyAgree
NaviSetting.updatePrivacyShow(context, true, true);
NaviSetting.updatePrivacyAgree(context, true);
具体信息请参考：https://lbs.amap.com/api/android-navi-sdk/guide/create-project/configuration-considerations
