# Android-FingerprintVerify

[![](https://jitpack.io/v/yangfeng1994/Android-FingerprintVerify.svg)](https://jitpack.io/#yangfeng1994/Android-FingerprintVerify)

#### 项目介绍

### 一个用于Android手机指纹验证与密码锁验证的项目，使用建造者模式，创建指纹密钥的实例或者调用手机密码进行验证。

项目支持 androidx 或者 support

本项目没有引入任何第三方库，不会对您的项目有任何的代码侵入

# 项目截图

![image](https://github.com/yangfeng1994/Android-FingerprintVerify/blob/dev/app/pic/finger_gif.gif)
<img src="/app/pic/finger_pic_screen.jpg" alt="图-2：finger_pic_screen" width="380px"></img>

#### 导入方法

##### 1. 在项目的根目录下的 build.gradle中添加
```groovy
        allprojects {

   		repositories {
   			...
   			maven { url 'https://jitpack.io' }
   		              }

            	}
```
##### 2. 在app下的 build.gradle中添加
```groovy
            dependencies {

	        implementation 'com.github.yangfeng1994:Android-FingerprintVerify:1.0.3'

	        }
```

##### 3. 千万不要忘记了这个权限，必须要加的权限
```java
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
```
#### 使用方法

#  1. 如果你想使用密码锁验证
####  1. 初始化指纹验证
```java
     // 这个建造者模式，是阶级建造者模式，必须按照顺序可以。

      fingerprintAuthenticatedCharacter = FingerprintCharacterStepBuilder
                                 .newBuilder() // 建造一个模型
                                 .setKeystoreAlias("key1")//添加一个密钥别名,不同项目中的，一定不能相同
                                 .setFingerprintCallback(this)// 设置回调
                                 .build();//构建建造者模式

       fingerprintAuthenticatedCharacter.show(Activity);//显示指纹验证的弹窗

```
####  2.指纹验证回调
```java

    /**
       * 指纹验证成功
       */
      @Override
      public void onFingerprintSucceed() {
          Toast.makeText(this, "指纹验证成功", Toast.LENGTH_SHORT).show();
      }

      /**
       * 指纹验证失败
       */
      @Override
      public void onFingerprintFailed() {
          Toast.makeText(this, "指纹验证失败", Toast.LENGTH_SHORT).show();
      }

      /**
       * 取消验证
       */
      @Override
      public void onFingerprintCancel() {
          Toast.makeText(this, "取消指纹验证", Toast.LENGTH_SHORT).show();
      }

      /**
       * 没有录入指纹或者不支持指纹识别
       */
      @Override
      public void onNoEnrolledFingerprints() {
          Toast.makeText(this, "没有录入指纹锁", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onNonsupportFingerprint() {
          Toast.makeText(this, "不支持指纹识别", Toast.LENGTH_SHORT).show();
      }
```


#  2. 可以进行手机自带密码锁的验证
##### 1.部分方法介绍
```java
   1.   setActivity()设置activity 上下文对象，用来获取密码管理类

   2.   setKeystoreAlias("")    添加你应用的密码库的别名

   3.   setUserAuthenticationValidityDurationSeconds(3)// 密码有效时长，秒 ，必须要大于0，等于0时，无限验证，
   	 小于0时 抛异常Caused by: java.lang.IllegalArgumentException: seconds must be -1 or larger

        大概意思是，  秒必须大于或等于-1
        可是当你填入1的时候，就会抛异常告诉你没有通过用户验证，为了保险，填一个大于0的数字就行了。

       android.security.KeyStoreException: Key user not authenticated

   4.   getKeyStore() 生成解密支付凭证、令牌等的密钥。

   5. setAuthenticationScreenCallBack() 设置回调

   6. build(); 构建一个CodedLockAuthenticatedStepBuilder 建造者模型对象

   7. codedLockAuthenticatedCharacter.isKeyguardSecure() //判断手机是否有密码锁

   8. codedLockAuthenticatedCharacter.onValidate();进行密码验证

```

##### 2. 初始化密码验证
```java
codedLockAuthenticatedCharacter = CodedLockAuthenticatedStepBuilder
                    .newBuilder()
                    .setActivity(MainActivity.this)
                    .getKeyguardManager()
                    .setKeystoreAlias("my_key")//可随便填写
                    .setUserAuthenticationValidityDurationSeconds(3)
                    .getKeyStore()
                    .setAuthenticationScreenCallBack(MainActivity.this)
                    .build();
```



##### 3.跳到密码验证界面，去验证密码
```java
	 if (codedLockAuthenticatedCharacter.isKeyguardSecure()) {
      	codedLockAuthenticatedCharacter.onValidate();
       }else{
	    Toast.makeText(MainActivity.this, "没有设置密码锁", Toast.LENGTH_SHORT).show();
	   }
```


##### 4.密码验证的回调
```java
	/**
    密码验证activity跳转回传的结果
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       codedLockAuthenticatedCharacter.onActivityResult(requestCode, resultCode, data);
    }

  	/**
      * 密码验证失败
      */
     @Override
     public void onCodedLockAuthenticationFailed() {
         Toast.makeText(this, "密码验证失败", Toast.LENGTH_SHORT).show();
     }
	/**
      * 密码验证成功
      */
     @Override
     public void onCodedLockAuthenticationSucceed() {
         Toast.makeText(this, "密码验证成功", Toast.LENGTH_SHORT).show();
     }
 	/**
      * 密码验证取消
      */
     @Override
     public void onCodedLockAuthenticationCancel() {
         Toast.makeText(this, "密码验证取消", Toast.LENGTH_SHORT).show();
     }
```

### 项目如需混淆
```text
-keep class com.yf.verify.** {*;}

```
### 如有问题 请联系微信 yf2921

