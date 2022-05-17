// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.

package com.example.detectionapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;


public class ResultView extends View {

    private final static int TEXT_X = 40;
    private final static int TEXT_Y = 35;
    private final static int TEXT_WIDTH = 260;
    private final static int TEXT_HEIGHT = 50;

    private Paint paintRectangle;
    private Paint paintText;
    private ArrayList<ResultDetection> resultDetections;

    public ResultView(Context context) {
        super(context);
    }

    public ResultView(Context context, AttributeSet attrs){
        super(context, attrs);
        paintRectangle = new Paint();
        paintRectangle.setColor(Color.YELLOW);
        paintText = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (resultDetections == null) return;
        for (ResultDetection resultDetection : resultDetections) {
            paintRectangle.setStrokeWidth(5);
            paintRectangle.setStyle(Paint.Style.STROKE);
            canvas.drawRect(resultDetection.rect, paintRectangle);

            Path mPath = new Path();
            RectF mRectF = new RectF(resultDetection.rect.left, resultDetection.rect.top, resultDetection.rect.left + TEXT_WIDTH,  resultDetection.rect.top + TEXT_HEIGHT);
            mPath.addRect(mRectF, Path.Direction.CW);
            paintText.setColor(Color.MAGENTA);
            canvas.drawPath(mPath, paintText);

            paintText.setColor(Color.WHITE);
            paintText.setStrokeWidth(0);
            paintText.setStyle(Paint.Style.FILL);
            paintText.setTextSize(32);

            System.out.println("LEFT BOX: " + resultDetection.rect.left);
            System.out.println("LEFT BOX: " + resultDetection.score);
            System.out.println("CLASS INDEX: " + resultDetection.classIndex);
            System.out.println("X: " + TEXT_X);
            System.out.println("TOP: " + resultDetection.rect.top);
            System.out.println("Y: " + TEXT_Y);
            System.out.println("text: " + paintText);
            canvas.drawText(String.format("%s %.2f", PrePostProcessor.classes[resultDetection.classIndex], resultDetection.score),
                    resultDetection.rect.left + TEXT_X, resultDetection.rect.top + TEXT_Y, paintText);
        }
    }

    public void setResults(ArrayList<ResultDetection> resultDetections) {
        this.resultDetections = resultDetections;
    }
}
