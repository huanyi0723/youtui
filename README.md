# 友推分享文档 
- sdk下载地址（http://youtui.oss-cn-hangzhou.aliyuncs.com/download/android/youtuiShare_foreign_android_v1.0.0.zip）

- 1.社交平台appkey Facebook,授权时使用SSO免登授权方式，需要验证keyhash
keyhash可以在CMD使用命令获取，也可以运行以下代码获取：
```java
public String getKeyHash(Context context, String packageName){
try {
PackageInfo info = context.getPackageManager()
.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
for (Signature signature : info.signatures) {
MessageDigest md = MessageDigest.getInstance("SHA");
md.update(signature.toByteArray());
return Base64.encodeToString(md.digest(), Base64.DEFAULT);
}
} catch (NameNotFoundException e) {

} catch (NoSuchAlgorithmException e) {
}
return null;
}
```

- 2.在AndroidManifest.xml 注册权限
```xml
<!-- 检测网络状态 -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<!-- 获取mac地址作为用户的备用唯一标识 -->
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<!-- 获取用户手机的IMEI，用来唯一的标识用户。 -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<!-- 写入SDcard权限 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<!--打开关闭sd卡权限--!>
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
<!--网络权限--!>
<uses-permission android:name="android.permission.INTERNET" />
<!-- 用于读取sd卡图片 -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

- 3.在 AndroidManifest.xml 注册需要的Activity
```xml
<!-- 友推渠道号，应用名(英文或拼音)+"_yt"，如：“jd_yt”，用于识别通过友推活动下载的应用，请正确填写，否则无法正确统计 -->
<meta-data
android:name="YOUTUI_CHANNEL"
android:value="youtui-sdk_yt" >
</meta-data>
<!-- 截屏分享activity -->
<activity
android:name="cn.bidaround.youtui_template.ScreenCapEditActivity"
android:screenOrientation="portrait"
android:theme="@android:style/Theme.Black.NoTitleBar" />
<!-- 分享activity -->
<activity
android:name="cn.bidaround.ytcore.activity.ShareActivity"
android:configChanges="keyboardHidden|orientation|screenSize"
android:exported="true"
android:launchMode="singleTop"
android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

- 4.分享代码流程
```java
//初始化代码
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
YtTemplate.init(this);/*初始化友推*/
/*集成检测，检测集成过程中是否有错误，检测完成后，请删除该代码或参数设为false*/
YtTemplate.checkConfig(true); 
}
```

```java
//释放内存代码
@Override
public void onDestroy() {
super.onDestroy();       
// Release the youtui memory resources
YtTemplate.release(this);
}

```

- 5.设置要分享的数据
- 创建ShareData实例，调用该实例的set方法设置自己需要分享的数据，关于该实例具体内容见下文，
- 如果只是分享应用则只需要设置setIsAppShare(true) 就可以分享应用在友推后台填写的信息和下载链接。
- ShareData 包含的字段：
- setIsAppShare		判断是否为分享应用，如果是应用分享，分享的数据需要在友推后台配置
- setShareType		分享格式，支持文字、图片、图文分享
- text				待分享的文字
- imagePath			待分享的本地图片地址
- imageUrl			待分享网络图片url，建议使用网络图片分享
- description			待分享内容的描述
- title				待分享内容的标题
- targetUrl			待分享内容的跳转链接

```java
//代码示例
ShareData shareData = new ShareData();
shareData.setIsAppShare(false);
shareData.setShareType(ShareData.SHARETYPE_IMAGEANDTEXT);
shareData.setTitle("友推分享");
shareData.setDescription("友推积分组件");
shareData.setText("通过友推积分组件，开发者几行代码就可以为应用添加分享送积分功能，并提供详尽的后台统计数据，除了本身具备的分享功能外，开发者也可将积分功能单独集成在已有分享组件的app上，快来试试吧 http://youtui.mobi");
shareData.setTargetUrl("http://youtui.mobi/");
shareData.setImageUrl("http://youtui.mobi/media/image/youtui.png");
```

- 因为各个平台的分享限制，请分享时尽量分享图片、设置跳转链接。各平台的分享限制详细如下：
- 1) Facebook
- 分享图片时需设置网络图片imageUrl
- 支持参数：imageUrl、text、targetUrl
- 2) Twitter
- 文字内容长度不能超过140个字符
- 支持参数：imageUrl、imagePath、text、targetUrl

- 5.调用友推分享推荐组件
```java
/*创建分享的模板，第一个参数为activity,第二个参数为分享窗口样式，第三个参数为是否需要积分*/
YtTemplate template = new YtTemplate(this, YouTuiViewType.WHITE_GRID, false);
/*ShareData为4.6创建的数据对象*/
template.setShareData(shareData);
template.addListeners(defaultShareListener);
template.setScreencapVisible(false);// 显示、隐藏截屏按钮
//template.addListener(YtPlatform.PLATFORM_MESSAGE, defaultShareListener);//为指定平台添加独立的分享事件
template.setPointShow(false);//是否显示下面的积分按钮
// template.setPopwindowHeight(400); //设置pop显示控件的高度
template.show();

YtShareListener defaultShareListener = new YtShareListener() {
@Override
public void onSuccess(ErrorInfo error) {
YtLog.w("Success, " + error.getErrorMessage());
}
@Override
public void onPreShare() {
YtLog.w("PerSharing, start...");
}
@Override
public void onError(ErrorInfo error) {
YtLog.w("Fail, " + error.getErrorMessage());
}
@Override
public void onCancel() {
YtLog.w("Canceling, Done...");
}
};
```


- 5.facebook分享就是一个坑啊
- 首先要注册开发者账号 开发模式下只能用自己的账户分享 其它账号无法分享
- 开发者模式下 keystore要与官网里面的key hashes对应起来 key hashes可以用代码生成
```java
public String getKeyHash(Context context, String packageName){
		try {
		PackageInfo info = context.getPackageManager()
		.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
		for (Signature signature : info.signatures) {
		MessageDigest md = MessageDigest.getInstance("SHA");
		md.update(signature.toByteArray());
		return Base64.encodeToString(md.digest(), Base64.DEFAULT);
		}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {
		}
		return null;
		}
```

- 进入产品模式需要审核 审核未通过前 其他账户可以授权成功 但是分享失败
