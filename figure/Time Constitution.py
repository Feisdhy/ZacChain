import matplotlib.pyplot as plt
import numpy as np

# 设置字体和字体大小
plt.rcParams['font.family'] = 'Arial'  # 设置字体
plt.rcParams['font.size'] = 28  # 设置默认字体大小

# 用户提供的数据
data = np.array([
    [64.9287341009, 43, 3.6522318465, 22.961243, 80.7039247128],
    [16.6905414927, 41, 2.6481956635, 7.7936085, 72.84678799695],
    [4.61433590025, 41, 2.5452360445, 6.5389958, 63.13577847735],
    [14.1676631647, 14, 1.4797176872, 7.4662372, 71.54706478604],
    [3.62136007701, 14, 0.9929029576, 6.3373321, 62.70536070350]
])

# 分类和堆叠标签
categories = ['MPT', 'M+T1', 'M+T2', 'M+T3', 'M+T4']
stack_labels = ['Access', 'Execute', 'Update', 'Commit', 'Persist']

# 每个部分的颜色和填充样式
# colors = ['#ffffff', '#e0e0e0', '#c2c2c2', '#a6a6a6', '#878787']
# colors = ['#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd']
# colors = ['#9467bd', '#d62728', '#2ca02c', '#ff7f0e', '#1f77b4']
colors = ['#f4f1de', '#df7a5e', '#3c405b', '#82b29a', '#f2cc8e']
# hatches = ['', '///', '', '', '\\\\\\']
hatches = ['', '', '', '', '']

# 条形图的位置
bar_positions = np.arange(len(categories))

# 绘图
fig, ax = plt.subplots(figsize=(8, 5.5))

# 定义每个条形图堆叠的宽度
bar_width = 0.5

# 初始化每个条形图堆叠的底部位置
bottoms = np.zeros(len(categories))

# 为每个堆叠标签添加条形图
for i in range(len(stack_labels)):
    ax.barh(bar_positions, data[:, i], height=bar_width, color=colors[i], edgecolor='#5c5c5c',
            label=stack_labels[i], left=bottoms, zorder=3, hatch=hatches[i])
    bottoms += data[:, i]  # 更新下一个堆叠的底部位置

# 反转 y 轴以逆序排列
ax.invert_yaxis()

# 设置X轴标签和标题
plt.xlabel('Time (ms)')

# 设置 y 轴标签
ax.set_yticks(bar_positions)
ax.set_yticklabels(categories, va='center')
# ax.set_yticklabels(categories, rotation=90, va='center')

# 设置 x 轴主要刻度为 40 的倍数，次要刻度为 20 的倍数
ax.xaxis.set_major_locator(plt.MultipleLocator(40))
ax.xaxis.set_minor_locator(plt.MultipleLocator(20))

# 在 x 轴次要刻度上添加网格线
ax.grid(which='both', axis='x', linestyle=(0, (5, 5)), linewidth=1.0, zorder=0)

# 添加图例，去掉边框，设置图例的字体大小并分成两列，微调位置和行间距
plt.legend(
    loc='lower right',
    fontsize=24,
    ncol=1,
    framealpha=1,
    edgecolor='white',
    handletextpad=0.35,  # 调整图标与文本之间的距离
    labelspacing=0.15,   # 调整每一行之间的距离
    bbox_to_anchor=(1.01, -0.02),  # 调整图例的位置
    handlelength=1.5,  # 调整图例矩形的长度
    handleheight=1  # 调整图例矩形的高度
)

# 紧凑布局以获得更好的间距
plt.tight_layout()

# 在绘制条形图并设置好坐标轴和标签后：
ax = plt.gca()  # 获取当前的 Axes 实例

# 将左侧的轴线移到条形图上方
ax.spines['left'].set_zorder(ax.spines['left'].get_zorder() + 1)

# 保存为 PDF 文件
plt.savefig('Time constitution.pdf', format='pdf', bbox_inches='tight')

# 显示图表
plt.show()
