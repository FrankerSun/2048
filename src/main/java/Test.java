package main.java;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/***
 * 用途：测试游戏
 * @author F.S.
 * @date 18/05/04
 */
public class Test {

    public static void main(String[] args) {
        JFrame game = new JFrame();
        game.setTitle("2048小游戏(Esc键重新开始)");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(350, 410);
        game.setResizable(false);
        game.add(new Game());
        game.setVisible(true);
        game.setLocationRelativeTo(null);
    }
}
