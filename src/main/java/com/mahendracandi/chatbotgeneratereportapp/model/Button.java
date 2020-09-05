package com.mahendracandi.chatbotgeneratereportapp.model;

public class Button<T> {
    private T button;

    public T getButton() {
        return button;
    }

    public void setButton(T button) {
        this.button = button;
    }

    @Override
    public String toString() {
        return "Button{" +
                "button=" + button +
                '}';
    }
}
