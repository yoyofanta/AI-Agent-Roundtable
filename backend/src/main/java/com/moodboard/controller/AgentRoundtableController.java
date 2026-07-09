package com.moodboard.controller;

import com.moodboard.common.R;
import com.moodboard.service.AgentRoundtableService;
import com.moodboard.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/agent")
public class AgentRoundtableController {

    private final AgentRoundtableService agentRoundtableService;
    private final AuthService authService;

    public AgentRoundtableController(
            AgentRoundtableService agentRoundtableService,
            AuthService authService
    ) {
        this.agentRoundtableService = agentRoundtableService;
        this.authService = authService;
    }

    /**
     * 多角色 Agent 圆桌会话
     *
     * POST /api/ai/agent/roundtable
     *
     * 支持场景：
     * 1. MBTI 多人格圆桌
     * 2. 内容生产 Agent 圆桌
     * 3. 漫剧 / 短剧选题拆解
     */
    @PostMapping("/roundtable")
    public Map<String, Object> roundtable(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestBody Map<String, Object> body
    ) {
        try {
            Long userId = resolveUserId(auth);

            String topic = str(body.get("topic"));

            if (topic.isBlank()) {
                topic = "请围绕用户当前内容需求进行多 Agent 圆桌分析。";
            }

            Object agentsObj = body.get("agents");

            if (!(agentsObj instanceof List<?> rawList)) {
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("success", false);
                data.put("message", "agents 必须是数组");
                return R.ok(data);
            }

            List<Map<String, Object>> agents = rawList.stream()
                    .filter(item -> item instanceof Map)
                    .map(item -> (Map<String, Object>) item)
                    .toList();

            if (agents.size() < 2) {
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("success", false);
                data.put("message", "至少需要选择 2 个 Agent 才能开始圆桌会话");
                data.put("agentCount", agents.size());
                return R.ok(data);
            }

            if (agents.size() > 4) {
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("success", false);
                data.put("message", "最多只能选择 4 个 Agent");
                data.put("agentCount", agents.size());
                return R.ok(data);
            }

            Map<String, Object> data = agentRoundtableService.runRoundtable(
                    userId,
                    topic,
                    agents
            );

            data.putIfAbsent("success", true);
            data.putIfAbsent("topic", topic);
            data.putIfAbsent("agentCount", agents.size());

            return R.ok(data);
        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("success", false);
            data.put("message", "Agent 圆桌会话失败：" + e.getMessage());

            return R.ok(data);
        }
    }

    private Long resolveUserId(String auth) {
        try {
            if (auth == null || auth.isBlank()) {
                return 1L;
            }

            return authService.currentUserId(auth);
        } catch (Exception e) {
            return 1L;
        }
    }

    private String str(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}