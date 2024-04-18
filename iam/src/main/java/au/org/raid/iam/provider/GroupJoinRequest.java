package au.org.raid.iam.provider;

public class GroupJoinRequest {
    private String groupId;

    // Default constructor (needed for JSON deserialization)
    public GroupJoinRequest() {}

    // Getters and setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}