package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTimeEntryRepository implements TimeEntryRepository
{
    AtomicLong sequence = new AtomicLong();
    Map<Long, TimeEntry> store = new HashMap<>();

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(sequence.incrementAndGet());
        store.put(timeEntry.getId(), timeEntry);
        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        return store.get(id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        timeEntry.setId(id);
        store.put(id, timeEntry);
        return timeEntry;
    }

    @Override
    public void delete(long id) {
        store.remove(id);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList(store.values());
    }
}
