package raido.idl.raidv1.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import raido.idl.raidv1.model.RaidModelInstitutionsInner;
import raido.idl.raidv1.model.RaidModelMeta;
import raido.idl.raidv1.model.RaidModelProvidersInner;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RaidModel
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-08-02T11:17:12.918130700+10:00[Australia/Brisbane]")
public class RaidModel {

  @JsonProperty("owner")
  private String owner;

  @JsonProperty("startDate")
  private String startDate;

  @JsonProperty("endDate")
  private String endDate;

  @JsonProperty("creationDate")
  private String creationDate;

  @JsonProperty("handle")
  private String handle;

  @JsonProperty("contentPath")
  private String contentPath;

  @JsonProperty("contentIndex")
  private String contentIndex;

  @JsonProperty("providers")
  @Valid
  private List<RaidModelProvidersInner> providers = null;

  @JsonProperty("institutions")
  @Valid
  private List<RaidModelInstitutionsInner> institutions = null;

  @JsonProperty("meta")
  private RaidModelMeta meta;

  public RaidModel owner(String owner) {
    this.owner = owner;
    return this;
  }

  /**
   * Provider that created the RAiD handle
   * @return owner
  */
  
  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public RaidModel startDate(String startDate) {
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

  public RaidModel endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * (optional) RAiD end Datetime (yyyy-MM-dd hh:mm:ss)
   * @return endDate
  */
  
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public RaidModel creationDate(String creationDate) {
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

  public RaidModel handle(String handle) {
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

  public RaidModel contentPath(String contentPath) {
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

  public RaidModel contentIndex(String contentIndex) {
    this.contentIndex = contentIndex;
    return this;
  }

  /**
   * Index of URL path ANDS handle
   * @return contentIndex
  */
  
  public String getContentIndex() {
    return contentIndex;
  }

  public void setContentIndex(String contentIndex) {
    this.contentIndex = contentIndex;
  }

  public RaidModel providers(List<RaidModelProvidersInner> providers) {
    this.providers = providers;
    return this;
  }

  public RaidModel addProvidersItem(RaidModelProvidersInner providersItem) {
    if (this.providers == null) {
      this.providers = new ArrayList<>();
    }
    this.providers.add(providersItem);
    return this;
  }

  /**
   * (optional) Providers associated to the RAiD
   * @return providers
  */
  @Valid 
  public List<RaidModelProvidersInner> getProviders() {
    return providers;
  }

  public void setProviders(List<RaidModelProvidersInner> providers) {
    this.providers = providers;
  }

  public RaidModel institutions(List<RaidModelInstitutionsInner> institutions) {
    this.institutions = institutions;
    return this;
  }

  public RaidModel addInstitutionsItem(RaidModelInstitutionsInner institutionsItem) {
    if (this.institutions == null) {
      this.institutions = new ArrayList<>();
    }
    this.institutions.add(institutionsItem);
    return this;
  }

  /**
   * (optional) Providers associated to the RAiD
   * @return institutions
  */
  @Valid 
  public List<RaidModelInstitutionsInner> getInstitutions() {
    return institutions;
  }

  public void setInstitutions(List<RaidModelInstitutionsInner> institutions) {
    this.institutions = institutions;
  }

  public RaidModel meta(RaidModelMeta meta) {
    this.meta = meta;
    return this;
  }

  /**
   * Get meta
   * @return meta
  */
  @Valid 
  public RaidModelMeta getMeta() {
    return meta;
  }

  public void setMeta(RaidModelMeta meta) {
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
    RaidModel raidModel = (RaidModel) o;
    return Objects.equals(this.owner, raidModel.owner) &&
        Objects.equals(this.startDate, raidModel.startDate) &&
        Objects.equals(this.endDate, raidModel.endDate) &&
        Objects.equals(this.creationDate, raidModel.creationDate) &&
        Objects.equals(this.handle, raidModel.handle) &&
        Objects.equals(this.contentPath, raidModel.contentPath) &&
        Objects.equals(this.contentIndex, raidModel.contentIndex) &&
        Objects.equals(this.providers, raidModel.providers) &&
        Objects.equals(this.institutions, raidModel.institutions) &&
        Objects.equals(this.meta, raidModel.meta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(owner, startDate, endDate, creationDate, handle, contentPath, contentIndex, providers, institutions, meta);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RaidModel {\n");
    sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate)).append("\n");
    sb.append("    handle: ").append(toIndentedString(handle)).append("\n");
    sb.append("    contentPath: ").append(toIndentedString(contentPath)).append("\n");
    sb.append("    contentIndex: ").append(toIndentedString(contentIndex)).append("\n");
    sb.append("    providers: ").append(toIndentedString(providers)).append("\n");
    sb.append("    institutions: ").append(toIndentedString(institutions)).append("\n");
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

