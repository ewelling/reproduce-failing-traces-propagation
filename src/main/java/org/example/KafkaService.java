package org.example;

import io.opentelemetry.context.Context;
import io.opentelemetry.extension.annotations.WithSpan;
import io.quarkus.opentelemetry.runtime.QuarkusContextStorage;
import io.smallrye.reactive.messaging.TracingMetadata;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KafkaService {

  private static final Logger LOG = Logger.getLogger(KafkaService.class);

  @Channel("myevents")
  Emitter<MyEvent> emitter;

  @Inject TracedService tracedService;

  @WithSpan("send")
  public void send() {

    var myEvent = new MyEvent(45);
    var message = Message.of(myEvent);
    var msgWithMetadata = message.addMetadata(TracingMetadata.withCurrent(Context.current()));

    System.out.println("Context when sending: " + msgWithMetadata.getMetadata(TracingMetadata.class).get().getCurrentContext().toString());

    emitter.send(msgWithMetadata);

//    emitter.send(myEvent);
  }

  @WithSpan("send2")
  public void send2() {

    var myEvent = new MyEvent(45);
    var message = Message.of(myEvent);
    var msgWithMetadata = message.addMetadata(TracingMetadata.withCurrent(Context.current()));

    System.out.println("Context when sending: " + msgWithMetadata.getMetadata(TracingMetadata.class).get().getCurrentContext().toString());

    Message<MyEvent> ceEventMessage =
        new CloudEventBuilder<MyEvent>()
            .withPayload(myEvent)
            .withId("12345")
            .withTracingMetadata(TracingMetadata.withCurrent(Context.current()))
            .build();

    emitter.send(ceEventMessage);
  }

  @Incoming("myevents-in")
  @WithSpan("listenToEvents")
  public CompletionStage<Void> listenToEvents(Message<MyEvent> event) {
    LOG.info("Received an event via Kafka");
    QuarkusContextStorage.INSTANCE.attach(
        event.getMetadata(TracingMetadata.class).get().getCurrentContext());

    System.out.println("Received context: " + event.getMetadata(TracingMetadata.class).get().getCurrentContext().toString());

    ((IncomingKafkaRecord) event)
        .getHeaders()
        .headers("traceparent")
        .forEach(header -> LOG.infof("Received Kafka header: %s", new String(header.value())));

    tracedService.logEvent(event.getPayload());
    return event.ack();
  }
}
