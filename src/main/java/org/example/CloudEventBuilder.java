package org.example;

import io.smallrye.reactive.messaging.TracingMetadata;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadataBuilder;
import java.net.URI;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;

public class CloudEventBuilder<T> {

  private static final String DATA_CONTENT_TYPE = "application/json";
  private TracingMetadata tracingMetadata;
  private T payload;

  private final OutgoingCloudEventMetadataBuilder<T> metadataBuilder =
      OutgoingCloudEventMetadata.builder();

  public CloudEventBuilder() {
    metadataBuilder
        .withSource(URI.create("my-event-source"))
        .withType("MyEvent")
        .withDataContentType(DATA_CONTENT_TYPE);
  }

  public CloudEventBuilder<T> withId(String id) {
    metadataBuilder.withId(id);
    return this;
  }

  public CloudEventBuilder<T> withPayload(T payload) {
    this.payload  = payload;
    return this;
  }

  public CloudEventBuilder<T> withTracingMetadata(TracingMetadata tracingMetadata) {
    this.tracingMetadata = tracingMetadata;
    return this;
  }


  public Message<T> build() {
    return Message.of(payload, Metadata.of(metadataBuilder.build(), tracingMetadata));
  }

}
