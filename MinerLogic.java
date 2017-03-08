package ru.lesson.lessons.miner.v8;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import javax.swing.SwingUtilities;

// логика для игры "Сапер"
 public class MinerLogic {

    final int BLOCK_SIZE = 30; // размер блока в пикселях
    //final String SIGN_OF_FLAG = "f"; // символ флага
    //final String SIGN_OF_BOMB = "B"; // символ бомбы

    final int FIELD_SIZE_X = 10; // размер поля (количество ячеек по X)
    final int FIELD_SIZE_Y = 10; // размер поля (количество ячеек по Y)
    final int COUNT_OF_MINES = 5; // количество мин на поле
    final int[] COLOURS_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0, 0x0, 0x0, 0x0}; // цвета цифр




    MinerFieldPaintable minerField;
    MinerCell[][] arrayMinerCells; // массив ячеек

    // константы, описывающие возможное состояние игры (поле gameState)
    final static int GAME_STATE_STOP = 0; // игра еще не началась
    final static int GAME_STATE_STARTED = 1; // игра продолжается
    final static int GAME_STATE_FINISHED_SUCCESS = 2; // игра завершилась с успехом
    final static int GAME_STATE_FINISHED_FAIL = 3; // игра завершилась с проигрышем

    // константы, описывающие что нужно сделать с ячейкой (ProcessCell(...,int action))
    final static int ACTION_OPEN = 0; // открыть ячейку
    final static int ACTION_MARK_UNMARK = 1; // пометить ячейку/снять пометку
    final static int ACTION_PRESS = 2; // пометить ячейку/снять пометку

    final static int MOUSE_BUTTON_LEFT = 1;
    final static int MOUSE_BUTTON_RIGHT = 3;



    Random random = new Random();
    int countOpenedCells;
    int countMarkedMines;
    private int gameState; // текущее состояние игры (GAME_STATE_STOP, GAME_STATE_STARTED, GAME_STATE_FINISHED_SUCCESS, GAME_STATE_FINISHED_FAIL)
    int bangX, bangY; // координаты взрыва

    MinerMain parentFrame; // форма, на которой расположено поле


    public MinerLogic(MinerMain parentFrame){
        this.parentFrame=parentFrame;
        minerField = new MinerFieldPaintable();
        minerField.setBLOCK_SIZE(BLOCK_SIZE);
        //minerField.setSIGN_OF_BOMB(SIGN_OF_BOMB);
        //minerField.setSIGN_OF_FLAG(SIGN_OF_FLAG);

        this.InitGame();


        MouseMotionEvents mouseMotionEvents = new MouseMotionEvents();
        minerField.addMouseListener(mouseMotionEvents);
        minerField.addMouseMotionListener(mouseMotionEvents);
    }





    public void InitGame(){
        setGameState(GAME_STATE_STOP);
        countOpenedCells=0;
        arrayMinerCells = new MinerCell[FIELD_SIZE_X][FIELD_SIZE_Y];
        this.GenerateField();
        minerField.repaint();
    }





    // класс для обработки клика мышью
    class MouseMotionEvents implements MouseListener,MouseMotionListener {

        //@Override
        public void mouseEntered(MouseEvent e) {
            System.out.println("mouseEntered");
        }
        //@Override
        public void mouseMoved(MouseEvent e) {
            //System.out.println("mouseMoved");
        }
        //@Override
        public void mouseExited(MouseEvent e) {
            System.out.println("mouseExited");
        }
        //@Override
        public void mouseDragged(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)){
                //System.out.println("mouseDragged_MOUSE_BUTTON_LEFT");
                int x = e.getX() / BLOCK_SIZE; // вычисляем координаты ячейки
                int y = e.getY() / BLOCK_SIZE;
                ProcessCell(x, y, MinerLogic.ACTION_PRESS);
            }

        }
        //@Override
        public void mousePressed(MouseEvent e) {
            System.out.println("mousePressed");
            if(e.getButton()==MOUSE_BUTTON_LEFT){
                //System.out.println("mousePressed");
                int x = e.getX() / BLOCK_SIZE; // вычисляем координаты ячейки
                int y = e.getY() / BLOCK_SIZE;
                ProcessCell(x, y, MinerLogic.ACTION_PRESS);
            }
        }
        //@Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("mouseClicked");
        }
        //@Override
        public void mouseReleased(MouseEvent e) {
            //System.out.println("mouseReleased");
            System.out.println("mouseReleased " + SwingUtilities.isLeftMouseButton(e) + " " + SwingUtilities.isRightMouseButton(e) + " " + e.getButton());
            //super.mouseReleased(e);
            int x = e.getX() / BLOCK_SIZE; // вычисляем координаты ячейки
            int y = e.getY() / BLOCK_SIZE;

            // запускаем обработку
            switch (e.getButton()) {
                case MOUSE_BUTTON_LEFT:
                    ProcessCell(x, y, MinerLogic.ACTION_OPEN);
                    break;
                case MOUSE_BUTTON_RIGHT:
                    ProcessCell(x, y, MinerLogic.ACTION_MARK_UNMARK);
                    break;
            }
        }
    }

    // обрабатываем действие пользователя
    public void ProcessCell(int x, int y, int action) {
        MinerCell cell = arrayMinerCells[x][y];
        if(getGameState()==GAME_STATE_STOP){ setGameState(GAME_STATE_STARTED); }
        if(getGameState()==GAME_STATE_STARTED){
            switch(action) {
                case ACTION_OPEN:
                    //if(cell.getCellState()==MinerCell.CELL_STATE_CLOSED){ // если ячейка не помечена - то открываем ее
                    if(cell.getCellState()==MinerCell.CELL_STATE_PRESSED){ // если ячейка не помечена - то открываем ее
                        OpenCell(x, y);
                        if (cell.getIsMine()){ // напоролись на мину - заканчиваем игру
                            setGameState(GAME_STATE_FINISHED_FAIL);
                            System.out.println("Game over.");
                            bangX=x;
                            bangY=y;
                        }
                        else{ // открыли свободную ячейку - проверяем закончилась ли игра
                            CheckGameOver();
                        }
                    }
                    break;
                case ACTION_MARK_UNMARK: // помечаем/снимаем метку
                    System.out.println(""+cell.getCellState());
                    switch(cell.getCellState()) {
                        case MinerCell.CELL_STATE_CLOSED:
                            cell.setCellState(MinerCell.CELL_STATE_MARKED_AS_MINE);
                            setCountMarkedMines(getCountMarkedMines()+1);
                            break;
                        case MinerCell.CELL_STATE_MARKED_AS_MINE:
                            cell.setCellState(MinerCell.CELL_STATE_CLOSED);
                            setCountMarkedMines(getCountMarkedMines()-1);
                            break;
                    }
                    break;
                case ACTION_PRESS: // держим ячейку нажатой
                    for(int cx=0;cx<FIELD_SIZE_X;cx++)
                        for(int cy=0;cy<FIELD_SIZE_Y;cy++) { // если сдвинули не отпуская кнопку мыши
                            if(!(cx==x && cy==y) && (arrayMinerCells[cx][cy].getCellState()==MinerCell.CELL_STATE_PRESSED)){
                                arrayMinerCells[cx][cy].setCellState(MinerCell.CELL_STATE_CLOSED);
                            }
                        }



                    if(cell.getCellState()==MinerCell.CELL_STATE_CLOSED){ // если ячейка не помечена - то рисуем нажатый блок
                        cell.setCellState(MinerCell.CELL_STATE_PRESSED);
                    }
                    break;
            }
            minerField.repaint();
        }
    }

    // открываем ячейку и соседние (если вокруг нет мин)
    public void OpenCell(int x, int y){
        MinerCell cell = arrayMinerCells[x][y];
        //if(cell.getCellState()==MinerCell.CELL_STATE_CLOSED){ // действуем только если ячейка закрыта
        if(cell.getCellState()==MinerCell.CELL_STATE_CLOSED || cell.getCellState()==MinerCell.CELL_STATE_PRESSED){ // действуем только если ячейка закрыта
            cell.setCellState(MinerCell.CELL_STATE_OPEN);
            countOpenedCells++;

            // открываем соседние ячейки
            if(cell.getCntMinesNear()==0) {
                OpenCellAround(x, y);
            }
        }
    }

    // идем вокруг текущей ячейки и рекурсивно открываем соседние ячейки
    public void OpenCellAround(int x, int y){
        for(int dx=-1;dx<2;dx++)
            for(int dy=-1;dy<2;dy++) {
                // 1) проверка на существование ячейки.  2) уже открытые ячейки не трогаем.
                if(((x+dx)>=0 && (x+dx)<=FIELD_SIZE_X-1 && (y+dy)>=0 && (y+dy)<=FIELD_SIZE_Y-1) && (arrayMinerCells[x+dx][y+dy].getCellState()!=MinerCell.CELL_STATE_OPEN)){
                    OpenCell(x+dx, y+dy);
                }
            }
    }

    // проверка на успешное завершение игры
    public void CheckGameOver(){
        if(countOpenedCells==(FIELD_SIZE_X * FIELD_SIZE_Y - COUNT_OF_MINES)){
            setGameState(GAME_STATE_FINISHED_SUCCESS);
            System.out.println("Game successfully finished.");
        }
    }

    // генерируем поле, расставляем мины, заполняем поле CntMinesNear
    public void GenerateField(){
        // заполняем поле ячейками
        for(int x=0;x<FIELD_SIZE_X;x++)
            for(int y=0;y<FIELD_SIZE_Y;y++) {
                arrayMinerCells[x][y] = new MinerCell();
            }

        // расставляем мины
        int cntMines=0;
        while(cntMines<COUNT_OF_MINES){
            int x=random.nextInt(FIELD_SIZE_X);
            int y=random.nextInt(FIELD_SIZE_Y);
            if(!arrayMinerCells[x][y].getIsMine()){
                arrayMinerCells[x][y].setIsMine(true);
                cntMines++;
            }
        }

        // считаем количество мин вокруг ячейки
        for(int x=0;x<FIELD_SIZE_X;x++)
            for(int y=0;y<FIELD_SIZE_Y;y++) {
                for(int dx=-1;dx<2;dx++) {
                    for (int dy = -1; dy < 2; dy++) {
                        if ((x + dx) >= 0 && (x + dx) <= FIELD_SIZE_X - 1 && (y + dy) >= 0 && (y + dy) <= FIELD_SIZE_Y - 1) { // проверка на существование ячейки
                            if (arrayMinerCells[x + dx][y + dy].getIsMine()) {
                                arrayMinerCells[x][y].setCntMinesNear(arrayMinerCells[x][y].getCntMinesNear() + 1);
                            }
                        }
                    }
                }
            }
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
        parentFrame.ProcessGameStateChange();
    }

    public int getCountMarkedMines() {
        return countMarkedMines;
    }

    public void setCountMarkedMines(int countMarkedMines) {
        this.countMarkedMines = countMarkedMines;
        parentFrame.ProcessCountMinesChange();
    }










    // Расширение класса MinerField (игровое поле для игры "Сапер") для реализации логики отрисовки ячеек.
    // Внутри класса MinerField эту логику не стал реализовывать, чтобы вся логика была внутри класса MinerLogic.
    class MinerFieldPaintable extends MinerField{

        // перерисовываем всё поле
        public void paint(Graphics g) {
            super.paint(g);
            for (int x = 0; x < FIELD_SIZE_X; x++){ // перерисовываем все ячейки
                for (int y = 0; y < FIELD_SIZE_Y; y++) {
                    drawCell(g, x, y);
                }
            }
        }


        // рисуем отдельную ячейку
        public void drawCell(Graphics g, int x, int y){

            MinerCell cell = arrayMinerCells[x][y];
            Color color = Color.black;
            int cntMinesNear = 0;
            String cellString = "";

            minerField.drawEmptyBlock(g, x, y);

            boolean isBang;
            if ((gameState==GAME_STATE_FINISHED_SUCCESS || gameState==GAME_STATE_FINISHED_FAIL) && (cell.getIsMine())) { // если игра закончена И мина - открываем ее
                if ((x==bangX && y==bangY) && (gameState==GAME_STATE_FINISHED_FAIL)) {isBang = true;}  // цвет бомбы
                else {isBang = false;}
                minerField.drawBomb(g, x, y, isBang);
                return;
            }
            if ((gameState==GAME_STATE_FINISHED_SUCCESS || gameState==GAME_STATE_FINISHED_FAIL) && !(cell.getIsMine()) && (cell.getCellState()==MinerCell.CELL_STATE_MARKED_AS_MINE)) { // если игра закончена И стоит неверный флаг - рисуем ошибочный флаг
                minerField.drawFlag(g, x, y, false);
                return;
            }
            //else{


                switch(cell.getCellState()) { // иначе смотрим на состояние ячейки
                    case MinerCell.CELL_STATE_CLOSED:
                        minerField.drawClosedBlock(g, x, y);
                        break;
                    case MinerCell.CELL_STATE_OPEN:
                        cntMinesNear = cell.getCntMinesNear();
                        if(cntMinesNear>0){
                            cellString = Integer.toString(cntMinesNear);
                            color = new Color(COLOURS_OF_NUMBERS[cntMinesNear-1]);
                            minerField.drawNumber(g, x, y, cellString, color);
                        }
                        break;
                    case MinerCell.CELL_STATE_MARKED_AS_MINE:
                        minerField.drawFlag(g,x,y,true);
                        break;
                    case MinerCell.CELL_STATE_PRESSED:
                        minerField.drawPressedBlock(g,x,y);
                        break;
                }
            //}
        }

    }

}

