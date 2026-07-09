<template>
  <div class="app-shell">
    <router-view />

    <nav class="bottom-nav">
      <router-link class="nav-item" to="/app/tree-hole?view=content">
        🎬 内容
      </router-link>

      <router-link class="nav-item" to="/app/tree-hole?view=roundtable">
        🤖 圆桌
      </router-link>

      <router-link class="nav-item" to="/app/tree-hole?view=rag">
        📚 知识库
      </router-link>

      <router-link class="nav-item" to="/app/profile">
        👤 我的
      </router-link>
    </nav>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(232, 220, 202, 0.7), transparent 30%),
    radial-gradient(circle at top right, rgba(220, 235, 228, 0.65), transparent 28%),
    #f8f6f1;
  padding-bottom: 96px;
}

.bottom-nav {
  position: fixed;
  left: 50%;
  bottom: 24px;
  z-index: 50;
  width: min(92vw, 680px);
  transform: translateX(-50%);
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  padding: 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 18px 48px rgba(80, 65, 45, 0.14);
  backdrop-filter: blur(14px);
}

.nav-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  border-radius: 999px;
  color: #8e887f;
  font-size: 14px;
  text-decoration: none;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background: #f5eee4;
  color: #6e5741;
}

.router-link-active {
  background: #eadcc6;
  color: #6e5741;
  font-weight: 700;
}
</style>