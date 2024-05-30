import matplotlib.pyplot as plt
from matplotlib import ticker

# Define the font properties using dictionary
font = {'family': 'Arial', 'size': 28}
plt.rcParams.update({'font.size': 28})

# Define categories and throughput data
categories = ['MPT', 'M+T1', 'M+T2', 'M+T3', 'M+T4']
throughput = [850.991, 1299.288, 1554.492, 1685.730, 2089.652]

# Define the colors list, one for each category
# colors = ['#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd']
colors = ['#f4f1de', '#df7a5e', '#3c405b', '#82b29a', '#f2cc8e']

# Set the bar width
bar_width = 0.4

# Calculate positions for the bars
bar_positions = range(len(categories))

# Create the figure and an axes
fig, ax = plt.subplots(figsize=(8, 5.5))

# Plot the bars
bars = ax.bar(bar_positions, throughput, width=bar_width, color=colors, edgecolor='#5c5c5c')

# Set the y axis label
ax.set_ylabel('Throughput (tx/s)', fontdict=font)

# Set the x-axis tick labels
ax.set_xticks(bar_positions)
ax.set_xticklabels(categories)

# Set the y-axis limits to accommodate the throughput values
ax.set_ylim(0, max(throughput) * 1.1)  # 10% more than the max to accommodate the top

# 先绘制辅助线
plt.grid(axis='y', linestyle=(0, (5, 5)), linewidth=1.0, zorder=0)

# 设置y轴范围
plt.ylim(0, 2300)

# 设置y轴刻度
plt.yticks(range(0, 2500, 1000))

# 设置y轴格式，使其以k为单位显示
plt.gca().yaxis.set_major_formatter(ticker.FuncFormatter(lambda x, pos: '{:.0f}k'.format(x * 1e-3)))

# Label the bars with the throughput data
for bar in bars:
    bar.zorder = 10
    yval = bar.get_height()
    plt.text(bar.get_x() + bar.get_width() / 2, yval, '{:.0f}'.format(yval), ha='center', va='bottom',
             fontdict={'size': 24})

# Save the figure as a PDF
plt.savefig('Throughput.pdf', format='pdf', bbox_inches='tight')

# Show the plot
plt.show()
