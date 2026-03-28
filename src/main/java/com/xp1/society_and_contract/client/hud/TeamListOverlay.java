package com.xp1.society_and_contract.client.hud;

import com.xp1.society_and_contract.client.data.ClientTeamData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class TeamListOverlay implements LayeredDraw.Layer {
    // 配置、状态
    private static final int OFFSET_X = 0;          // 距离右边的距离
    private static final int OFFSET_Y = 0;          // 顶部起始Y
    private static final int LINE_SPACING = 11;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int SHADOW_COLOR = 0xFF000000;
    private static final boolean DRAW_SHADOW = true;

    public static boolean SHOW = false;


    // 从 Client 数据获取
    public static class TeamEntry {
        String name;
        int color;
        String leader;
        int memberCount;

        public TeamEntry(String name, int color, String leader, int memberCount) {
            this.name = name;
            this.color = color;
            this.leader = leader;
            this.memberCount = memberCount;
        }
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (!SHOW) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused()) return;

        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // 如果没有队伍数据，直接返回（防空列表）
        if (ClientTeamData.TEAMS.isEmpty()) return;

        // 计算列宽（基于所有队伍）
        int maxTeamWidth = font.width("队伍名称");
        int maxLeaderWidth = font.width("队长");
        int maxCountWidth = font.width("人数");

        for (TeamEntry team : ClientTeamData.TEAMS) {
            maxTeamWidth = Math.max(maxTeamWidth, font.width(team.name));
            maxLeaderWidth = Math.max(maxLeaderWidth, font.width(team.leader));
            maxCountWidth = Math.max(maxCountWidth, font.width(String.valueOf(team.memberCount)));
        }

        int padding = 16;
        int colTeamW = maxTeamWidth + padding * 2;
        int colLeaderW = maxLeaderWidth + padding * 2;
        int colCountW = maxCountWidth + padding * 2;
        int totalWidth = colTeamW + colLeaderW + colCountW;

        // 垂直居中计算
        int titleGap = 8;
        int rowHeight = LINE_SPACING;
        int contentHeight = rowHeight + titleGap + (ClientTeamData.TEAMS.size() * rowHeight);
        int verticalPad = 20;
        int totalHeight = contentHeight + verticalPad * 2;

        int baseX = (screenWidth - totalWidth) / 2;
        int baseY = (screenHeight - totalHeight) / 2;

        // 背景
        graphics.fill(
                baseX - 12,
                baseY,
                baseX + totalWidth + 15,
                baseY + totalHeight,
                0xB0222222
        );

        // 标题行
        int currentY = baseY + verticalPad;
        int titleColor = 0xFFEEEEEE;

        drawShadowedString(graphics, font, "队伍名称", baseX + padding, currentY, titleColor);
        drawShadowedString(graphics, font, "队长", baseX + colTeamW + padding, currentY, titleColor);

        String countTitle = "人数";
        int countTitleWidth = font.width(countTitle);
        drawShadowedString(graphics, font, countTitle,
                baseX + colTeamW + colLeaderW + colCountW - countTitleWidth - padding, currentY, titleColor);

        currentY += rowHeight + titleGap;

        // 内容行：直接循环 ClientTeamData.TEAMS（包含所有队伍，包括玩家的）
        for (TeamEntry team : ClientTeamData.TEAMS) {
            String countStr = String.valueOf(team.memberCount);
            int countWidth = font.width(countStr);

            drawShadowedString(graphics, font, team.name, baseX + padding, currentY, team.color);
            drawShadowedString(graphics, font, team.leader, baseX + colTeamW + padding, currentY, TEXT_COLOR);
            drawShadowedString(graphics, font, countStr,
                    baseX + colTeamW + colLeaderW + colCountW - countWidth - padding, currentY, 0xFFEEEEEE);

            currentY += rowHeight;
        }
    }

    private void drawShadowedString(GuiGraphics graphics, Font font, String text, int x, int y, int color) {
        if (DRAW_SHADOW) {
            graphics.drawString(font, text, x + 1, y + 1, SHADOW_COLOR);
        }
        graphics.drawString(font, text, x, y, color);
    }
}
