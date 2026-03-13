# 密码保护功能使用说明

## 🔐 功能概述

网站现在具有密码保护功能，用户访问时需要输入正确的密码才能进入。

## ✨ 功能特性

1. ✅ 美观的密码验证页面（紫色渐变背景）
2. ✅ 支持URL参数自动填充密码 (`?password=xxx`)
3. ✅ 密码保存在后端配置文件中
4. ✅ 使用 sessionStorage 保存验证状态（关闭浏览器后需重新验证）
5. ✅ 支持回车键快速提交
6. ✅ 优雅的动画效果和错误提示
7. ✅ 完全响应式设计

## 🚀 使用方法

### 方式一：正常访问（手动输入密码）

1. 访问网站：`http://yourdomain.com`
2. 看到密码验证页面
3. 输入密码
4. 点击"验证"按钮或按回车键
5. 验证成功后进入主页面

### 方式二：URL参数自动填充

访问带密码参数的URL：
```
http://yourdomain.com?password=your_secret_password_here
```

系统会自动填充密码并验证，验证成功后直接进入主页面。

## 🛠️ 后端配置

### 第一步：添加后端API接口

请查看 `BACKEND_PASSWORD_API.md` 文件，按照说明添加后端代码。

主要步骤：
1. 在 `application.yml` 中添加密码配置
2. 创建 `AuthController.java`
3. 创建必要的DTO类
4. 重启后端服务

### 第二步：设置密码

在 `application.yml` 中设置密码：

```yaml
site:
  access:
    password: your_secret_password_here
```

**生产环境建议使用环境变量：**
```yaml
site:
  access:
    password: ${SITE_PASSWORD:default_password}
```

然后在服务器上设置环境变量：
```bash
export SITE_PASSWORD="your_strong_password"
```

## 📋 前端文件说明

### 已修改的文件

1. **src/components/PasswordProtection.vue** - 密码验证组件
   - 渐变紫色背景
   - 锁图标动画
   - 密码输入框
   - 自动填充URL参数
   - 验证逻辑

2. **src/App.vue** - 主应用组件
   - 添加密码保护组件
   - 仅在验证通过后显示内容

## 🔒 安全建议

### 密码强度
✅ 使用至少12位的强密码  
✅ 包含大小写字母、数字、特殊字符  
✅ 不要使用常见密码或字典词汇

### 配置安全
✅ 生产环境使用环境变量存储密码  
✅ 不要将密码提交到Git仓库  
✅ 定期更换密码  
✅ 限制知道密码的人数

### 额外保护（可选）
✅ 后端可添加失败次数限制（见 BACKEND_PASSWORD_API.md）  
✅ 添加IP白名单  
✅ 使用HTTPS加密传输  
✅ 记录访问日志

## 🎨 界面截图

验证页面特点：
- 🎨 紫色渐变背景（#667eea → #764ba2）
- 🔒 动画锁图标（脉动效果）
- 💫 平滑的淡入淡出动画
- ⚡ 响应式设计，移动端友好
- ❌ 优雅的错误提示（震动动画）

## 🧪 测试步骤

### 1. 测试后端API
```bash
curl -X POST http://localhost:8080/api/1.0/auth/verify-password \
  -H "Content-Type: application/json" \
  -d '{"password":"your_password"}'
```

预期响应：
```json
{
  "success": true,
  "message": "验证成功"
}
```

### 2. 测试前端

**测试正常流程：**
1. 访问 `http://localhost:5173`
2. 看到密码验证页面
3. 输入正确密码
4. 验证成功，进入主页面

**测试URL参数：**
1. 访问 `http://localhost:5173?password=your_password`
2. 自动验证并进入主页面

**测试错误密码：**
1. 输入错误密码
2. 看到错误提示（红色文字，震动动画）
3. 输入框清空，可重新输入

**测试会话保持：**
1. 验证成功后刷新页面
2. 不需要重新验证（sessionStorage保存状态）
3. 关闭浏览器后重新打开
4. 需要重新验证

## 🔧 自定义配置

### 修改验证状态保存方式

如果希望验证状态持久化（关闭浏览器后仍然有效），可以修改：

```typescript
// PasswordProtection.vue
// 将 sessionStorage 改为 localStorage
sessionStorage.setItem('site_authenticated', 'true');
// 改为
localStorage.setItem('site_authenticated', 'true');
```

### 修改配色方案

```css
/* PasswordProtection.vue */
.password-mask {
  /* 修改背景渐变色 */
  background: linear-gradient(135deg, #your-color-1 0%, #your-color-2 100%);
}
```

### 修改API端点

```typescript
// PasswordProtection.vue
const response = await request({
  url: '/api/1.0/auth/verify-password',  // 修改这里
  method: 'post',
  data: { password: password.value }
});
```

## ❓ 常见问题

### Q: 忘记密码怎么办？
A: 密码存储在后端的 `application.yml` 文件中，查看该文件即可。

### Q: 能否设置多个密码？
A: 当前版本只支持单个密码。如需多密码，需要修改后端逻辑，使用数组存储密码并逐个比对。

### Q: 密码是否安全传输？
A: 密码通过POST请求发送到后端。生产环境务必使用HTTPS确保安全。

### Q: 验证状态保存在哪里？
A: 保存在浏览器的 sessionStorage 中，关闭浏览器后会清除。

### Q: 能否添加"记住我"功能？
A: 可以。将 sessionStorage 改为 localStorage 并添加过期时间逻辑即可。

## 📝 后续优化建议

1. ⭐ 添加"记住我"功能（checkbox）
2. ⭐ 支持多个密码或密码列表
3. ⭐ 添加找回密码/重置功能
4. ⭐ 添加密码强度检查
5. ⭐ 添加验证码防止暴力破解
6. ⭐ 集成OAuth或其他认证方式
7. ⭐ 添加访问日志和统计

## 🎉 完成

密码保护功能已全部实现并配置完成！

如有任何问题，请检查：
1. 后端API是否正确添加
2. 密码配置是否正确
3. 浏览器控制台是否有错误
4. 网络请求是否成功

