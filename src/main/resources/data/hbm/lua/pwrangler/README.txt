PWRangler -- HBM PWR control application for CC:Tweaked

Craft this labelled, read-only program disk from any CC floppy disk and one HBM
PWR casing. Put it in a CC disk drive; it does not replace ordinary blank disks.

Connect the existing HBM PWR controller directly or over a wired modem.
Run: disk/PWRangler [reactor peripheral name] [monitor peripheral name]
Use the mount name reported by disk.getMountPath if the drive mounted as disk2.

The source panel's full 54x45 visible area is preserved in a native window.
An advanced monitor at text scale 0.5 is recommended. Smaller terminals can pan
with arrow keys or the mouse wheel. Click/release the original buttons; a
monitor touch activates the same button. Q or QUIT exits without inserting rods.

The original safeguards stay active: >90% core heat, <10000 cold coolant, or
>50% hot-buffer fill inserts rods to 100. The two source switches control the
first two safeguards; the source hot-buffer safeguard is always enabled.
Readouts, per-refresh coolant differences and 0.25-second polling are retained.
RGB colours are displayed through the connected terminal's native16-colour
palette.

只读的 PWRangler 控制软盘。将磁盘放入 CC 驱动器，连接现有 PWR 控制器后
运行 disk/PWRangler。可附加指定控制器和显示器外设名称。完整面板较大，
小屏可用方向键或滚轮平移；按钮使用点击/松开或显示器触摸。Q 或 QUIT 退出。
