package com.wallet.service.interfaces;

import org.springframework.data.domain.Sort;

import java.util.Set;

public interface IPagingService {
    Set<String> getAllFields(final Class<?> type);

    Sort.Direction getSortDirection(String direction);

    boolean checkPropertPresent(final Set<String> properties, final String propertyName);
}
