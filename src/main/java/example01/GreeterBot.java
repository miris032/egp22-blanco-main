package example01;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class GreeterBot extends AbstractBehavior<Greeter.Greeted> {

    public static Behavior<Greeter.Greeted> create() {
        return Behaviors.setup(context -> new GreeterBot(context));
    }

    private GreeterBot(ActorContext<Greeter.Greeted> context) {
        super(context);
    }

    @Override
    public Receive<Greeter.Greeted> createReceive() {
        return newReceiveBuilder().onMessage(Greeter.Greeted.class, this::onGreeted).build();
    }

    private Behavior<Greeter.Greeted> onGreeted(Greeter.Greeted message) {
        getContext().getLog().info("Greeting for {}",  message.whom);
        message.from.tell(new Greeter.Greet(message.whom, getContext().getSelf()));
        return this;
    }
}
