package raido.endpoint.message;

/** Class exists not to encapsulate client messages, but just to group them
 together so they're easy to find.
 This is intended to assist consistency by making it easier to quickly
 scan previously created messages for similar terminology and phrasing.
 It's not about i18n or anything (that's why it's just a bunch of statics).
 */
public class ApiMessage {
  public static final String RAID_V1_MINT_DATA_ERROR = 
    "There was a problem with the input data." +
    " This endpoint exists to support legacy V1 usage for existing customers," +
    " please contact support or updgrade to a newer endpoint version.";
  
}
