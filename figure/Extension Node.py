import matplotlib.pyplot as plt

# 设置字体和字体大小
plt.rcParams['font.family'] = 'Arial'  # 设置字体
plt.rcParams['font.size'] = 28  # 设置默认字体大小

# 情况及其对应的数量
cases = list(range(1, 6))  # 只保留前五个情况
counts = [826604, 50930, 3243, 181, 12]  # 只保留前五个情况的数量

# 计算总数
total = sum(counts)

# 计算每个情况的百分比
percentages = [count / total * 100 for count in counts]

# 绘制百分比分布折线图
plt.figure(figsize=(8, 5.5))
plt.plot(cases, percentages, marker='o', color='#df7a5e', linestyle='-', linewidth=2)

# 在每个数据点显示值
for i, (x, y) in enumerate(zip(cases, percentages)):
    # 判断y的值，如果为0则显示整数0，否则显示带有两位小数的百分比
    plt.text(x + 0.1, y, f'{y:.2f}%', ha='left', va='bottom', fontsize=28)

plt.xlabel('Shared nibble(s)')
plt.ylabel('Percentage (%)')

plt.xticks(range(1, 6))

# 设置 y 轴的范围从 0 到 100
plt.ylim(-2.5, 105)
plt.xlim(0.75, 6.0)

# 先绘制辅助线
plt.grid(axis='x', linestyle=(0, (5, 5)), linewidth=1.0, zorder=0)
plt.grid(axis='y', linestyle=(0, (5, 5)), linewidth=1.0, zorder=0)

# 保存为PDF图像文件，注意此行应在show之前
plt.savefig('Extension node.pdf', format='pdf', bbox_inches='tight')

# 显示图表
plt.show()
