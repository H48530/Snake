package snake;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.Random;

public class SnakeAPP extends Application {
    //定义变量
    //游戏区域大小，格子的的宽和高
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    //绘制图形的时候，格子所占的大小
    private static final int GRID_SIZE = 40;
    private static final int CANVAS_WIDTH = WIDTH * GRID_SIZE;
    private static final int CANVAS_HEIGHT = HEIGHT * GRID_SIZE;
    //定义游戏本身
    //移动速度
    //speed,每秒的帧数
    private static int speed;
    //食物
    private static Point food = new Point(-1, -1);
    //蛇,最多长1000节
    private static Point[] snake = new Point[1000];
    //现在多少节
    private static int snakeLength = 0;

    //朝向
    enum Direction {
        UP, LEFT, DOWN, RIGHT
    }

    private static Direction direction;
    //随机生成器
    private static Random random = new Random();
    //游戏结束标志
    private static boolean gameOver;

    //初始化游戏数据
    private static void newGame() {
        speed = 3;
        //蛇的初始有3节
        Arrays.fill(snake, null);
        snakeLength = 0;
        snake[snakeLength++] = new Point(WIDTH / 2, HEIGHT / 2);
        snake[snakeLength++] = new Point(WIDTH / 2, HEIGHT / 2);
        snake[snakeLength++] = new Point(WIDTH / 2, HEIGHT / 2);
        //生成食物
        newFood();
        direction = Direction.LEFT;
        //游戏未结束
        gameOver = false;
    }

    //在地图上随机生成食物
    private static void newFood() {
        //1，不能生成在外面
        //2.不能和蛇的身体碰撞
        int x, y;
        do {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
        } while (isCollision(x, y));

        food.x = x;
        food.y = y;
    }

    private static boolean isCollision(int x, int y) {
        //遍历蛇
        //不要用foreach
        for (int i = 0; i < snakeLength; i++) {
            Point point = snake[i];
            if (point.x == x && point.y == y) {
                return true;
            }
        }
        return false;
    }

    //每一帧做的动作
    private static void frame() {
        //移动身体
        for (int i = snakeLength - 1; i >= 1; i--) {
            //变成前一个坐标
            snake[i].x = snake[i - 1].x;
            snake[i].y = snake[i - 1].y;
        }
        //移动头
        Point head = snake[0];
        switch (direction) {
            case UP:
                head.y--;
                break;
            case DOWN:
                head.y++;
                break;
            case LEFT:
                head.x--;
                break;
            case RIGHT:
                head.x++;
                break;
        }
        //判断头是否出边界
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            gameOver = true;
            return;
        }
        //判断是否碰到身体
        for (int i = 1; i < snakeLength; i++) {
            Point point = snake[i];
            if (head.x == point.x && head.y == point.y) {
                gameOver = true;
                return;
            }
        }
        //判断是否吃到食物
        // 如果吃到食物
        // 1.身体增加1节    2.重新生成食物    3. 速度增加
        if (head.x == food.x && head.y == food.y) {
            snake[snakeLength++] = new Point(-1, -1);
            newFood();
            speed++;
        }
    }

    //每一帧绘制工作，渲染
    private static void render(GraphicsContext gc) {
        //1.从新绘制背景
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        //2.蛇的绘制
        for (int i = 0; i < snakeLength; i++) {
            Point point = snake[i];
            gc.setFill(Color.GREEN);
            gc.fillRect(point.x * GRID_SIZE + 1, point.y * GRID_SIZE + 1, GRID_SIZE - 2, GRID_SIZE - 2);
        }
        //3.食物的绘制
        gc.setFill(Color.YELLOW);
        gc.fillOval(food.x * GRID_SIZE + 1, food.y * GRID_SIZE + 1, GRID_SIZE, GRID_SIZE);
        //3.进行游戏结束绘制
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font(40));
            gc.fillText("游戏结束,按R继续", 200, 200);
        }

        //得分
        gc.setFill(Color.AQUAMARINE);
        gc.setFont(new Font(20));
        gc.fillText("当前得分"+ (speed-3)+"分", 40, 40);
        //作者
        gc.setFill(Color.AQUA);
        gc.setFont(new Font(15));
        gc.fillText("版本：v1.0    作者：H485301267 ", 20, 740);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //游戏的初始化
        newGame();
        Pane pane = new Pane();
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        pane.getChildren().add(canvas);

        Scene scene = new Scene(pane);

        final GraphicsContext gc = canvas.getGraphicsContext2D();
        AnimationTimer timer = new AnimationTimer() {
            long lastTiick;

            @Override
            public void handle(long now) {
                if (gameOver) {
                    return;
                }
                if (lastTiick == 0 || now - lastTiick > 1e9 / speed) {
                    lastTiick = now;
                    frame();
                    render(gc);
                }
            }
        };
        timer.start();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case W:
                    case UP:
                        if (direction != Direction.DOWN) {
                            direction = Direction.UP;
                        }
                        break;
                    case A:
                    case LEFT:
                        if (direction != Direction.RIGHT) {
                            direction = Direction.LEFT;
                        }
                        break;
                    case S:
                    case DOWN:
                        if (direction != Direction.UP) {
                            direction = Direction.DOWN;
                        }
                        break;
                    case D:
                    case RIGHT:
                        if (direction != Direction.LEFT) {
                            direction = Direction.RIGHT;
                        }
                        break;
                    case R:
                        if (gameOver) {
                            newGame();
                        }
                        break;
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.setTitle("贪吃蛇");
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
