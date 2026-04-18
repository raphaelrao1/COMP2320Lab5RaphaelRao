package edu.westga.comp2320.studymate.model;

public record StudySession(char dayOfWeek, String subject, String task) {
    public StudySession(char dayOfWeek, String subject, String task) {

        char upperDay = Character.toUpperCase(dayOfWeek);
        if (upperDay != 'M' && upperDay != 'T' && upperDay != 'W'
                && upperDay != 'R' && upperDay != 'F') {
            throw new IllegalArgumentException("Day must be M, T, W, R, or F");
        }

        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject is required");
        }

        this.dayOfWeek = upperDay;
        this.subject = subject.trim();
        this.task = (task == null || task.trim().isEmpty()) ? null : task.trim();
    }

    public StudySession(char dayOfWeek, String subject) {
        this(dayOfWeek, subject, null);
    }

    @Override
    public String toString() {
        String dayName = this.getDayName();
        if (this.task == null) {
            return dayName + ": " + this.subject;
        }
        return dayName + ": " + this.subject + " - " + this.task;
    }

    private String getDayName() {
        return switch (this.dayOfWeek) {
            case 'M' -> "Monday";
            case 'T' -> "Tuesday";
            case 'W' -> "Wednesday";
            case 'R' -> "Thursday";
            case 'F' -> "Friday";
            default -> "";
        };
    }
}
