# Lyra
提供一些Java的底层操作。<br>
这些操作严重破坏了Java的安全性，请谨慎使用<br>
agent	Java Agent相关操作，运行时动态添加Agent<br>
asm	字节码操作。<br>
filesystem	文件系统，包括获取ClassPath和jar文件处理及内部资源读取等。<br>
klass	类操作，修改类成员、访问类方法，操作对象，以及ClassLoader相关工具，在代码上下文中动态加载字节码。<br>
lang	Java语言层面相关操作，反射、句柄工具类，修改安全限制、移除反射过滤等。<br>
vm	JVM相关操作，改JVM参数（启动参数位于C++层，Java内的启动参数为副本，修改不生效），访问JVM内部变量和方法等。<br>