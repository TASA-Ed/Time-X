# Time X

Time X，时间系列的第十代产品。X 为罗马数字 10。

## 介绍

你可以用它查看精确的时间。

## 构建

构建前安装 Python 与 fonttools：

```shell
python -m pip install fonttools
# 或使用 UV
uv tool install fonttools
```

也可以通过 `-PpyftsubsetExecutable=/path/to/pyftsubset` 指定可执行文件路径。

全量构建：

```shell
./gradlew build
```

Android 构建：

```shell
./gradlew assembleDebug
./gradlew assembleRelease
```

Desktop 构建：

```shell
./gradlew run
./gradlew packageUberJarForCurrentOS
./gradlew packageDistributionForCurrentOS
```
