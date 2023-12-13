package ru.practicum.validator;

import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

public class RequestValidator {
    public void validRequester(Long userId, Event event) {
        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("Нельзя делать запрос на мероприятие, которое создали Вы сами.");
        }
    }

    public void existRequester(Request request, User user) {
        if (request.getRequester().equals(user)) {
            throw new ConflictException("Запрос с ID-" + request.getId() + " не был создан пользователем с ID-" +
                    user.getId() + ". Изменение невозможно.");
        }
    }
}
