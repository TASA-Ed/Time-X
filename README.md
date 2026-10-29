# Time X

Time X，时间系列的第十代产品。X 为罗马数字 10。

## 介绍

你可以用它查看精确的时间。

## 构建与字体子集化

构建前安装 Python 与 fonttools，并确保 `pyftsubset` 在 `PATH` 中：

```shell
python -m pip install fonttools
./gradlew.bat build
```

也可以通过 `-PpyftsubsetExecutable=/path/to/pyftsubset` 指定可执行文件路径。

- `shared/fontSources/` 仅存放原始的 `source_han_sans_sc_bold.otf` 与 `source_han_sans_sc_regular.otf`，不直接打包。
- `:shared:subsetFonts` 从 `shared/src/commonMain/composeResources/values*/**/*.xml` 的文本中提取字集，解码 XML 实体与 Compose 的 `\uXXXX`、`\n`、`\t`、`\\` 转义，再调用 `pyftsubset`。
- 子集字体生成到 `shared/build/generated/fontSubset/composeResources/font/`，与其他资源合并后供 Android 和 Desktop 共用；字体资源名称不变，Source Sans 3 不参与子集化。
- 字体或本地化文件变化时自动重新生成；输入不变时跳过子集化。`clean` 会清除生成文件，下次构建重新生成。

子集字集只来自本地化资源，不保证覆盖用户输入或运行时生成的任意字符。
