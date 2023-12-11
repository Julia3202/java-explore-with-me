package ru.practicum.validator;


import ru.practicum.exception.ValidationException;

public class LocationValidator {
    public void validateLat(Double lat) {
        if (lat > 90 || lat < 90) {
            throw new ValidationException("Неверно указана широта, введите корректные данные.");
        }
    }

    public void validateLon(Double lon) {
        if (lon > 180 || lon < 180) {
            throw new ValidationException("Неверно указана долгота, введите корректные данные.");
        }
    }

    public void validLocation(Double lat, Double lon){
        validateLat(lat);
        validateLon(lon);
    }
}
