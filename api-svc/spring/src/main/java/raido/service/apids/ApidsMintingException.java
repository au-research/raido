package raido.service.apids;

public class ApidsMintingException extends Throwable {
    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    private String errors;

    public ApidsMintingException(String errors) {
        this.errors = errors;
    }

}
