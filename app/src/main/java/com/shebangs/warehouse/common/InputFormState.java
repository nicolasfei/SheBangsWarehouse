package com.shebangs.warehouse.common;

import androidx.annotation.Nullable;

public class InputFormState {
    @Nullable
    private String inputError;
    private boolean isDataValid;

    public InputFormState(@Nullable String inputError) {
        this.inputError = inputError;
        this.isDataValid = false;
    }

    public InputFormState(boolean isDataValid) {
        this.inputError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public String getInputError() {
        return inputError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
