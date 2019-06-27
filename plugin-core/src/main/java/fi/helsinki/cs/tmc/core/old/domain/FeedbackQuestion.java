package fi.helsinki.cs.tmc.core.old.old.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A domain class representing feedback questions such as
 * "How difficult this exercise was". It may be either text or intrange
 * question; former has textbox for answer, latter has a bar representing range
 * of integers.
 */
public class FeedbackQuestion {

    private static final Pattern INT_RANGE_REGEXP = Pattern.compile("^intrange\\[(-?\\d+)\\.\\.(-?\\d+)\\]$");

    private int id;
    private String question;
    private String kind;

    public FeedbackQuestion() {

    }

    public FeedbackQuestion(final int id, final String question, final String kind) {

        this.id = id;
        this.question = question;
        this.kind = kind;
    }

    public int getId() {

        return id;
    }

    public void setId(final int id) {

        this.id = id;
    }

    public String getQuestion() {

        return question;
    }

    public void setQuestion(final String question) {

        this.question = question;
    }

    public String getKind() {

        return kind;
    }

    public void setKind(final String kind) {

        this.kind = kind;
    }

    public boolean isText() {

        return kind.equals("text");
    }

    public boolean isIntRange() {

        return intRangeMatcher().matches();
    }

    public int getIntRangeMin() {

        final Matcher matcher = intRangeMatcher();
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalStateException("Not an intrange");
        }
    }

    public int getIntRangeMax() {

        final Matcher matcher = intRangeMatcher();
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(2));
        } else {
            throw new IllegalStateException("Not an intrange");
        }
    }

    private Matcher intRangeMatcher() {

        return INT_RANGE_REGEXP.matcher(kind);
    }
}
