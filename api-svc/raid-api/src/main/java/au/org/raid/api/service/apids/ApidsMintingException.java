package au.org.raid.api.service.apids;

public class ApidsMintingException extends Throwable {
    private String errors;

    public ApidsMintingException(String errors) {
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

}
