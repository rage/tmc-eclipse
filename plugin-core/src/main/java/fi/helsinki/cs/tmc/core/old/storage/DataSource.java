package fi.helsinki.cs.tmc.core.old.old.storage;

import java.util.List;

public interface DataSource<T> {

    List<T> load();

    void save(List<T> elements);

}
