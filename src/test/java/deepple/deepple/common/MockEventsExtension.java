package deepple.deepple.common;

import deepple.deepple.common.event.Events;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

public class MockEventsExtension implements BeforeEachCallback, AfterEachCallback {

    private MockedStatic<Events> mockedEvents;

    @Override
    public void beforeEach(ExtensionContext context) {
        mockedEvents = mockStatic(Events.class);
        mockedEvents.when(() -> Events.raise(any())).then(invocation -> null);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        mockedEvents.close();
    }
}