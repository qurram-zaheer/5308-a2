
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import os


commit_metrics = {}
current_path = os.path.abspath(os.getcwd())
for file in os.listdir(current_path):
    joined_path = os.path.join(current_path, file)
    if os.path.isdir(joined_path):
        commit_metrics[file] = {}
        for inner_file in os.listdir(joined_path):
            if inner_file[-3:] == 'csv':
                commit_metrics[file][inner_file.split(".")[0]] = pd.read_csv(
                    os.path.join(joined_path, inner_file))


smell_causes = {}
architecture_density = []
project_loc = []
implementation_density = []
design_density = []
smell_number = []
class_metrics_mean = []
class_metrics_max = []
for i, (commit, category_dict) in enumerate(commit_metrics.items()):
    class_loc = []
    class_wmc = []
    class_lcom = []
    smell_sum = 0
    for category, df in category_dict.items():
        if category == 'ArchitectureSmells':
            smell_sum += df.shape[0]
            architecture_density.append(df.shape[0])
        elif category == 'DesignSmells':
            smell_sum += df.shape[0]
            design_density.append(df.shape[0])
        elif category == 'ImplementationSmells':
            smell_sum += df.shape[0]
            implementation_density.append(df.shape[0])
        elif category == 'TypeMetrics':
            df_copy = df[(df['LOC'] != -1) & (df['LCOM'] != -1)
                         & (df['WMC'] != -1)]
            cols = ['LOC', 'LCOM', 'WMC']
            class_metrics_mean.append(df_copy[cols].mean().values)
            class_metrics_max.append(df_copy[cols].max().values)
            project_loc.append(df['LOC'].sum())
    smell_number.append(smell_sum)


architecture_density = [x for _, x in sorted(
    zip(project_loc, architecture_density))]
implementation_density = [x for _, x in sorted(
    zip(project_loc, implementation_density))]
design_density = [x for _, x in sorted(zip(project_loc, design_density))]
smell_number = [x for _, x in sorted(zip(project_loc, smell_number))]
class_metrics_mean = [x for _, x in sorted(
    zip(project_loc, class_metrics_mean))]
class_metrics_max = [x for _, x in sorted(zip(project_loc, class_metrics_max))]
project_loc_sorted = sorted(project_loc)


commits = commit_metrics.keys()

versions = ['V' + str(i+1) for i in range(len(commits))]
fig, ax1 = plt.subplots()
ax1.plot(commits, project_loc_sorted, marker='o', linewidth=1, label='LOC')
ax1.set_xlabel("Commits")
ax1.set_ylabel("Project LOC")
ax2 = ax1.twinx()
ax2.plot(commits, smell_number, color='orange',
         marker='o', linewidth=1, label='# of smells')
ax2.set_ylabel("Total smells")
locs, labels = plt.xticks()
plt.xticks(locs, versions)

ax1.set_title("Total LOC & Total smells vs. Commits")
ax1.grid(visible=True)

lines = ax1.get_lines() + ax2.get_lines()
ax1.legend(lines, [line.get_label() for line in lines], loc='lower right')

fig.tight_layout()
plt.savefig('total.png', facecolor='white', transparent=False)


width = 0.35

a = np.array(architecture_density)
b = np.array(implementation_density)
c = np.array(design_density)
plt.grid(visible=True)
plt.rc('axes', axisbelow=True)
p1 = plt.bar(versions, a)
p2 = plt.bar(versions, b, bottom=a)
p3 = plt.bar(versions, c, bottom=a+b)

plt.ylabel('Smells')
plt.title("Smell types per commit")
plt.legend((p1[0], p2[0], p3[0]), ('Architecture', 'Implementation', "Design"))

plt.savefig('smell_share.png', facecolor='white', transparent=False)


x = np.arange(len(versions))
x = np.array([2*i for i in x])
fig, ax = plt.subplots()
width = 0.5
rects1 = ax.bar(x-3*width/2, a, width, label='Arch. Smells')
rects2 = ax.bar(x-width/2, b, width, label='Impl. Smells')
rects3 = ax.bar(x+width/2, c, width, label='Desn. Smells')
ax.set_xticks([i-width/2 for i in x], versions)
ax.set_xlabel("Commits")
ax.set_ylabel("# of smells")
plt.legend()
plt.grid(visible=True)
plt.title("Smell count per type")
fig.tight_layout()
plt.savefig('smell_share1.png', facecolor='white', transparent=False)


fig, ax1 = plt.subplots()
ax1.plot(commits, project_loc_sorted, marker='o', linewidth=1, label="LOC")
ax1.set_xlabel("Commits")
ax1.set_ylabel("Project LOC")
ax2 = ax1.twinx()
ax2.plot(commits, [x*1000/y for x, y in zip(architecture_density, project_loc)],
         color='orange', marker='o', linewidth=1, label="Arch. Density")
ax2.set_ylabel("Smells")
locs, labels = plt.xticks()
plt.xticks(locs, versions)

ax1.set_title("Smells vs. Commits")
ax1.grid(visible=True)

lines = ax1.get_lines() + ax2.get_lines()
ax1.legend(lines, [line.get_label() for line in lines], loc='lower right')

fig.tight_layout()
plt.savefig('architecture_d.png', facecolor='white', transparent=False)


fig, ax1 = plt.subplots()
ax1.plot(commits, project_loc_sorted, marker='o', linewidth=1, label="LOC")
ax1.set_xlabel("Commits")
ax1.set_ylabel("Project LOC")
ax2 = ax1.twinx()
ax2.plot(commits, [x*1000/y for x, y in zip(implementation_density, project_loc)], color='orange',
         marker='o', linewidth=1, label="Smell Density")
ax2.set_ylabel("Implementation Smells")
locs, labels = plt.xticks()
plt.xticks(locs, versions)

ax1.set_title("Implementation Smells vs. Commits")
ax1.grid(visible=True)

lines = ax1.get_lines() + ax2.get_lines()
ax1.legend(lines, [line.get_label() for line in lines], loc='lower right')

fig.tight_layout()
plt.savefig('implementation_d.png', facecolor='white', transparent=False)


fig, ax1 = plt.subplots()
ax1.plot(commits, project_loc_sorted, marker='o', linewidth=1, label="LOC")
ax1.set_xlabel("Commits")
ax1.set_ylabel("Project LOC")
ax2 = ax1.twinx()
ax2.plot(commits, [x*1000/y for x, y in zip(design_density, project_loc)], color='orange',
         marker='o', linewidth=1, label="Smell Density")
ax2.set_ylabel("Design Smells")
locs, labels = plt.xticks()
plt.xticks(locs, versions)

ax1.set_title("Design Smells vs. Commits")
ax1.grid(visible=True)

lines = ax1.get_lines() + ax2.get_lines()
ax1.legend(lines, [line.get_label() for line in lines], loc='lower right')

fig.tight_layout()
plt.savefig('design_d.png', facecolor='white', transparent=False)


# LOC, LCOM, WMC
np_max_metrics = np.array(class_metrics_max)
np_mean_metrics = np.array(class_metrics_mean)


for i, (maxes, means) in enumerate(zip(np_max_metrics.T, np_mean_metrics.T)):
    fig, ax1 = plt.subplots()
    ax1.plot(commits, maxes, marker='o', linewidth=1, label="Max " + cols[i])
    ax1.set_ylabel("Maximum class " + cols[i])
    ax1.set_xlabel("Commits")
    ax2 = ax1.twinx()
    ax2.plot(commits, means, color='orange',
             marker='o', linewidth=1, label="Mean " + cols[i])
    ax2.set_ylabel("Mean class LOC")
    locs, labels = plt.xticks()
    plt.xticks(locs, versions)

    ax1.set_title("Class LOC vs. Commits")
    ax1.grid(visible=True)

    lines = ax1.get_lines() + ax2.get_lines()
    ax1.legend(lines, [line.get_label() for line in lines], loc='lower right')

    fig.tight_layout()
    plt.savefig(cols[i] + '.png', facecolor='white', transparent=False)
