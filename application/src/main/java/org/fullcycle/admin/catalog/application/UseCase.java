package org.fullcycle.admin.catalog.application;

public abstract class UseCase<IN, OUT> {

    public abstract OUT execute(final IN in);

}
