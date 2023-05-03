package org.fullcycle.admin.catalog.domain.castmember;

import org.fullcycle.admin.catalog.domain.Identifier;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;

import java.util.Objects;

public class CastMemberID extends Identifier {

    private final String value;

    private CastMemberID(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static CastMemberID unique() {
        return CastMemberID.from(
            IdentifierUtils.unique()
        );
    }

    public static CastMemberID from(final String unique) {
        return new CastMemberID(
            IdentifierUtils.from(unique)
        );
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
