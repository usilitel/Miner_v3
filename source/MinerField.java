package ru.lesson.lessons.miner.v8.source;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

// игровое поле для игры "Сапер"
// данный класс расширяется внутри класса MinerLogic для реализации логики отрисовки ячеек
public class MinerField  extends JPanel{

    private int BLOCK_SIZE; // размер блока в пикселях
    private String SIGN_OF_FLAG; // символ флага
    private String SIGN_OF_BOMB; // символ бомбы




    public void setBLOCK_SIZE(int BLOCK_SIZE) {
        this.BLOCK_SIZE = BLOCK_SIZE;
    }
/*
    public void setSIGN_OF_FLAG(String SIGN_OF_FLAG) {
        this.SIGN_OF_FLAG = SIGN_OF_FLAG;
    }

    public void setSIGN_OF_BOMB(String SIGN_OF_BOMB) {
        this.SIGN_OF_BOMB = SIGN_OF_BOMB;
    }
*/

    //------------------------
    // методы для отрисовки разных типов ячеек

    public void drawNumber(Graphics g, int x, int y, String string, Color color){ // рисуем цифру (количество мин вокруг)
        g.setColor(color);
        g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
        g.drawString(string, x*BLOCK_SIZE + BLOCK_SIZE/4, (int)Math.floor(y*BLOCK_SIZE+BLOCK_SIZE*0.87));
    }
    public void drawBomb(Graphics g, int x, int y, boolean isBang){ // рисуем мину
        //this.drawNumber(g,x,y,SIGN_OF_BOMB,color);
        if(isBang){drawImageFromFile(g,x,y,"../pics/MineExplosion.png");}
        else{drawImageFromFile(g,x,y,"../pics/Mine.png");}
    }

    public void drawFlag(Graphics g, int x, int y, boolean isCorrectFlag){ // рисуем метку
        if(isCorrectFlag){ // рисуем правильный флаг
            drawClosedBlock(g,x,y);
            drawImageFromFile(g,x,y,"../pics/Flag.png");
        }
        else{ // рисуем неправильно установленный флаг
            drawImageFromFile(g,x,y,"../pics/WrongFlag.png");
        }
    }

    public void drawEmptyBlock(Graphics g, int x, int y){ // рисуем пустой открытый блок
        g.setColor(Color.lightGray);
        g.drawRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
    }
    public void drawClosedBlock(Graphics g, int x, int y){ // рисуем закрытый блок
        g.setColor(Color.lightGray);
        g.fill3DRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
    }
    public void drawPressedBlock(Graphics g, int x, int y){ // рисуем закрытый блок
        g.setColor(Color.lightGray);
        g.fill3DRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, false);
    }

    public void drawImageFromFile(Graphics g, int x, int y, String fileName){ // рисуем картинку
        BufferedImage bufImg = null;
        try
        {
            bufImg = ImageIO.read(getClass().getResource(fileName));
        } catch (IOException e){e.printStackTrace();}
        if(bufImg != null) {
            g.drawImage(bufImg, x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
        }
    }

    //------------------------

}
