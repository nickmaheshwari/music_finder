package edu.uchicago.nmaheshwari.vaadin.service;

@FunctionalInterface
public interface ResponseCallback<T> {
    void operationFinished(T results);
}