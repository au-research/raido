package au.org.raid.api.exception;

public class InvalidDateException extends RaidApiException {
    final String date;
    public InvalidDateException(String date) {
        this.date = date;
    }

    @Override
    public String getTitle() {
        return "Invalid date";
    }

    @Override
    public int getStatus() {
        return 400;
    }

    @Override
    public String getDetail() {
        return "%s is an invalid date or has an unsupported format.".formatted(date);
    }
}
