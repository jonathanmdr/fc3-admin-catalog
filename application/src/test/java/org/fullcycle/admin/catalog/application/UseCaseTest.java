package org.fullcycle.admin.catalog.application;

import org.fullcycle.admin.catalog.domain.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public abstract class UseCaseTest {

    protected abstract List<Object> getMocks();

    @BeforeEach
    void beforeEach() {
        Mockito.reset(getMocks().toArray());
    }

    protected Set<String> listIdAsString(final Collection<? extends Identifier> identifiers) {
        return identifiers.stream()
            .map(Identifier::getValue)
            .collect(Collectors.toSet());
    }

}
