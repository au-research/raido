package au.org.raid.idl.raidv1.model;

import java.net.URI;
import java.util.Objects;
import au.org.raid.idl.raidv1.model.RaidCreateModelMeta;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RaidCreateModel
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-01-09T09:51:41.988751+11:00[Australia/Melbourne]")
public class RaidCreateModel {

  @JsonProperty("contentPath")
  private String contentPath;

  @JsonProperty("startDate")
  private String startDate;

  @JsonProperty("meta")
  private RaidCreateModelMeta meta;

  public RaidCreateModel contentPath(String contentPath) {
    this.contentPath = contentPath;
    return this;
  }

  /**
   * URL path minted with ANDS
   * @return contentPath
  */
  public String getContentPath() {
    return contentPath;
  }

  public void setContentPath(String contentPath) {
    this.contentPath = contentPath;
  }

  public RaidCreateModel startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * RAiD start datetime (yyyy-MM-dd hh:mm:ss), which will default to the current datetime if none is supplied
   * @return startDate
  */
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public RaidCreateModel meta(RaidCreateModelMeta meta) {
    this.meta = meta;
    return this;
  }

  /**
   * Get meta
   * @return meta
  */
  public RaidCreateModelMeta getMeta() {
    return meta;
  }

  public void setMeta(RaidCreateModelMeta meta) {
    this.meta = meta;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RaidCreateModel raidCreateModel = (RaidCreateModel) o;
    return Objects.equals(this.contentPath, raidCreateModel.contentPath) &&
        Objects.equals(this.startDate, raidCreateModel.startDate) &&
        Objects.equals(this.meta, raidCreateModel.meta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contentPath, startDate, meta);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RaidCreateModel {\n");
    sb.append("    contentPath: ").append(toIndentedString(contentPath)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    meta: ").append(toIndentedString(meta)).append("\n");
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

