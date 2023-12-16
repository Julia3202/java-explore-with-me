package ru.practicum.utils;

import ru.practicum.exception.NotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

public class RequestValidator {
    public void existRequester(Request request, User user) {
        if (request.getRequester().equals(user)) {
            throw new NotFoundException("Запрос с ID-" + request.getId() + " не был создан пользователем с ID-" +
                    user.getId() + ". Изменение невозможно.");
        }
    }
}
