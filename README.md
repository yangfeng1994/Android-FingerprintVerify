# Android-FingerprintVerify

#### 项目介绍
一个用于Android手机指纹验证与密码锁验证的项目

#### 软件架构

本依赖使用了建造者模式。


#### 使用方法

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
