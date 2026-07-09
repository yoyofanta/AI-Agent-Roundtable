package com.moodboard.controller;

import com.moodboard.common.R;
import com.moodboard.service.ContentAnalyzeToolService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/tools/content")
public class ContentAnalyzeToolController {

    private final ContentAnalyzeToolService contentAnalyzeToolService;

    public ContentAnalyzeToolController(ContentAnalyzeToolService contentAnalyzeToolService) {
        this.contentAnalyzeToolService = contentAnalyzeToolService;
    }

    /**
     * 内容标签分析工具
     *
     * POST /api/ai/tools/content/analyze
     */
    @PostMapping("/analyze")
    public Map<String, Object> analyze(@RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> data = contentAnalyzeToolService.analyze(body);
            return R.ok(data);
        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("toolType", "CONTENT_ANALYZE");
            data.put("toolName", "内容标签分析工具");
            data.put("success", false);
            data.put("message", "内容分析失败：" + e.getMessage());

            return R.ok(data);
        }
    }
}