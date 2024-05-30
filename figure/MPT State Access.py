import matplotlib.pyplot as plt

# 设置字体和字体大小
plt.rcParams['font.family'] = 'Arial'  # 设置字体
plt.rcParams['font.size'] = 28  # 设置默认字体大小

# 定义数据
x_values = ["0.01m", "0.1m", "1m", "10m", "100m"]
labels = ['MPT']
y_values = [
    [10.165, 10.170, 13.146, 19.250, 66.612],
    [2.811, 5.465, 6.550, 8.097, 18.868],
    [2.477, 3.008, 4.135, 6.193, 10.358],
    [1.789, 2.197, 2.455, 2.474, 3.416],
    [1.139, 2.052, 3.695, 3.939, 8.090],
    [1.788, 3.854, 4.514, 8.316, 8.727]
]

# 颜色和样式列表
# colors = ['#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b']
colors = ['#df7a5e', '#f4f1de', '#3c405b', '#82b29a', '#f2cc8e', '#e2b6ae']
hatches = ['/', '\\', '|', '-', '+', 'x']

# 绘制柱状图
num_bars = len(labels)
bar_width = 0.45
bar_positions = list(range(len(x_values)))

plt.figure(figsize=(8, 5.5))  # 修改图像的大小应该在绘图之前

# 先绘制辅助线
plt.grid(axis='y', linestyle=(0, (5, 5)), linewidth=1.0, zorder=0)

for i in range(num_bars):
    for j in range(len(x_values)):
        plt.bar(
            bar_positions[j] + i * bar_width,
            y_values[i][j],
            width=bar_width,
            color=colors[i],  # 设置颜色
            edgecolor='black',  # 设置边缘颜色
            label=labels[i] if j == 0 else "",  # 只在第一次循环时添加标签
            # hatch=hatches[i]  # 设置填充样式
            zorder=10
        )
        plt.text(
            bar_positions[j] + i * bar_width,
            y_values[i][j] + 1,  # 调整文本的垂直位置
            f'{y_values[i][j]:.2f}',
            ha='center',
            va='bottom',
            fontsize=28,
            zorder=10
        )

# 设置X轴标签和标题
plt.xlabel('State number')
plt.ylabel('Average time(μs)')

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
plt.ylim(0, 76)

# 设置y轴刻度
plt.yticks(range(0, 75, 10))

# 保存为PDF图像文件，注意此行应在show之前
plt.savefig('MPT state access.pdf', format='pdf', bbox_inches='tight')

# 显示图表
plt.show()
