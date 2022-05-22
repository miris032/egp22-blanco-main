package example02;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.Objects;

// #greeter
public class Relayer extends AbstractBehavior<Relayer.Message> {

    private final int max;

    public static final class Message {
        public final int count;
        public final ActorRef<Message>[] queue;

        public Message(int count, ActorRef<Message>[] queue) {
            this.count = count;
            this.queue = queue;
        }
    }

    public static Behavior<Message> create(int max) {
        return Behaviors.setup(context -> new Relayer(context, max));
    }

    private Relayer(ActorContext<Message> context, int max) {
        super(context);
        this.max = max;
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder().onMessage(Message.class, this::onMessage).build();
    }

    private Behavior<Message> onMessage(Message msg) {
        getContext().getLog().info("Hello {} ({})!", this.getContext().getSelf(), msg.count);
        if (msg.count == max) {
            return Behaviors.stopped();
        } else {
            //#greeter-send-message
            ActorRef[] newQueue = {msg.queue[1], msg.queue[2], msg.queue[0]};

            msg.queue[0].tell(new Message(msg.count + 1, newQueue));
            //#greeter-send-message
            return this;
        }
    }
}
// #greeter

