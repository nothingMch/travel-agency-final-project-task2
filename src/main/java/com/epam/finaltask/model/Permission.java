package com.epam.finaltask.model;

public enum Permission {
    ADMIN_READ("Администратор: Чтение"),
    ADMIN_UPDATE("Администратор: Обновление"),
    ADMIN_CREATE("Администратор: Создание"),
    ADMIN_DELETE("Администратор: Удаление"),
    MANAGER_UPDATE("Менеджер: Обновление"),
    USER_READ("Пользователь: Чтение"),
    USER_UPDATE("Пользователь: Обновление"),
    USER_CREATE("Пользователь: Создание"),
    USER_DELETE("Пользователь: Удаление");

    private final String description;

    Permission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
