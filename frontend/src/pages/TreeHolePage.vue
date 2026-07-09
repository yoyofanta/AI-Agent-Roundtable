<template>
  <main class="tree-page">
    <section class="hero-card">
      <div>
        <p class="eyebrow">AI Agent Roundtable</p>
        <h1>AI Agent 多角色圆桌与剧本生成系统</h1>
        <p class="hero-desc">
          支持直接生成剧本方案，也可以先让多个 Agent 进行创意圆桌讨论，
          再将总结带入剧本生成流程。
        </p>
      </div>

      <div class="hero-actions">
        <button
          class="ghost-btn"
          :class="{ active: view === 'content' }"
          @click="goView('content')"
        >
          内容生产
        </button>
        <button
          class="primary-btn"
          @click="openAgentAssist"
        >
          多 Agent 协助
        </button>
      </div>
    </section>

    <!-- 内容生产主页面 -->
    <section v-if="view === 'content'" class="page-grid">
      <aside class="side-panel">
        <p class="eyebrow">Content Mode</p>
        <h2>生成方式</h2>
        <p>
          内容生产提供两种路径：可以直接生成，也可以先进行多 Agent 圆桌讨论，
          再把总结带入剧本生成。
        </p>

        <button
          class="side-btn"
          :class="{ active: contentMode === 'choice' }"
          @click="contentMode = 'choice'"
        >
          选择生成方式
        </button>

        <button
          class="side-btn"
          :class="{ active: contentMode === 'direct' }"
          @click="openDirectContent"
        >
          直接生成方案
        </button>

        <button
          class="side-btn"
          @click="openAgentAssist"
        >
          多 Agent 协助生成
        </button>

        <div v-if="roundtableSummaryToContent" class="mini-summary">
          <strong>已带入圆桌总结</strong>
          <p>{{ shortText(roundtableSummaryToContent, 90) }}</p>
        </div>
      </aside>

      <section class="main-panel">
        <!-- 内容页：只显示两个入口 -->
        <div v-if="contentMode === 'choice'" class="content-choice">
          <div class="panel-head">
            <div>
              <p class="eyebrow">Content Generation</p>
              <h2>选择剧本生成方式</h2>
              <p>
                你可以直接输入需求生成剧本方案，也可以先让多个 Agent 进行圆桌讨论，
                总结创作方向后再生成最终内容方案。
              </p>
            </div>
          </div>

          <div class="choice-grid">
            <button class="choice-card" @click="openAgentAssist">
              <span>🤖</span>
              <strong>多 Agent 协助生成</strong>
              <p>
                先选择不同 Agent 进行圆桌讨论，由不同人格、角色或内容生产视角分别发言，
                生成总结后再带入剧本方案生成。
              </p>
            </button>

            <button class="choice-card" @click="openDirectContent">
              <span>🎬</span>
              <strong>直接生成方案</strong>
              <p>
                直接输入题材、平台、受众、风格和具体需求，系统结合内容分析工具与知识库生成剧本方案。
              </p>
            </button>
          </div>
        </div>

        <!-- 内容页：直接生成方案 -->
        <div v-else class="content-generator">
          <div class="panel-head">
            <div>
              <p class="eyebrow">Script Generation</p>
              <h2>剧本方案生成</h2>
              <p>
                输入内容需求，系统会结合 RAG 知识库、Tool Calling 内容分析工具和可选的圆桌总结生成最终方案。
              </p>
            </div>

            <button class="ghost-btn" @click="backToContentChoice">
              返回生成方式
            </button>
          </div>

          <div v-if="roundtableSummaryToContent" class="summary-import-card">
            <div class="summary-head">
              <strong>已带入多 Agent 圆桌总结</strong>
              <button @click="clearImportedSummary">清空</button>
            </div>

            <textarea
              v-model="roundtableSummaryToContent"
              rows="6"
              placeholder="这里会自动填入圆桌总结，也可以手动修改。"
            />
          </div>

          <div class="form-grid">
            <label>
              题材类型
              <input
                v-model="contentForm.type"
                placeholder="例如：追妻火葬场 / 复仇逆袭 / 先婚后爱"
              />
            </label>

            <label>
              平台场景
              <input
                v-model="contentForm.platform"
                placeholder="例如：短视频漫剧 / 小程序短剧 / 抖音短剧"
              />
            </label>

            <label>
              目标受众
              <input
                v-model="contentForm.audience"
                placeholder="例如：18-25岁女性 / 情感爽文用户"
              />
            </label>

            <label>
              内容风格
              <input
                v-model="contentForm.style"
                placeholder="例如：强冲突、快节奏、有反转、情绪拉满"
              />
            </label>
          </div>

          <label class="block-label">
            具体需求
            <textarea
              v-model="contentForm.requirement"
              rows="6"
              placeholder="例如：情绪拉满"
              />
            </label>
          </div>

          <div v-if="contentMode === 'direct'" class="actions-row">
            <button
              class="ghost-btn"
              :disabled="loading"
              @click="analyzeContent"
            >
              {{ loading ? '分析中...' : '先分析内容' }}
            </button>

            <button
              class="primary-btn"
              :disabled="loading"
              @click="generateScriptPlan"
            >
              {{ loading ? '生成中...' : roundtableSummaryToContent ? '基于圆桌总结生成最终方案' : '直接生成剧本方案' }}
            </button>
          </div>

          <div v-if="toolResult" class="tool-card">
  <p class="eyebrow">Tool Calling Result</p>
  <h3>内容分析结果</h3>

  <div class="tool-grid">
    <div>
      <strong>冲突强度</strong>
      <p>{{ toolResult.conflictLevel || '未识别' }}</p>
    </div>

    <div>
      <strong>情绪基调</strong>
      <p>{{ toolResult.emotionTone || '未识别' }}</p>
    </div>

    <div>
      <strong>推荐标签</strong>
      <p>{{ formatTags(toolResult.tags) }}</p>
    </div>
  </div>

  <p class="suggestion">
    {{ toolResult.suggestion || '暂无优化建议。' }}
  </p>
</div>

<div v-if="scriptResult" class="result-card">
  <p class="eyebrow">Final Script Plan</p>
  <h3>最终剧本方案</h3>
  <pre>{{ scriptResult }}</pre>
</div>
      </section>
    </section>

    <!-- 多 Agent 圆桌页面 -->
    <section v-if="view === 'roundtable'" class="page-grid">
      <aside class="side-panel">
        <p class="eyebrow">Sessions</p>
        <h2>历史圆桌</h2>
        <p>本地保存当前账号的圆桌记录，方便面试时展示多轮会话管理。</p>

        <button class="side-btn active" @click="newRoundtable">
          + 新建圆桌
        </button>

        <div class="session-list">
          <button
            v-for="session in sessions"
            :key="session.id"
            class="session-card"
            :class="{ active: activeSession?.id === session.id }"
            @click="selectSession(session.id)"
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
            <h2>多 Agent 协助生成</h2>
            <p>
              选择 2-4 个 Agent，围绕同一内容需求进行创意讨论。
              讨论完成后可生成总结，并一键带入剧本生成页面。
            </p>
          </div>

          <div class="count-pill">
            已选 {{ selectedAgentCodes.length }} / 4
          </div>
        </div>

        <div class="roundtable-tools">
          <button class="ghost-btn" @click="showCustomEditor = !showCustomEditor">
            {{ showCustomEditor ? '收起自定义角色' : '+ 新建人格 / 角色' }}
          </button>

          <span class="tool-hint">
            默认提供 16 型人格，可设置男 / 女 / 不限；也可以创建自定义 Agent。
          </span>
        </div>

        <div v-if="showCustomEditor" class="custom-editor">
          <div class="form-grid">
            <label>
              角色名称
              <input
                v-model="customForm.name"
                placeholder="例如：冷静女编剧 / 毒舌运营 / 温柔男主顾问"
              />
            </label>

            <label>
              参考人格
              <select v-model="customForm.baseCode">
                <option
                  v-for="agent in mbtiAgents"
                  :key="agent.code"
                  :value="agent.code"
                >
                  {{ agent.code }} {{ agent.name }}
                </option>
              </select>
            </label>

            <label>
              性别设定
              <select v-model="customForm.gender">
                <option value="不限">不限</option>
                <option value="男">男</option>
                <option value="女">女</option>
              </select>
            </label>

            <label>
              头像符号
              <input v-model="customForm.icon" placeholder="例如：✨ / 🎭 / 🧠" />
            </label>
          </div>

          <label class="block-label">
            角色设定
            <textarea
              v-model="customForm.role"
              rows="4"
              placeholder="例如：你是一位擅长短剧冲突设计的女性编剧，说话直接，喜欢从人物动机和反转节奏出发提出建议。"
            />
          </label>

          <div class="actions-row">
            <button class="primary-btn" @click="saveCustomAgent">
              保存并加入圆桌
            </button>
            <button class="ghost-btn" @click="showCustomEditor = false">
              取消
            </button>
          </div>
        </div>

        <div class="agent-grid">
  <div
    v-for="agent in allAgents"
    :key="agent.code"
    class="agent-card"
    :class="{ selected: selectedAgentCodes.includes(agent.code) }"
    @click="toggleAgent(agent.code)"
  >
    <span>{{ agent.icon }}</span>

    <strong>
      {{ agent.source === 'custom' ? agent.name : agent.code + ' ' + agent.name }}
    </strong>

    <p>{{ agent.role }}</p>

    <div
      v-if="agent.source !== 'custom'"
      class="gender-row"
      @click.stop
    >
      <button
        type="button"
        :class="{ active: getAgentGender(agent.code) === '不限' }"
        @click="setAgentGender(agent.code, '不限')"
      >
        不限
      </button>

      <button
        type="button"
        :class="{ active: getAgentGender(agent.code) === '男' }"
        @click="setAgentGender(agent.code, '男')"
      >
        男
      </button>

      <button
        type="button"
        :class="{ active: getAgentGender(agent.code) === '女' }"
        @click="setAgentGender(agent.code, '女')"
      >
        女
      </button>
    </div>

    <div
      v-else
      class="custom-actions"
      @click.stop
    >
      <span class="custom-pill">自定义</span>

      <button
        type="button"
        @click="deleteCustomAgent(agent.code)"
      >
        删除
      </button>
    </div>
  </div>
</div>

        <label class="block-label">
          圆桌议题
          <textarea
            v-model="roundtableTopic"
            rows="5"
            placeholder="例如：我想做一个追妻火葬场题材的漫剧，女主被误会后离开，男主后期追悔莫及。请从剧情结构、人物情绪、创意反转和落地执行角度帮我讨论。"
          />
        </label>

        <div class="actions-row">
          <button class="ghost-btn" @click="goView('content')">
            返回内容页
          </button>

          <button
            class="primary-btn"
            :disabled="loading"
            @click="runRoundtable"
          >
            {{ loading ? '圆桌讨论中...' : '开始圆桌讨论' }}
          </button>
        </div>

        <div v-if="activeSession?.messages.length" class="chat-panel">
          <div
            v-for="message in activeSession.messages"
            :key="message.id"
            class="message-row"
          >
            <div class="avatar">{{ message.icon }}</div>
            <div class="bubble">
              <div class="bubble-head">
                <strong>{{ message.name }}</strong>
                <span>{{ message.role }}</span>
              </div>
              <p>{{ message.content }}</p>
            </div>
          </div>
        </div>

        <div v-if="activeSession?.summary" class="summary-card">
          <p class="eyebrow">Roundtable Summary</p>
          <h3>圆桌总结</h3>
          <p>{{ activeSession.summary }}</p>

          <div class="actions-row">
            <button class="primary-btn" @click="applyRoundtableToContent">
              带入剧本生成
            </button>
          </div>
        </div>
      </section>
    </section>

    <!-- 知识库页面 -->
    <section v-if="view === 'rag'" class="single-panel">
      <div class="panel-head">
        <div>
          <p class="eyebrow">RAG Knowledge Base</p>
          <h2>Agent 内容知识库</h2>
          <p>
            用于给多 Agent 圆桌和剧本生成提供知识增强，覆盖漫剧创作、强冲突开场、
            追妻火葬场结构、角色对白、情绪节奏、标题标签和运营分析等内容生产知识。
          </p>
        </div>
      </div>

      <div class="rag-tips">
        <span>可测试问题：</span>
        <button @click="ragQuery = '追妻火葬场题材怎么设计强冲突开场？'">
          强冲突开场
        </button>
        <button @click="ragQuery = '短视频漫剧标题怎么写更吸引人？'">
          标题优化
        </button>
        <button @click="ragQuery = '误会反转剧情应该怎么设计伏笔？'">
          误会反转
        </button>
        <button @click="ragQuery = '漫剧分集节奏怎么安排更容易留住用户？'">
          分集节奏
        </button>
      </div>

      <label class="block-label">
        输入你的内容问题
        <textarea
          v-model="ragQuery"
          rows="5"
          placeholder="例如：追妻火葬场题材怎么设计强冲突开场？"
        />
      </label>

      <div class="actions-row">
        <button
          class="primary-btn"
          :disabled="loading"
          @click="askRag"
        >
          {{ loading ? '知识检索中...' : 'RAG 生成回答' }}
        </button>
      </div>

      <div v-if="ragAnswer" class="answer-card">
        <h3>知识增强回答</h3>
        <p>{{ ragAnswer }}</p>

        <div class="actions-row">
          <button class="ghost-btn" @click="archiveCurrentRag">
            存档当前问答
          </button>
        </div>
      </div>

      <div v-if="ragContexts.length" class="contexts">
        <h3>命中的内容知识片段</h3>

        <div
          v-for="(ctx, index) in ragContexts"
          :key="index"
          class="context-card"
        >
          <strong>{{ getContextTitle(ctx, index) }}</strong>
          <p>{{ getContextContent(ctx) }}</p>
        </div>
      </div>

      <div v-if="!ragAnswer && !ragContexts.length" class="empty-rag">
        输入内容问题后，系统会从 RAG 知识库中检索相关片段，并生成增强回答。
      </div>

      <div v-if="ragArchives.length" class="archive-panel">
        <h3>知识库问答存档</h3>

        <div
          v-for="item in ragArchives"
          :key="item.id"
          class="archive-card"
        >
          <div class="archive-head">
            <strong>{{ item.question }}</strong>
            <button @click="deleteRagArchive(item.id)">
              删除
            </button>
          </div>

          <p class="archive-time">{{ item.createdAt }}</p>
          <p>{{ item.answer }}</p>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import axios from 'axios'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

type PageView = 'content' | 'roundtable' | 'rag'
type ContentMode = 'choice' | 'direct'
type AgentGender = '不限' | '男' | '女'
type AgentSource = 'preset' | 'custom'

type Agent = {
  code: string
  name: string
  icon: string
  role: string
  gender?: AgentGender
  source?: AgentSource
}

type RoundtableMessage = {
  id: number
  agentCode: string
  name: string
  role: string
  icon: string
  content: string
}

type RoundtableSession = {
  id: number
  title: string
  topic: string
  agents: Agent[]
  messages: RoundtableMessage[]
  summary: string
  createdAt: string
}

type RagArchive = {
  id: number
  question: string
  answer: string
  contexts: unknown[]
  createdAt: string
}

type ToolResult = {
  conflictLevel?: string
  emotionTone?: string
  tags?: string[] | string
  suggestion?: string
}

const route = useRoute()
const router = useRouter()

const http = axios.create({
  baseURL: '/api',
  timeout: 120000
})

http.interceptors.request.use(config => {
  const token = localStorage.getItem('token')

  if (token) {
    config.headers.Authorization = token.startsWith('Bearer ')
      ? token
      : `Bearer ${token}`
  }

  return config
})

const loading = ref(false)
const view = ref<PageView>(resolveView(route.query.view))
const contentMode = ref<ContentMode>('choice')

const roundtableSummaryToContent = ref('')
const fromRoundtable = ref(false)

const contentForm = reactive({
  type: '追妻火葬场',
  platform: '短视频漫剧',
  audience: '18-25岁女性',
  style: '强冲突、快节奏、有反转、情绪拉满',
  requirement: '女主被男主误会后离开，男主后来发现真相追悔莫及，希望设计15集漫剧方案。'
})

const toolResult = ref<ToolResult | null>(null)
const scriptResult = ref('')

const roundtableTopic = ref(
  '我想做一个追妻火葬场题材的漫剧，女主被误会后离开，男主后期追悔莫及。请从剧情结构、人物情绪、创意反转和落地执行角度帮我讨论。'
)

const selectedAgentCodes = ref<string[]>(['INTJ', 'INFP', 'ENTP', 'ESTJ'])
const sessions = ref<RoundtableSession[]>([])
const activeSessionId = ref<number | null>(null)
const showCustomEditor = ref(false)

const customForm = reactive({
  name: '',
  baseCode: 'INFJ',
  gender: '不限' as AgentGender,
  icon: '✨',
  role: ''
})

const agentGenderMap = reactive<Record<string, AgentGender>>({})

const ragQuery = ref('追妻火葬场题材怎么设计强冲突开场？')
const ragAnswer = ref('')
const ragContexts = ref<unknown[]>([])
const ragArchives = ref<RagArchive[]>([])

const mbtiAgents: Agent[] = [
  {
    code: 'INTJ',
    name: '冷静规划师',
    icon: '🧠',
    role: '擅长理性规划、结构拆解、长期策略和目标管理',
    source: 'preset'
  },
  {
    code: 'INTP',
    name: '逻辑研究员',
    icon: '🔍',
    role: '擅长逻辑推演、概念分析、发现问题本质',
    source: 'preset'
  },
  {
    code: 'ENTJ',
    name: '目标指挥官',
    icon: '🚀',
    role: '擅长目标拆解、决策推进、资源整合和结果导向',
    source: 'preset'
  },
  {
    code: 'ENTP',
    name: '灵感辩手',
    icon: '⚡',
    role: '擅长提出新角度、反向思考、创意发散和观点碰撞',
    source: 'preset'
  },
  {
    code: 'INFJ',
    name: '深度洞察者',
    icon: '🌿',
    role: '擅长洞察情绪、理解深层动机、关注长期意义',
    source: 'preset'
  },
  {
    code: 'INFP',
    name: '温柔共情者',
    icon: '🌙',
    role: '擅长共情、价值感表达、情绪接纳和人物内心分析',
    source: 'preset'
  },
  {
    code: 'ENFJ',
    name: '共情引导者',
    icon: '🫂',
    role: '擅长关系协调、情绪引导、鼓励表达和团队氛围建设',
    source: 'preset'
  },
  {
    code: 'ENFP',
    name: '行动鼓励者',
    icon: '☀️',
    role: '擅长激发灵感、提供鼓励、发现可能性和推动行动',
    source: 'preset'
  },
  {
    code: 'ISTJ',
    name: '稳妥执行者',
    icon: '📘',
    role: '擅长步骤规划、稳定执行、规则意识和细节检查',
    source: 'preset'
  },
  {
    code: 'ISFJ',
    name: '温和守护者',
    icon: '🕯️',
    role: '擅长照顾细节、稳定支持、关系维护和风险提醒',
    source: 'preset'
  },
  {
    code: 'ESTJ',
    name: '执行管理者',
    icon: '📋',
    role: '擅长流程管理、效率优化、任务分配和落地执行',
    source: 'preset'
  },
  {
    code: 'ESFJ',
    name: '氛围照顾者',
    icon: '🤝',
    role: '擅长关注关系、照顾感受、组织协作和反馈沟通',
    source: 'preset'
  },
  {
    code: 'ISTP',
    name: '冷静实干者',
    icon: '🛠️',
    role: '擅长现实分析、快速判断、解决具体问题和动手实践',
    source: 'preset'
  },
  {
    code: 'ISFP',
    name: '感受创作者',
    icon: '🎨',
    role: '擅长审美表达、情绪氛围、细腻感受和内容风格设计',
    source: 'preset'
  },
  {
    code: 'ESTP',
    name: '现实行动者',
    icon: '🏃',
    role: '擅长快速试错、现实判断、冲突处理和即时行动',
    source: 'preset'
  },
  {
    code: 'ESFP',
    name: '氛围带动者',
    icon: '🎭',
    role: '擅长情绪感染、表达张力、舞台感和用户吸引力',
    source: 'preset'
  }
]

const contentAgents: Agent[] = [
  {
    code: 'PLOT',
    name: '剧情策划 Agent',
    icon: '🧩',
    role: '负责故事结构、开场冲突、人物关系和反转设计',
    source: 'preset'
  },
  {
    code: 'DIALOGUE',
    name: '角色对白 Agent',
    icon: '💬',
    role: '负责角色对白、情绪台词和短剧化表达',
    source: 'preset'
  },
  {
    code: 'RHYTHM',
    name: '情绪节奏 Agent',
    icon: '🎢',
    role: '负责爽点、虐点、反转点和用户留存节奏',
    source: 'preset'
  },
  {
    code: 'OPS',
    name: '运营分析 Agent',
    icon: '📈',
    role: '负责标题、标签、受众卖点和平台传播建议',
    source: 'preset'
  }
]

const customAgents = ref<Agent[]>([])

const allAgents = computed(() => {
  return [...mbtiAgents, ...customAgents.value]
})

const selectedAgents = computed(() => {
  return allAgents.value
    .filter(agent => selectedAgentCodes.value.includes(agent.code))
    .map(agent => withGenderSetting(agent))
})

const activeSession = computed(() => {
  return sessions.value.find(item => item.id === activeSessionId.value) || null
})

watch(
  () => route.query.view,
  next => {
    view.value = resolveView(next)

    if (view.value === 'content' && !fromRoundtable.value) {
      contentMode.value = 'choice'
    }
  }
)

function resolveView(raw: unknown): PageView {
  if (raw === 'roundtable') {
    return 'roundtable'
  }

  if (raw === 'rag') {
    return 'rag'
  }

  return 'content'
}

function goView(next: PageView) {
  view.value = next

  if (next === 'content') {
    contentMode.value = 'choice'
  }

  router.replace({
    path: '/app/tree-hole',
    query: {
      view: next
    }
  })
}

function openDirectContent() {
  view.value = 'content'
  contentMode.value = 'direct'

  // 直接生成模式下，不默认带入圆桌总结
  roundtableSummaryToContent.value = ''
  fromRoundtable.value = false
  toolResult.value = null
  scriptResult.value = ''

  router.replace({
    path: '/app/tree-hole',
    query: {
      view: 'content'
    }
  })
}

function openAgentAssist() {
  view.value = 'roundtable'

  router.replace({
    path: '/app/tree-hole',
    query: {
      view: 'roundtable'
    }
  })

  if (!activeSession.value) {
    newRoundtable()
  }
}

function backToContentChoice() {
  view.value = 'content'
  contentMode.value = 'choice'
}

function clearImportedSummary() {
  roundtableSummaryToContent.value = ''
  fromRoundtable.value = false
}

function unwrapResponse(response: unknown): any {
  const res = response as any
  const body = res?.data ?? res

  if (body?.data !== undefined) {
    return body.data
  }

  return body
}

function getCurrentUserKey() {
  return localStorage.getItem('moodboard_current_user') || 'guest'
}

function getSessionStorageKey() {
  return `moodboard_agent_roundtable_sessions_${getCurrentUserKey()}`
}

function getCustomAgentStorageKey() {
  return `moodboard_custom_roundtable_agents_${getCurrentUserKey()}`
}

function getRagArchiveKey() {
  return `ai_agent_rag_archives_${getCurrentUserKey()}`
}

function loadSessions() {
  try {
    const raw = localStorage.getItem(getSessionStorageKey())
    sessions.value = raw ? JSON.parse(raw) : []

    if (sessions.value.length) {
      activeSessionId.value = sessions.value[0].id
    }
  } catch (error) {
    console.warn('读取圆桌历史失败', error)
    sessions.value = []
  }
}

function saveSessions() {
  localStorage.setItem(getSessionStorageKey(), JSON.stringify(sessions.value))
}

function newRoundtable() {
  const session: RoundtableSession = {
    id: Date.now(),
    title: '新的 Agent 圆桌',
    topic: '',
    agents: [],
    messages: [],
    summary: '',
    createdAt: new Date().toLocaleString()
  }

  sessions.value.unshift(session)
  activeSessionId.value = session.id
  selectedAgentCodes.value = ['INTJ', 'INFP', 'ENTP', 'ESTJ']
  roundtableTopic.value =
    '我想做一个追妻火葬场题材的漫剧，女主被误会后离开，男主后期追悔莫及。请从剧情结构、人物情绪、创意反转和落地执行角度帮我讨论。'

  saveSessions()
}

function selectSession(id: number) {
  activeSessionId.value = id

  const session = activeSession.value

  if (session) {
    roundtableTopic.value = session.topic || roundtableTopic.value
    selectedAgentCodes.value = session.agents.map(item => item.code).slice(0, 4)
  }
}

function withGenderSetting(agent: Agent): Agent {
  if (agent.source === 'custom') {
    return agent
  }

  const gender = agentGenderMap[agent.code] || agent.gender || '不限'
  const genderText = gender === '不限' ? '不限性别' : `${gender}性`

  return {
    ...agent,
    gender,
    name: `${agent.code} ${agent.name}`,
    role: `${agent.role}。角色性别设定：${genderText}。请在圆桌发言中保持该角色身份、语气和视角。`
  }
}

function setAgentGender(code: string, gender: AgentGender) {
  agentGenderMap[code] = gender
}

function getAgentGender(code: string) {
  return agentGenderMap[code] || '不限'
}

function toggleAgent(code: string) {
  if (selectedAgentCodes.value.includes(code)) {
    selectedAgentCodes.value = selectedAgentCodes.value.filter(item => item !== code)
    return
  }

  if (selectedAgentCodes.value.length >= 4) {
    alert('最多选择 4 个 Agent')
    return
  }

  selectedAgentCodes.value.push(code)
}

function loadCustomAgents() {
  try {
    const raw = localStorage.getItem(getCustomAgentStorageKey())
    customAgents.value = raw ? JSON.parse(raw) : []
  } catch (error) {
    console.warn('读取自定义角色失败', error)
    customAgents.value = []
  }
}

function saveCustomAgents() {
  localStorage.setItem(getCustomAgentStorageKey(), JSON.stringify(customAgents.value))
}

function saveCustomAgent() {
  if (!customForm.name.trim()) {
    alert('请输入角色名称')
    return
  }

  if (!customForm.role.trim()) {
    alert('请输入角色设定')
    return
  }

  const base = mbtiAgents.find(item => item.code === customForm.baseCode)

  const agent: Agent = {
    code: `CUSTOM_${Date.now()}`,
    name: customForm.name.trim(),
    icon: customForm.icon || '✨',
    gender: customForm.gender,
    source: 'custom',
    role:
      `这是一个用户自定义 Agent。` +
      `参考基础人格：${base ? base.code + ' ' + base.name : customForm.baseCode}。` +
      `性别设定：${customForm.gender}。` +
      `具体角色设定：${customForm.role.trim()}。` +
      `请严格按照该角色设定参与圆桌讨论。`
  }

  customAgents.value.unshift(agent)
  saveCustomAgents()

  if (!selectedAgentCodes.value.includes(agent.code)) {
    if (selectedAgentCodes.value.length >= 4) {
      selectedAgentCodes.value.shift()
    }

    selectedAgentCodes.value.push(agent.code)
  }

  customForm.name = ''
  customForm.role = ''
  customForm.gender = '不限'
  customForm.icon = '✨'
  showCustomEditor.value = false
}

function deleteCustomAgent(code: string) {
  customAgents.value = customAgents.value.filter(item => item.code !== code)
  selectedAgentCodes.value = selectedAgentCodes.value.filter(item => item !== code)
  saveCustomAgents()
}

async function runRoundtable() {
  if (selectedAgents.value.length < 2) {
    alert('请至少选择 2 个 Agent')
    return
  }

  if (selectedAgents.value.length > 4) {
    alert('最多选择 4 个 Agent')
    return
  }

  if (!roundtableTopic.value.trim()) {
    alert('请输入圆桌议题')
    return
  }

  let session = activeSession.value

  if (!session) {
    newRoundtable()
    session = activeSession.value
  }

  if (!session) {
    return
  }

  loading.value = true

  try {
    const payload = {
      topic: roundtableTopic.value,
      agents: selectedAgents.value.map(agent => ({
        code: agent.code,
        name: agent.name,
        role: agent.role
      }))
    }

    const response = await http.post('/ai/agent/roundtable', payload)
    const data = unwrapResponse(response)

    const replies = Array.isArray(data?.agents) ? data.agents : []

    const messages: RoundtableMessage[] = replies.map((item: any, index: number) => {
      const code = String(item.agentCode || item.code || selectedAgents.value[index]?.code || `AGENT_${index + 1}`)
      const originAgent = selectedAgents.value.find(agent => agent.code === code) || selectedAgents.value[index]

      return {
        id: Date.now() + index,
        agentCode: code,
        name: String(item.agentName || item.name || originAgent?.name || code),
        role: originAgent?.role || 'Agent 圆桌成员',
        icon: originAgent?.icon || '🤖',
        content: String(item.reply || item.answer || item.content || '该 Agent 暂未生成有效回复。')
      }
    })

    session.title = shortText(roundtableTopic.value, 24)
    session.topic = roundtableTopic.value
    session.agents = selectedAgents.value
    session.messages = messages
    session.summary = String(data?.summary || buildLocalSummary(messages))

    saveSessions()
  } catch (error: any) {
    console.error(error)
    alert(error?.response?.data?.message || '多 Agent 圆桌生成失败，请检查后端接口和 DeepSeek 配置。')
  } finally {
    loading.value = false
  }
}

function buildLocalSummary(messages: RoundtableMessage[]) {
  if (!messages.length) {
    return ''
  }

  return messages
    .map(item => `${item.name}：${shortText(item.content, 80)}`)
    .join('\n')
}

function applyRoundtableToContent() {
  const summary = activeSession.value?.summary || ''

  if (!summary.trim()) {
    alert('请先完成圆桌讨论并生成总结')
    return
  }

  roundtableSummaryToContent.value =
    '以下内容来自多 Agent 圆桌讨论总结，请基于该总结继续生成完整剧本方案：\n\n' +
    summary

  fromRoundtable.value = true

  toolResult.value = null
  scriptResult.value = ''

  view.value = 'content'
  contentMode.value = 'direct'

  router.replace({
    path: '/app/tree-hole',
    query: {
      view: 'content'
    }
  })
}

async function analyzeContent() {
  loading.value = true
  toolResult.value = null

  const text = buildContentRequirementText()

  try {
    const response = await http.post('/ai/tools/content/analyze', {
      type: contentForm.type,
      platform: contentForm.platform,
      audience: contentForm.audience,
      style: contentForm.style,
      text
    })

    const data = unwrapResponse(response)
    const normalized = normalizeToolResult(data, text)

    toolResult.value = normalized
  } catch (error) {
    console.warn('后端内容分析接口不可用，使用前端兜底分析', error)
    toolResult.value = buildLocalToolResult(text)
  } finally {
    loading.value = false
  }
}

function normalizeToolResult(data: any, text: string): ToolResult {
  if (!data || typeof data !== 'object') {
    return buildLocalToolResult(text)
  }

  const conflictLevel =
    data.conflictLevel ||
    data.conflict_level ||
    data.conflict ||
    data.level ||
    ''

  const emotionTone =
    data.emotionTone ||
    data.emotion_tone ||
    data.emotion ||
    data.tone ||
    ''

  const tags =
    data.tags ||
    data.tagList ||
    data.keywords ||
    []

  const suggestion =
    data.suggestion ||
    data.advice ||
    data.recommendation ||
    data.message ||
    ''

  const result: ToolResult = {
    conflictLevel: conflictLevel || buildLocalToolResult(text).conflictLevel,
    emotionTone: emotionTone || buildLocalToolResult(text).emotionTone,
    tags: Array.isArray(tags) && tags.length ? tags : buildLocalToolResult(text).tags,
    suggestion: suggestion || buildLocalToolResult(text).suggestion
  }

  return result
}

function buildLocalToolResult(text: string): ToolResult {
  const source = text || ''

  const highConflictWords = [
    '误会',
    '离开',
    '追悔',
    '背叛',
    '复仇',
    '火葬场',
    '真相',
    '反转',
    '霸总',
    '白月光',
    '替身',
    '离婚',
    '重生',
    '逆袭'
  ]

  const hitCount = highConflictWords.filter(word => source.includes(word)).length

  let conflictLevel = '中'
  if (hitCount >= 4) {
    conflictLevel = '高'
  } else if (hitCount <= 1) {
    conflictLevel = '低'
  }

  let emotionTone = '情感 / 反转'

  if (source.includes('追妻') || source.includes('火葬场') || source.includes('追悔')) {
    emotionTone = '虐恋 / 追悔 / 反转'
  }

  if (source.includes('复仇') || source.includes('逆袭')) {
    emotionTone = '复仇 / 逆袭 / 爽感'
  }

  const tags: string[] = []

  if (source.includes('追妻') || source.includes('火葬场')) {
    tags.push('追妻火葬场')
  }

  if (source.includes('误会')) {
    tags.push('误会')
  }

  if (source.includes('反转') || source.includes('真相')) {
    tags.push('反转')
  }

  if (source.includes('霸总')) {
    tags.push('霸总')
  }

  if (source.includes('离开') || source.includes('离婚')) {
    tags.push('离开')
  }

  if (source.includes('女主')) {
    tags.push('女主成长')
  }

  if (!tags.length) {
    tags.push('强冲突', '情绪拉扯', '短剧化表达')
  }

  return {
    conflictLevel,
    emotionTone,
    tags,
    suggestion:
      '建议前 3 秒直接抛出核心误会或关键矛盾，例如女主离开、男主误判、真相被隐藏等情节；中段强化人物关系拉扯，后段集中释放反转和追悔情绪。'
  }
}

async function generateScriptPlan() {
  if (!contentForm.requirement.trim() && !roundtableSummaryToContent.value.trim()) {
    alert('请输入具体需求，或先通过多 Agent 圆桌带入总结')
    return
  }

  loading.value = true
  scriptResult.value = ''

  try {
    if (!toolResult.value) {
      await analyzeContent()
    }

    const topic = buildFinalScriptPrompt()

    const response = await http.post('/ai/agent/roundtable', {
      topic,
      agents: contentAgents.map(agent => ({
        code: agent.code,
        name: agent.name,
        role: agent.role
      }))
    })

    const data = unwrapResponse(response)
    const replies = Array.isArray(data?.agents) ? data.agents : []

    scriptResult.value = formatScriptResult(replies, data?.summary)
  } catch (error: any) {
    console.error(error)
    alert(error?.response?.data?.message || '剧本方案生成失败，请检查后端接口。')
  } finally {
    loading.value = false
  }
}

function buildContentRequirementText() {
  return [
    `题材类型：${contentForm.type}`,
    `平台场景：${contentForm.platform}`,
    `目标受众：${contentForm.audience}`,
    `内容风格：${contentForm.style}`,
    `具体需求：${contentForm.requirement}`,
    roundtableSummaryToContent.value ? `多 Agent 圆桌总结：${roundtableSummaryToContent.value}` : ''
  ]
    .filter(Boolean)
    .join('\n')
}

function buildFinalScriptPrompt() {
  const hasRoundtable = roundtableSummaryToContent.value.trim().length > 0

  const toolText = toolResult.value
    ? `
内容分析工具结果：
冲突强度：${toolResult.value.conflictLevel || '未识别'}
情绪基调：${toolResult.value.emotionTone || '未识别'}
推荐标签：${formatTags(toolResult.value.tags)}
优化建议：${toolResult.value.suggestion || '暂无'}
`
    : ''

  if (!hasRoundtable) {
    return `
你现在是一个短视频漫剧 / 短剧内容策划 Agent。
请基于用户输入，快速生成一份结构清晰、可执行的剧本方案。

基础信息：
题材类型：${contentForm.type}
平台场景：${contentForm.platform}
目标受众：${contentForm.audience}
内容风格：${contentForm.style}
具体需求：${contentForm.requirement}

${toolText}

请直接生成最终剧本方案，包含：
1. 故事简介
2. 人物设定
3. 人物关系
4. 核心冲突
5. 三幕式结构
6. 15 集左右分集大纲
7. 关键对白示例
8. 情绪爆点
9. 标题和标签建议
10. 运营建议

要求：
- 不要写圆桌讨论过程；
- 不要分析每个 Agent 的观点；
- 直接给出完整方案；
- 语言要像可以交付给内容团队的策划案。
`
  }

  return `
你现在是一个多 Agent 圆桌后的剧本统筹 Agent。
下面的内容不是普通用户需求，而是已经经过多个 Agent 圆桌讨论后的创作总结。
请你必须基于圆桌总结进行整合，不要当作普通单次生成任务处理。

基础信息：
题材类型：${contentForm.type}
平台场景：${contentForm.platform}
目标受众：${contentForm.audience}
内容风格：${contentForm.style}
具体需求：${contentForm.requirement}

${toolText}

多 Agent 圆桌总结：
${roundtableSummaryToContent.value}

请生成“基于多 Agent 圆桌讨论后的最终剧本方案”，必须包含：

一、圆桌讨论结论
1. 本次圆桌形成的核心共识
2. 不同 Agent 分别提供了什么有效观点
3. 哪些观点被采纳进最终方案
4. 哪些风险点需要避免

二、最终剧本方案
1. 故事简介
2. 人物设定
3. 人物关系
4. 核心冲突
5. 三幕式结构
6. 15 集左右分集大纲
7. 关键对白示例
8. 情绪爆点
9. 标题和标签建议
10. 运营建议

三、圆桌观点如何影响最终方案
请明确说明：
- 哪些设计来自剧情策划视角；
- 哪些设计来自角色对白视角；
- 哪些设计来自情绪节奏视角；
- 哪些设计来自运营分析视角；
- 如果圆桌中包含自定义人格或 MBTI 人格，也要说明其观点如何影响人物性格和剧情走向。

要求：
- 必须体现“先圆桌讨论，再生成方案”的区别；
- 不要只给普通剧本大纲；
- 要把圆桌讨论转化成可执行的内容方案；
- 语言要像内容团队内部创意会后的正式策划稿。
`
}

function formatScriptResult(replies: any[], summary: unknown) {
  const parts: string[] = []

  if (replies.length) {
    replies.forEach((item, index) => {
      const name = item.agentName || item.name || `Agent ${index + 1}`
      const reply = item.reply || item.answer || item.content || ''

      parts.push(`【${name}】\n${reply}`)
    })
  }

  if (summary) {
    parts.push(`【综合方案总结】\n${summary}`)
  }

  return parts.join('\n\n') || '暂未生成有效内容。'
}

function formatTags(tags: unknown) {
  if (Array.isArray(tags)) {
    return tags.join('、')
  }

  return String(tags || '暂无')
}

async function askRag() {
  if (!ragQuery.value.trim()) {
    alert('请输入知识库问题')
    return
  }

  loading.value = true
  ragAnswer.value = ''
  ragContexts.value = []

  try {
    const response = await http.get('/knowledge/ask', {
      params: {
        q: ragQuery.value,
        topK: 5
      }
    })

    const data = unwrapResponse(response)

    ragAnswer.value = String(data?.answer || data?.reply || data?.content || data || '')
    ragContexts.value = Array.isArray(data?.contexts)
      ? data.contexts
      : Array.isArray(data?.chunks)
        ? data.chunks
        : Array.isArray(data?.results)
          ? data.results
          : []
  } catch (error: any) {
    console.error(error)
    alert(error?.response?.data?.message || 'RAG 生成回答失败，请检查知识库接口。')
  } finally {
    loading.value = false
  }
}

function loadRagArchives() {
  try {
    const raw = localStorage.getItem(getRagArchiveKey())
    ragArchives.value = raw ? JSON.parse(raw) : []
  } catch (error) {
    console.warn('读取 RAG 存档失败', error)
    ragArchives.value = []
  }
}

function saveRagArchives() {
  localStorage.setItem(getRagArchiveKey(), JSON.stringify(ragArchives.value))
}

function archiveCurrentRag() {
  if (!ragQuery.value || !ragAnswer.value) {
    alert('请先生成 RAG 回答')
    return
  }

  ragArchives.value.unshift({
    id: Date.now(),
    question: ragQuery.value,
    answer: ragAnswer.value,
    contexts: ragContexts.value || [],
    createdAt: new Date().toLocaleString()
  })

  saveRagArchives()
  alert('已存档当前问答')
}

function deleteRagArchive(id: number) {
  ragArchives.value = ragArchives.value.filter(item => item.id !== id)
  saveRagArchives()
}

function getContextTitle(ctx: unknown, index: number) {
  if (ctx && typeof ctx === 'object' && 'title' in ctx) {
    return String((ctx as any).title || `知识片段 ${index + 1}`)
  }

  return `知识片段 ${index + 1}`
}

function getContextContent(ctx: unknown) {
  if (ctx && typeof ctx === 'object') {
    const item = ctx as any
    return String(item.content || item.text || item.chunk || JSON.stringify(item))
  }

  return String(ctx || '')
}

function shortText(text: string, length: number) {
  if (!text) {
    return ''
  }

  const normalized = text.replace(/\s+/g, ' ').trim()

  if (normalized.length <= length) {
    return normalized
  }

  return normalized.slice(0, length) + '...'
}

onMounted(() => {
  loadSessions()
  loadCustomAgents()
  loadRagArchives()
})
</script>

<style scoped>
.tree-page {
  min-height: 100vh;
  padding: 32px 32px 120px;
  color: #2d2d2d;
}

.hero-card,
.side-panel,
.main-panel,
.single-panel {
  border-radius: 34px;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 18px 50px rgba(80, 65, 45, 0.08);
}

.hero-card {
  max-width: 1500px;
  margin: 0 auto 28px;
  padding: 38px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 28px;
}

.hero-desc {
  max-width: 860px;
  color: #8e887f;
  font-size: 17px;
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.page-grid {
  max-width: 1500px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 340px minmax(0, 1fr);
  gap: 24px;
}

.side-panel {
  padding: 28px;
  align-self: start;
  position: sticky;
  top: 24px;
}

.side-panel p {
  color: #8e887f;
  line-height: 1.7;
}

.main-panel,
.single-panel {
  padding: 34px;
}

.single-panel {
  max-width: 1500px;
  margin: 0 auto;
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
  color: #2d2d2d;
}

h1 {
  font-size: 42px;
}

h2 {
  font-size: 30px;
}

h3 {
  font-size: 22px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 24px;
}

.panel-head p {
  margin: 10px 0 0;
  color: #8e887f;
  line-height: 1.8;
}

.primary-btn,
.ghost-btn,
.side-btn {
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

.ghost-btn {
  background: #f4eee5;
  color: #765f45;
}

.ghost-btn.active {
  background: #c6aa82;
  color: white;
}

.primary-btn:disabled,
.ghost-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.side-btn {
  width: 100%;
  margin-top: 12px;
  background: #f4eee5;
  color: #765f45;
  text-align: center;
}

.side-btn.active {
  background: #eadcc6;
  font-weight: 700;
}

.mini-summary {
  margin-top: 18px;
  padding: 16px;
  border-radius: 22px;
  background: #fffaf2;
  border: 1px solid #eadcc6;
}

.mini-summary strong {
  color: #6e5741;
}

.mini-summary p {
  margin: 8px 0 0;
}

.choice-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 22px;
  margin-top: 24px;
}

.choice-card {
  min-height: 260px;
  border: 1px solid #eee4d7;
  border-radius: 34px;
  background: #fbf8f3;
  cursor: pointer;
  padding: 34px;
  text-align: left;
  transition: all 0.2s ease;
}

.choice-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 40px rgba(80, 65, 45, 0.12);
  border-color: #c6aa82;
}

.choice-card span {
  display: block;
  margin-bottom: 18px;
  font-size: 42px;
}

.choice-card strong {
  display: block;
  color: #2d2d2d;
  font-size: 24px;
  margin-bottom: 12px;
}

.choice-card p {
  margin: 0;
  color: #8e887f;
  line-height: 1.8;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

label,
.block-label {
  display: block;
  color: #6e5741;
  font-weight: 700;
}

input,
textarea,
select {
  width: 100%;
  margin-top: 8px;
  border: 1px solid #eee4d7;
  outline: none;
  border-radius: 20px;
  padding: 14px 16px;
  background: #fbf8f3;
  color: #2d2d2d;
  font-size: 15px;
  box-sizing: border-box;
}

textarea {
  resize: vertical;
  line-height: 1.7;
}

.block-label {
  margin-top: 18px;
}

.actions-row {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  margin-top: 18px;
}

.summary-import-card,
.tool-card,
.result-card,
.answer-card,
.summary-card {
  margin-top: 22px;
  padding: 24px;
  border-radius: 28px;
  background: #fffaf2;
  border: 1px solid #eadcc6;
}

.summary-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.summary-head strong {
  color: #6e5741;
}

.summary-head button {
  border: none;
  border-radius: 999px;
  padding: 6px 12px;
  background: #f5dede;
  color: #9b5048;
  cursor: pointer;
}

.tool-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 14px;
}

.tool-grid div {
  padding: 16px;
  border-radius: 20px;
  background: #fbf8f3;
}

.tool-grid strong {
  color: #6e5741;
}

.tool-grid p,
.suggestion {
  color: #8e887f;
  line-height: 1.7;
}

.result-card pre {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.8;
  color: #3a332d;
  font-family: inherit;
}

.count-pill {
  flex-shrink: 0;
  padding: 12px 20px;
  border-radius: 999px;
  background: #f4eee5;
  color: #6e5741;
  font-weight: 700;
}

.roundtable-tools {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}

.tool-hint {
  color: #92887c;
  font-size: 14px;
}

.custom-editor {
  margin-bottom: 20px;
  padding: 22px;
  border-radius: 28px;
  background: #fffaf2;
  border: 1px solid #eadcc6;
}

.agent-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  max-height: 470px;
  overflow-y: auto;
  padding-right: 6px;
}

.agent-card {
  min-height: 210px;
  padding: 22px;
  border-radius: 28px;
  border: 2px solid transparent;
  background: #fbf8f3;
  cursor: pointer;
  text-align: center;
  transition: all 0.2s ease;
}

.agent-card:hover {
  transform: translateY(-2px);
  border-color: #eadcc6;
}

.agent-card.selected {
  background: #edf8f0;
  border-color: #a7c7b4;
}

.agent-card span {
  display: block;
  font-size: 34px;
  margin-bottom: 12px;
}

.agent-card strong {
  display: block;
  font-size: 18px;
  color: #2d2d2d;
}

.agent-card p {
  color: #8e887f;
  line-height: 1.6;
  font-size: 14px;
}

.gender-row {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: 14px;
}

.gender-row button {
  border: none;
  border-radius: 999px;
  padding: 6px 12px;
  background: #f1eadf;
  color: #7b6b5c;
  cursor: pointer;
  font-size: 12px;
}

.gender-row button.active {
  background: #c6aa82;
  color: white;
  font-weight: 700;
}

.custom-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 14px;
}

.custom-pill {
  padding: 6px 10px;
  border-radius: 999px;
  background: #eaf3ee;
  color: #5d806d;
  font-size: 12px;
  font-weight: 700;
}

.custom-actions button {
  border: none;
  border-radius: 999px;
  padding: 6px 10px;
  background: #f5dede;
  color: #9b5048;
  cursor: pointer;
  font-size: 12px;
}

.session-list {
  margin-top: 18px;
  display: grid;
  gap: 12px;
}

.session-card {
  border: 1px solid transparent;
  border-radius: 22px;
  padding: 16px;
  text-align: left;
  background: #fbf8f3;
  cursor: pointer;
}

.session-card.active {
  background: #eadcc6;
  border-color: #c6aa82;
}

.session-card strong {
  display: block;
  color: #3a332d;
  line-height: 1.5;
}

.session-card span {
  display: block;
  margin-top: 6px;
  color: #92887c;
  font-size: 13px;
}

.chat-panel {
  margin-top: 24px;
  display: grid;
  gap: 18px;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.avatar {
  width: 46px;
  height: 46px;
  flex-shrink: 0;
  border-radius: 16px;
  background: #edf8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.bubble {
  max-width: 900px;
  padding: 18px 20px;
  border-radius: 24px;
  background: #fbf8f3;
  border: 1px solid #eee4d7;
}

.bubble-head {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.bubble-head strong {
  color: #2d2d2d;
}

.bubble-head span {
  color: #a59a8f;
  font-size: 13px;
}

.bubble p,
.summary-card p,
.answer-card p {
  margin: 0;
  color: #5f5a54;
  line-height: 1.8;
}

.rag-tips {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 18px;
  color: #8e887f;
}

.rag-tips span {
  font-weight: 700;
  color: #6e5741;
}

.rag-tips button {
  border: none;
  cursor: pointer;
  border-radius: 999px;
  padding: 8px 14px;
  background: #f4eee5;
  color: #765f45;
  font-size: 13px;
}

.rag-tips button:hover {
  background: #eadcc6;
}

.empty-rag {
  margin-top: 18px;
  padding: 26px;
  border-radius: 24px;
  background: #fbf8f3;
  color: #9a9188;
  text-align: center;
}

.contexts {
  margin-top: 24px;
}

.context-card {
  padding: 18px;
  margin-top: 12px;
  border-radius: 22px;
  background: #fbf8f3;
  border: 1px solid #eee4d7;
}

.context-card strong {
  color: #6e5741;
}

.context-card p {
  color: #8e887f;
  line-height: 1.7;
}

.archive-panel {
  margin-top: 24px;
}

.archive-card {
  padding: 20px;
  margin-top: 14px;
  border-radius: 24px;
  background: #fbf8f3;
  border: 1px solid #eee4d7;
}

.archive-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.archive-head button {
  border: none;
  border-radius: 999px;
  padding: 6px 12px;
  background: #f5dede;
  color: #9b5048;
  cursor: pointer;
}

.archive-time {
  color: #a59a8f;
  font-size: 13px;
}

@media (max-width: 1100px) {
  .page-grid {
    grid-template-columns: 1fr;
  }

  .side-panel {
    position: static;
  }

  .agent-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .tool-grid,
  .choice-grid,
  .form-grid {
    grid-template-columns: 1fr;
  }

  .hero-card {
    flex-direction: column;
  }
}

@media (max-width: 640px) {
  .tree-page {
    padding: 20px 16px 110px;
  }

  h1 {
    font-size: 32px;
  }

  .main-panel,
  .single-panel,
  .hero-card,
  .side-panel {
    padding: 24px;
    border-radius: 28px;
  }

  .agent-grid {
    grid-template-columns: 1fr;
  }
}
</style>