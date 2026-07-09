<template>
  <main class="page">
    <section class="hero">
      <div>
        <p class="eyebrow">AI Agent Roundtable</p>
        <h1>AI Agent 多角色圆桌系统</h1>
        <p class="desc">
          面向漫剧 / 短剧内容生产场景，支持多 Agent 协作发言、RAG 知识增强、Memory 记忆和工具调用式内容分析。
        </p>
      </div>

      <div class="hero-actions">
        <button class="ghost-btn" @click="switchView('content')">内容生产</button>
        <button class="primary-btn" @click="switchView('roundtable')">开始圆桌</button>
      </div>
    </section>

    <section v-if="view === 'home'" class="home-grid">
      <button class="feature-card" @click="switchView('content')">
        <span class="feature-icon">🎬</span>
        <h3>内容生产 Agent 工作台</h3>
        <p>剧情策划、角色对白、情绪节奏、运营分析四类 Agent 协作生成内容方案。</p>
      </button>

      <button class="feature-card" @click="switchView('roundtable')">
        <span class="feature-icon">🤖</span>
        <h3>多 Agent 圆桌</h3>
        <p>选择 2-4 个 Agent 围绕同一问题依次发言，并生成综合总结。</p>
      </button>

      <button class="feature-card" @click="switchView('rag')">
        <span class="feature-icon">📚</span>
        <h3>RAG 知识库</h3>
        <p>检索情绪支持、MBTI 风格、漫剧创作结构、标题优化等知识片段。</p>
      </button>

      <button class="feature-card" @click="switchView('memory')">
        <span class="feature-icon">🧠</span>
        <h3>Memory 用户记忆</h3>
        <p>查看用户最近需求、常聊 Agent、最近问题和 AI 回答，支持多轮优化。</p>
      </button>
    </section>

    <!-- 内容生产 Agent 工作台 -->
    <section v-if="view === 'content'" class="workspace">
      <aside class="side-panel">
        <div class="side-head">
          <p class="eyebrow">Content Agent</p>
          <h2>内容生产工作台</h2>
          <p>固定 4 个内容生产 Agent，适合面试演示“任务拆解 + 多 Agent 协作”。</p>
        </div>

        <div class="agent-mini-list">
          <div v-for="agent in contentAgents" :key="agent.code" class="mini-agent">
            <span>{{ agent.icon }}</span>
            <div>
              <strong>{{ agent.name }}</strong>
              <p>{{ agent.role }}</p>
            </div>
          </div>
        </div>
      </aside>

      <section class="main-panel">
        <div class="panel-head">
          <div>
            <p class="eyebrow">Script Production</p>
            <h2>漫剧 / 短剧内容方案生成</h2>
            <p>输入一个内容需求，系统会让 4 个 Agent 分别从剧情、对白、节奏和运营角度输出方案。</p>
          </div>
        </div>

        <div class="form-grid">
          <label>
            题材类型
            <input v-model="contentForm.type" placeholder="例如：追妻火葬场 / 误会反转 / 甜宠逆袭" />
          </label>

          <label>
            平台场景
            <input v-model="contentForm.platform" placeholder="例如：短视频漫剧 / 小红书图文 / 抖音短剧" />
          </label>

          <label>
            目标受众
            <input v-model="contentForm.audience" placeholder="例如：18-25岁女性 / 文娱内容用户" />
          </label>

          <label>
            内容风格
            <input v-model="contentForm.style" placeholder="例如：强冲突、快节奏、反转、爽感" />
          </label>
        </div>

        <label class="block-label">
          具体需求
          <textarea
            v-model="contentForm.topic"
            rows="5"
            placeholder="例如：霸总误会女主，女主离开后男主追悔莫及，希望生成一个15集漫剧的核心内容方案。"
          />
        </label>

        <div class="tool-card">
          <div>
            <p class="tool-title">Tool Calling：内容标签分析工具</p>
            <p class="tool-desc">点击生成前，系统会先用本地规则分析冲突强度、情绪基调和推荐标签。</p>
          </div>
          <button class="ghost-btn" @click="analyzeContent">分析内容</button>
        </div>

        <div v-if="toolResult" class="tool-result">
          <p><strong>冲突强度：</strong>{{ toolResult.conflictLevel }}</p>
          <p><strong>情绪基调：</strong>{{ toolResult.emotionTone }}</p>
          <p><strong>推荐标签：</strong>{{ toolResult.tags.join('、') }}</p>
          <p><strong>优化建议：</strong>{{ toolResult.suggestion }}</p>
        </div>

        <div class="actions-row">
          <button class="primary-btn" :disabled="loading" @click="runContentAgents">
            {{ loading ? 'Agent 正在协作...' : '生成内容生产方案' }}
          </button>
          <button class="ghost-btn" @click="clearCurrent">清空结果</button>
        </div>

        <RoundtableResult
          v-if="messages.length"
          :messages="messages"
          :summary="summary"
        />
      </section>
    </section>

    <!-- 多 Agent 圆桌 -->
    <section v-if="view === 'roundtable'" class="workspace">
      <aside class="side-panel">
        <div class="side-head">
          <p class="eyebrow">Sessions</p>
          <h2>历史圆桌</h2>
          <p>本地保存当前账号的圆桌记录，方便面试时展示多轮会话管理。</p>
        </div>

        <button class="new-btn" @click="newRoundtable">+ 新建圆桌</button>

        <div class="session-list">
          <button
            v-for="session in sessions"
            :key="session.id"
            class="session-card"
            :class="{ active: session.id === activeSessionId }"
            @click="openSession(session.id)"
          >
            <strong>{{ session.title }}</strong>
            <span>{{ session.createdAt }}</span>
          </button>
        </div>
      </aside>

      <section class="main-panel">
        <div class="panel-head">
          <div>
            <p class="eyebrow">Multi-Agent Roundtable</p>
            <h2>选择参与圆桌的 Agent</h2>
            <p>选择 2-4 个 Agent，输入问题后，系统会让每个 Agent 依次发言并生成综合总结。</p>
          </div>

          <span class="count-pill">已选 {{ selectedAgents.length }} / 4</span>
        </div>

        <div class="agent-grid">
          <button
            v-for="agent in allAgents"
            :key="agent.code"
            class="agent-card"
            :class="{ selected: selectedAgentCodes.includes(agent.code) }"
            @click="toggleAgent(agent.code)"
          >
            <span>{{ agent.icon }}</span>
            <strong>{{ agent.name }}</strong>
            <p>{{ agent.role }}</p>
          </button>
        </div>

        <label class="block-label">
          圆桌议题
          <textarea
            v-model="roundtableTopic"
            rows="4"
            placeholder="例如：我想做一个追妻火葬场题材的漫剧，需要帮我拆解剧情、对白、节奏和运营标题。"
          />
        </label>

        <div class="actions-row">
          <button class="primary-btn" :disabled="loading" @click="runSelectedAgents">
            {{ loading ? 'Agent 正在发言...' : '开始多 Agent 圆桌' }}
          </button>
          <button class="ghost-btn" @click="clearCurrent">清空结果</button>
        </div>

        <RoundtableResult
          v-if="messages.length"
          :messages="messages"
          :summary="summary"
        />
      </section>
    </section>

    <!-- RAG 知识库 -->
    <section v-if="view === 'rag'" class="single-panel">
      <div class="panel-head">
        <div>
          <p class="eyebrow">RAG Knowledge Base</p>
          <h2>RAG 知识库测试</h2>
          <p>用于演示知识检索、TopK 返回和知识增强回答。建议补充漫剧创作、强冲突开场、标题优化等内容生产知识。</p>
        </div>
      </div>

      <label class="block-label">
        输入问题
        <textarea
          v-model="ragQuery"
          rows="4"
          placeholder="例如：追妻火葬场题材怎么设计强冲突开场？"
        />
      </label>

      <div class="actions-row">
        <button class="primary-btn" :disabled="loading" @click="askRag">
          {{ loading ? '检索生成中...' : 'RAG 生成回答' }}
        </button>
      </div>

      <div v-if="ragAnswer" class="answer-card">
        <h3>RAG 回答</h3>
        <p>{{ ragAnswer }}</p>
      </div>

      <div v-if="ragContexts.length" class="contexts">
        <h3>命中的知识片段</h3>
        <div v-for="(ctx, index) in ragContexts" :key="index" class="context-card">
          <strong>{{ ctx.title || `知识片段 ${index + 1}` }}</strong>
          <p>{{ ctx.content || ctx }}</p>
        </div>
      </div>
    </section>

    <!-- Memory -->
    <section v-if="view === 'memory'" class="single-panel">
      <div class="panel-head">
        <div>
          <p class="eyebrow">Memory</p>
          <h2>用户记忆面板</h2>
          <p>展示后端 user_memory 中记录的最近需求、常聊 Agent、最近问题和 AI 回答。</p>
        </div>

        <div class="hero-actions">
          <button class="ghost-btn" @click="loadMemory">刷新</button>
          <button class="danger-btn" @click="clearMemory">清空记忆</button>
        </div>
      </div>

      <div class="memory-grid">
        <div class="memory-card">
          <span>最近情绪 / 需求摘要</span>
          <p>{{ memory.recentEmotionSummary || memory.chatSummary || '暂无记录' }}</p>
        </div>

        <div class="memory-card">
          <span>最近常聊 Agent</span>
          <p>{{ memory.recentPersonaName || memory.recentPersonaCode || '暂无记录' }}</p>
        </div>

        <div class="memory-card">
          <span>最近一次用户问题</span>
          <p>{{ memory.lastQuestion || '暂无记录' }}</p>
        </div>

        <div class="memory-card">
          <span>最近一次 AI 回答</span>
          <p>{{ memory.lastAnswer || '暂无记录' }}</p>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, defineComponent, h, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

type ViewType = 'home' | 'content' | 'roundtable' | 'rag' | 'memory'

type Agent = {
  code: string
  name: string
  icon: string
  role: string
}

type RoundtableMessage = {
  role: 'user' | 'agent'
  agentCode?: string
  agentName?: string
  content: string
}

type Session = {
  id: number
  title: string
  topic: string
  agents: Agent[]
  messages: RoundtableMessage[]
  summary: string
  createdAt: string
}

type ToolResult = {
  conflictLevel: string
  emotionTone: string
  tags: string[]
  suggestion: string
}

const route = useRoute()
const router = useRouter()

const view = ref<ViewType>('home')
const loading = ref(false)

const contentForm = reactive({
  type: '追妻火葬场',
  platform: '短视频漫剧',
  audience: '18-25岁女性',
  style: '强冲突、快节奏、反转',
  topic: '霸总误会女主，女主离开后男主追悔莫及，希望生成一个15集漫剧的核心内容方案。'
})

const roundtableTopic = ref('我想做一个追妻火葬场题材的漫剧，需要帮我拆解剧情、对白、节奏和运营标题。')
const selectedAgentCodes = ref<string[]>(['PLOT', 'DIALOGUE', 'RHYTHM', 'OPS'])

const messages = ref<RoundtableMessage[]>([])
const summary = ref('')
const toolResult = ref<ToolResult | null>(null)

const ragQuery = ref('追妻火葬场题材怎么设计强冲突开场？')
const ragAnswer = ref('')
const ragContexts = ref<any[]>([])

const memory = reactive<Record<string, any>>({})

const sessions = ref<Session[]>([])
const activeSessionId = ref<number | null>(null)

const contentAgents: Agent[] = [
  {
    code: 'PLOT',
    name: '剧情策划 Agent',
    icon: '🧩',
    role: '负责故事结构、开场冲突、人物关系和反转设计'
  },
  {
    code: 'DIALOGUE',
    name: '角色对白 Agent',
    icon: '💬',
    role: '负责角色对白、情绪台词和短剧化表达'
  },
  {
    code: 'RHYTHM',
    name: '情绪节奏 Agent',
    icon: '🎢',
    role: '负责爽点、虐点、反转点和用户留存节奏'
  },
  {
    code: 'OPS',
    name: '运营分析 Agent',
    icon: '📈',
    role: '负责标题、标签、受众卖点和平台传播建议'
  }
]

const mbtiAgents: Agent[] = [
  { code: 'INTJ', name: 'INTJ 理性规划者', icon: '🧠', role: '拆解问题、制定计划' },
  { code: 'INFP', name: 'INFP 温柔共情者', icon: '🌙', role: '理解感受、接住情绪' },
  { code: 'ENFP', name: 'ENFP 行动鼓励者', icon: '☀️', role: '点燃动力、提供鼓励' },
  { code: 'ISTJ', name: 'ISTJ 稳妥执行者', icon: '📘', role: '重视步骤、稳定推进' }
]

const allAgents = computed(() => [...contentAgents, ...mbtiAgents])

const selectedAgents = computed(() => {
  return allAgents.value.filter(agent => selectedAgentCodes.value.includes(agent.code))
})

function getCurrentUserKey() {
  return localStorage.getItem('moodboard_current_user') || 'guest'
}

function getSessionStorageKey() {
  return `moodboard_agent_roundtable_sessions_${getCurrentUserKey()}`
}

function getToken() {
  return localStorage.getItem('token') || ''
}

function getTodayTime() {
  const now = new Date()
  const month = `${now.getMonth() + 1}`.padStart(2, '0')
  const day = `${now.getDate()}`.padStart(2, '0')
  const hour = `${now.getHours()}`.padStart(2, '0')
  const minute = `${now.getMinutes()}`.padStart(2, '0')
  return `${month}-${day} ${hour}:${minute}`
}

function switchView(next: ViewType) {
  view.value = next
  router.replace({
    path: '/app/tree-hole',
    query: {
      view: next
    }
  })

  if (next === 'memory') {
    loadMemory()
  }
}

watch(
  () => route.query.view,
  value => {
    const v = String(value || 'home') as ViewType
    if (['home', 'content', 'roundtable', 'rag', 'memory'].includes(v)) {
      view.value = v
    }
  },
  { immediate: true }
)

onMounted(() => {
  loadSessions()
})

function loadSessions() {
  try {
    const raw = localStorage.getItem(getSessionStorageKey())
    sessions.value = raw ? JSON.parse(raw) : []
  } catch (e) {
    console.warn('读取圆桌历史失败', e)
    sessions.value = []
  }
}

function saveSessions() {
  localStorage.setItem(getSessionStorageKey(), JSON.stringify(sessions.value))
}

function newRoundtable() {
  activeSessionId.value = null
  messages.value = []
  summary.value = ''
  roundtableTopic.value = ''
  selectedAgentCodes.value = ['PLOT', 'DIALOGUE']
}

function openSession(id: number) {
  const session = sessions.value.find(item => item.id === id)
  if (!session) return

  activeSessionId.value = id
  roundtableTopic.value = session.topic
  selectedAgentCodes.value = session.agents.map(agent => agent.code)
  messages.value = session.messages
  summary.value = session.summary
}

function saveCurrentSession(topic: string, agents: Agent[]) {
  const title = topic.length > 18 ? `${topic.slice(0, 18)}...` : topic
  const session: Session = {
    id: activeSessionId.value || Date.now(),
    title: title || '新圆桌',
    topic,
    agents,
    messages: messages.value,
    summary: summary.value,
    createdAt: getTodayTime()
  }

  const index = sessions.value.findIndex(item => item.id === session.id)

  if (index >= 0) {
    sessions.value[index] = session
  } else {
    sessions.value.unshift(session)
  }

  activeSessionId.value = session.id
  saveSessions()
}

function toggleAgent(code: string) {
  const exists = selectedAgentCodes.value.includes(code)

  if (exists) {
    selectedAgentCodes.value = selectedAgentCodes.value.filter(item => item !== code)
    return
  }

  if (selectedAgentCodes.value.length >= 4) {
    alert('最多选择 4 个 Agent')
    return
  }

  selectedAgentCodes.value.push(code)
}

function clearCurrent() {
  messages.value = []
  summary.value = ''
  ragAnswer.value = ''
  ragContexts.value = []
  toolResult.value = null
}

async function analyzeContent() {
  const text = `${contentForm.type} ${contentForm.topic} ${contentForm.style}`

  try {
    const body = await postJson('/ai/tools/content/analyze', {
      type: contentForm.type,
      platform: contentForm.platform,
      audience: contentForm.audience,
      style: contentForm.style,
      text
    })

    const data = pickData(body)
    const output = data.output || data

    toolResult.value = {
      conflictLevel: output.conflictLevel || '中',
      emotionTone: output.emotionTone || '情感 / 剧情 / 内容创意',
      tags: Array.isArray(output.tags) ? output.tags : ['剧情向', '内容创意'],
      suggestion: output.suggestion || '建议补充更明确的人物冲突、反转节点和标题标签。'
    }
  } catch (e) {
    console.warn('内容分析工具调用失败，使用前端兜底', e)
    analyzeContentFallback()
  }
}

function analyzeContentFallback() {
  const text = `${contentForm.type} ${contentForm.topic} ${contentForm.style}`

  const tags: string[] = []

  if (text.includes('追妻') || text.includes('火葬场')) tags.push('追妻火葬场')
  if (text.includes('误会')) tags.push('误会')
  if (text.includes('反转')) tags.push('反转')
  if (text.includes('霸总')) tags.push('霸总')
  if (text.includes('强冲突')) tags.push('强冲突')
  if (text.includes('甜')) tags.push('甜宠')
  if (tags.length === 0) tags.push('情感向', '剧情向', '内容创意')

  let conflictLevel = '中'

  if (text.includes('误会') || text.includes('离开') || text.includes('追悔') || text.includes('强冲突')) {
    conflictLevel = '高'
  }

  let emotionTone = '情感 / 成长'

  if (text.includes('追妻') || text.includes('追悔')) {
    emotionTone = '虐恋 / 追悔 / 反转'
  } else if (text.includes('甜')) {
    emotionTone = '甜宠 / 治愈 / 轻喜'
  }

  toolResult.value = {
    conflictLevel,
    emotionTone,
    tags,
    suggestion: '开头需要尽快抛出核心误会或强冲突，前3秒给出人物关系张力，中段加入反转，结尾保留追更悬念。'
  }
}

function buildContentTopic() {
  return [
    `题材类型：${contentForm.type}`,
    `平台场景：${contentForm.platform}`,
    `目标受众：${contentForm.audience}`,
    `内容风格：${contentForm.style}`,
    `具体需求：${contentForm.topic}`,
    toolResult.value
      ? `工具分析结果：冲突强度 ${toolResult.value.conflictLevel}；情绪基调 ${toolResult.value.emotionTone}；推荐标签 ${toolResult.value.tags.join('、')}；建议 ${toolResult.value.suggestion}`
      : ''
  ].filter(Boolean).join('\n')
}

async function runContentAgents() {
  if (!contentForm.topic.trim()) {
    alert('请先输入内容需求')
    return
  }

  await analyzeContent()
const topic = buildContentTopic()
await runRoundtable(topic, contentAgents)
}

async function runSelectedAgents() {
  if (selectedAgents.value.length < 2) {
    alert('至少选择 2 个 Agent')
    return
  }

  if (!roundtableTopic.value.trim()) {
    alert('请先输入圆桌议题')
    return
  }

  await runRoundtable(roundtableTopic.value, selectedAgents.value)
}

async function runRoundtable(topic: string, agents: Agent[]) {
  loading.value = true
  summary.value = ''

  messages.value = [
    {
      role: 'user',
      content: topic
    }
  ]

  try {
    const body = await postJson('/ai/agent/roundtable', {
      topic,
      agents: agents.map(agent => ({
        code: agent.code,
        name: agent.name,
        role: agent.role
      }))
    })

    const data = pickData(body)

    const agentReplies = Array.isArray(data.agents)
      ? data.agents.map((item: any, index: number) => ({
          role: 'agent' as const,
          agentCode: item.agentCode || item.code || agents[index]?.code,
          agentName: item.agentName || item.name || agents[index]?.name,
          content: item.reply || item.content || item.answer || mockAgentReply(agents[index], topic)
        }))
      : agents.map(agent => ({
          role: 'agent' as const,
          agentCode: agent.code,
          agentName: agent.name,
          content: mockAgentReply(agent, topic)
        }))

    messages.value.push(...agentReplies)

    summary.value =
      data.summary ||
      data.finalSummary ||
      buildMockSummary(topic, agents)

    saveCurrentSession(topic, agents)
  } catch (e) {
    console.warn('后端圆桌接口调用失败，使用前端演示兜底', e)

    const fallbackReplies = agents.map(agent => ({
      role: 'agent' as const,
      agentCode: agent.code,
      agentName: agent.name,
      content: mockAgentReply(agent, topic)
    }))

    messages.value.push(...fallbackReplies)
    summary.value = buildMockSummary(topic, agents)
    saveCurrentSession(topic, agents)
  } finally {
    loading.value = false
  }
}

async function askRag() {
  if (!ragQuery.value.trim()) {
    alert('请输入问题')
    return
  }

  loading.value = true
  ragAnswer.value = ''
  ragContexts.value = []

  try {
    const query = encodeURIComponent(ragQuery.value)
    const body = await getJson(`/knowledge/ask?q=${query}&topK=3`)
    const data = pickData(body)

    ragAnswer.value =
      data.answer ||
      data.reply ||
      data.content ||
      '已完成检索，但后端未返回 answer 字段。'

    ragContexts.value =
      data.contexts ||
      data.results ||
      data.chunks ||
      []
  } catch (e) {
    console.warn('RAG 接口调用失败，使用演示兜底', e)

    ragAnswer.value = '这是一个演示兜底回答：可以从强冲突开场、人物关系张力、反转节点和标题标签四个角度优化内容方案。'
    ragContexts.value = [
      {
        title: '短视频漫剧强冲突开场',
        content: '前3秒需要快速呈现人物矛盾或利益冲突，让用户立即理解角色关系和追看动机。'
      },
      {
        title: '标题优化方法',
        content: '标题可以突出误会、反转、追悔、身份差异等关键词，提高内容点击率。'
      }
    ]
  } finally {
    loading.value = false
  }
}

async function loadMemory() {
  loading.value = true

  try {
    const body = await getJson('/memory/current')
    const data = pickData(body)

    Object.keys(memory).forEach(key => delete memory[key])
    Object.assign(memory, data)
  } catch (e) {
    console.warn('Memory 获取失败', e)
    Object.keys(memory).forEach(key => delete memory[key])
    Object.assign(memory, {
      recentEmotionSummary: '演示模式：暂无后端 Memory 数据',
      recentPersonaName: '内容生产 Agent',
      lastQuestion: '用户最近在测试 Agent 圆桌内容生产流程',
      lastAnswer: '系统生成了剧情策划、对白、节奏和运营分析方案'
    })
  } finally {
    loading.value = false
  }
}

async function clearMemory() {
  try {
    await postJson('/memory/clear', {})
    Object.keys(memory).forEach(key => delete memory[key])
    alert('Memory 已清空')
  } catch (e) {
    console.warn('Memory 清空失败', e)
    alert('Memory 清空失败，请检查后端接口')
  }
}

function pickData(body: any) {
  if (!body) return {}
  if (body.data) return body.data
  return body
}

async function getJson(path: string) {
  const res = await fetch(`/api${path}`, {
    method: 'GET',
    headers: {
      Authorization: getToken() ? `Bearer ${getToken()}` : ''
    }
  })

  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`)
  }

  return res.json()
}

async function postJson(path: string, payload: any) {
  const res = await fetch(`/api${path}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: getToken() ? `Bearer ${getToken()}` : ''
    },
    body: JSON.stringify(payload)
  })

  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`)
  }

  return res.json()
}

function mockAgentReply(agent: Agent, topic: string) {
  const shortTopic = topic.length > 80 ? `${topic.slice(0, 80)}...` : topic

  switch (agent.code) {
    case 'PLOT':
      return `我会先从剧情结构拆解这个需求：${shortTopic}。建议采用“强冲突开场—误会升级—女主离开—男主追悔—反转和解”的结构，前3秒直接抛出误会冲突，中段通过身份差异和情绪拉扯制造追看点。`
    case 'DIALOGUE':
      return `从对白角度看，台词要短、直接、有情绪。比如男主可以说“你走了才知道，我输掉的不是面子，是你。”女主对白要保留克制和反击感，让观众感受到委屈和反转。`
    case 'RHYTHM':
      return `节奏上建议每集保留一个情绪钩子：第1集误会爆发，第2-4集女主受委屈，第5-8集女主离开成长，第9-12集男主追悔，第13-15集反转收束。爽点和虐点要交替出现。`
    case 'OPS':
      return `运营侧建议标题突出“误会、追悔、反转、追妻火葬场”。可用标题如《她离开后，霸总终于慌了》《误会她三年后，他跪求原谅》。标签建议：追妻火葬场、霸总、反转、强冲突、虐恋。`
    case 'INTJ':
      return `我会先拆解目标和优先级。这个问题可以分为内容定位、角色关系、冲突设计和执行计划四步，先确定核心受众，再确定前三集的强冲突。`
    case 'INFP':
      return `我会更关注角色的情绪合理性。这个故事要让女主的委屈被观众理解，也要让男主的追悔有足够铺垫，不能只靠强行反转。`
    case 'ENFP':
      return `这个题材有很强的情绪爆发空间！可以加入一个特别抓人的反转点，比如女主离开后事业逆袭，男主再想靠近时已经失去资格。`
    case 'ISTJ':
      return `建议按步骤推进：先写人物设定，再写核心误会，再拆15集大纲，最后补标题和标签。每一步都要有明确产出。`
    default:
      return `作为 ${agent.name}，我会围绕这个议题给出一个补充视角，帮助完善整体方案。`
  }
}

function buildMockSummary(topic: string, agents: Agent[]) {
  const names = agents.map(agent => agent.name).join('、')
  return `综合 ${names} 的意见，本方案应围绕“任务拆解、角色分工、强冲突开场、情绪节奏和运营标签”展开。用户输入的需求可以先由剧情策划 Agent 定结构，再由对白 Agent 补充角色台词，情绪节奏 Agent 调整爽点和反转，最后由运营分析 Agent 输出标题、标签和受众卖点。`
}

const RoundtableResult = defineComponent({
  name: 'RoundtableResult',
  props: {
    messages: {
      type: Array,
      required: true
    },
    summary: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    return () =>
      h('div', { class: 'result-area' }, [
        h('h3', 'Agent 发言结果'),
        ...(props.messages as any[]).map((message, index) =>
          h(
            'div',
            {
              class: message.role === 'user' ? 'message-card user' : 'message-card agent',
              key: index
            },
            [
              h('div', { class: 'message-name' }, message.role === 'user' ? '用户需求' : message.agentName),
              h('p', message.content)
            ]
          )
        ),
        props.summary
          ? h('div', { class: 'summary-card' }, [
              h('strong', '综合总结'),
              h('p', props.summary)
            ])
          : null
      ])
  }
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 32px 32px 120px;
  color: #2d2d2d;
}

.hero {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: flex-start;
  max-width: 1400px;
  margin: 0 auto 28px;
  padding: 34px;
  border-radius: 36px;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 18px 50px rgba(80, 65, 45, 0.08);
}

.eyebrow {
  margin: 0 0 8px;
  color: #a59a8f;
  font-size: 14px;
  letter-spacing: 0.03em;
}

h1,
h2,
h3 {
  margin: 0;
  color: #2e2e2e;
}

h1 {
  font-size: 38px;
  line-height: 1.2;
}

h2 {
  font-size: 26px;
}

.desc {
  max-width: 780px;
  margin: 16px 0 0;
  color: #8e887f;
  font-size: 17px;
  line-height: 1.8;
}

.hero-actions,
.actions-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.primary-btn,
.ghost-btn,
.danger-btn,
.new-btn {
  border: none;
  cursor: pointer;
  border-radius: 999px;
  padding: 13px 22px;
  font-size: 15px;
  transition: all 0.2s ease;
}

.primary-btn {
  background: #c6aa82;
  color: white;
  font-weight: 700;
}

.primary-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.ghost-btn {
  background: #f4eee5;
  color: #765f45;
}

.danger-btn {
  background: #f5dede;
  color: #9b5048;
}

.new-btn {
  width: 100%;
  margin: 18px 0;
  background: #eadcc6;
  color: #6e5741;
  font-weight: 700;
}

.home-grid {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.feature-card {
  border: none;
  text-align: left;
  cursor: pointer;
  padding: 28px;
  min-height: 220px;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 18px 44px rgba(80, 65, 45, 0.08);
}

.feature-card:hover {
  transform: translateY(-4px);
}

.feature-icon {
  display: inline-flex;
  width: 54px;
  height: 54px;
  align-items: center;
  justify-content: center;
  border-radius: 18px;
  background: #f4eee5;
  font-size: 28px;
}

.feature-card h3 {
  margin-top: 18px;
  font-size: 21px;
}

.feature-card p {
  margin: 12px 0 0;
  color: #8e887f;
  line-height: 1.7;
}

.workspace {
  max-width: 1500px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 24px;
  align-items: start;
}

.side-panel,
.main-panel,
.single-panel {
  border-radius: 34px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 18px 50px rgba(80, 65, 45, 0.08);
}

.side-panel {
  padding: 28px;
  min-height: 640px;
}

.main-panel,
.single-panel {
  padding: 32px;
}

.single-panel {
  max-width: 1200px;
  margin: 0 auto;
}

.side-head p {
  color: #8e887f;
  line-height: 1.7;
}

.agent-mini-list {
  display: grid;
  gap: 12px;
  margin-top: 18px;
}

.mini-agent {
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 20px;
  background: #f8f3eb;
}

.mini-agent span {
  font-size: 22px;
}

.mini-agent p {
  margin: 5px 0 0;
  color: #92887c;
  font-size: 13px;
  line-height: 1.5;
}

.session-list {
  display: grid;
  gap: 12px;
}

.session-card {
  border: 1px solid transparent;
  padding: 16px;
  border-radius: 22px;
  text-align: left;
  background: #f8f3eb;
  color: #3b332b;
  cursor: pointer;
}

.session-card.active {
  border-color: #c6aa82;
  background: #eadcc6;
}

.session-card span {
  display: block;
  margin-top: 6px;
  color: #91877c;
  font-size: 12px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: flex-start;
  margin-bottom: 22px;
}

.panel-head p {
  color: #8e887f;
  line-height: 1.7;
}

.count-pill {
  white-space: nowrap;
  border-radius: 999px;
  padding: 10px 16px;
  background: #f4eee5;
  color: #765f45;
  font-weight: 700;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 14px;
}

label,
.block-label {
  display: grid;
  gap: 8px;
  color: #7e7368;
  font-size: 14px;
  font-weight: 700;
}

input,
textarea {
  width: 100%;
  border: 1px solid #eee4d7;
  outline: none;
  border-radius: 20px;
  padding: 14px 16px;
  background: #fbf8f3;
  color: #2d2d2d;
  font-size: 15px;
  resize: vertical;
}

input:focus,
textarea:focus {
  border-color: #c6aa82;
  background: white;
}

.agent-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.agent-card {
  border: 2px solid transparent;
  border-radius: 24px;
  padding: 22px 16px;
  text-align: center;
  cursor: pointer;
  background: #fbf8f3;
}

.agent-card.selected {
  border-color: #a7c4b5;
  background: #eef8f1;
}

.agent-card span {
  display: block;
  font-size: 28px;
  margin-bottom: 10px;
}

.agent-card p {
  margin: 8px 0 0;
  color: #948a80;
  font-size: 13px;
  line-height: 1.5;
}

.tool-card,
.tool-result,
.answer-card,
.summary-card,
.context-card,
.memory-card {
  border-radius: 24px;
  background: #fbf8f3;
}

.tool-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-top: 16px;
  padding: 18px;
}

.tool-title {
  margin: 0;
  font-weight: 800;
  color: #4b4036;
}

.tool-desc {
  margin: 6px 0 0;
  color: #91877c;
}

.tool-result {
  margin-top: 12px;
  padding: 18px;
  color: #4b4036;
  line-height: 1.7;
}

.actions-row {
  margin: 20px 0;
}

.result-area {
  display: grid;
  gap: 14px;
  margin-top: 20px;
}

.message-card {
  padding: 18px;
  border-radius: 24px;
  line-height: 1.8;
}

.message-card.user {
  background: #eadcc6;
}

.message-card.agent {
  background: #fbf8f3;
}

.message-name {
  margin-bottom: 8px;
  color: #8e7860;
  font-weight: 800;
}

.message-card p,
.summary-card p,
.answer-card p,
.context-card p,
.memory-card p {
  margin: 0;
  white-space: pre-wrap;
  line-height: 1.8;
}

.summary-card {
  padding: 20px;
  border: 1px solid #eadcc6;
  background: #fffaf2;
}

.answer-card,
.context-card {
  padding: 20px;
  margin-top: 14px;
}

.contexts {
  margin-top: 20px;
}

.memory-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.memory-card {
  padding: 22px;
}

.memory-card span {
  display: block;
  margin-bottom: 10px;
  color: #9b8d7d;
  font-weight: 700;
}

@media (max-width: 1100px) {
  .home-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .workspace {
    grid-template-columns: 1fr;
  }

  .side-panel {
    min-height: auto;
  }

  .agent-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .page {
    padding: 20px 14px 120px;
  }

  .hero,
  .panel-head,
  .tool-card {
    flex-direction: column;
  }

  .home-grid,
  .form-grid,
  .memory-grid {
    grid-template-columns: 1fr;
  }

  h1 {
    font-size: 30px;
  }
}
</style>