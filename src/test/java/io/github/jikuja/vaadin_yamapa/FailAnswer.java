package io.github.jikuja.vaadin_yamapa;


import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.fail;

/**
 * Default mockup method to fail()
 */
public class FailAnswer implements Answer {
    @Override
    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        fail();
        return null;
    }
}
