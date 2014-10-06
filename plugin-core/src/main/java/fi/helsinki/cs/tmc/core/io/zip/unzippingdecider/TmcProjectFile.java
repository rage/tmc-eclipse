package fi.helsinki.cs.tmc.core.io.zip.unzippingdecider;

import fi.helsinki.cs.tmc.core.io.FileIO;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

/**
 * Represents the contents of a {@code .tmcproject.yml} file.
 */
public class TmcProjectFile {

    private static final Logger LOG = Logger.getLogger(TmcProjectFile.class.getName());

    private List<String> extraStudentFiles;

    public TmcProjectFile(final FileIO file) {

        extraStudentFiles = Collections.emptyList();
        load(file);
    }

    public List<String> getExtraStudentFiles() {

        return extraStudentFiles;
    }

    public void setExtraStudentFiles(final List<String> extraStudentFiles) {

        this.extraStudentFiles = Collections.unmodifiableList(extraStudentFiles);
    }

    private void load(final FileIO file) {

        if (!file.fileExists()) {
            return;
        }
        try {
            final Reader reader = file.getReader();
            try {
                final Object root = new Yaml().load(reader);
                final List<String> extraStudentFiles = parse(root);
                if (extraStudentFiles != null) {
                    setExtraStudentFiles(extraStudentFiles);
                }
            } finally {
                reader.close();
            }
        } catch (final IOException e) {
            LOG.log(Level.WARNING, "Failed to read {0}: {1}", new Object[] { file.getPath(), e.getMessage() });
        }
    }

    private List<String> parse(final Object root) {

        if (!(root instanceof Map)) {
            return null;
        }
        final Map<?, ?> rootMap = (Map<?, ?>) root;
        final Object files = rootMap.get("extra_student_files");
        if (files instanceof List) {
            final List<String> extraStudentFiles = new ArrayList<String>();
            for (final Object value : (List<?>) files) {
                if (value instanceof String) {
                    extraStudentFiles.add((String) value);
                }
            }
            return extraStudentFiles;
        }
        return null;
    }
}
