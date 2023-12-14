package ru.practicum.utils;

import java.util.Optional;

public enum SorterEvent {
    EVENT_DATE,
    VIEWS;

    public static Optional<SorterEvent> from(String stateAction) {
        for (SorterEvent state : SorterEvent.values()) {
            if (state.name().equalsIgnoreCase(stateAction)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
