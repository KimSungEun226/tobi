package springbook.learningtest.template;

import java.io.IOException;

public interface LineCallback<T> {
	T doSomethingLine(String line, T value) throws IOException;
}
