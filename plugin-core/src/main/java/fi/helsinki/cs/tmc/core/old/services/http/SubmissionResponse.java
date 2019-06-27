package fi.helsinki.cs.tmc.core.old.old.services.http;

import java.net.URI;

public class SubmissionResponse {

    private final URI submissionUrl;
    private final URI pasteUrl;

    public SubmissionResponse(final URI submissionUrl, final URI pasteUrl) {

        this.submissionUrl = submissionUrl;
        this.pasteUrl = pasteUrl;
    }

    public URI getSubmissionUrl() {

        return submissionUrl;
    }


    public URI getPasteUrl() {

        return pasteUrl;
    }
}
