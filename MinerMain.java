package ru.lesson.lessons.miner.v8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.Timer;



// Форма для игры "Сапер"
 public class MinerMain extends JFrame {

    final String TITLE_OF_PROGRAM = "Miner";
    final int START_LOCATION = 200;
    final int FIELD_DX = 6; // добавка в пикселях по X (чтобы все умещалось в форму)
    final int FIELD_DY = 29+100; // + 16; // добавка в пикселях по Y

    MinerLogic minerLogic;
    MinerField minerField;

    JLabel labelTimer;
    int countSeconds;
    Timer timer;
    JLabel labelGameState;
    JLabel labelCountMinesRest;
    JButton btnGameStart;
    JPanel timerPanel;



    public static void main(String[] args){
        new MinerMain();
    }


    public MinerMain(){

        minerLogic = new MinerLogic(this);
        minerField = minerLogic.minerField;

        this.setTitle(TITLE_OF_PROGRAM);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(START_LOCATION, START_LOCATION, minerLogic.FIELD_SIZE_X * minerLogic.BLOCK_SIZE + FIELD_DX, minerLogic.FIELD_SIZE_Y * minerLogic.BLOCK_SIZE + FIELD_DY);
        this.setResizable(false);
        this.setLayout(null);

        // поле игры
        minerField.setBounds(0, 0, minerLogic.FIELD_SIZE_X * minerLogic.BLOCK_SIZE + FIELD_DX, minerLogic.FIELD_SIZE_Y * minerLogic.BLOCK_SIZE+1);

        // дополнительные визуальные элементы
        timerPanel = new JPanel();
        timerPanel.setLayout(null);
        timerPanel.setBounds(0, minerLogic.FIELD_SIZE_Y * minerLogic.BLOCK_SIZE + 1, minerLogic.FIELD_SIZE_X * minerLogic.BLOCK_SIZE + FIELD_DX, FIELD_DY);
        labelCountMinesRest = new JLabel();
        labelTimer = new JLabel();
        labelGameState = new JLabel();
        btnGameStart = new JButton();
        ActionListener btnGameStartListener = new BtnGameStartListener();
        btnGameStart.addActionListener(btnGameStartListener);
        labelCountMinesRest.setBounds(10, 10, 200, 20);
        labelTimer.setBounds(10, 30, 200, 20);
        labelGameState.setBounds(10, 50, 200, 20);
        btnGameStart.setBounds(10, 70, minerLogic.FIELD_SIZE_X * minerLogic.BLOCK_SIZE  - 20, 20);

        timerPanel.add(labelTimer);
        timerPanel.add(labelGameState);
        timerPanel.add(labelCountMinesRest);
        timerPanel.add(btnGameStart);

        timer = new Timer(1000, new TimerListener());

        ProcessGameStateChange();

        this.add(minerField);
        this.add(timerPanel);
        this.setVisible(true);
    }

    public void ProcessCountMinesChange() { //обрабатываем изменение количества помеченных мин
        labelCountMinesRest.setText("Осталось бомб: " + Integer.toString(minerLogic.COUNT_OF_MINES - minerLogic.countMarkedMines));
    }

    public void ProcessGameStateChange(){ //обрабатываем изменение статуса игры
        if(minerLogic!=null) {
            System.out.println("GameStateChange: " + minerLogic.getGameState());
            ProcessCountMinesChange();
            switch (minerLogic.getGameState()) {
                case MinerLogic.GAME_STATE_STOP:
                    labelTimer.setText("Время: 00:00");
                    labelGameState.setText("Игра остановлена");
                    btnGameStart.setText("Начать игру");
                    timer.stop();
                    break;
                case MinerLogic.GAME_STATE_STARTED:
                    labelGameState.setText("Игра началась");
                    btnGameStart.setText("Перезапустить игру");
                    countSeconds = 0;
                    timer.start();
                    break;
                case MinerLogic.GAME_STATE_FINISHED_SUCCESS:
                    labelGameState.setText("Вы выиграли!");
                    btnGameStart.setText("Начать игру");
                    timer.stop();
                    break;
                case MinerLogic.GAME_STATE_FINISHED_FAIL:
                    labelGameState.setText("Вы проиграли!");
                    btnGameStart.setText("Начать игру");
                    timer.stop();
                    break;
            }
        }
        this.repaint();
    }

    public void InitNewGame(){ // готовим новое игровое поле
        this.remove(minerField);
        minerLogic=null;
        minerLogic = new MinerLogic(this);
        minerField = minerLogic.minerField;
        minerField.setBounds(0, 0, minerLogic.FIELD_SIZE_X * minerLogic.BLOCK_SIZE + FIELD_DX, minerLogic.FIELD_SIZE_Y * minerLogic.BLOCK_SIZE+1);
        this.add(minerField);
    }


    public class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            labelTimer.setText("Время: " + (String.format("%02d:%02d", countSeconds / 60, countSeconds % 60)));
            countSeconds++;
        }
    }

    public class BtnGameStartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            InitNewGame();
            minerLogic.setGameState(MinerLogic.GAME_STATE_STARTED);
        }
    }

}
