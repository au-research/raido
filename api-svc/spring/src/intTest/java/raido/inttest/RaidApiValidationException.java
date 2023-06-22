package raido.inttest;

import feign.FeignException;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;

/**
 I couldn't figure out how to get ObjectMapper to de-pickle directly to an
 actual ValidationException, because RaidApiException has abstract methods
 that are not compatible with dynamic object creation.
 Even though it's really just a POJO from an RPC/REST perspective on the 
 client side, we still sub-class Exception so that it can be thrown by Feign 
 and asserted properly in tests.
 */
public class RaidApiValidationException extends RuntimeException {

  private List<ValidationFailure> failures;
  private String type;
  private String title;
  private int status;
  private String detail;
  private String instance;

  /**
   The original "BadRequest" that the feign Default ErrorDecoder generates.
   Just in case we want to get access to it in a test one day.
   */
  private FeignException.BadRequest badRequest;

  public RaidApiValidationException() {
  }

  public List<ValidationFailure> getFailures() {
    return failures;
  }

  public RaidApiValidationException setFailures(List<ValidationFailure> failures) {
    this.failures = failures;
    return this;
  }

  public String getType() {
    return type;
  }

  public RaidApiValidationException setType(String type) {
    this.type = type;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public RaidApiValidationException setTitle(String title) {
    this.title = title;
    return this;
  }

  public int getStatus() {
    return status;
  }

  public RaidApiValidationException setStatus(int status) {
    this.status = status;
    return this;
  }

  public String getDetail() {
    return detail;
  }

  public RaidApiValidationException setDetail(String detail) {
    this.detail = detail;
    return this;
  }

  public String getInstance() {
    return instance;
  }

  public RaidApiValidationException setInstance(String instance) {
    this.instance = instance;
    return this;
  }

  public FeignException.BadRequest getBadRequest() {
    return badRequest;
  }

  public RaidApiValidationException setBadRequest(FeignException.BadRequest badRequest) {
    this.badRequest = badRequest;
    return this;
  }
}
