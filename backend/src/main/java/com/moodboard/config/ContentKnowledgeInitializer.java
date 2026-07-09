package com.moodboard.config;

import com.moodboard.entity.KnowledgeChunk;
import com.moodboard.repository.KnowledgeChunkRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ContentKnowledgeInitializer {

    private final KnowledgeChunkRepository knowledgeChunkRepository;

    public ContentKnowledgeInitializer(KnowledgeChunkRepository knowledgeChunkRepository) {
        this.knowledgeChunkRepository = knowledgeChunkRepository;
    }

    @PostConstruct
    public void initContentKnowledge() {
        Set<String> existingTitles = knowledgeChunkRepository.findAll()
                .stream()
                .map(KnowledgeChunk::getTitle)
                .collect(Collectors.toSet());

        addIfMissing(existingTitles,
                "短视频漫剧强冲突开场",
                "短视频漫剧需要在开头快速建立冲突，让用户在前几秒理解人物关系和矛盾点。常见方式包括误会爆发、身份反差、利益冲突、情感背叛、关键选择等。强冲突开场的目标不是解释完整背景，而是先制造追看动机。",
                "漫剧,短剧,强冲突,开场,内容生产"
        );

        addIfMissing(existingTitles,
                "前3秒钩子设计",
                "短视频内容的前3秒需要明确告诉用户为什么要继续看。可以通过一句强台词、一个反转画面、一个高压冲突或一个悬念问题吸引注意。例如：她离开后，他才知道自己误会了她三年。",
                "短视频,钩子,前3秒,留存,内容生产"
        );

        addIfMissing(existingTitles,
                "追妻火葬场剧情结构",
                "追妻火葬场题材通常包含误会、伤害、离开、成长、追悔和反转几个阶段。前期需要让观众理解女主受委屈的原因，中段强化男主失去后的后悔，后期通过反转或弥补形成情绪释放。",
                "追妻火葬场,误会,反转,情感,漫剧"
        );

        addIfMissing(existingTitles,
                "误会反转剧情设计",
                "误会反转类剧情需要先让角色基于错误信息做出判断，再在关键节点揭露真相。反转不能过于突然，前期应埋下细节伏笔，例如一个被忽视的证据、一句没有说完的话或第三方误导。",
                "误会,反转,伏笔,剧情设计"
        );

        addIfMissing(existingTitles,
                "人物关系张力设计",
                "人物关系张力来自角色目标不一致、情感不对等、身份差异或利益冲突。漫剧内容中，男女主、竞争者、家人、朋友之间的关系可以形成多层矛盾，让剧情不仅靠事件推进，也靠关系变化吸引用户。",
                "人物关系,张力,角色设计,剧情"
        );

        addIfMissing(existingTitles,
                "三幕式短剧结构",
                "短剧和漫剧可以参考三幕式结构：第一幕建立人物和核心冲突，第二幕升级矛盾并制造误会或阻碍，第三幕揭示真相并完成情绪释放。用于短视频时，每一幕都需要进一步拆成高密度小节点。",
                "三幕式,剧情结构,短剧,漫剧"
        );

        addIfMissing(existingTitles,
                "分集节奏设计",
                "分集内容需要每一集都有明确任务，例如提出冲突、升级误会、揭露线索、制造反转、留下悬念。每集结尾最好保留一个未解决的问题，让用户产生继续观看下一集的动机。",
                "分集,节奏,悬念,留存"
        );

        addIfMissing(existingTitles,
                "角色对白生成原则",
                "短剧和漫剧对白应尽量短、直接、有情绪。对白要服务于冲突和人物关系，而不是单纯解释剧情。适合传播的台词通常具有情绪爆点，例如后悔、反击、误会、告别、真相揭露等。",
                "对白,台词,角色,情绪表达"
        );

        addIfMissing(existingTitles,
                "爽点与虐点节奏",
                "内容节奏可以通过爽点和虐点交替维持用户情绪。虐点负责制造委屈、误会和压抑感，爽点负责反击、揭露真相和情绪释放。两者需要交替出现，避免一直压抑或一直平铺。",
                "爽点,虐点,情绪节奏,用户留存"
        );

        addIfMissing(existingTitles,
                "漫剧标题优化方法",
                "漫剧标题需要突出冲突、反转和情绪价值。常见关键词包括误会、离开、追悔、逆袭、真相、反击、霸总、白月光等。标题要让用户一眼知道人物关系和看点。",
                "标题,运营,点击率,短视频"
        );

        addIfMissing(existingTitles,
                "内容标签归纳方法",
                "内容标签应从题材、人设、情绪和冲突四个维度提取。例如追妻火葬场、霸总、误会、反转、甜宠、虐恋、逆袭、强冲突等。标签可以帮助内容更清晰地匹配目标用户。",
                "标签,运营,内容分析,用户匹配"
        );

        addIfMissing(existingTitles,
                "用户评论反馈分析",
                "用户评论可以反映内容是否击中情绪点。高频评论如果集中在心疼女主、男主活该、快追妻、求更新等方向，说明内容的情绪冲突和追更动机较强。Agent 可以根据评论反馈优化后续剧情节奏。",
                "评论分析,用户反馈,运营分析,内容优化"
        );
    }

    private void addIfMissing(Set<String> existingTitles, String title, String content, String tags) {
        if (existingTitles.contains(title)) {
            return;
        }

        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setTitle(title);
        chunk.setContent(content);
        chunk.setTags(tags);

        knowledgeChunkRepository.save(chunk);
        existingTitles.add(title);
    }
}