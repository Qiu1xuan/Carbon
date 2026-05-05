# Carbon 项目协作指南

欢迎加入 Carbon 项目！如果你是 Git 小白，别慌，下面这份教程会一步步带你**把代码从 GitHub 上拿下来、修改、再提交回去**，并且学会**切换分支**（这是团队协作的核心）。

## 1. 准备工作

- 安装 Git：https://git-scm.com/downloads
- 注册 GitHub 账号：https://github.com
- 把你的 GitHub 用户名告诉项目管理员，他会把你加为协作者（你会收到邮件邀请，接受即可）。

## 2. 克隆项目到本地

打开电脑上的 **Git Bash**（Windows）或 **终端**（Mac/Linux），运行：

```bash
git clone https://github.com/你的用户名/Carbon.git


这会把整个项目完整地下载到你当前的文件夹里，并自动关联好远程仓库（别名为 origin）。

3. 理解分支（重要！）
分支就像平行宇宙：main 分支是“主干现实”（稳定版本），每个人都在自己的分支上开发，测试无误后再合并到 main。

永远不要直接在 main 分支上修改代码。

每次做新功能或修 bug，都从 main 新建一个个人分支。

4. 日常协作流程（附命令）
4.1 进入项目目录并切换到 main 分支
bash
cd Carbon
git checkout main
4.2 拉取最新的远程代码（确保你本地的 main 是最新的）
bash
git pull origin main
4.3 从 main 新建一个自己的分支（分支名用“功能/名字”格式，例如 add-login-page）
bash
git checkout -b 你的分支名
这条命令会创建并切换到新分支。之后你的所有修改都只在这个分支上。

4.4 写代码、修改文件…
用你的 IDE（比如 IntelliJ IDEA）正常干活。

4.5 把修改提交到本地分支
bash
git add .                 # 把所有改动加入“暂存区”
git commit -m "简短描述你做了什么"   # 提交到本地分支
可以多次 add 和 commit，每次 commit 就像一次存档。

4.6 把本地分支推送到 GitHub
bash
git push origin 你的分支名
这样 GitHub 上就会出现你的分支，其他人也能看到。

4.7 发起合并请求（Pull Request / PR）
打开项目的 GitHub 页面，你会看到一条提示“你刚刚推送了分支，是否创建 Pull Request？”——点击 Compare & pull request。

写清楚你做了哪些改动，然后点击 Create pull request。

项目管理员或其他成员会审核你的代码，没问题就会合并到 main 分支。

4.8 回到 main 并拉取合并后的最新代码
bash
git checkout main
git pull origin main
然后你就可以再次从最新的 main 新建分支，开始下一个任务了。

5. 常见问题（新手必看）
问题	解决办法
“我忘了当前在哪个分支了”	运行 git branch，带 * 号的就是当前分支。
“我想切换到另一个分支”	git checkout 分支名（前提是分支已存在）。
“我改了代码，但想放弃所有更改”	git checkout .（会丢弃所有未提交的改动，慎用！）。
“我 add 错了文件，想撤回”	git restore --staged 文件名。
“推送时提示 failed to push some refs”	先 git pull origin 你的分支名 拉取最新再推送。
“不知道怎么给项目管理员发审核请求”	推送后去 GitHub 网页点 Pull requests → New pull request，选择 base: main 和 compare: 你的分支名。
6. 记住三个核心原则
先拉后推 – 每次开始工作前，git pull origin main。

分支隔离 – 永远不在 main 上改代码。

提交描述清晰 – 让别人一眼看懂你改了啥。

如果卡在任何地方，把终端里的报错信息完整地截图发到群里，我们会帮你解决。

祝你协作愉快！

text

---

把这段内容保存为 `README.md` 放在项目根目录，然后提交并推送到 GitHub：

```bash
git add README.md
git commit -m "添加新手协作指南，强调分支用法"
git push origin main
这样任何一个不懂 Git 的人，只要能按照 README 的步骤操作，就不会出错。如果你希望更自动化（比如用 GitHub Classroom 或模板），也可以在此基础上扩展。
