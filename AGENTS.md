# Time X 项目指南

本项目为 Compose Multiplatform 应用，面向 Desktop 与 Android。

应用主要展示精确时间。

## 规范

- 保证双端 UI 一致（平台特性除外），不要写消耗性能的逻辑，如每毫秒更新 UI。
- 保证可 i18n，不要硬编码字符串。
- 编辑代码后运行 `./gradlew.bat build` 保证代码无误。
- 不要擅自调用 `android`、`adb` 等调试。
