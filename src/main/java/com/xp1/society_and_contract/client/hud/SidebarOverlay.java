package com.xp1.society_and_contract.client.hud;

import com.xp1.society_and_contract.client.data.ClientPlayerData;
import com.xp1.society_and_contract.client.data.ClientTeamData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class SidebarOverlay implements LayeredDraw.Layer {
    private static final int OFFSET_X = 10;
    private static final int OFFSET_Y = 20;
    private static final int LINE_SPACING = 11;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int SHADOW_COLOR = 0xFF000000;
    private static final boolean DRAW_SHADOW = true;
    private static final int PADDING = 10;
    private static final int VERTICAL_PAD = 8;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused()) return;

        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // 动态数据（当前玩家自己的）
        String teamName = ClientPlayerData.teamName;
        String teamLeader= ClientPlayerData.teamLeader;
        int teamColor = ClientPlayerData.teamColor;
        String position = ClientPlayerData.position;
        int money = ClientPlayerData.money;

        // 计算宽度
        int maxWidth = 0;
        maxWidth = Math.max(maxWidth, font.width("队伍：" + teamName));
        maxWidth = Math.max(maxWidth, font.width("队长：" + teamLeader));
        maxWidth = Math.max(maxWidth, font.width("职位：" + position));
        maxWidth = Math.max(maxWidth, font.width("金币：" + money));

        int totalWidth = maxWidth + PADDING * 2;
        int numLines = 4; // 标题 + 3行
        int contentHeight = numLines * LINE_SPACING;
        int totalHeight = contentHeight + VERTICAL_PAD * 2;

        int baseX = screenWidth - totalWidth - OFFSET_X;
        int baseY = (screenHeight - totalHeight) / 2 - OFFSET_Y;

        // 背景
        graphics.fill(baseX, baseY, baseX + totalWidth, baseY + totalHeight + 5, 0xA0111111);

        // 标题
        int currentX = baseX + PADDING;
        int currentY = baseY + VERTICAL_PAD;

        drawShadowedString(graphics, font, "个人信息", baseX + (totalWidth - font.width("个人信息")) / 2, currentY, 0xFFDDDDDD);
        currentY += LINE_SPACING + 4;

        drawShadowedString(graphics, font, "队伍：" + teamName, currentX, currentY, teamColor);
        currentY += LINE_SPACING;
        drawShadowedString(graphics, font, "队长：" + teamLeader, currentX, currentY, teamColor);
        currentY += LINE_SPACING;
        drawShadowedString(graphics, font, "职位：" + position, currentX, currentY, TEXT_COLOR);
        currentY += LINE_SPACING;
        drawShadowedString(graphics, font, "金币：" + money, currentX, currentY, 0xFFFFD700);
    }

    private void drawShadowedString(GuiGraphics graphics, Font font, String text, int x, int y, int color) {
        if (DRAW_SHADOW) {
            graphics.drawString(font, text, x + 1, y + 1, SHADOW_COLOR);
        }
        graphics.drawString(font, text, x, y, color);
    }
}