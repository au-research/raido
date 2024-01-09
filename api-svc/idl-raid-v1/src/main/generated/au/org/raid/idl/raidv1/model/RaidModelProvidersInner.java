package au.org.raid.idl.raidv1.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RaidModelProvidersInner
 */

@JsonTypeName("RaidModel_providers_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-01-09T09:51:41.988751+11:00[Australia/Melbourne]")
public class RaidModelProvidersInner {

  @JsonProperty("startDate")
  private String startDate;

  @JsonProperty("endDate")
  private String endDate;

  @JsonProperty("name")
  private String name;

  public RaidModelProvidersInner startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Datetime the provider associated with the RAiD  (yyyy-MM-dd hh:mm:ss)
   * @return startDate
  */
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public RaidModelProvidersInner endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * (optional) Datetime the provider dissociated with the RAiD  (yyyy-MM-dd hh:mm:ss)
   * @return endDate
  */
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public RaidModelProvidersInner name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of provider
   * @return name
  */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RaidModelProvidersInner raidModelProvidersInner = (RaidModelProvidersInner) o;
    return Objects.equals(this.startDate, raidModelProvidersInner.startDate) &&
        Objects.equals(this.endDate, raidModelProvidersInner.endDate) &&
        Objects.equals(this.name, raidModelProvidersInner.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDate, endDate, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RaidModelProvidersInner {\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

