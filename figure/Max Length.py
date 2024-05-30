import matplotlib.pyplot as plt

# 设置字体和字体大小
plt.rcParams['font.family'] = 'Arial'  # 设置字体
plt.rcParams['font.size'] = 28  # 设置默认字体大小

# 定义数据
x_values = ["0.01m", "0.1m", "1m", "10m", "100m"]
labels = ['MPT', 'M+ Trie 2', 'M+ Trie 4', 'M+ Trie 16', 'M+ Trie 64', 'M+ Trie 256']
y_values = [
    [15, 17, 18, 19, 20],
    [6, 7, 8, 9, 10],
    [4, 5, 6, 7, 8],
    [3, 4, 5, 6, 7],
    [2, 3, 4, 5, 6],
    [2, 3, 4, 4, 5]
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
plt.ylabel('Path length (nodes)')

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
plt.ylim(0, 22)

# 保存为EPS图像文件，注意此行应在show之前
plt.savefig('Max length.pdf', format='pdf', bbox_inches='tight')

# 显示图表
plt.show()
