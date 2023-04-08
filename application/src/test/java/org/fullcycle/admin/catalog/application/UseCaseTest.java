package org.fullcycle.admin.catalog.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public abstract class UseCaseTest {

    protected abstract List<Object> getMocks();

    @BeforeEach
    void beforeEach() {
        Mockito.reset(getMocks().toArray());
    }

}
