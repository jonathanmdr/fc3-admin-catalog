package org.fullcycle.admin.catalog.application;

public abstract class UnitUseCase<IN> {

    public abstract void execute(final IN in);

}
