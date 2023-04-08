package org.fullcycle.admin.catalog.domain.castmember;

import org.fullcycle.admin.catalog.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class CastMemberID extends Identifier {
    private final String value;

    private CastMemberID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static CastMemberID unique() {
        return CastMemberID.from(UUID.randomUUID());
    }

    public static CastMemberID from(final String id) {
        return CastMemberID.from(UUID.fromString(id));
    }

    public static CastMemberID from(final UUID id) {
        return new CastMemberID(id.toString().toLowerCase());
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CastMemberID that = (CastMemberID) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

}
