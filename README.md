## Lyra

提供一些Java的底层操作。<br>
这些操作严重破坏了Java的安全性，请谨慎使用<br>
lyra.asm    字节码操作。目前没写完，感觉用不上。<br>
lyra.filesystem    文件系统相关操作，包括获取ClassPath。<br>
lyra.filesystem.jar    jar文件处理及内部资源读取。<br>
lyra.internal    底层指针内存操作。<br>
lyra.internal.oops    根据OpenJDK的源码编写的对象模型相关操作。<br>
lyra.klass    类操作，遍历类的字段和方法，以及扫描类、获取泛型信息，ClassLoader相关操作。<br>
lyra.klass.special    一些特殊用途的类接口，包括镜像类。<br>
lyra.lang    反射、句柄、jdk.internal.misc.Unsafe操作。<br>
lyra.lang.annotation    注解相关操作。<br>
lyra.lang.base    基本的Java访问权限绕过和反射、句柄操作，用于库的权限启动点。<br>
lyra.object    对象的相关操作。<br>
lyra.vm    JVM相关操作。<br>
lyra.vm.agent    Java Agent动态attach。<br>
lyra.vm.base    获取JVM参数。<br>