package uebung04;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Lagerist extends AbstractBehavior<Lagerist.Response> {

  private final ActorRef<Lagerverwaltung.Request> lagerverwaltung;

  public interface Response {}
  public static final class Success implements Response {}
  public static final class Fail implements Response {}

  public static Behavior<Response> create(ActorRef<Lagerverwaltung.Request> lagerverwaltung) {
    return Behaviors.setup(context -> new Lagerist(context, lagerverwaltung));
  }

  private Lagerist(ActorContext<Response> context, ActorRef<Lagerverwaltung.Request> lagerverwaltung) {
    super(context);
    this.lagerverwaltung = lagerverwaltung;

    if (Math.random() < 0.5) {
      lagerverwaltung.tell(new Lagerverwaltung.Get(this.getContext().getSelf()));
    } else {
      lagerverwaltung.tell(new Lagerverwaltung.Put(this.getContext().getSelf()));
    }
  }

  @Override
  public Receive<Response> createReceive() {
    return newReceiveBuilder()
            .onMessage(Success.class, this::onSuccess)
            .onMessage(Fail.class, this::onFail)
            .build();
  }

  private Behavior<Response> onSuccess(Success command) {
    getContext().getLog().info("Success");
    if (Math.random() < 0.5) {
      lagerverwaltung.tell(new Lagerverwaltung.Get(this.getContext().getSelf()));
    } else {
      lagerverwaltung.tell(new Lagerverwaltung.Put(this.getContext().getSelf()));
    }
    return this;
  }

  private Behavior<Response> onFail(Fail command) {
    getContext().getLog().info("Fail");
    return Behaviors.stopped();
  }
}
