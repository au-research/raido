package raido.apisvc.exception;

import static raido.apisvc.endpoint.message.RaidApiMessage.DISALLOWED_X_SVC_CALL;

public class CrossAccountAccessException extends RuntimeException {
  final Long servicePointId;

  public CrossAccountAccessException(final Long servicePointId) {
    super(DISALLOWED_X_SVC_CALL);
    this.servicePointId = servicePointId;
  }

  public String getDetail() {
    return String.format("You don't have permission to access RAiDs with a service point of %d", this.servicePointId);
  }
}
