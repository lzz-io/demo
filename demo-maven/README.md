# mvn

###### mvn -X debug 输出

###### mvn -e 输出错误信息



## 强制更新本地依赖

mvn dependency:purge-local-repository

mvn -U



## maven-scm-plugin

##### 常用：

###### 从服务器上获取最新的版本

mvn scm:update

###### 提交变更

mvn scm:checkin -Dmessage="描述说明"



## mvn versions 

```sh
# 更改版本
mvn versions:set -DnewVersion=0.1.0-SNAPSHOT

# 下一个快照版
mvn versions:set -DnextSnapshot

# 移除snapshot，使之成为release
mvn versions:set -DremoveSnapshot=true

# 提交
mvn versions:commit

# 回退
mvn versions:revert

# revert和commit 都会删除backup文件
```



##### SCM插件具有以下目标：

```
scm：add - 命令添加文件
scm：bootstrap - 用于签出和构建项目的命令
scm：branch - 分支项目
scm：changelog - 用于显示源代码修订的命令
scm：check-local-modification - 如果有任何本地修改，则构建失败
scm：checkin - 用于提交更改的命令
scm：checkout - 获取源代码的命令
scm：diff - 用于显示工作副本与远程副本的区别的命令
scm：edit - 用于在工作副本上开始编辑的命令
scm：export - 获取新的导出副本的命令
scm：list - 用于获取项目文件列表的命令
scm：remove - 用于标记要删除的文件集的命令
scm：status - 用于显示工作副本的scm状态的命令
scm：tag - 用于标记特定修订的命令
scm：unedit - 停止编辑工作副本的命令
scm：update - 用于使用最新更改更新工作副本的命令
scm：update-subprojects - 用于更新多项目构建中的所有项目的命令
scm：validate - 验证pom中的scm信息
```



## maven-release-plugin

##### 添加 maven-release-plugin 插件

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-release-plugin</artifactId>
	<configuration>
		<tagNameFormat>v@{project.version}</tagNameFormat>
		<!-- 每个模块都具有与父POM相同的版本 -->
		<autoVersionSubmodules>true</autoVersionSubmodules>
	</configuration>
</plugin>
```



###### prepare 更改版本
mvn release:prepare



###### autoVersionSubmodules参数 所有模块的发布版本以及新的SNAPSHOT开发版本都保持一致，可配置在pom.xml中

mvn release:prepare -DautoVersionSubmodules=true



###### 清理

mvn clean release:clean



###### rollback回滚
mvn release:rollback



###### perform 发布jar、-sources.jar和-javadoc.jar到私库

mvn release:perform



##### 非交互式模式的发布

从属性文件release.properties中读取发布参数，进行无人值守的自动发布。

命令：

###### mvn -B release:prepare release:perform

  或

###### mvn --batch-mode release:prepare release:perform



##### release:update-versions

自动更新maven工程及子工程的版本号，并且也会自动更新framework.version属性的值，但唯一有问题的是其版本号只能以-SNAPSHOT结尾，即使使用developmentVersion参数指定了不带SNAPSHOT结尾的版本号也不行

###### mvn release:update-versions -DautoVersionSubmodules=false -DdevelopmentVersion=1.0.3

