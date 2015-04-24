package com.example.adedayoadelowokan.assingment02;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.Random;

public class CustomView extends View
{
    // variable declarations
    private Paint black, grey, white, blue, green, yellow, red, text_colour;
    private Rect square;
    private int touch_spot[][] = new int[10][10];
    private int bomb_spot[][] = new int[10][10];
    private int bomb_count[][] = new int[10][10];
    private int marked[][] = new int[10][10];
    int sqHeight, sqWidth;
    int unopen = 0, bomb = -1, open = 2, mark = 9, unmark = 8;

    public CustomView(Context c)
    {
        super(c);
        init();
    }

    // constructor that takes in a context and also a list of attributes
    // that were set through XML
    public CustomView(Context c, AttributeSet as)
    {
        super(c, as);
        init();
    }

    // constructor that take in a context, attribute set and also a default
    // style in case the view is to be styled in a certian way
    public CustomView(Context c, AttributeSet as, int default_style)
    {
        super(c, as, default_style);
        init();
    }

    // refactored init method as most of this code is shared by all the constructors
    private void init()
    {
        black = new Paint(Paint.ANTI_ALIAS_FLAG);
        grey = new Paint(Paint.ANTI_ALIAS_FLAG);
        white = new Paint(Paint.ANTI_ALIAS_FLAG);
        blue = new Paint(Paint.ANTI_ALIAS_FLAG);
        green = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellow = new Paint(Paint.ANTI_ALIAS_FLAG);
        red = new Paint(Paint.ANTI_ALIAS_FLAG);
        grey.setColor(Color.GRAY);
        black.setColor(Color.BLACK);
        white.setColor(Color.WHITE);
        white.setStrokeWidth(2);
        white.setStyle(Paint.Style.STROKE);
        blue.setColor(Color.BLUE);
        green.setColor(Color.GREEN);
        yellow.setColor(Color.YELLOW);
        red.setColor(Color.RED);
        reset();
    }

    // makes sure that the board is a square
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    // draws the board as the game progresses
    public void onDraw(Canvas canvas)
    {
        sqHeight = canvas.getHeight()/10;
        sqWidth = canvas.getWidth()/10;
        setRect(sqWidth, sqHeight);
        textHeight(sqHeight);
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                canvas.save();
                canvas.translate(sqWidth * i, sqHeight * j);
                canvas.drawRect(square, black);
                canvas.drawRect(square, white);
                int spot = touch_spot[i][j];

                if(marked[i][j] == mark)
                    canvas.drawRect(square, yellow);

                if(spot == open && bomb_spot[i][j] != bomb)
                {
                    canvas.drawRect(square, grey);
                    textColour(i,j);
                    if(bomb_count[i][j] > 0)
                        canvas.drawText(bomb_count[i][j] + "", 0, +sqHeight, text_colour);
                }
                else if(bomb_spot[i][j] == bomb && spot == open)
                {
                    canvas.drawRect(square, red);
                    canvas.drawText("M", 0, +sqHeight, black);
                    endGame();
                }
                canvas.restore();
            }
        }
        super.onDraw(canvas);
    }

    // define shape size of Rect
    public void setRect(int width, int height)
    {
        square = new Rect(0, 0, width, height);
    }

    // set the text colour depending on the bomb count
    public void textColour(int i, int j)
    {
        if(bomb_count[i][j] == 1)
            text_colour = blue;
        else if(bomb_count[i][j] == 2)
            text_colour = green;
        else if(bomb_count[i][j] == 3)
            text_colour = yellow;
        else if(bomb_count[i][j] >= 4)
            text_colour = red;
    }

    // sets the height of the text
    public void textHeight(int sqHeight)
    {
        blue.setTextSize(sqHeight);
        red.setTextSize(sqHeight);
        yellow.setTextSize(sqHeight);
        green.setTextSize(sqHeight);
        black.setTextSize(sqHeight);
    }

    // checks for definite positions of bombs
    public void bombMark()
    {
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                int unopened = unopenedSpots(i, j);
                if(unopened == bomb_count[i][j] && unopened != 0)
                    markBomb(i,j);
            }
        }
    }

    // marks definite positions of bombs around a given position
    public void markBomb(int x, int y)
    {
        for(int i = x-1; i <= x+1; i++)
        {
            for(int j = y-1; j <= y+1; j++)
            {
                if(i < 0 || i > 9 || j < 0 || j > 9);
                else if(i == x && j == y);
                else if(touch_spot[i][j] == unopen && bomb_spot[i][j] == bomb)
                {
                    marked[i][j] = mark;
                }
            }
        }
    }

    // counts the amount of unopened slots around a given position
    public int unopenedSpots(int x, int y)
    {
        int unopened = 0;
        if(touch_spot[x][y] == open)
        {
            for(int i = x-1; i <= x+1; i++)
            {
                for(int j = y-1; j <= y+1; j++)
                {
                    if(i < 0 || i > 9 || j < 0 || j > 9);
                    else if(i == x && j == y);
                    else if(touch_spot[i][j] == unopen)
                    {
                        unopened++;
                    }
                }
            }
        }
        return unopened;
    }

    // reset the game board
    public void reset()
    {
        clearBoard();
        plantMines();
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                bomb_count[i][j] = bombCheck(i, j);
            }
        }
        invalidate();
    }

    // counts the amount of bombs around a given position
    public int bombCheck(int x, int y)
    {
        int bomb_count = 0;
        for(int i = x-1; i <= x+1; i++)
        {
            for(int j = y-1; j <= y+1; j++)
            {
                if(i < 0 || i > 9 || j < 0 || j > 9);
                else if(i == x && j == y);
                else if(bomb_spot[i][j] == bomb)
                {
                    bomb_count++;
                }
            }
        }
        return bomb_count;
    }

    // plant all the bombs on the board randomly
    public void plantMines()
    {
        int randx, randy;
        Random rand = new Random();
        for(int mines = 0; mines < 20; mines++)
        {
            randx = rand.nextInt(10);
            randy = rand.nextInt(10);
            if(bomb_spot[randx][randy] != bomb)
            {
                bomb_spot[randx][randy] = bomb;
            }
            else{
                mines--;
            }
        }
    }

    // clear the game board
    public void clearBoard()
    {
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                touch_spot[i][j] = unopen;
                bomb_spot[i][j] = 0;
                bomb_count[i][j] = 0;
                marked[i][j] = unmark;
            }
        }
    }

    // game loss notification
    public void endGame()
    {
        new AlertDialog.Builder(this.getContext())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Game Over")
            .setMessage("You landed on a mine, restart game.")
            .setPositiveButton("Reset",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        reset();
                    }
                }
            )
            .show();
    }

    // checks for possible win
    public boolean isEndGame()
    {
        int unopenedSlots = 0;
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                if(touch_spot[i][j] == unopen)
                    unopenedSlots++;
            }
        }
        return unopenedSlots == 20;
    }

    // prompt that tells player they have won
    public void gameOver()
    {
        new AlertDialog.Builder(this.getContext())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("You Win!")
            .setMessage("Restart Game")
            .setPositiveButton("Reset",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        reset();
                    }
                }
            )
            .show();
    }

    // open all the positions around a given point that has no bomb around it
    public void openZeros(int x, int y)
    {
        if(touch_spot[x][y] == open && bomb_count[x][y] == 0)
        {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (i < 0 || i > 9 || j < 0 || j > 9);
                    else if (i == x && j == y);
                    else if (bomb_spot[i][j] != bomb && touch_spot[i][j] == unopen) {
                        touch_spot[i][j] = open;
                        openZeros(i,j);
                    }
                }
            }
            bombMark();
        }
    }

    // check for touch positions, if landed on a mine end the game
    // if the player lands on a marked mine, continue
    // if the player doesn't do either, uncover the position and update the board
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            float touch_x = event.getX();
            float touch_y = event.getY();
            int x = (int) (touch_x / sqWidth);
            int y = (int) (touch_y / sqHeight);

            if (bomb_spot[x][y] == bomb && marked[x][y] != 1)
                touch_spot[x][y] = open;
            else if(marked[x][y] == mark);
            else
            {
                touch_spot[x][y] = open;
                openZeros(x, y);
                boolean end = isEndGame();
                if(end)
                    gameOver();
                bombMark();
            }

        }
        // if we get to this point they we have not handled the touch
        // ask the system to handle it instead
        invalidate();
        return super.onTouchEvent(event);
    }
}