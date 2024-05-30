import matplotlib.pyplot as plt
from matplotlib.patches import Patch

# 设置字体和字体大小
plt.rcParams['font.family'] = 'Arial'  # 设置字体
plt.rcParams['font.size'] = 28  # 设置默认字体大小

# 定义数据
x_values = ["0.01m", "0.1m", "1m", "10m", "100m"]
labels = ['MPT', 'M+ Trie 2', 'M+ Trie 4', 'M+ Trie 16', 'M+ Trie 64', 'M+ Trie 256']
y_values = [
    [7.070, 7.877, 8.675, 9.487, 10.332],
    [3.601, 4.422, 5.246, 6.084, 6.928],
    [2.930, 3.822, 4.703, 5.525, 6.284],
    [2.539, 3.260, 3.977, 4.882, 5.809],
    [1.898, 2.816, 3.702, 4.523, 5.236],
    [1.590, 2.263, 2.927, 3.881, 4.809]
]

# 颜色和样式列表
colors = ['#f4f1de', '#df7a5e', '#3c405b', '#82b29a', '#f2cc8e', '#e2b6ae']
hatches = ['/', '\\', '|', '-', '+', 'x']

# 绘制柱状图
num_bars = len(labels)
bar_width = 0.15
bar_positions = list(range(len(x_values)))

plt.figure(figsize=(8, 5.5))  # 修改图像的大小应该在绘图之前

for i in range(num_bars):
    plt.bar(
        [x + i * bar_width for x in bar_positions],
        y_values[i],
        width=0.13,
        facecolor=colors[i],  # 设置颜色
        label=labels[i],
        zorder=10
    )

# 设置X轴标签和标题
plt.xlabel('State number')
plt.ylabel('Path length (nodes)')

# 设置X轴刻度标签
plt.xticks([x + (num_bars - 1) * bar_width / 2 for x in bar_positions], x_values)

# 设置y轴范围和刻度
plt.ylim(0, 10.5)
plt.yticks(range(0, 11, 2))

# 清空当前图形，保留图例
plt.clf()

# 创建图例矩形标记
legend_handles = [Patch(facecolor=color, label=label, edgecolor='black') for color, label in zip(colors, labels)]

# 仅显示图例
figlegend = plt.figure(figsize=(8, 1))
plt.figlegend(
    handles=legend_handles,
    loc='center',
    ncol=6,
    framealpha=1,
    edgecolor='white'
)

# 保存图例为PDF图像文件
figlegend.savefig('Storage model evaluation legend.pdf', format='pdf', bbox_inches='tight')

# 显示图例
plt.show()
