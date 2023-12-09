package ru.practicum.validator;

import ru.practicum.exception.ValidationException;

public class ValidatorSizeAndFrom {
    public Boolean validFrom(int from) {
        if (from < 0) {
            throw new ValidationException("Значение первого элемента должно быть строго больше 0.");
        }
        return true;
    }

    public Boolean validSize(int size) {
        if (size <= 0) {
            throw new ValidationException("Количество выводимых строк строго должно быть больше 0.");
        }
        return true;
    }
}
