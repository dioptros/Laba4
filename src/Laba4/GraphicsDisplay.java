package Laba4;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.geom.*;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
    // Список координат точек для построения графика
    private Double[][] graphicsData;
    // Флаговые переменные, задающие правила отображения графика
    private boolean showAxis= true;
    private boolean showMarkers= true;
    private boolean showGrid = true;
    // Границы диапазона пространства, подлежащего отображению
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    // Используемый масштаб отображения
    private double scale;
    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke gridStroke;
    // Шрифт отображения подписей к осям координат
    private Font axisFont;


    public GraphicsDisplay(){
        // Цвет заднего фона области отображения -белый
        setBackground(Color.WHITE);
        // Сконструировать необходимые объекты, используемые в рисовании
        // Перо для рисования графика
        graphicsStroke= new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {4,4}, 0.0f);
        // Перо для рисования осей координат
        axisStroke= new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // Перо для рисования контуров маркеров
        markerStroke= new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, null, 0.0f);
        gridStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
        // Шрифт для подписей осей координат
        axisFont= new Font("Serif", Font.BOLD, 36);
    }
    // Метод вызывается из обработчикаэлемента меню  "Открыть файл с графиком"
    // главного окна приложения в случае успешной загрузки данных
    public void showGraphics(Double[][] graphicsData) {
        // Сохранить массив точек во внутреннем поле класса
        this.graphicsData= graphicsData;
        // Запросить перерисовку компонента(неявно вызвать paintComponent())
        repaint();
    }
    // Методы-модификаторы для изменения параметров отображения графика
    // Изменение любого параметра приводит к перерисовке области
    public void setShowAxis(boolean showAxis) {
        this.showAxis= showAxis;
        repaint();
    }
    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers= showMarkers;
        repaint();
    }
    public void setShowGrid(boolean showGrid){
        this.showGrid = showGrid;
        repaint();
    }
    protected Point2D.Double xyToPoint(double x, double y){
        // Вычисляем смещение X от самой левой точки (minX)
        double deltaX= x-minX;
        // Вычисляем смещение Y от точки верхней точки (maxY)
        double deltaY = maxY-y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY){
        // Инициализировать новый экземпляр точки
        Point2D.Double dest = new Point2D.Double();
        // Задать еѐ координаты как координаты существующей точки +
        // заданные смещения
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
    protected void paintGraphics(Graphics2D canvas){
        // Выбрать линию для рисования графика
        canvas.setStroke(graphicsStroke);
        // Выбрать цвет линии
        canvas.setColor(Color.RED);
        /* Будем рисовать линию графика как путь, состоящий из множества сегментов (GeneralPath).
        Начало пути устанавливается в первую точку графика, после чего прямой соединяется соследующими точками*/
        GeneralPath graphics = new GeneralPath();
        for(int i=0; i<graphicsData.length; i++) {
            // Преобразовать значения (x,y) в точку на экране poinmt
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            if(i>0) {
                // Не первая итерация –вести линию в точку point
                graphics.lineTo(point.getX(), point.getY());
            }
            else {
                // Первая итерация -установить начало пути в точку point
                graphics.moveTo(point.getX(), point.getY());
            }

        }
        // Отобразить график
        canvas.draw(graphics);
    }
    protected void paintGrid(Graphics2D canvas){
        canvas.setStroke(gridStroke);
        canvas.setColor(Color.GRAY);
        GeneralPath path = new GeneralPath();
        double shagX = getSize().getWidth()/10;
        double shagY = getSize().getHeight()/10;

        for(int i = 0;i < 10;i++) {

            canvas.drawLine(0, (int)(i*shagY), (int)getSize().getWidth(), (int)(i*shagY)); // Горизонтали
            canvas.drawLine((int)(i*shagX), 0,(int)(i*shagX),(int)getSize().getHeight() ); // Вертикали

        }



    }

    protected void paintAxis(Graphics2D canvas) {
        // Шаг 1 –установить необходимые настройки рисования
        // Установить особое начертание для осей
        canvas.setStroke(axisStroke);
        // Оси рисуются чѐрным цветом
        canvas.setColor(Color.BLACK);
        // Стрелки заливаются чѐрным цветом
        canvas.setPaint(Color.BLACK);
        // Подписи к координатным осям делаются специальным шрифтом
        canvas.setFont(axisFont);
        // Создать объект контекста отображения текста -для получения
        // характеристик устройства (экрана)
        FontRenderContext context= canvas.getFontRenderContext();
        // Шаг 2 -Определить, должна ли быть видна ось Y на графике



        if(minX<=0.0 && maxX>=0.0){
            // Она видна, если левая граница показываемой области minX<=0.0,
            // а правая (maxX) >= 0.0
            // Шаг 2а -осьY-это линия между точками (0, maxY) и (0, minY)
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            // Шаг 2б -Стрелка оси Y
            GeneralPath arrow = new GeneralPath();
            // Установить начальную точку ломаной точно на верхний конец оси Y
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            // Вести левый "скат" стрелки в точку с относительными
            // координатами (5,20)
            arrow.lineTo(arrow.getCurrentPoint().getX()+5, arrow.getCurrentPoint().getY()+20);
            // Вести нижнюю часть стрелки в точку с относительными // координатами (-10, 0)
            arrow.lineTo(arrow.getCurrentPoint().getX()-10, arrow.getCurrentPoint().getY());
            // Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow);// Закрасить стрелку
            // Шаг 2в -Нарисовать подпись к оси Y// Определить, сколько места понадобится для надписи “y”
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            // Вывести надпись в точке с вычисленными координатами
            canvas.drawString("y", (float)labelPos.getX() + 10, (float)(labelPos.getY() -bounds.getY()));
        }
        //Шаг 3 -Определить, должна ли быть видна ось Xна графике
        if(minY<=0.0 && maxY>=0.0) {
            // Она видна, если верхняя граница показываемой областиmax)>=0.0,
            // а нижняя (minY) <= 0.0// Шаг 3а -осьX-это линия между точками (minX, 0) и (maxX, 0)
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
            // Шаг 3б -Стрелка оси X
            GeneralPath arrow = new GeneralPath();
            // Установить начальную точку ломаной точно на правый конец оси X
            Point2D.Double lineEnd = xyToPoint(maxX, 0); arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            // Вести верхний "скат" стрелки в точку с относительными
            // координатами (-20,-5)
            arrow.lineTo(arrow.getCurrentPoint().getX()-20, arrow.getCurrentPoint().getY()-5);
            // Вести левую часть стрелки в точку
            // с относительными координатами (0, 10)
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY()+10);
            // Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow);// Закрасить стрелку
            // Шаг 3в -Нарисовать подпись к оси X
            // Определить, сколько места понадобится для надписи “x”
            Rectangle2D bounds= axisFont.getStringBounds("x", context);
            Point2D.Double labelPos= xyToPoint(maxX, 0);
            // Вывести надпись в точке с вычисленными координатами
            canvas.drawString("x",(float)(labelPos.getX()-bounds.getWidth()-10), (float)(labelPos.getY() + bounds.getY()));


        }
        Rectangle2D bounds = axisFont.getStringBounds("0", context);
        Point2D.Double labelPos = xyToPoint(0,0);
        canvas.drawString("0",(float)(labelPos.getX()-bounds.getX()),(float)(labelPos.getY()-bounds.getY()));
    }
    protected void paintMarkers(Graphics2D canvas){



        for(Double[] point: graphicsData){
            boolean flag = true;

            Double temp = Math.abs(point[1]); // Класс Double
            String str = temp.toString();
            str.replace(".","");
            str.replace(",","");
            int i = 1;
            while (i < str.length()) { // formatter
                if (str.charAt(i) < str.charAt(i-1)) {
                    flag = false;
                    break;
                }

            }
            if (!flag) {
                canvas.setColor(Color.RED);
                canvas.setPaint(Color.RED);
            }
            else {
                canvas.setColor(Color.BLUE);
                canvas.setPaint(Color.BLUE);
            }
            canvas.setStroke(markerStroke);
            GeneralPath path = new GeneralPath();
            // Центр -в точке (x,y)
            Point2D.Double center = xyToPoint(point[0], point[1]);
            // Угол прямоугольника -отстоит на расстоянии (3,3)
            canvas.draw(new Line2D.Double(shiftPoint(center, -11, 0), shiftPoint(center, 11, 0)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, 11), shiftPoint(center, 0, -11)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 11, 11), shiftPoint(center, -11, -11)));
            canvas.draw(new Line2D.Double(shiftPoint(center, -11, 11), shiftPoint(center, 11, -11)));
            // Point2D.Double corner = shiftPoint(center, 3, 3);

        }
    }
    public void paintComponent(Graphics g){
        /* Шаг 1 -Вызвать метод предка для заливки области цветом заднего фона*
        Эта функциональность -единственное, что осталось в наследство от *
        paintComponent класса JPanel*/
        super.paintComponent(g);
        // Шаг 2 -Если данные графика не загружены (при показе компонента при
        // запуске программы) -ничего не делать
        if(graphicsData==null|| graphicsData.length==0){
            return;
        }
        // Шаг 3 -Определить начальные границы области отображения
        // Еѐ верхний левый угол -(minX, maxY), правый нижний -(maxX, minY)
        minX= graphicsData[0][0];

        maxX= graphicsData[graphicsData.length-1][0];
        minY= graphicsData[0][1];
        maxY= minY;
        // Найти минимальное и максимальное значение функции
        for(int i = 1; i<graphicsData.length; i++) {
            if(graphicsData[i][1]<minY) {
                minY= graphicsData[i][1];
            }
            if(graphicsData[i][1]>maxY) {
                maxY= graphicsData[i][1];
            }
        }
        /* Шаг 4 -Определить (исходя из размеров окна) масштабы по осям Xи Y–сколько пикселов  приходится на единицу длины по Xи по Y*/
        double scaleX = getSize().getWidth() / (maxX-minX);
        double scaleY = getSize().getHeight() / (maxY-minY);
        // Выбрать единый масштаб как минимальный из двух
        scale= Math.min(scaleX, scaleY);
        // Шаг 5-корректировка границ области согласно выбранному масштабу
        if(scale==scaleX) {
            /* Если за основу был взят масштаб по оси X, значит по оси Yделений меньше, т.е. подлежащий отображению диапазон по Y
            будет меньше высоты окна. Значит необходимо добавить делений, сделаем это так:
            1) Вычислим, сколько делений влезет по Y при выбранном масштабе -getSize().getHeight()/scale;
            2) Вычтем из этогозначения сколько делений требовалось изначально;
            3) Набросим по половине недостающего расстояния на maxYи minY*/
            double yIncrement = (getSize().getHeight()/scale-(maxY-minY))/2;
            maxY+= yIncrement;
            minY-= yIncrement;
        }
        if(scale==scaleY) {
            // Если за основу был взят масштаб по оси Y, действовать по аналогии
            double xIncrement = (getSize().getWidth()/scale-(maxX-minX))/2;
            maxX+= xIncrement;
            minX-= xIncrement;
        }
        // Шаг 5 –Преобразовать экземплярGraphics к Graphics2D
        Graphics2D canvas= (Graphics2D) g;
        // Шаг 6-Сохранить текущие настройки холста
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont= canvas.getFont();
        // Шаг 8 -В нужном порядке вызвать методы отображения элементов графика
        // Порядок вызова методов имеет значение, т.к. предыдущий рисунок будет
        // затираться последующим
        // Первым (если нужно) отрисовываются оси координат.
        if(showAxis) paintAxis(canvas);
        if(showGrid) paintGrid(canvas);
        // Затем отображаетсясам график
        paintGraphics(canvas);
        // Затем (если нужно) отображаются маркеры точек графика.
        if(showMarkers) paintMarkers(canvas);


        // Шаг 9 -Восстановить старые настройки холста
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);


    }

}