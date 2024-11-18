package com.example.a22213502_termproject;

import Game.GameManager;
import Game.Structure;
import Game.Unit;
import GameEngine.Instance;
import GameEngine.Object;
import Game.Button;
import GameEngine.ObjectManager;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GameManager gameManager;
    private Paint paint;
    private Paint textPaint; // 경과 시간 표시용 Paint
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameManager = new GameManager();
        paint = new Paint();

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50); // 텍스트 크기 설정

        // GameManager 초기화
        gameManager.init();

        Instance.getObjectManager().addObject(new Button(this, 100, 1600, 200, 200, Color.YELLOW, "LadderButton", Button.ButtonType.LADDER));
        Instance.getObjectManager().addObject(new Button(this, 300, 1600, 200, 200, Color.BLACK, "BlockButton", Button.ButtonType.BLOCK));

        Instance.getObjectManager().addObject(new Unit(100, 800, 100, 100, Color.BLUE, "TestUnit", 5, Unit.UnitType.RESCUE));
        Instance.getObjectManager().addObject(new Unit(800, 800, 100, 100, Color.RED, "TestUnit", 5, Unit.UnitType.TARGET));

        Instance.getObjectManager().addObject(new Structure(800, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        Instance.getObjectManager().addObject(new Structure(100, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        Instance.getObjectManager().addObject(new Structure(200, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        Instance.getObjectManager().addObject(new Structure(300, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        Instance.getObjectManager().addObject(new Structure(400, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        Instance.getObjectManager().addObject(new Structure(500, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        gestureDetector = new GestureDetector(this, new GestureListener());
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

    private class GameView extends View {
        public GameView(MainActivity context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Instance.getObjectManager().drawObjects(canvas, paint); // 모든 객체 그리기
            gameManager.update(); // 게임 매니저 업데이트 호출
            gameManager.draw(canvas, paint); // GameManager의 아이템 및 버튼 그리기


            // 경과 시간 계산하여 초 단위로 표시
            long elapsedMillis = gameManager.getElapsedTime();
            int seconds = (int) (elapsedMillis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            // 경과 시간을 텍스트로 화면 상단에 그리기
            String timeText = String.format("%02d:%02d", minutes, seconds);
            canvas.drawText(timeText, 50, 100, textPaint); // 화면 상단에 텍스트 표시

            invalidate(); // 화면 갱신
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 취소 버튼 등 터치 처리
                if (gameManager.handleTouchEvent(touchX, touchY, getContext())) {
                    invalidate();
                    return true;
                }

                // 현재 모드에 따라 동작
                if (gameManager.getCurrentAction() == GameManager.ActionType.MOVE_UNIT && gameManager.getSelectedUnit() != null) {
                    // 유닛 이동 모드일 때 목표 위치 설정
                    gameManager.setTargetX(touchX);
                } else if (gameManager.getCurrentAction() == GameManager.ActionType.MOVE_ITEM) {
                    // 아이템 배치 모드일 때 아이템 이동 처리
                    gameManager.handleTouchEvent(touchX, touchY, getContext());
                } else {
                    // 버튼 클릭으로 아이템 배치 모드 전환
                    for (Object obj : Instance.getObjectManager().getObjects()) {
                        if (obj instanceof Button) {
                            Button button = (Button) obj;
                            if (button.isClicked(touchX, touchY)) {
                                if (button.getButtonType() == Button.ButtonType.LADDER) {
                                    gameManager.setItemMode(GameManager.ItemMode.LADDER, getContext());
                                } else if (button.getButtonType() == Button.ButtonType.BLOCK) {
                                    gameManager.setItemMode(GameManager.ItemMode.BLOCK, getContext());
                                }
                                break;
                            }
                        } else if (obj instanceof Unit) {
                            // 유닛 선택
                            Unit unit = (Unit) obj;
                            if (unit.getAABB().contains(touchX, touchY)) {
                                gameManager.setSelectedUnit(unit, getContext());
                                break;
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 아이템 배치 모드에서 더블 클릭으로 아이템 설치
            gameManager.handleDoubleTap(getApplicationContext());
            return true;
        }
    }
}
