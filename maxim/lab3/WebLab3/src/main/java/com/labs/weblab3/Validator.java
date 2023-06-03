package com.labs.weblab3;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class Validator {
    private Double xValue;
    private Double yValue;
    private Double rValue;
    private String hitResult;
    private boolean checkFirstQuarterHit(){
        return ((rValue/2) >= xValue && xValue >= 0 && yValue >= 0 && yValue <= rValue);
    }
    private boolean checkThirdQuarterHit(){
        return ((pow(xValue, 2) + pow(yValue, 2) <= abs(rValue/2)) && xValue <= 0 && yValue <= 0);
    }
    private boolean checkFourQuarterHit(){
        return (xValue >= 0 && yValue <= 0 && yValue >= (2*xValue - rValue));
    }
    public boolean checkHit(){
        if (checkFirstQuarterHit() || checkThirdQuarterHit() || checkFourQuarterHit()) {
            setHitResult("Попадание");
            return true;
        } else {
            setHitResult("Промах");
            return false;
        }
    }

    public Double getValueX() {
        return xValue;
    }

    public Double getValueY() {
        return yValue;
    }

    public Double getValueR() {
        return rValue;
    }

    public void setValueX(Double xValue) {
        this.xValue = xValue;
    }

    public void setValueY(Double yValue) {
        this.yValue = yValue;
    }

    public void setValueR(Double rValue) {
        this.rValue = rValue;
    }

    public String getHitResult() {
        return hitResult;
    }

    public void setHitResult(String hitResult) {
        this.hitResult = hitResult;
    }
}
