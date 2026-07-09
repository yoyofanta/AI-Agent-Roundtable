<template>
  <main class="profile-page">
    <section class="hero-card">
      <div>
        <p class="eyebrow">Profile</p>
        <h1>我的 Agent 工作台</h1>
        <p class="desc">
          管理当前账号、查看项目能力，并快速进入多 Agent 圆桌与内容生产工作台。
        </p>
      </div>

      <button class="logout-btn" @click="logout">退出登录</button>
    </section>

    <section class="profile-card">
      <div class="avatar">🤖</div>

      <div class="user-info">
        <p class="label">当前账号</p>
        <h2>{{ username }}</h2>
        <p>AI Agent 多角色圆桌系统使用者</p>
      </div>
    </section>

    <section class="panel">
      <div class="panel-head">
        <div>
          <p class="eyebrow">System</p>
          <h2>系统能力</h2>
          <p>当前项目主要用于 AI Agent 圆桌对谈、内容生产、RAG 增强、Memory 记忆和工具调用演示。</p>
        </div>
      </div>

      <div class="ability-grid">
        <div class="ability-card">
          <span>🤖</span>
          <strong>多 Agent 圆桌</strong>
          <p>支持选择 2-4 个 Agent 围绕同一议题依次发言，并生成综合总结。</p>
        </div>

        <div class="ability-card">
          <span>🧠</span>
          <strong>16 型人格 Agent</strong>
          <p>内置 16 型人格角色，可设置男 / 女 / 不限，形成不同表达视角。</p>
        </div>

        <div class="ability-card">
          <span>✨</span>
          <strong>自定义角色 Agent</strong>
          <p>支持创建专属角色，填写角色名称、性别、参考人格和 Prompt 设定。</p>
        </div>

        <div class="ability-card">
          <span>🎬</span>
          <strong>内容生产工作台</strong>
          <p>支持剧情策划、角色对白、情绪节奏和运营分析等内容生产 Agent。</p>
        </div>

        <div class="ability-card">
          <span>📚</span>
          <strong>RAG 知识增强</strong>
          <p>后端内置内容生产知识库，用于增强 Agent 回答的相关性。</p>
        </div>

        <div class="ability-card">
          <span>🛠️</span>
          <strong>Tool Calling</strong>
          <p>支持内容标签分析工具，分析冲突强度、情绪基调和推荐标签。</p>
        </div>
      </div>
    </section>

    <section class="panel">
      <div class="panel-head">
        <div>
          <p class="eyebrow">Quick Actions</p>
          <h2>快速入口</h2>
        </div>
      </div>

      <div class="quick-actions">
        <button class="primary-btn" @click="goRoundtable">进入多 Agent 圆桌</button>
        <button class="ghost-btn" @click="goContent">进入内容生产工作台</button>
        <button class="danger-btn" @click="clearLocalData">清空本地会话记录</button>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const username = computed(() => {
  return localStorage.getItem('moodboard_current_user') || 'Agent User'
})

function goRoundtable() {
  router.push('/app/tree-hole?view=roundtable')
}

function goContent() {
  router.push('/app/tree-hole?view=content')
}

function clearLocalData() {
  const confirmClear = confirm('确定要清空当前浏览器中的本地圆桌会话记录吗？')

  if (!confirmClear) {
    return
  }

  const keys = Object.keys(localStorage)

  keys.forEach(key => {
    if (
      key.startsWith('moodboard_agent_roundtable_sessions_') ||
      key.startsWith('moodboard_custom_roundtable_agents_')
    ) {
      localStorage.removeItem(key)
    }
  })

  alert('本地会话记录已清空')
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('moodboard_current_user')
  router.push('/login')
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding: 32px 32px 120px;
  color: #2d2d2d;
}

.hero-card,
.profile-card,
.panel {
  max-width: 1400px;
  margin: 0 auto 24px;
  border-radius: 34px;
  background: rgba(255, 255, 255, 0.84);
  box-shadow: 0 18px 50px rgba(80, 65, 45, 0.08);
}

.hero-card {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: flex-start;
  padding: 34px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #a59a8f;
  font-size: 14px;
  letter-spacing: 0.03em;
}

h1,
h2 {
  margin: 0;
  color: #2e2e2e;
}

h1 {
  font-size: 38px;
}

h2 {
  font-size: 26px;
}

.desc {
  max-width: 760px;
  margin: 14px 0 0;
  color: #8e887f;
  font-size: 17px;
  line-height: 1.8;
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 22px;
  padding: 30px;
}

.avatar {
  display: flex;
  width: 88px;
  height: 88px;
  align-items: center;
  justify-content: center;
  border-radius: 28px;
  background: #f4eee5;
  font-size: 42px;
}

.user-info .label {
  margin: 0 0 6px;
  color: #a59a8f;
}

.user-info p {
  margin: 8px 0 0;
  color: #8e887f;
}

.panel {
  padding: 30px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 22px;
}

.panel-head p {
  margin: 10px 0 0;
  color: #8e887f;
  line-height: 1.7;
}

.ability-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.ability-card {
  min-height: 160px;
  padding: 22px;
  border-radius: 26px;
  background: #fbf8f3;
}

.ability-card span {
  display: block;
  margin-bottom: 12px;
  font-size: 30px;
}

.ability-card strong {
  display: block;
  color: #3a332d;
  font-size: 18px;
}

.ability-card p {
  margin: 10px 0 0;
  color: #91877c;
  line-height: 1.7;
}

.quick-actions {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.primary-btn,
.ghost-btn,
.danger-btn,
.logout-btn {
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

.danger-btn {
  background: #f5dede;
  color: #9b5048;
}

.logout-btn {
  background: #f4eee5;
  color: #765f45;
}

@media (max-width: 900px) {
  .hero-card,
  .profile-card {
    flex-direction: column;
  }

  .ability-grid {
    grid-template-columns: 1fr;
  }
}
</style>