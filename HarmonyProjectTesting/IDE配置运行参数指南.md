# IDE配置运行参数指南

## IntelliJ IDEA 配置步骤

### 方法1：通过运行配置界面（推荐）

1. **打开运行配置**
   - 找到 `Application.java` 文件（`src/main/java/org/springblade/Application.java`）
   - 右键点击文件 → 选择 `Run 'Application'` 或 `Debug 'Application'`
   - 或者点击右上角的运行配置下拉菜单 → 选择 `Edit Configurations...`

2. **添加运行参数**
   - 在左侧找到你的运行配置（通常是 `Application`）
   - 在右侧找到 `Program arguments` 或 `VM options` 字段
   - 在 `Program arguments` 中添加：
     ```
     --spring.profiles.active=dev
     ```
   - 或者在 `VM options` 中添加：
     ```
     -Dspring.profiles.active=dev
     ```

3. **保存并运行**
   - 点击 `Apply` 保存配置
   - 点击 `OK` 关闭窗口
   - 点击运行按钮启动应用

### 方法2：通过环境变量配置

1. **打开运行配置**
   - 右键 `Application.java` → `Run` → `Edit Configurations...`

2. **添加环境变量**
   - 在运行配置中找到 `Environment variables` 字段
   - 点击右侧的文件夹图标或 `...` 按钮
   - 点击 `+` 添加新变量：
     - Name: `SPRING_PROFILES_ACTIVE`
     - Value: `dev`
   - 点击 `OK` 保存

3. **保存并运行**
   - 点击 `Apply` → `OK`
   - 运行应用

### 方法3：临时运行（不保存配置）

1. **运行菜单**
   - 右键 `Application.java` → `Run 'Application.main()'`
   - 在运行配置下拉菜单中，点击 `Edit Configurations...`

2. **修改参数**
   - 在 `Program arguments` 中添加：`--spring.profiles.active=dev`
   - 点击运行

## Eclipse 配置步骤

### 方法1：通过运行配置

1. **打开运行配置**
   - 右键 `Application.java` → `Run As` → `Run Configurations...`
   - 或者 `Run` → `Run Configurations...`

2. **创建/编辑配置**
   - 在左侧找到 `Java Application` → 展开
   - 找到 `Application` 配置（如果没有则新建）
   - 在右侧 `Arguments` 标签页
   - 在 `Program arguments` 中添加：
     ```
     --spring.profiles.active=dev
     ```

3. **保存并运行**
   - 点击 `Apply` 保存
   - 点击 `Run` 启动应用

### 方法2：通过环境变量

1. **打开运行配置**
   - `Run` → `Run Configurations...`

2. **配置环境变量**
   - 选择 `Environment` 标签页
   - 点击 `New...` 添加环境变量：
     - Name: `SPRING_PROFILES_ACTIVE`
     - Value: `dev`
   - 点击 `OK`

3. **保存并运行**
   - 点击 `Apply` → `Run`

## Visual Studio Code 配置步骤

### 方法1：通过launch.json

1. **创建/编辑launch.json**
   - 按 `F5` 或点击调试按钮
   - 选择 `Java` 环境
   - 会自动创建 `.vscode/launch.json` 文件

2. **添加参数**
   - 在 `launch.json` 中找到你的配置
   - 添加 `args` 字段：
     ```json
     {
       "type": "java",
       "name": "Application",
       "request": "launch",
       "mainClass": "org.springblade.Application",
       "projectName": "Bei-Xiang",
       "args": "--spring.profiles.active=dev"
     }
     ```

3. **运行**
   - 按 `F5` 启动调试

### 方法2：通过环境变量

在 `launch.json` 中添加：
```json
{
   "env": {
      "SPRING_PROFILES_ACTIVE": "dev"
   }
}
```

## 验证配置是否生效

启动应用后，查看控制台输出，应该看到：

```
The following profiles are active: dev
```

如果没有看到这行，说明配置没有生效，请检查：
1. 参数是否正确添加
2. 是否保存了配置
3. 是否使用了正确的运行配置

## 常见问题

### Q: 添加参数后还是使用默认配置？
A: 
- 检查参数格式是否正确（注意前面有两个短横线 `--`）
- 确认使用的是正确的运行配置
- 尝试重启IDE

### Q: 如何确认当前使用的配置文件？
A:
查看启动日志，应该看到：
```
The following profiles are active: dev
```
以及数据库连接信息应该显示远程数据库地址。

### Q: 可以同时配置多个参数吗？
A:
可以，用空格分隔：
```
--spring.profiles.active=dev --server.port=9999
```

## 推荐配置方式

**IntelliJ IDEA**: 使用方法1（Program arguments），最简单直接
**Eclipse**: 使用方法1（Program arguments）
**VS Code**: 使用launch.json配置

## 截图说明位置

### IntelliJ IDEA 配置位置：
```
Run → Edit Configurations...
  → Application
    → Configuration 标签页
      → Program arguments: --spring.profiles.active=dev
```

### Eclipse 配置位置：
```
Run → Run Configurations...
  → Java Application → Application
    → Arguments 标签页
      → Program arguments: --spring.profiles.active=dev
```

