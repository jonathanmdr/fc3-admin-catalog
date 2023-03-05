package org.fullcycle.admin.catalog.application;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public abstract class UseCaseTest implements BeforeEachCallback {

    protected abstract List<Object> getMocks();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        Mockito.reset(getMocks().toArray());
    }

}
