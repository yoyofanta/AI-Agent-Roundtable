# AI Agent 多角色圆桌对谈与剧本生成系统

## 一、项目简介

本项目是一个面向 AI Agent 应用开发场景的多角色圆桌对谈与剧本生成系统，基于 Vue3 + Spring Boot + DeepSeek API 实现。

系统围绕“漫剧 / 短剧内容生产”场景设计，支持两种剧本生成路径：

1. 直接生成方案：用户输入题材、平台、受众、风格和具体需求，系统结合 Tool Calling 内容分析工具与 RAG 知识库生成剧本方案。
2. 多 Agent 协助生成：用户先选择 2-4 个 Agent 进行圆桌讨论，由不同人格、角色或内容生产视角分别发言，生成圆桌总结后，再一键带入剧本生成页面，生成最终剧本方案。

---

## 二、项目亮点

- 支持“直接生成方案”和“多 Agent 协助生成”两种内容生产路径
- 支持 2-4 个 Agent 围绕同一内容需求进行圆桌讨论
- 支持 16 型人格 Agent，并可设置男 / 女 / 不限性别
- 支持自定义角色 Agent，可配置角色名称、参考人格、性别、头像和角色 Prompt
- 支持圆桌总结一键带入剧本生成流程
- 支持 Tool Calling 内容分析，识别冲突强度、情绪基调、推荐标签和优化建议
- 支持 RAG 内容知识库问答，并可对知识库问答进行本地存档
- 支持 Memory 用户记忆，用于记录用户最近问题、常用 Agent、历史回复和对话摘要
- 支持 H2 文件数据库，方便本地开发、测试和演示
- 支持 Postman、Python requests、pytest 接口测试与自动化测试

---

## 三、技术栈

### 前端

- Vue3
- TypeScript
- Vite
- Axios
- HTML / CSS

### 后端

- Spring Boot
- Java
- Spring Data JPA
- RESTful API
- H2 / MySQL 兼容配置

### AI 应用能力

- DeepSeek API
- Prompt Engineering
- Multi-Agent Roundtable
- RAG 知识增强
- Memory 用户记忆
- Tool Calling 工具调用

### 测试与工具

- Postman
- Python requests
- pytest
- pytest-html
- Chrome DevTools
- H2 Console
- Git / GitHub

---

## 四、核心功能

### 1. 内容生产主流程

系统将内容生产作为主入口，提供两种方式：

#### 方式一：直接生成方案

用户直接输入：

- 题材类型
- 平台场景
- 目标受众
- 内容风格
- 具体需求

系统会先调用 Tool Calling 内容分析工具，对文本进行冲突强度、情绪基调和标签分析，再结合内容知识库生成最终剧本方案。

流程如下：

text
输入内容需求
→ Tool Calling 内容分析
→ RAG 知识增强
→ 调用 DeepSeek
→ 生成剧本方案


### 方式二：多 Agent 协助生成

用户选择 2-4 个 Agent 进行圆桌讨论。每个 Agent 会基于自身角色 Prompt、性别设定、用户需求和前面 Agent 的观点依次发言。

圆桌完成后，系统生成综合总结，并支持一键带入剧本生成页面。

流程如下：

输入内容需求
→ 选择 2-4 个 Agent
→ 多 Agent 依次发言
→ 生成圆桌总结
→ 一键带入内容生成
→ Tool Calling 重新分析
→ 生成最终剧本方案

### 2. 多 Agent 圆桌讨论

系统支持选择 2-4 个 Agent 参与讨论。

每个 Agent 会根据不同角色 Prompt 给出不同视角，例如：

Agent 类型	主要能力
剧情策划 Agent	故事结构、开场冲突、人物关系和反转设计
角色对白 Agent	角色对白、情绪台词和短剧化表达
情绪节奏 Agent	爽点、虐点、反转点和用户留存节奏
运营分析 Agent	标题、标签、受众卖点和平台传播建议
MBTI 人格 Agent	以不同人格风格参与创意讨论
自定义角色 Agent	根据用户自定义 Prompt 参与圆桌讨论

圆桌对话不是最终结果，而是剧本生成前的创意讨论过程。

### 3. 16 型人格 Agent

系统默认提供 16 型人格 Agent，例如：

Agent	角色定位
INTJ 冷静规划师	理性拆解、长期规划、结构分析
INFP 温柔共情者	情绪理解、人物内心、价值表达
ENTP 灵感辩手	创意发散、反向思考、观点碰撞
ESTJ 执行管理者	流程管理、效率优化、落地执行

说明：16 型人格在本项目中主要作为 Agent 角色风格设定使用，不作为心理诊断或科学人格测评结论。

### 4. 自定义角色 Agent

用户可以创建自定义 Agent，并加入圆桌讨论。

可配置字段包括：

角色名称
参考基础人格
性别设定
头像符号
角色设定 Prompt

示例：

角色名称：毒舌女编剧
参考人格：ENTP
性别设定：女
角色设定：你是一位擅长短剧冲突设计的女性编剧，说话直接，喜欢从人物动机、反转节奏和观众爽点出发提出建议。
### 5. Tool Calling 内容分析工具

系统实现了一个轻量级内容分析工具，用于分析用户输入的剧情需求。

分析结果包括：

冲突强度
情绪基调
推荐标签
优化建议

示例结果：

{
  "conflictLevel": "高",
  "emotionTone": "虐恋 / 追悔 / 反转",
  "tags": ["追妻火葬场", "误会", "反转", "女主成长"],
  "suggestion": "建议前 3 秒直接抛出核心误会或关键矛盾，中段强化人物关系拉扯，后段集中释放反转和追悔情绪。"
}

两种生成方式都会使用 Tool Calling：

直接生成方案：分析原始用户需求
多 Agent 协助生成：分析原始需求 + 圆桌总结
### 6. RAG 内容知识库

系统内置轻量级 RAG 内容知识库，知识片段存储在 H2 数据库的 knowledge_chunk 表中。

知识库内容包括：

短视频漫剧强冲突开场
前 3 秒钩子设计
追妻火葬场剧情结构
误会反转伏笔设计
人物关系张力设计
角色对白生成原则
分集节奏设计
标题优化方法
多 Agent 圆桌任务拆解
RAG 幻觉控制原则

RAG 流程：

用户问题
→ 检索 knowledge_chunk
→ 命中相关知识片段
→ 拼接进 Prompt
→ 调用 DeepSeek
→ 生成知识增强回答

知识库页面支持随意提问，并可将问答结果进行本地存档。

### 7. Memory 用户记忆

系统支持记录用户最近问题、常用 Agent、历史回复和对话摘要，用于提升多轮对话连续性。

RAG 和 Memory 的区别：

RAG：外部知识库，提供领域知识
Memory：用户历史上下文，记录用户最近关注内容
## 五、核心接口
### 1. 多 Agent 圆桌接口
POST /api/ai/agent/roundtable

请求示例：

{
  "topic": "我想做一个追妻火葬场题材的漫剧，女主被误会后离开，男主后期追悔莫及。请从剧情结构、人物情绪、创意反转和落地执行角度帮我讨论。",
  "agents": [
    {
      "code": "INTJ",
      "name": "INTJ 冷静规划师",
      "role": "擅长理性规划、结构拆解、长期策略和目标管理。角色性别设定：男性。"
    },
    {
      "code": "INFP",
      "name": "INFP 温柔共情者",
      "role": "擅长共情、价值感表达、情绪接纳和人物内心分析。角色性别设定：女性。"
    }
  ]
}
### 2. Tool Calling 内容分析接口
POST /api/ai/tools/content/analyze

请求示例：

{
  "type": "追妻火葬场",
  "platform": "短视频漫剧",
  "audience": "18-25岁女性",
  "style": "强冲突、快节奏、有反转",
  "text": "女主被男主误会后离开，男主后来发现真相追悔莫及，希望设计15集漫剧方案。"
}
### 3. RAG 知识库问答接口
GET /api/knowledge/ask?q=追妻火葬场题材怎么设计强冲突开场
### 4. Memory 相关接口
GET /api/memory/current
POST /api/memory/clear
## 六、项目结构
AI-Agent-Roundtable/
├─ backend/
│  ├─ src/main/java/com/moodboard/
│  │  ├─ controller/
│  │  ├─ service/
│  │  ├─ repository/
│  │  ├─ entity/
│  │  └─ config/
│  └─ pom.xml
│
├─ frontend/
│  ├─ src/
│  │  ├─ pages/
│  │  ├─ components/
│  │  ├─ api/
│  │  └─ router.ts
│  └─ package.json
│
├─ docs/
│  ├─ 01-测试计划.md
│  ├─ 02-功能测试用例表.xlsx
│  ├─ 03_Bug记录表.xlsx
│  ├─ 04-测试总结.md
│  ├─ 05-接口测试记录.md
│  ├─ 06-AI回复质量评测表.xlsx
│  └─ 07-AI应用专项测试.md
│
├─ tests/
│  ├─ api_test.py
│  ├─ test_api_pytest.py
│  └─ reports/
│     └─ api_report.html
│
├─ README.md
└─ .gitignore
## 七、运行方式
### 1. 启动后端
cd backend
mvn spring-boot:run

后端默认地址：

http://localhost:8889
### 2. 启动前端
cd frontend
npm install
npm run dev

前端默认地址：

http://localhost:5174
### 3. DeepSeek API 配置

项目支持 mock / real 两种模式。

开发测试可使用 mock 模式：

AI_MODE=mock

真实调用 DeepSeek 时，建议通过环境变量配置：

AI_MODE=real
DEEPSEEK_API_KEY=你的_API_KEY
AI_MODEL=deepseek-chat

注意：不要将真实 API Key 提交到 GitHub。

## 八、H2 数据库

项目开发阶段使用 H2 文件数据库，方便本地启动、接口测试和面试演示。

H2 Console：

http://localhost:8889/h2-console

JDBC URL：

jdbc:h2:file:./data/moodboard;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE

用户名：

sa

密码为空。

查看 RAG 知识库：

select id, title, content from knowledge_chunk;

查看知识库数量：

select count(*) from knowledge_chunk;
## 九、接口自动化测试

项目使用 Python requests + pytest 进行接口自动化测试。

安装依赖：

pip install requests pytest pytest-html

执行测试：

python -m pytest tests/test_api_pytest.py --html=tests/reports/api_report.html --self-contained-html

测试覆盖：

登录注册
Tool Calling 内容分析
RAG 知识库问答
Memory 用户记忆
多 Agent 圆桌
16 型人格 Agent
自定义角色 Agent
内容生成流程
## 十、测试文档
文档	内容
01-测试计划.md	测试范围、测试方法、测试环境和准出条件
02-功能测试用例表.xlsx	功能测试用例
03_Bug记录表.xlsx	Bug 记录、复现步骤、原因分析和修复结果
04-测试总结.md	测试执行情况、Bug 统计和测试结论
05-接口测试记录.md	核心接口测试记录
06-AI回复质量评测表.xlsx	AI 回复相关性、一致性、安全性和可执行性评测
07-AI应用专项测试.md	RAG、Memory、Tool Calling、多 Agent 编排专项测试
## 十一、AI 应用专项测试维度

项目针对 AI Agent 应用补充了专项测试维度：

Agent 角色一致性
多 Agent 发言是否重复
圆桌总结是否覆盖各 Agent 观点
Tool Calling 是否正确识别冲突强度和标签
RAG 回答是否命中知识库内容
最终剧本方案是否包含人物设定、冲突、分集、对白、标题标签
高风险内容是否避免绝对化和不当建议
多轮对话和用户记忆是否保持上下文连续性
## 十二、项目成果
实现“直接生成方案”和“多 Agent 协助生成”两种内容生产路径
实现 16 型人格 Agent 圆桌
实现自定义角色 Agent
实现圆桌总结一键带入剧本生成
实现 Tool Calling 内容分析工具
实现 RAG 内容知识库问答与存档
实现 Memory 用户记忆
接入 DeepSeek API
完成接口测试和 pytest 自动化测试
输出测试计划、测试用例、Bug 记录、接口测试记录、测试总结和 AI 应用专项测试文档

## 十三、后续优化方向
引入 Embedding 和向量数据库，提高 RAG 检索准确性
增加更多内容生产领域知识片段
优化 Agent 调度策略，减少多 Agent 发言重复
增加用户对生成结果的评分反馈
增加 AI 回复质量自动评估
支持将 H2 切换为 MySQL，用于正式部署

