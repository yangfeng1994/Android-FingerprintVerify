# Android-FingerprintVerify

#### 项目介绍

### 一个用于Android手机指纹验证与密码锁验证的项目，使用建造者模式，创建指纹密钥的实例
，调用弹出验证框进行验证。


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

3. 千万不要忘记了这个可有可无，但又必须要加的权限                  < uses-permission android:name="android.permission.USE_FINGERPRINT"  />

	       
#### 使用方法
 
 1. 如果你想使用密码锁验证
 

     //初始化指纹验证

      fingerprintAuthenticatedCharacter = FingerprintCharacterStepBuilder

                                 .newBuilder() // 建造一个模型
                                 .setKeystoreAlias("key1")//添加一个密钥别名,不同项目中的，一定不能相同
                                 .setDialogTag(FingerprintCharacterStepBuilder.DIALOG_FRAGMENT_TAG)//设置dialog的tag，可随便填写
                                 .setFingerprintCallback(this)// 设置回调
                                 .build();//构建建造者模式
                                 
       fingerprintAuthenticatedCharacter.show(getSupportFragmentManager());//显示指纹验证的弹窗


        这个建造者模式，是阶级建造者模式，必须按照顺序可以。

  /**
  *指纹验证成功的回调 withFingerprint 不用管这个
  **/
  
  
  @Override
    public void onFingerprintAuthenticatedSucceed(FingerprintManager.CryptoObject cryptoObject, boolean withFingerprint) {

    }

    暂时没有失败的回调，失败的回调已经在项目中，自己处理，如有需要，后期加入。

##    当项目没有指纹验证时，您可以进行自定义的密码验证

    /**
    *自定义密码验证的回调，你输入的密码，会回调给这个方法
    **/
  @Override
    public void onFingerprintAuthenticatedSucceed(String passWord, InputPassWordCallback passWordCallback) {

        if ("1234".equals(passWord)) {//成功后，调用成功的方法，在dialog中，可以让dialog关闭
            if (null != passWordCallback) {
                passWordCallback.onInputSucceed();
            }
        } else {
            if (null != passWordCallback) {//失败后，调用失败的方法，在dialog中，可以弹出toast，如果想自己定义，可以不调用次方法
                passWordCallback.onInputFailed();
            }
        }
    }

   1. 在回调中，如果密码匹配成功了，你必须调用

    if (null != passWordCallback) {
         passWordCallback.onInputSucceed();
          }

   这样才能使弹窗消失
   2. 失败后，调用失败的方法，在dialog中，可以弹出toast，如果想自己定义，可以不调用次方法

   if (null != passWordCallback) {

     passWordCallback.onInputFailed();

  }

#  可以进行手机自带密码锁的验证，当你跳转到密码验证界面的时候，
还可以进行指纹验证

      //初始化密码验证

codedLockAuthenticatedCharacter = CodedLockAuthenticatedStepBuilder

                    .newBuilder()
                    .setActivity(MainActivity.this)
                    .getKeyguardManager()
                    .setKeystoreAlias("my_key")
                    .setUserAuthenticationValidityDurationSeconds(10)
                    .getKeyStore()
                    .setAuthenticationScreenCallBack(MainActivity.this)
                    .build();

   1.   setActivity()设置activity 上下文对象，用来获取密码管理类

   2.   setKeystoreAlias("")    添加你应用的密码库的别名

   3.   setUserAuthenticationValidityDurationSeconds(10)// 密码有效时长，秒 ，必须要大于0，等于0时，无限验证，小于0时 抛异常

      Caused by: java.lang.IllegalArgumentException: seconds must be -1 or larger

        大概意思是，  秒必须大于或等于-1
        可是当你填入1的时候，就会抛异常告诉你没有通过用户验证，为了保险，填一个大于0的数字就行了。

       android.security.KeyStoreException: Key user not authenticated
       跳转到密码验证界面

        if (codedLockAuthenticatedCharacter.isKeyguardSecure()) {
            codedLockAuthenticatedCharacter.onValidate();
           }

   4.   getKeyStore() 生成解密支付凭证、令牌等的密钥。

   5. setAuthenticationScreenCallBack() 设置回调

   6. build(); 构建一个CodedLockAuthenticatedStepBuilder 建造者模型对象

   7. codedLockAuthenticatedCharacter.isKeyguardSecure() //判断手机是否有密码锁

   8. codedLockAuthenticatedCharacter.onValidate();进行密码验证


## 密码验证的回调

1.  /**
    密码验证activity跳转回传的结果
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodedLockCharacter.REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {

            if (resultCode == RESULT_OK) {
                if (codedLockAuthenticatedCharacter.onValidate()) {
                    LogUtils.e("yyy", "onActivityResult");   密码验证成功
                } else {
                      密码验证失败
                }
            } else {
                //用户取消或没有完成锁定屏幕
                Toast.makeText(this, "用户取消或没有完成锁定屏幕", Toast.LENGTH_SHORT).show();
            }
        }
    }

 2.   /**
       密码锁验证失败
      */

     @Override
     public void onCodedLockAuthenticationFailed() {
         LogUtils.e("yyy", "密码验证失败");
     }







