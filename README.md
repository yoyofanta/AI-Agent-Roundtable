# AI Agent 多角色圆桌对谈与内容生产系统

## 一、项目简介

本项目是一个面向 **AI Agent 应用开发** 场景的多角色圆桌协作系统，基于 **Vue3 + Spring Boot + DeepSeek API** 实现。

系统支持多个 Agent 围绕同一内容需求进行协作发言，并结合 **RAG 知识库、Memory 用户记忆、Tool Calling 内容分析工具** 生成综合内容方案。

项目重点面向 **漫剧 / 短剧 / 文娱内容生产** 场景，模拟实际内容团队中的角色分工，例如剧情策划、角色对白、情绪节奏和运营分析，适合作为 AI Agent 应用开发实习岗位项目展示。

---

## 二、项目定位

本项目从原有 AI 情绪树洞系统收敛为：

> 面向漫剧内容生产的 AI Agent 圆桌协作系统

核心目标是展示：

- 多 Agent 角色设定与协作流程
- 大语言模型 API 调用
- Prompt 拼接与角色差异化输出
- RAG 知识增强
- Memory 用户记忆
- Tool Calling 工具调用
- 接口测试与自动化测试

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
- JPA
- H2 / MySQL
- DeepSeek API

### AI 应用能力

- LLM API 调用
- Prompt Engineering
- RAG 知识库
- Memory 用户记忆
- Tool Calling
- Multi-Agent Roundtable

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

### 1. 内容生产 Agent 工作台

面向漫剧 / 短剧内容生产场景，固定设置 4 类 Agent：

| Agent | 职责 |
|---|---|
| 剧情策划 Agent | 负责故事结构、开场冲突、人物关系和反转设计 |
| 角色对白 Agent | 负责角色对白、情绪台词和短剧化表达 |
| 情绪节奏 Agent | 负责爽点、虐点、反转点和用户留存节奏 |
| 运营分析 Agent | 负责标题、标签、受众卖点和平台传播建议 |

用户输入内容需求后，系统会让多个 Agent 依次发言，并生成综合内容方案。

示例输入：

```text
题材类型：追妻火葬场
平台场景：短视频漫剧
目标受众：18-25岁女性
内容风格：强冲突、快节奏、反转
具体需求：霸总误会女主，女主离开后男主追悔莫及


完成 68 条功能测试用例、25 个接口测试点和 17 条接口自动化测试用例，覆盖登录注册、RAG、Memory、Tool Calling、16 型人格圆桌、自定义角色 Agent 圆桌和内容生产 Agent 圆桌等核心流程。