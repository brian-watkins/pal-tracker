package io.pivotal.pal.tracker;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TimeEntryHealthIndicatorTest {
    private TimeEntryHealthIndicator subject;
    private TimeEntryRepository fakeTimeEntryRepository = mock(TimeEntryRepository.class);

    @Before
    public void setup() {
        subject = new TimeEntryHealthIndicator(fakeTimeEntryRepository);
    }

    @Test
    public void isUpIfLessThanFiveTimeEntries() {
        Health actualHealth = subject.health();

        assertThat(actualHealth.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void isDownIfGreaterThanOrEqualToFiveTimeEntries() {
        when(fakeTimeEntryRepository.list()).thenReturn(Arrays.asList(
                mock(TimeEntry.class),
                mock(TimeEntry.class),
                mock(TimeEntry.class),
                mock(TimeEntry.class),
                mock(TimeEntry.class),
                mock(TimeEntry.class),
                mock(TimeEntry.class)
        ));

        Health actualHealth = subject.health();

        assertThat(actualHealth.getStatus()).isEqualTo(Status.DOWN);
    }

}