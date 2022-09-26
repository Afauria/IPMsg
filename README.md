# 简介

大三的课程作业《飞鸽传书》，局域网聊天和文件传输工具。

> Java GUI开发忘得差不多了，想起来整理一下，当年的代码真的很青涩。

使用Java Swing+UDP+TCP

* 使用Eclipse打开工程
* 运行：`java -jar IPMsg.jar`

> 该jar是使用JDK 1.8编译，使用其他版本可能无法运行，需要重新编译

编译命令如下：

```shell
# 编译java源代码，生成class文件
javac -d target -extdirs lib -encoding utf-8 -sourcepath src -sourcepath res -XDignore.symbol.file src/**/*.java
# 进入target目录
cd target
# 打包jar
jar -cvfm out.jar META-INF/MANIFEST.MF  **/*.class images
```

# 效果图

![](res/images/capture1.png)

![](res/images/capture2.png)