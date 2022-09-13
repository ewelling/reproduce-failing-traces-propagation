package org.example;

import io.opentelemetry.context.Context;
import io.smallrye.reactive.messaging.TracingMetadata;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

public class PojoEmitter {

  private final MyEvent myEvent;
  private final Emitter<MyEvent> emitter;

  public PojoEmitter(Emitter<MyEvent> emitter, MyEvent myEvent) {
    this.emitter = emitter;
    this.myEvent = myEvent;
  }

  public void send() {
    var message = Message.of(myEvent);
    var msgWithMetadata = message.addMetadata(TracingMetadata.withCurrent(Context.current()));

    emitter.send(msgWithMetadata);
    emitter.send(msgWithMetadata);
  }
}
