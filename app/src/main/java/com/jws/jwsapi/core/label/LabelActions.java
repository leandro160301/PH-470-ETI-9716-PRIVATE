package com.jws.jwsapi.core.label;

public interface LabelActions {
    void onEditClick(LabelViewHolder holder, int position);

    void updateViews(LabelViewHolder holder, int position);

    void onSpinnerSelection(int i, int position, LabelViewHolder holder);
}
