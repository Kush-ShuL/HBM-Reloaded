# HBM Reloaded

将 HBM's Nuclear Tech Mod 的核工业世界带到 **Minecraft 1.21.1 / NeoForge**。

从矿物开采、材料加工和石油化工，到核反应堆、粒子加速器、导弹与核武器，建设属于你的工业体系，并应对辐射、污染和危险材料带来的挑战。

本项目是基于原版 **1.0.27_X5846** 的非官方社区移植，保留原版的内容、模型、材质和 GUI，并适配现代 Minecraft 的世界高度、标签、配方与物品系统。

## 主要内容

- **工业生产**：矿物与深层矿石、冶炼与铸造、化工与炼油、大型机器、储能和物流系统。
- **核能与高能技术**：研究反应堆、增殖反应堆、锆诺克斯、RBMK、PWR、聚变与粒子加速器。
- **武器与防御**：枪械、弹药、装备、炮塔、爆炸物、核弹及自定义导弹。
- **世界探索**：自然建筑、地下资源、陨石、特殊实体与战利品，以及辐射、疾病和污染系统。
- **游戏内辅助**：机器说明、动画教程、进度系统，以及 JEI／REI 配方与 Jade 信息展示。

## 安装

| 环境 | 要求 |
| --- | --- |
| Minecraft | **1.21.1** |
| 加载器 | **NeoForge 21.1.224 或更新的兼容 21.1 版本** |
| Java | **21** |

1. 安装 Minecraft 1.21.1 和 NeoForge。
2. 将 `HBM-Reloaded-X5846.jar` 放入游戏实例的 `mods` 目录。
3. 按需安装下表中的可选模组，启动游戏。

多人游戏需要在客户端和服务端安装本模组。请使用新存档；本项目不提供旧版 HBM、其他移植版存档或旧配置字段的迁移。

## 可选兼容

| 模组 | 用途 |
| --- | --- |
| **JEI 或 REI** | 查看机器配方、加工流程与流体获取方式 |
| **Jade** | 查看方块、机器和实体信息 |
| **CC: Tweaked** | 通过电脑与外设控制机器，使用 PWRangler 控制程序 |
| **Sodium / Iris** | 渲染优化与光影支持 |
| **Applied Energistics 2** | 专用仓储接入与存储单元内容辐射 |

可选模组需要另行安装与 Minecraft 1.21.1 / NeoForge 对应的版本。

HBM 的 **HE 能量与流体默认使用独立系统**。外部能量互通通过有损转换器实现；外部流体互通需要在配置中显式开启。世界生成、机器参数、核爆和矿物产率等也可通过配置调整。

## 反馈问题

请在 [Issues](https://github.com/Kush-ShuL/HBM-Reloaded/issues) 中提供模组版本、复现步骤及相关日志。模型或界面问题请附截图；涉及光影时，请同时注明光影包和渲染模组版本。

## 从源码构建

安装 JDK 21 后，在项目根目录运行：

```bat
gradlew.bat build
```

构建产物位于 `build/libs/`。Linux 使用 `./gradlew build`。网络代理等本机设置请放在个人 Gradle 配置中。

启动开发客户端：

```bat
gradlew.bat runClient -Precipe_viewer=jei -Pwith_jade=true -Pwith_computercraft=true
```

`recipe_viewer` 可选 `jei`、`rei` 或 `none`。翻译维护在 `src/main/resources/assets/hbm/lang/`，简体中文为 `zh_cn.json`，英文为 `en_us.json`。

## 许可证与致谢

原版 [HBM's Nuclear Tech Mod（HBM-NTM）](https://github.com/HbmMods/Hbm-s-Nuclear-Tech-GIT) 由 [HbmMods（Bobcat）](https://github.com/HbmMods) 及上游贡献者开发。HBM Reloaded 的代码、模型、材质、音效和玩法建立在他们的工作之上，感谢所有原作者与贡献者。

本项目沿用上游的 **GNU Lesser General Public License version 3（LGPL-3.0）**，参见[上游许可证说明](https://github.com/HbmMods/Hbm-s-Nuclear-Tech-GIT#license)。完整许可文本保留于 [LICENSE](LICENSE)（GNU GPL v3 基础条款）及 [LICENSE.LESSER](LICENSE.LESSER)（GNU LGPL v3 补充条款），分发时一并提供。原作者及其他贡献者的版权与署名保留。
