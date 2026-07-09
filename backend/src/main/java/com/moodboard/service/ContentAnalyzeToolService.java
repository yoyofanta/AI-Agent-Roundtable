package com.moodboard.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContentAnalyzeToolService {

    public Map<String, Object> analyze(Map<String, Object> body) {
        String text = str(body.get("text"));
        String type = str(body.get("type"));
        String platform = str(body.get("platform"));
        String audience = str(body.get("audience"));
        String style = str(body.get("style"));

        String allText = String.join(" ", text, type, platform, audience, style);

        List<String> tags = inferTags(allText);
        String conflictLevel = inferConflictLevel(allText);
        String emotionTone = inferEmotionTone(allText);
        String suggestion = buildSuggestion(tags, conflictLevel);

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("text", text);
        input.put("type", type);
        input.put("platform", platform);
        input.put("audience", audience);
        input.put("style", style);

        Map<String, Object> output = new LinkedHashMap<>();
        output.put("conflictLevel", conflictLevel);
        output.put("emotionTone", emotionTone);
        output.put("tags", tags);
        output.put("suggestion", suggestion);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("toolType", "CONTENT_ANALYZE");
        result.put("toolName", "内容标签分析工具");
        result.put("description", "根据剧情文本分析冲突强度、情绪基调、内容标签和优化建议");
        result.put("input", input);
        result.put("output", output);

        return result;
    }

    private List<String> inferTags(String text) {
        List<String> tags = new ArrayList<>();

        if (contains(text, "追妻", "火葬场")) {
            tags.add("追妻火葬场");
        }

        if (contains(text, "霸总", "总裁")) {
            tags.add("霸总");
        }

        if (contains(text, "误会", "冤枉", "误解")) {
            tags.add("误会");
        }

        if (contains(text, "反转", "真相", "揭露")) {
            tags.add("反转");
        }

        if (contains(text, "离开", "分手", "告别")) {
            tags.add("离开");
        }

        if (contains(text, "逆袭", "成长", "翻身")) {
            tags.add("逆袭");
        }

        if (contains(text, "甜", "甜宠", "恋爱")) {
            tags.add("甜宠");
        }

        if (contains(text, "虐", "后悔", "追悔")) {
            tags.add("虐恋");
        }

        if (contains(text, "强冲突", "冲突", "争吵", "背叛")) {
            tags.add("强冲突");
        }

        if (tags.isEmpty()) {
            tags.add("剧情向");
            tags.add("情感向");
            tags.add("内容创意");
        }

        return tags;
    }

    private String inferConflictLevel(String text) {
        int score = 0;

        if (contains(text, "误会", "冤枉", "背叛")) {
            score += 2;
        }

        if (contains(text, "离开", "分手", "告别")) {
            score += 2;
        }

        if (contains(text, "追悔", "后悔", "火葬场")) {
            score += 2;
        }

        if (contains(text, "霸总", "身份差", "阶层")) {
            score += 1;
        }

        if (contains(text, "强冲突", "争吵", "真相")) {
            score += 2;
        }

        if (contains(text, "反转", "逆袭")) {
            score += 1;
        }

        if (score >= 5) {
            return "高";
        }

        if (score >= 2) {
            return "中";
        }

        return "低";
    }

    private String inferEmotionTone(String text) {
        if (contains(text, "追妻", "火葬场", "追悔", "后悔")) {
            return "虐恋 / 追悔 / 反转";
        }

        if (contains(text, "甜", "甜宠", "恋爱")) {
            return "甜宠 / 治愈 / 轻喜";
        }

        if (contains(text, "逆袭", "成长", "反击")) {
            return "成长 / 逆袭 / 爽感";
        }

        if (contains(text, "误会", "离开", "真相")) {
            return "委屈 / 拉扯 / 真相揭露";
        }

        return "情感 / 剧情 / 内容创意";
    }

    private String buildSuggestion(List<String> tags, String conflictLevel) {
        StringBuilder suggestion = new StringBuilder();

        if ("高".equals(conflictLevel)) {
            suggestion.append("当前选题冲突强度较高，适合短视频漫剧开场。建议前3秒直接抛出核心误会或关键矛盾，快速建立人物关系张力。");
        } else if ("中".equals(conflictLevel)) {
            suggestion.append("当前选题具备一定冲突，但开场还可以更直接。建议增加误会、身份差、离开或真相揭露等元素，提高追看动机。");
        } else {
            suggestion.append("当前选题冲突较弱，建议补充更明确的人物矛盾、情绪拉扯或反转节点。");
        }

        if (tags.contains("追妻火葬场")) {
            suggestion.append(" 追妻火葬场题材可以采用“误会爆发—女主离开—男主追悔—真相反转”的结构。");
        }

        if (tags.contains("反转")) {
            suggestion.append(" 反转节点不要过于突然，前期可以埋下证据、误导或未说完的话作为伏笔。");
        }

        suggestion.append(" 运营侧可围绕误会、追悔、反转、强冲突等关键词设计标题和标签。");

        return suggestion.toString();
    }

    private boolean contains(String text, String... keywords) {
        if (text == null) {
            return false;
        }

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private String str(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}