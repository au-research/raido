package raido.idl.raidv1.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * (optional) Object that acts as a document for optional metadata KeyValues
 */

@JsonTypeName("RaidModel_meta")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-08-02T11:17:12.918130700+10:00[Australia/Brisbane]")
public class RaidModelMeta {

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  public RaidModelMeta name(String name) {
    this.name = name;
    return this;
  }

  /**
   * A name alias for the RAiD
   * @return name
  */
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RaidModelMeta description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A short description of the RAiD
   * @return description
  */
  
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RaidModelMeta raidModelMeta = (RaidModelMeta) o;
    return Objects.equals(this.name, raidModelMeta.name) &&
        Objects.equals(this.description, raidModelMeta.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RaidModelMeta {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

