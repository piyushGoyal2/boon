package org.boon.slumberdb.service.config;


import org.boon.Str;
import org.boon.core.Sys;
import org.boon.json.annotations.JsonIgnore;
import org.boon.slumberdb.service.protocol.ProtocolConstants;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.boon.Lists.list;

/**
 * Created by Richard on 7/1/14.
 */
public class DataStoreClientConfig {

    private static final String DEFAULT_FILE_LOCATION = "/opt/org/slumberdb/client.json";

    private List<Bucket> buckets;

    private String websocketURI;

    private String restURI;

    private String clientId;
    @JsonIgnore
    private AtomicLong count = new AtomicLong();


    private int maxFrameSize = 0; //true

    private int batchResultSize = 15; //true

    public DataStoreClientConfig(Bucket... buckets) {
        this(list(buckets));
    }


    public DataStoreClientConfig(List<Bucket> buckets) {
        this.buckets = buckets;

        sequence();

    }

    public static DataStoreClientConfig load() {
        String fileLocation = Sys.sysPropMultipleKeys(
                "org.boon.slumberdb.DataStoreClientConfig", // POSSIBLE KEY, KEPT FOR BACKWARD COMPATIBLE
                "DataStoreClientConfig"                     // POSSIBLE KEY
        );

        return Sys.loadFromFileLocation(DataStoreClientConfig.class, fileLocation, DEFAULT_FILE_LOCATION);
    }

    public static DataStoreClientConfig config() {
        return new DataStoreClientConfig();
    }

    public static DataStoreClientConfig config(Bucket... buckets) {
        return new DataStoreClientConfig(buckets);
    }

    public List<Bucket> buckets() {
        return buckets;
    }

    public DataStoreClientConfig buckets(List<Bucket> buckets) {
        this.buckets = buckets;


        sequence();

        return this;
    }

    public DataStoreClientConfig buckets(Bucket... buckets) {
        this.buckets = list(buckets);


        sequence();

        return this;
    }

    private void sequence() {
        int index = 0;
        for (Bucket bucket : buckets) {
            bucket.index(index);
            index++;
        }
    }

    @Override
    public boolean equals(Object o) {
        sequence();
        if (this == o) return true;
        if (!(o instanceof DataStoreClientConfig)) return false;

        DataStoreClientConfig that = (DataStoreClientConfig) o;

        if (buckets != null ? !buckets.equals(that.buckets) : that.buckets != null) return false;

        return true;
    }

    @Override
    public int hashCode() {

        sequence();
        return buckets != null ? buckets.hashCode() : 0;
    }

    @Override
    public String toString() {

        sequence();
        return "DataStoreClientConfig{" +
                "buckets=" + buckets +
                '}';
    }

    public Bucket pickBucket(String key) {
        return pickBucket(key.hashCode());
    }

    public Bucket pickBucket(int hash) {
        return buckets.get( (hash < 0 ? -hash : hash) % buckets.size() );
    }

    public String websocketURI() {
        return websocketURI == null ? ProtocolConstants.DEFAULT_WEBSOCKET_URI : websocketURI;
    }

    public DataStoreClientConfig websocketURI(String websocketURI) {
        this.websocketURI = websocketURI;
        return this;
    }

    public String restURI() {
        return restURI == null ? ProtocolConstants.DEFAULT_WEBSOCKET_URI : restURI;
    }

    public DataStoreClientConfig restURI(String restURI) {
        this.restURI = restURI;
        return this;
    }

    public int maxFrameSize() {
        return maxFrameSize == 0 ? 20_000_000 : maxFrameSize;
    }

    public DataStoreClientConfig maxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
        return this;
    }


    public String clientId() {

        if (count == null) {
            count = new AtomicLong();
        }
        if (clientId == null) {
            clientId = UUID.randomUUID().toString();
        }
        return Str.add(clientId, ".", "" + count.incrementAndGet());
    }

    public DataStoreClientConfig clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }
}
