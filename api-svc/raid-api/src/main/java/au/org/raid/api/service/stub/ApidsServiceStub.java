package au.org.raid.api.service.stub;

import au.org.raid.api.service.apids.ApidsService;
import au.org.raid.api.service.apids.model.ApidsMintResponse;
import au.org.raid.api.service.stub.util.IdFactory;
import au.org.raid.api.spring.bean.LogMetric;
import au.org.raid.api.spring.config.environment.StubProperties;
import au.org.raid.api.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.infoLogExecutionTime;
import static au.org.raid.api.util.ThreadUtil.sleep;

/**
 * "InMemory" because eventually we'll need a proper standalone service,
 * running on the other side of a network call (it'll still just be a stub
 * though).
 */
public class ApidsServiceStub extends ApidsService {
    public static final int MAX_NODE_ID_CHARS = 8;
    public static final String IN_MEMORY_HANDLE_PREFIX = "inmem";
    private static final Log log = to(ApidsServiceStub.class);
    private StubProperties.Apids stubProperties;

    /**
     * prepended to handle suffixes to guarantee uniqueness across nodes
     */
    private String nodePrefix;
    private IdFactory idFactory;

    public ApidsServiceStub(
            StubProperties.Apids stubProperties
    ) {
        super(null, null);
        this.stubProperties = stubProperties;

        var digest = getSha1MessageDigest();

        idFactory = new IdFactory();
    }

    /**
     * Handle DB table is 32 char max.  So we only have 26 chars to play with.
     * The aws_task_id is like "ce0b35946b8c4dcd9c21d4fee9cb46be".
     * So, in an ECS env, the  handle would be
     * but `inmem/ce0b35946b8c4dcd9c21d4fee9cb46be20230418064639635` - 56 chars.
     * So we reduce the size of the prefix down to MAX_NODE_ID_CHARS (default 8).
     * There is a risk of generating the same hash, but it's unlikely.
     */
    private static String generateShortenedNodeId(
            MessageDigest sha1,
            String nodeId
    ) {
        byte[] nodeIdBytes = nodeId.getBytes(StandardCharsets.UTF_8);
        byte[] hashBytes = sha1.digest(nodeIdBytes);

    /* Convert the hash bytes to a hexadecimal string and truncate it to
     the desired length (e.g., 8 characters) */
        StringBuilder hexHash = new StringBuilder();
        for (byte b : hashBytes) {
            hexHash.append(String.format("%02x", b));
        }
        return hexHash.substring(0, MAX_NODE_ID_CHARS);
    }

    private static MessageDigest getSha1MessageDigest() {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return messageDigest;
    }

    @Override
    public ApidsMintResponse mintApidsHandleContentPrefix(
            Function<String, String> raidLandingPageUrl
    ) {
        ApidsMintResponse resp = new ApidsMintResponse();
        resp.type = "success";
        resp.identifier = new ApidsMintResponse.Identifier();

        resp.identifier.handle = IN_MEMORY_HANDLE_PREFIX + "/" +
                idFactory.generateUniqueId(nodePrefix, false);

        resp.identifier.property = new ApidsMintResponse.Identifier.Property();
    /* these are just copied from the example comment in ApidsMintResponse,
    just trying to get the mint process working. */
        resp.identifier.property.value = "RAID handle";
        resp.identifier.property.index = 1;
        resp.identifier.property.type = "DESC";

        log.with("delay", stubProperties.getDelay()).
                with("handle", resp.identifier.handle).
                debug("simulate APIDS mint request");
        infoLogExecutionTime(httpLog, LogMetric.APIDS_MINT_WITH_DESC, () -> {
            sleep(stubProperties.getDelay());
            return null;
        });

        log.with("delay", stubProperties.getDelay()).
                debug("simulate APIDS add request");
        infoLogExecutionTime(httpLog, LogMetric.APIDS_ADD_URL_VALUE, () -> {
            sleep(stubProperties.getDelay());
            return null;
        });

        return resp;
    }
}
