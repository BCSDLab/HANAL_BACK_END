package com.bcsdlab.biseo.enums;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Department {
    기계공학부(1000),
    메카트로닉스공학부(2000),
    전기공학과(3000),
    전자공학과(3100),
    정보통신공학과(3200),
    컴퓨터공학부(4000),
    건축공학과(5000),
    응용화학공학과(6000),
    에너지신소재공학과(6100),
    산업경영학부(7000),
    고용서비스정책학과(8000),
    전체(9000);

    private final int value;
    private static final Map<Integer, String> department = Collections.unmodifiableMap(
        Stream.of(values()).collect(Collectors.toMap(Department::getValue, Department::name))
    );
    Department(int i) {
        this.value = i;
    }

    public Integer getValue() {
        return value;
    }

    public static String getDepartment(int value) {
        return department.get(value);
    }
}
