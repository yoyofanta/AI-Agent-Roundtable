package com.moodboard.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AgentRoundtableService {

    private final MemoryChatService memoryChatService;
    private final MemoryService memoryService;

    public AgentRoundtableService(
            MemoryChatService memoryChatService,
            MemoryService memoryService
    ) {
        this.memoryChatService = memoryChatService;
        this.memoryService = memoryService;
    }

    public Map<String, Object> runRoundtable(
            Long userId,
            String topic,
            List<Map<String, Object>> agents
    ) {
        if (topic == null || topic.isBlank()) {
            topic = "用户希望多个 Agent 围绕当前内容需求进行圆桌分析。";
        }

        if (agents == null || agents.size() < 2) {
            throw new RuntimeException("至少需要选择 2 个 Agent");
        }

        if (agents.size() > 4) {
            throw new RuntimeException("最多只能选择 4 个 Agent");
        }

        String memoryPrompt = memoryService.buildMemoryPrompt(userId);

        List<Map<String, Object>> agentReplies = new ArrayList<>();

        StringBuilder conversationContext = new StringBuilder();
        conversationContext.append("用户需求：").append(topic).append("\n\n");
        conversationContext.append("用户长期记忆：\n").append(memoryPrompt).append("\n\n");

        boolean contentProductionMode = hasContentProductionAgent(agents);

        for (int i = 0; i < agents.size(); i++) {
            Map<String, Object> agent = agents.get(i);

            String code = str(agent.get("code"));
            String name = str(agent.get("name"));
            String role = str(agent.get("role"));

            if (code.isBlank()) {
                code = "AGENT_" + (i + 1);
            }

            if (name.isBlank()) {
                name = code + " Agent";
            }

            String agentInput = buildAgentPrompt(
                    topic,
                    memoryPrompt,
                    conversationContext.toString(),
                    code,
                    name,
                    role,
                    contentProductionMode
            );

            Map<String, Object> result = memoryChatService.chat(
                    userId,
                    agentInput,
                    "AGENT_ROUNDTABLE",
                    code,
                    name
            );

            String reply = extractReply(result);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("order", i + 1);
            item.put("agentCode", code);
            item.put("agentName", name);
            item.put("agentRole", role);
            item.put("reply", reply);

            agentReplies.add(item);

            conversationContext.append(code)
                    .append(" ")
                    .append(name)
                    .append("：")
                    .append(reply)
                    .append("\n\n");
        }

        String summary = buildFinalSummaryByLLM(
                userId,
                topic,
                memoryPrompt,
                agentReplies,
                contentProductionMode
        );

        if (summary == null || summary.isBlank()) {
            summary = buildFallbackSummary(topic, agentReplies, contentProductionMode);
        }

        memoryService.updateMemory(
                userId,
                memoryService.inferEmotionSummary(topic + " " + summary),
                "AGENT_ROUNDTABLE",
                contentProductionMode ? "内容生产 Agent 圆桌" : "多角色 Agent 圆桌",
                "用户发起了一次 Agent 圆桌会话，主题为：" + truncate(topic, 120),
                truncate(topic, 300),
                truncate(summary, 500)
        );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("topic", topic);
        data.put("memory", memoryPrompt);
        data.put("agentCount", agents.size());
        data.put("mode", contentProductionMode ? "content_production" : "roundtable");
        data.put("agents", agentReplies);
        data.put("summary", summary);

        return data;
    }

    private String buildAgentPrompt(
            String topic,
            String memoryPrompt,
            String conversationContext,
            String code,
            String name,
            String role,
            boolean contentProductionMode
    ) {
        String rolePrompt = buildRolePrompt(code, name, role);

        StringBuilder prompt = new StringBuilder();

        prompt.append("现在正在进行一场 AI Agent 圆桌会话。\n\n");

        if (contentProductionMode) {
            prompt.append("本轮圆桌场景：漫剧 / 短剧内容生产。\n")
                    .append("目标：围绕用户输入的内容需求，完成选题拆解、剧情设计、角色对白、情绪节奏和运营建议。\n\n");
        } else {
            prompt.append("本轮圆桌场景：多角色 Agent 协作讨论。\n\n");
        }

        prompt.append("用户需求：\n")
                .append(topic)
                .append("\n\n");

        prompt.append("用户长期记忆：\n")
                .append(memoryPrompt == null || memoryPrompt.isBlank() ? "暂无可用记忆。" : memoryPrompt)
                .append("\n\n");

        prompt.append("前面其他 Agent 的观点：\n")
                .append(conversationContext == null || conversationContext.isBlank() ? "暂无。" : conversationContext)
                .append("\n\n");

        prompt.append("你的 Agent 设定：\n")
                .append(rolePrompt)
                .append("\n\n");

        prompt.append("请你作为 ")
                .append(code)
                .append(" ")
                .append(name)
                .append(" 发言。\n\n");

        prompt.append("要求：\n")
                .append("1. 不要重复前面 Agent 已经说过的内容；\n")
                .append("2. 必须体现你的角色分工和专业视角；\n")
                .append("3. 回复要具体、可执行，不要空泛；\n")
                .append("4. 回复 2-5 句话；\n")
                .append("5. 像真实圆桌讨论，不要写成论文；\n");

        if (contentProductionMode) {
            prompt.append("6. 内容要贴合漫剧、短剧、文娱内容生产场景；\n")
                    .append("7. 可以给出剧情、对白、节奏、标题、标签等具体建议。\n");
        }

        return prompt.toString();
    }

    /**
     * 根据 Agent code 构造不同角色 Prompt。
     * 面试时可以说：后端根据 Agent code 动态拼接角色 Prompt，再调用大模型生成差异化回复。
     */
    private String buildRolePrompt(String code, String name, String role) {
        String upperCode = code == null ? "" : code.toUpperCase(Locale.ROOT);

        return switch (upperCode) {
            case "PLOT" -> """
                    你是剧情策划 Agent。
                    你的职责是负责故事结构、开场冲突、人物关系、反转节点和分集节奏。
                    你需要把用户的内容需求拆成清晰的剧情方案，重点关注强冲突开场、人物动机和后续追看点。
                    """;

            case "DIALOGUE" -> """
                    你是角色对白 Agent。
                    你的职责是负责人物台词、情绪表达、冲突对白和适合短剧/漫剧的语言风格。
                    你需要给出能直接用于分镜或脚本的对白示例，台词要短、直接、有情绪。
                    """;

            case "RHYTHM" -> """
                    你是情绪节奏 Agent。
                    你的职责是分析剧情的爽点、虐点、反转点、悬念点和用户留存节奏。
                    你需要判断内容在哪些位置应该制造情绪爆点，哪里需要反转，哪里适合留下追更钩子。
                    """;

            case "OPS" -> """
                    你是运营分析 Agent。
                    你的职责是负责内容标题、标签、受众卖点、平台传播建议和用户反馈分析。
                    你需要从短视频平台和内容运营视角，给出更容易吸引点击和评论的包装建议。
                    """;

            case "INTJ" -> """
                    你是 INTJ 理性规划者 Agent。
                    你的特点是理性分析、结构拆解、目标规划和长期策略。
                    你需要帮助用户理清问题结构，给出清晰优先级和执行步骤。
                    """;

            case "INFP" -> """
                    你是 INFP 温柔共情者 Agent。
                    你的特点是关注情绪、价值感、内心动机和人物感受。
                    你需要先理解情绪，再给出柔和但有启发的建议。
                    """;

            case "ENFP" -> """
                    你是 ENFP 行动鼓励者 Agent。
                    你的特点是提供灵感、鼓励行动、发现可能性和激发创意。
                    你需要让讨论更有能量，同时给出有创意的方向。
                    """;

            case "ISTJ" -> """
                    你是 ISTJ 稳妥执行者 Agent。
                    你的特点是重视规则、步骤、稳定执行和可落地计划。
                    你需要把想法整理成清晰步骤，帮助用户稳定推进。
                    """;

            default -> {
                if (role != null && !role.isBlank()) {
                    yield "你是 " + name + "。\n你的角色职责是：" + role + "\n请严格围绕这个职责发言。";
                }

                yield "你是 " + name + "。\n请围绕用户需求，从你的角色视角给出具体建议。";
            }
        };
    }

    private String buildFinalSummaryByLLM(
            Long userId,
            String topic,
            String memoryPrompt,
            List<Map<String, Object>> replies,
            boolean contentProductionMode
    ) {
        try {
            StringBuilder prompt = new StringBuilder();

            prompt.append("请你作为综合总结 Agent，对下面的多 Agent 圆桌内容进行总结。\n\n");

            if (contentProductionMode) {
                prompt.append("场景：漫剧 / 短剧内容生产。\n")
                        .append("目标：输出一个结构清晰、可执行的内容生产方案。\n\n");
            }

            prompt.append("用户需求：\n")
                    .append(topic)
                    .append("\n\n");

            prompt.append("用户长期记忆：\n")
                    .append(memoryPrompt == null || memoryPrompt.isBlank() ? "暂无。" : memoryPrompt)
                    .append("\n\n");

            prompt.append("各 Agent 发言：\n");

            for (Map<String, Object> reply : replies) {
                prompt.append(reply.get("agentCode"))
                        .append(" ")
                        .append(reply.get("agentName"))
                        .append("：")
                        .append(reply.get("reply"))
                        .append("\n\n");
            }

            prompt.append("请输出综合总结。\n");

            if (contentProductionMode) {
                prompt.append("总结格式建议包含：\n")
                        .append("1. 核心选题定位；\n")
                        .append("2. 剧情结构；\n")
                        .append("3. 角色对白方向；\n")
                        .append("4. 情绪节奏与反转；\n")
                        .append("5. 标题标签和运营建议。\n");
            } else {
                prompt.append("总结要包含主要观点、共识、分歧和下一步行动建议。\n");
            }

            Map<String, Object> result = memoryChatService.chat(
                    userId,
                    prompt.toString(),
                    "AGENT_SUMMARY",
                    "SUMMARY",
                    "综合总结 Agent"
            );

            return extractReply(result);
        } catch (Exception e) {
            return "";
        }
    }

    private String buildFallbackSummary(
            String topic,
            List<Map<String, Object>> replies,
            boolean contentProductionMode
    ) {
        StringBuilder builder = new StringBuilder();

        if (contentProductionMode) {
            builder.append("围绕「")
                    .append(topic)
                    .append("」，本次内容生产圆桌形成了以下方向：\n\n");

            for (Map<String, Object> reply : replies) {
                builder.append("・")
                        .append(reply.get("agentName"))
                        .append("：")
                        .append(shortText(str(reply.get("reply")), 90))
                        .append("\n");
            }

            builder.append("\n综合来看，可以先确定核心题材和受众，再围绕强冲突开场、人物关系张力、情绪反转和标题标签进行细化。")
                    .append("建议后续把方案拆成分集大纲、关键对白、前三秒钩子和运营标题四个部分继续完善。");

            return builder.toString();
        }

        builder.append("围绕「")
                .append(topic)
                .append("」，不同 Agent 给出了几个角度：\n\n");

        for (Map<String, Object> reply : replies) {
            builder.append("・")
                    .append(reply.get("agentCode"))
                    .append(" ")
                    .append(reply.get("agentName"))
                    .append("：")
                    .append(shortText(str(reply.get("reply")), 80))
                    .append("\n");
        }

        builder.append("\n综合来看，可以先确认当前最重要的问题，再将其拆成可执行的小步骤，并根据反馈继续调整。");

        return builder.toString();
    }

    private boolean hasContentProductionAgent(List<Map<String, Object>> agents) {
        if (agents == null) {
            return false;
        }

        Set<String> contentCodes = Set.of("PLOT", "DIALOGUE", "RHYTHM", "OPS");

        for (Map<String, Object> agent : agents) {
            String code = str(agent.get("code")).toUpperCase(Locale.ROOT);

            if (contentCodes.contains(code)) {
                return true;
            }
        }

        return false;
    }

    private String extractReply(Map<String, Object> result) {
        if (result == null) {
            return "这个 Agent 暂时没有生成有效回复。";
        }

        Object reply = result.get("reply");

        if (reply == null) {
            reply = result.get("answer");
        }

        if (reply == null) {
            reply = result.get("content");
        }

        return reply == null ? "这个 Agent 暂时没有生成有效回复。" : String.valueOf(reply);
    }

    private String str(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength) + "...";
    }

    private String shortText(String text, int maxLength) {
        if (text == null || text.isBlank()) {
            return "暂无观点";
        }

        text = text.replaceAll("\\s+", " ");

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength) + "...";
    }
}