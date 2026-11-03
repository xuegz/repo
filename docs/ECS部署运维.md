# ECS 部署与运维手册

生产环境：阿里云 ECS（华东1 杭州），公网 IP `47.111.99.105`，当前**仅 HTTP 80** 对外（未绑定域名、未做 HTTPS）。

## 一、环境概况

| 项 | 值 |
|---|---|
| 实例 ID | `i-bp1676at048jneetw1tl` |
| 系统 | Alibaba Cloud Linux 3 |
| 规格 | 2 核 / 4 GiB（可用约 3.5 GiB） |
| 磁盘 | 40 GB ESSD |
| JDK | OpenJDK 17（`/usr/bin/java`） |
| MySQL | 8.0.46，仅监听本机 3306 |
| Nginx | 1.24，监听 80 |
| 后端端口 | 8080，仅本机访问（由 Nginx 反代） |

## 二、目录与服务

```
/opt/jiancai/
├── jiancai-server.jar     # 后端可执行包
├── admin/                 # 管理后台构建产物（index.html + assets/）
├── config/application.yml # 生产配置（含数据库/OSS/JWT 凭据，不要提交仓库）
├── upload/                # 本地图片回退目录（Nginx /upload/ 直接映射）
└── logs/                  # 预留日志目录（当前日志走 journald）
```

| 组件 | 单元 | 常用命令 |
|---|---|---|
| 后端 | `jiancai.service` | `systemctl status/restart jiancai` |
| 前端 | `nginx` | `systemctl reload nginx` |
| 数据库 | `mysqld` | `systemctl restart mysqld` |

启动命令（systemd）：

```
/usr/bin/java -jar /opt/jiancai/jiancai-server.jar \
  --spring.config.additional-location=file:/opt/jiancai/config/application.yml
```

数据库：库名 `jiancai`，应用账号 `jiancai@localhost`；root 与 jiancai 的密码见密码管理工具，**不写在本文件**。

## 三、配置管理

- 后端配置 = 仓库内 `server/src/main/resources/application.yml`（默认值、不含密钥）+ 服务器上 `/opt/jiancai/config/application.yml`（覆盖项、含密钥）。
- 外部配置优先级更高，改完需 `systemctl restart jiancai`。
- 覆盖项包含：`server.port`、`spring.profiles.active=mysql`、`spring.datasource.*`、`mybatis-plus.*`、`jiancai.jwt.secret`、`jiancai.upload.path=/opt/jiancai/upload/`、`jiancai.oss.*`、`jiancai.wx.*`。
- 修改配置前先备份：`cp /opt/jiancai/config/application.yml{,.bak}`。

## 四、日常发布

### 4.1 后端

本机构建（JDK 17）：

```bash
cd server
JAVA_HOME="D:/Program Files/Java/jdk-17" mvn -DskipTests clean package
```

上传并重启（保留上一版用于回滚）：

```bash
scp -i ~/.ssh/jiancai-ecs server/target/jiancai-server-1.0.0.jar \
  root@47.111.99.105:/opt/jiancai/jiancai-server.jar.new

ssh -i ~/.ssh/jiancai-ecs root@47.111.99.105 '
  cp /opt/jiancai/jiancai-server.jar /opt/jiancai/jiancai-server.jar.prev &&
  mv /opt/jiancai/jiancai-server.jar.new /opt/jiancai/jiancai-server.jar &&
  systemctl restart jiancai && sleep 5 && systemctl is-active jiancai'
```

验证：`curl -s http://127.0.0.1:8080/api/mp/categories`（在服务器上执行）。

### 4.2 管理后台

```bash
cd admin
npm run build
scp -i ~/.ssh/jiancai-ecs -r admin/dist/* root@47.111.99.105:/opt/jiancai/admin/
```

前端是静态文件，无需 reload Nginx；浏览器强刷即可。

> 前端接口地址由构建时的 `BASE_URL`/相对路径决定，生产走同域 `/api`，因此不需要改代码，只要 Nginx 反代生效。

### 4.3 回滚

```bash
ssh -i ~/.ssh/jiancai-ecs root@47.111.99.105 \
  'cp /opt/jiancai/jiancai-server.jar.prev /opt/jiancai/jiancai-server.jar && systemctl restart jiancai'
```

前端回滚：重新上传上一版 `dist` 覆盖 `/opt/jiancai/admin/`。

**注意**：如果新版本引入了新的 Flyway 迁移（如 `V3__`），回滚 jar 不会回滚数据库结构；MySQL 的 DDL 不可逆，涉及删列/改类型的迁移必须先备份再发布。

## 五、数据库

### 5.1 迁移规范

- 结构变更只新增 `server/src/main/resources/db/migration/V{n}__desc.sql`，禁止修改已执行过的脚本（checksum 校验会阻止启动）。
- `baseline-on-migrate: true` + `baseline-version: 2`：接入 Flyway 之前已存在的库会以 V2 建基线，不重复执行 V1/V2。
- 校验状态：`mysql -ujiancai -p jiancai -e "select version,description,success from flyway_schema_history"`。

### 5.2 备份与恢复

```bash
# 备份
mysqldump -ujiancai -p --single-transaction --routines jiancai > /opt/jiancai/backup/jiancai-$(date +%F).sql
# 恢复
mysql -ujiancai -p jiancai < /opt/jiancai/backup/jiancai-2026-11-03.sql
```

建议加 crontab 每日备份并保留 7 天。

## 六、日志与排障

```bash
journalctl -u jiancai -n 200 --no-pager        # 后端启动/异常
journalctl -u jiancai -f                       # 实时跟踪
tail -f /var/log/nginx/access.log /var/log/nginx/error.log
```

排障顺序（外网打不开时）：

1. **安全组**：实例 → 网络与安全 → 安全组 → 入方向是否有 `TCP 80/443 0.0.0.0/0`（历史上就是这里漏配，表现为端口连接超时）
2. **实例防火墙**：`systemctl is-active firewalld`、`iptables -S INPUT`
3. **端口监听**：`ss -lntp | grep -E ':80|:8080'`
4. **应用**：`systemctl status jiancai`、`journalctl -u jiancai -n 100`
5. **Nginx 配置**：`nginx -t`，`/etc/nginx/conf.d/jiancai.conf`

常见现象：

| 现象 | 原因 |
|---|---|
| 外网连接超时，SSH 正常 | 安全组未放行 80 |
| 页面 200 但接口 401 | 未登录或 token 过期（`/api/auth/login` 重新获取） |
| 上传 200 但图片 403 | 访问的是 Bucket 根路径（列目录），应测具体对象 URL |
| 启动报 Flyway checksum mismatch | 历史迁移脚本被修改，需还原脚本 |

## 七、安全待办

1. **默认账号**：`admin/123456`、`merchant/123456` 以及新商户初始密码 `123456` 仍在线上。改密码（BCrypt，strength 10）：

   ```bash
   # 在服务器上生成哈希（已内置 spring-security-crypto 依赖）
   cd /tmp && python3 - <<'EOF'
   import zipfile
   z = zipfile.ZipFile("/opt/jiancai/jiancai-server.jar")
   open("/tmp/ssc.jar","wb").write(z.read("BOOT-INF/lib/spring-security-crypto-6.2.3.jar"))
   open("/tmp/jcl.jar","wb").write(z.read("BOOT-INF/lib/spring-jcl-6.1.6.jar"))
   EOF
   printf 'import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;\nSystem.out.println(new BCryptPasswordEncoder().encode("新密码"));\n/exit\n' \
     | jshell --class-path "/tmp/ssc.jar:/tmp/jcl.jar" -q

   # 用生成的 $2a$10$... 更新（注意单引号防止 shell 展开）
   mysql -ujiancai -p jiancai -e "update sys_user set password='\$2a\$10\$...' where username='admin'"
   ```

   > 直接在库中改密码不会触发小程序端任何逻辑；用户下次登录即用新密码。

2. **凭据轮换**：OSS AccessKey、小程序 AppSecret 曾在对话中明文出现，建议在阿里云 RAM / 微信公众平台重置，新值只写入 `/opt/jiancai/config/application.yml` 后重启。
3. **HTTPS**：等域名 `mycailutong.com` 完成实名 + ICP 备案后再做，需 Nginx 443 + 证书 + 小程序合法域名（当前域名未实名、未备案，无法解析）。
4. **MySQL 不暴露公网**：当前 3306 只监听回环；安全组不要放行 3306。

## 八、当前验证基线（2026-11-03）

- `http://47.111.99.105/` → 200，跳转 `/login`
- `POST /api/auth/login`（admin/123456）→ 200 并返回 token
- `GET /api/mp/categories` → 200，返回 8 个品类
- 带 token 访问 `/api/admin/{stats,merchants,products}` → 200
- `POST /api/admin/upload` → 200，返回 OSS 地址且图片 URL 可公网访问
