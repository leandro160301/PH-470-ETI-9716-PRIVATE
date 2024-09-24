package com.jws.jwsapi.core.label;

public interface LabelInterface {
    void editClick(LabelViewHolder holder, int position);
    void updateViews(LabelViewHolder holder, int position);
    void spinnerSelection(int i, int position, LabelViewHolder holder);
}
