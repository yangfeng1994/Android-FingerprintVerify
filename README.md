# Android-FingerprintVerify

#### 项目介绍
一个用于Android手机指纹验证与密码锁验证的项目

#### 软件架构

本依赖使用了建造者模式。


#### 导入方法

1. 在项目的根目录下的 build.gradle中添加

        allprojects {

   		repositories {
   			...
   			maven { url 'https://jitpack.io' }
   		              }

            	}
2. 在app下的 build.gradle中添加

            dependencies {

	        implementation 'com.github.yangfeng1994:Android-FingerprintVerify:1.0.1'
	        }
	       
#### 使用方法
 
 1.如果你想使用密码锁验证
 
   //初始化指纹验证
      fingerprintAuthenticatedCharacter = FingerprintCharacterStepBuilder
                                 .newBuilder() // 建造一个模型
                                 .setKeystoreAlias("key1")//添加一个密钥别名
                                 .setDialogTag(FingerprintCharacterStepBuilder.DIALOG_FRAGMENT_TAG)//设置dialog的tag，可随便填写
                                 .setFingerprintCallback(this)// 设置回调
                                 .build();//构建建造者模式
                                 
  fingerprintAuthenticatedCharacter.show(getSupportFragmentManager());//显示指纹验证的弹窗

这个建造者模式，是阶级建造者模式，必须按照顺序可以。

  /**
  *指纹验证成功的回调
    withFingerprint 不用管这个
  **/
  @Override
    public void onFingerprintAuthenticatedSucceed(FingerprintManager.CryptoObject cryptoObject, boolean withFingerprint) {
    
    }
    
    暂时没有失败的回调，失败的回调已经在项目中，自己处理，如有需要，后期加入。


