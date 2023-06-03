package com.labs.weblab3;

import javax.persistence.*;

@Entity
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)

    @Column(name = "id", nullable = false)
    private Long id;
    private Double xValue;
    private Double yValue;
    private Double rValue;
    private String hitResult;
    public Coordinates(){}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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