package au.org.raid.idl.raidv1.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RaidPublicModel
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-01-09T09:51:41.988751+11:00[Australia/Melbourne]")
public class RaidPublicModel {

  @JsonProperty("creationDate")
  private String creationDate;

  @JsonProperty("handle")
  private String handle;

  @JsonProperty("contentPath")
  private String contentPath;

  public RaidPublicModel creationDate(String creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * RAiD created datetime (yyyy-MM-dd hh:mm:ss)
   * @return creationDate
  */
  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public RaidPublicModel handle(String handle) {
    this.handle = handle;
    return this;
  }

  /**
   * Unique minted ANDS handle
   * @return handle
  */
  public String getHandle() {
    return handle;
  }

  public void setHandle(String handle) {
    this.handle = handle;
  }

  public RaidPublicModel contentPath(String contentPath) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RaidPublicModel raidPublicModel = (RaidPublicModel) o;
    return Objects.equals(this.creationDate, raidPublicModel.creationDate) &&
        Objects.equals(this.handle, raidPublicModel.handle) &&
        Objects.equals(this.contentPath, raidPublicModel.contentPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(creationDate, handle, contentPath);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RaidPublicModel {\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate)).append("\n");
    sb.append("    handle: ").append(toIndentedString(handle)).append("\n");
    sb.append("    contentPath: ").append(toIndentedString(contentPath)).append("\n");
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

