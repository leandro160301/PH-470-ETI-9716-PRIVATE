package com.jws.jwsapi.core.label;

public class LabelModel {

    private String fieldName;
    private int position;

    public LabelModel(String fieldName, int position) {
        this.fieldName = fieldName;
        this.position = position;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }



}