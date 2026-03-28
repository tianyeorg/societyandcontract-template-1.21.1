package com.xp1.society_and_contract.client.gui;

import com.xp1.society_and_contract.nerworking.packet.CreateTeamPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.awt.Color;

public class TeamCreateScreen extends Screen {

    private EditBox nameBox;
    private EditBox hexBox;

    // HSB
    private float hue = 0.6f;
    private float sat = 0.8f;
    private float bri = 1f;

    // dragging
    private boolean dragHue;
    private boolean dragSB;

    // layout cache
    private int panelX;
    private int panelY;

    private int wheelX;
    private int wheelY;
    private int wheelR = 45;

    private int boxX;
    private int boxY;
    private int boxSize = 120;

    public TeamCreateScreen() {
        super(Component.literal("创建队伍"));
    }


    @Override
    protected void init() {

        int panelW = 420;
        int panelH = 260;

        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;


        nameBox = new EditBox(

                font,
                panelX + 140,
                panelY + 30,
                220,
                20,
                Component.literal("")
        );

        nameBox.setMaxLength(16);

        addRenderableWidget(nameBox);


        hexBox = new EditBox(

                font,
                panelX + 140,
                panelY + 60,
                220,
                20,
                Component.literal("")
        );

        hexBox.setValue("#3399FF");

        hexBox.setResponder(this::onHexChange);

        addRenderableWidget(hexBox);


        addRenderableWidget(

                Button.builder(Component.literal("创建"),

                                b -> create())

                        .bounds(panelX + 120, panelY + 220, 80, 20)

                        .build()

        );


        addRenderableWidget(

                Button.builder(Component.literal("取消"),

                                b -> onClose())

                        .bounds(panelX + 220, panelY + 220, 80, 20)

                        .build()

        );


        wheelX = panelX + 80;

        wheelY = panelY + 150;


        boxX = panelX + 160;

        boxY = panelY + 95;

    }


    private void create() {

        String name = nameBox.getValue().trim();

        if (name.isEmpty())
            return;


        int rgb = Color.HSBtoRGB(hue, sat, bri);


        PacketDistributor.sendToServer(

                new CreateTeamPacket(name, rgb)

        );


        onClose();

    }


    private void onHexChange(String hex) {

        try {

            int rgb = Color.decode(hex).getRGB() & 0xFFFFFF;

            float[] hsb = Color.RGBtoHSB(

                    (rgb >> 16) & 255,

                    (rgb >> 8) & 255,

                    rgb & 255,

                    null

            );


            hue = hsb[0];

            sat = hsb[1];

            bri = hsb[2];

        }

        catch (Exception ignored) {}

    }



    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float pt) {


        renderBackground(g);


        drawPanel(g);


        drawTexts(g);


        super.render(g, mouseX, mouseY, pt);


        drawHueWheel(g, mouseX, mouseY);


        drawSB(g, mouseX, mouseY);


        drawPreview(g);


    }

    private void renderBackground(GuiGraphics g) {
    }


    private void drawPanel(GuiGraphics g) {

        g.fill(panelX, panelY,

                panelX + 420,

                panelY + 260,

                0xDD1B1B1B);


        g.fill(panelX + 2, panelY + 2,

                panelX + 418,

                panelY + 258,

                0xAA000000);

    }



    private void drawTexts(GuiGraphics g) {

        g.drawCenteredString(

                font,

                "创建队伍",

                width / 2,

                panelY + 8,

                0xffffff);


        g.drawString(

                font,

                "名称",

                panelX + 30,

                panelY + 35,

                0xAAAAAA);


        g.drawString(

                font,

                "HEX",

                panelX + 30,

                panelY + 65,

                0xAAAAAA);


        g.drawString(

                font,

                "颜色",

                panelX + 30,

                panelY + 100,

                0xAAAAAA);

    }



    private void drawHueWheel(GuiGraphics g, int mouseX, int mouseY) {


        for (int i = 0; i < 360; i++) {

            float h = i / 360f;

            int color = Color.HSBtoRGB(h, 1, 1);


            double rad = Math.toRadians(i);


            int px = wheelX + (int)(Math.cos(rad) * wheelR);

            int py = wheelY + (int)(Math.sin(rad) * wheelR);


            g.fill(px - 2, py - 2, px + 2, py + 2, color);

        }


        double rad = hue * Math.PI * 2;


        int px = wheelX + (int)(Math.cos(rad) * wheelR);

        int py = wheelY + (int)(Math.sin(rad) * wheelR);


        g.fill(px - 4, py - 4, px + 4, py + 4, 0xffffffff);



        if (dragHue) {

            double dx = mouseX - wheelX;

            double dy = mouseY - wheelY;


            hue = (float)(Math.atan2(dy, dx) / (Math.PI * 2));


            if (hue < 0)
                hue += 1;


            updateHex();

        }


    }



    private void drawSB(GuiGraphics g, int mouseX, int mouseY) {


        for (int x = 0; x < boxSize; x++) {

            for (int y = 0; y < boxSize; y++) {


                int color = Color.HSBtoRGB(

                        hue,

                        x / (float)boxSize,

                        1 - y / (float)boxSize

                );


                g.fill(

                        boxX + x,

                        boxY + y,

                        boxX + x + 1,

                        boxY + y + 1,

                        color);

            }

        }



        int px = boxX + (int)(sat * boxSize);

        int py = boxY + (int)((1 - bri) * boxSize);


        g.fill(px - 4, py - 4, px + 4, py + 4, 0xffffffff);



        if (dragSB) {

            sat = (mouseX - boxX) / (float)boxSize;

            bri = 1 - (mouseY - boxY) / (float)boxSize;


            sat = clamp(sat);

            bri = clamp(bri);


            updateHex();

        }

    }



    private void drawPreview(GuiGraphics g) {


        int color = Color.HSBtoRGB(hue, sat, bri);


        g.fill(

                panelX + 30,

                panelY + 200,

                panelX + 90,

                panelY + 220,

                color);

    }



    private void updateHex() {

        int rgb = Color.HSBtoRGB(hue, sat, bri) & 0xffffff;

        hexBox.setValue(

                String.format("#%06X", rgb)

        );

    }



    private float clamp(float v) {

        return Math.max(0, Math.min(1, v));

    }



    @Override
    public boolean mouseClicked(double x, double y, int b) {


        if (b == 0) {


            double dx = x - wheelX;

            double dy = y - wheelY;


            double dist = Math.sqrt(dx*dx + dy*dy);


            if (Math.abs(dist - wheelR) < 8) {

                dragHue = true;

                return true;

            }



            if (

                    x >= boxX &&

                            x <= boxX + boxSize &&

                            y >= boxY &&

                            y <= boxY + boxSize

            ) {

                dragSB = true;

                return true;

            }

        }



        return super.mouseClicked(x, y, b);

    }



    @Override
    public boolean mouseReleased(double x, double y, int b) {

        dragHue = false;

        dragSB = false;

        return super.mouseReleased(x, y, b);

    }



    @Override
    public boolean isPauseScreen() {

        return false;

    }

}