import matplotlib.pyplot as plt
from matplotlib import ticker

# 设置字体和字体大小
plt.rcParams['font.family'] = 'Arial'  # 设置字体
plt.rcParams['font.size'] = 28  # 设置默认字体大小

# 定义数据
x_values = ["0.01m", "0.1m", "1m", "10m", "100m"]
labels = ['MPT', 'M+ Trie 2', 'M+ Trie 4', 'M+ Trie 16', 'M+ Trie 64', 'M+ Trie 256']
y_values = [
    [41.807, 45.241, 52.249, 91.292, 161.011],
    [16.110, 17.390, 20.016, 57.910, 109.575],
    [14.207, 14.288, 16.455, 53.130, 98.943],
    [13.107, 13.439, 13.488, 46.466, 85.395],
    [19.491, 21.955, 24.126, 67.578, 129.225],
    [21.871, 23.722, 27.270, 71.811, 146.193]
]

# 颜色和样式列表
# colors = ['#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b']
colors = ['#f4f1de', '#df7a5e', '#3c405b', '#82b29a', '#f2cc8e', '#e2b6ae']
hatches = ['/', '\\', '|', '-', '+', 'x']

# 绘制柱状图
num_bars = len(labels)
bar_width = 0.15
bar_positions = list(range(len(x_values)))

plt.figure(figsize=(8, 5.5))  # 修改图像的大小应该在绘图之前

# 先绘制辅助线
plt.grid(axis='y', linestyle=(0, (5, 5)), linewidth=1.0, zorder=0)

for i in range(num_bars):
    plt.bar(
        [x + i * bar_width for x in bar_positions],
        y_values[i],
        width=0.15,
        color=colors[i],  # 设置颜色
        edgecolor='black',  # 设置边缘颜色
        label=labels[i],
        # hatch=hatches[i]  # 设置填充样式
        zorder=10
    )

# 设置X轴标签和标题
plt.xlabel('State number')
plt.ylabel('Time (μs)')

# 设置X轴刻度标签
plt.xticks([x + (num_bars - 1) * bar_width / 2 for x in bar_positions], x_values)

# 添加图例，去掉边框，设置图例的字体大小并分成两列，微调位置和行间距
# plt.legend(
#     loc='upper left',
#     fontsize=14,
#     ncol=2,
#     framealpha=1,
#     edgecolor='white',
#     bbox_to_anchor=(0, 1.02),
#     handletextpad=0.5,  # 调整图标与文本之间的距离
#     labelspacing=0.5   # 调整每一行之间的距离
# )

# 设置y轴范围
plt.ylim(0, 170)

# 设置y轴刻度
plt.yticks(range(0, 161, 20))

# 保存为EPS图像文件，注意此行应在show之前
plt.savefig('State persistence.pdf', format='pdf', bbox_inches='tight')

# 显示图表
plt.show()
