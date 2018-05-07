package main.java;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.JPanel;

/***
 * 用途：2048
 * @author F.S.
 * @date 18/05/04
 */
public class Game extends JPanel {

    private static final Color BG_COLOR = new Color(0xFFFFFF);
    private static final String FONT_NAME = "微软雅黑";
    private static final int TILE_SIZE = 64;
    private static final int TILES_MARGIN = 16;

    private static final int BLOCK_NUM = 4;
    private static final int DEGREE_ANGLE_90 = 90;
    private static final int DEGREE_ANGLE_180 = 180;
    private static final int DEGREE_ANGLE_270 = 270;

    private Block[] blocks;
    private int score = 0;
    private boolean isWin = false;
    private boolean isLose = false;

    public Game() {
        setPreferredSize(new Dimension(340, 400));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            /**
             * 监听按键、每次按键之后判断是否结束游戏
             */
            @Override
            public void keyPressed(KeyEvent e) {
                if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
                    resetGame();
                }
                if (!canMove()) {
                    isLose = true;
                }
                if (!(isWin || isLose)) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            left();
                            break;
                        case KeyEvent.VK_RIGHT:
                            right();
                            break;
                        case KeyEvent.VK_DOWN:
                            down();
                            break;
                        case KeyEvent.VK_UP:
                            up();
                            break;
                        default:
                            break;
                    }
                }
                if (!isWin && !canMove()) {
                    isLose = true;
                }
                repaint();
            }
        });
        resetGame();
    }

    //region 主要操作：上下左右   先旋转一定度数x(90/180/270)，然后调用同一个方法(left)，再旋转360-x(270/180/90)。
    /**
     * 上
     */
    private void up() {
        blocks = rotate(DEGREE_ANGLE_270);
        left();
        blocks = rotate(DEGREE_ANGLE_90);
    }

    /**
     * 下
     */
    private void down() {
        blocks = rotate(DEGREE_ANGLE_90);
        left();
        blocks = rotate(DEGREE_ANGLE_270);
    }

    /**
     * 左
     */
    private void left() {
        boolean needAddTile = false;
        for (int i = 0; i < BLOCK_NUM; i++) {
            Block[] line = getLine(i);
            Block[] merged = mergeLine(moveLine(line));
            setLine(i, merged);
            if (!needAddTile && !compare(line, merged)) {
                needAddTile = true;
            }
        }
        if (needAddTile) {
            addNewBlockNum();
        }
    }

    /**
     * 右
     */
    private void right() {
        blocks = rotate(DEGREE_ANGLE_180);
        left();
        blocks = rotate(DEGREE_ANGLE_180);
    }


    private Block[] rotate(int degree) {
        Block[] newBlocks = new Block[BLOCK_NUM * BLOCK_NUM];
        int offsetX = BLOCK_NUM -1, offsetY = BLOCK_NUM -1;
        if (degree == DEGREE_ANGLE_90) {
            offsetY = 0;
        } else if (degree == DEGREE_ANGLE_270) {
            offsetX = 0;
        }
        double rad = Math.toRadians(degree);
        int cos = (int) Math.cos(rad);
        int sin = (int) Math.sin(rad);
        for (int x = 0; x < BLOCK_NUM; x++) {
            for (int y = 0; y < BLOCK_NUM; y++) {
                int newX = (x * cos) - (y * sin) + offsetX;
                int newY = (x * sin) + (y * cos) + offsetY;
                newBlocks[(newX) + (newY) * BLOCK_NUM] = tileAt(x, y);
            }
        }
        return newBlocks;
    }

    //endregion


    private Block tileAt(int x, int y) {
        return blocks[x + y * BLOCK_NUM];
    }


    /**
     * 添加一个新的Block中的数字
     */
    private void addNewBlockNum() {
        List<Block> list = availableSpace();
        if (!availableSpace().isEmpty()) {
            int index = (int) (Math.random() * list.size()) % list.size();
            Block emptyTime = list.get(index);
            emptyTime.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    private List<Block> availableSpace() {
        final List<Block> list = new ArrayList<>(BLOCK_NUM * BLOCK_NUM);
        for (Block t : blocks) {
            if (t.isEmpty()) {
                list.add(t);
            }
        }
        return list;
    }

    private boolean isFull() {
        return availableSpace().size() == 0;
    }

    private boolean canMove() {
        if (!isFull()) {
            return true;
        }
        for (int x = 0; x < BLOCK_NUM; x++) {
            for (int y = 0; y < BLOCK_NUM; y++) {
                Block t = tileAt(x, y);
                boolean f =  (x < BLOCK_NUM -1 && t.value == tileAt(x + 1, y).value)
                    || ((y < BLOCK_NUM - 1) && t.value == tileAt(x, y + 1).value);
                if (f) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compare(Block[] line1, Block[] line2) {
        if (line1 == line2) {
            return true;
        } else if (line1.length != line2.length) {
            return false;
        }
        for (int i = 0; i < line1.length; i++) {
            if (line1[i].value != line2[i].value) {
                return false;
            }
        }
        return true;
    }

    private Block[] moveLine(Block[] oldLine) {
        LinkedList<Block> l = new LinkedList<>();
        for (int i = 0; i < BLOCK_NUM; i++) {
            if (!oldLine[i].isEmpty()) {
                l.addLast(oldLine[i]);
            }
        }
        if (l.size() == 0) {
            return oldLine;
        } else {
            Block[] newLine = new Block[BLOCK_NUM];
            ensureSize(l);
            for (int i = 0; i < BLOCK_NUM; i++) {
                newLine[i] = l.removeFirst();
            }
            return newLine;
        }
    }

    private Block[] mergeLine(Block[] oldLine) {
        LinkedList<Block> list = new LinkedList<>();
        for (int i = 0; i < BLOCK_NUM && !oldLine[i].isEmpty(); i++) {
            int num = oldLine[i].value;
            if (i < BLOCK_NUM - 1 && oldLine[i].value == oldLine[i + 1].value) {
                num *= 2;
                score += num;
                int ourTarget = 2048;
                if (num == ourTarget) {
                    isWin = true;
                }
                i++;
            }
            list.add(new Block(num));
        }
        if (list.size() == 0) {
            return oldLine;
        } else {
            ensureSize(list);
            return list.toArray(new Block[BLOCK_NUM]);
        }
    }

    private static void ensureSize(List<Block> l) {
        while (l.size() != BLOCK_NUM) {
            l.add(new Block());
        }
    }

    private Block[] getLine(int index) {
        Block[] result = new Block[BLOCK_NUM];
        for (int i = 0; i < BLOCK_NUM; i++) {
            result[i] = tileAt(i, index);
        }
        return result;
    }

    private void setLine(int index, Block[] re) {
        System.arraycopy(re, 0, blocks, index * BLOCK_NUM, BLOCK_NUM);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int y = 0; y < BLOCK_NUM; y++) {
            for (int x = 0; x < BLOCK_NUM; x++) {
                drawTile(g, blocks[x + y * BLOCK_NUM], x, y);
            }
        }
    }

    private void drawTile(Graphics g2, Block block, int x, int y) {
        Graphics2D g = ((Graphics2D) g2);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        int value = block.value;
        int xOffset = offsetCoors(x);
        int yOffset = offsetCoors(y);
        g.setColor(block.getBackground());
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);
        g.setColor(block.getForeground());
        final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
        final Font font = new Font(FONT_NAME, Font.BOLD, size);
        g.setFont(font);
        String s = String.valueOf(value);
        final FontMetrics fm = getFontMetrics(font);
        final int w = fm.stringWidth(s);
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];
        if (value != 0) {
            g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);
        }
        if (isWin || isLose) {
            g.setColor(new Color(255, 255, 255, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(78, 139, 202));
            g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
            if (isWin) {
                g.drawString("恭喜，真溜!", 68, 150);
            }
            if (isLose) {
                g.drawString("游戏结束!", 50, 130);
                g.drawString("你输了~", 64, 200);
            }
            if (isWin || isLose) {
                g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
                g.setColor(new Color(128, 128, 128, 128));
                g.drawString("Press ESC to play again", 80, getHeight() - 40);
            }
        }
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        g.drawString("得分: " + score, 200, 365);
    }

    private static int offsetCoors(int arg) {
        return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
    }


    /**
     * 重新开一局
     */
    private void resetGame() {
        score = 0;
        isWin = false;
        isLose = false;
        blocks = new Block[BLOCK_NUM * BLOCK_NUM];
        IntStream.range(0, blocks.length).forEach(i -> blocks[i] = new Block());
        addNewBlockNum();
        addNewBlockNum();
    }
}
