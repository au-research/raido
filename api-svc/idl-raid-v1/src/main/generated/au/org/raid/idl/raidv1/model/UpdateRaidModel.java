package au.org.raid.idl.raidv1.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateRaidModel
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-01-09T09:51:41.988751+11:00[Australia/Melbourne]")
public class UpdateRaidModel {

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("contentPath")
  private String contentPath;

  public UpdateRaidModel name(String name) {
    this.name = name;
    return this;
  }

  /**
   * A name alias for the RAiD that is stored in the metadata
   * @return name
  */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UpdateRaidModel description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A short description of the RAiD that is stored in the metadata
   * @return description
  */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public UpdateRaidModel contentPath(String contentPath) {
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
    UpdateRaidModel updateRaidModel = (UpdateRaidModel) o;
    return Objects.equals(this.name, updateRaidModel.name) &&
        Objects.equals(this.description, updateRaidModel.description) &&
        Objects.equals(this.contentPath, updateRaidModel.contentPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, contentPath);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateRaidModel {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

