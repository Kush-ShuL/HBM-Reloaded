# HBM Reloaded

将指定的 HBM X5846 源码移植至 Minecraft 1.21.1 / NeoForge，使用 Java 21。

本项目基于 [HBM's Nuclear Tech Mod（HBM-NTM）](https://github.com/HbmMods/Hbm-s-Nuclear-Tech-GIT)，原作者为 [HbmMods（Bobcat）](https://github.com/HbmMods)。感谢原作者及上游贡献者提供的代码、模型、材质和玩法。本移植以指定的 `1.0.27_X5846` 源码为依据。

移植仍在进行，完整功能、模型、界面和性能尚未全部验收。当前版本用于开发和测试。

## 构建与运行

安装 JDK 21 后，在项目根目录运行：

```bat
gradlew.bat build
gradlew.bat runClient -Precipe_viewer=jei -Pwith_jade=true -Pwith_computercraft=true
```

Linux 使用 `./gradlew`。配方查看器可选 `jei`、`rei` 或 `none`，通过 `recipe_viewer` 指定。构建产物位于 `build/libs/`。

本机 Windows 验收客户端使用的启动脚本及缓存不纳入 Git。新检出的项目可用 Gradle 的 `runClient` 运行。

## 目录

- `src/`：移植后的源码、资源及加载器元数据。
- `gradle/`：Gradle Wrapper 构建入口。

本地参考源码、原始源码包、移植工具和记录、客户端启动脚本、IDE／代理配置、缓存、构建产物、存档与测试模组不纳入版本控制。

静态物品变体逐步使用独立注册 ID，不提供旧存档或旧配置字段迁移。电脑集成使用 CC: Tweaked。HE 与流体默认使用 HBM 私有系统，外部互通按转换器和配置控制。

## 许可证与致谢

本项目沿用上游的 **GNU Lesser General Public License version 3（LGPL-3.0）**，参见[上游许可证说明](https://github.com/HbmMods/Hbm-s-Nuclear-Tech-GIT#license)。完整许可文本保留于 [LICENSE](LICENSE)（GNU GPL v3 基础条款）及 [LICENSE.LESSER](LICENSE.LESSER)（GNU LGPL v3 补充条款），分发时一并提供。原作者及其他贡献者的版权与署名保留。
