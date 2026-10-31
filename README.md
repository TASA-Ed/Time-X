# Time X

Time X，时间系列的第十代产品。X 为罗马数字 10。

## 介绍

你可以用它查看精确的时间。

支持通过 NTP 服务器获取精准时间并展示，并自动计算与本地时间的差异。（默认关闭，需要设置里面开启！）

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
./gradlew packageMsi
./gradlew packageDeb
./gradlew packageReleaseMsi
./gradlew packageReleaseDeb
```
