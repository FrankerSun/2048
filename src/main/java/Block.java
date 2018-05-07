package main.java;

import java.awt.Color;

/***
 * 用途：2048方块
 * 构成：数值+背景色函数
 * @author F.S.
 * @date 18/05/04
 */
class Block {

    int value;

    Block() {
        this(0);
    }

    Block(int num) {
        value = num;
    }

    boolean isEmpty() {
        return value == 0;
    }

    Color getForeground() {
        return value < 16 ? new Color(0x776e65) : new Color(0xf9f6f2);
    }

    Color getBackground() {
        switch (value) {
            case 2:
                return new Color(0xeee4da);
            case 4:
                return new Color(0xede0c8);
            case 8:
                return new Color(0xf2b179);
            case 16:
                return new Color(0xf59563);
            case 32:
                return new Color(0xf67c5f);
            case 64:
                return new Color(0xf65e3b);
            case 128:
                return new Color(0xedcf72);
            case 256:
                return new Color(0xedcc61);
            case 512:
                return new Color(0xedc850);
            case 1024:
                return new Color(0xedc53f);
            case 2048:
                return new Color(0xedc22e);
            default:
                break;
        }
        return new Color(0xcdc1b4);
    }

}
