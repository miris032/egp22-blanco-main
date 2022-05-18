package com.example;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Loadbalancer extends AbstractBehavior<Loadbalancer.Response> {

    private final ActorRef<Kaffeekasse.Request> Kaffeekasse;
    public interface Request {}
    public interface Response {}
    public static final class Success implements Response {}
    public static final class Fail implements Response {}


    public static Behavior<Response> create(ActorRef<Kaffeekasse.Request> Kaffeekasse) {
      return Behaviors.setup(context -> new Loadbalancer(context, Kaffeekasse));
    }


    //Constructor
    private Loadbalancer(ActorContext<Response> context, ActorRef<Kaffeekasse.Request> Kaffeekasse) {
      super(context);
      this.Kaffeekasse = Kaffeekasse;
    }


    @Override
    public Receive<Response> createReceive() {
      return newReceiveBuilder()
              .onMessage(Success.class, this::onSuccess)
              .build();
    }


    private Behavior<Response> onSuccess(Success command) {
        getContext().getLog().info("Success");

        //if...else
        Kaffeekasse.tell(new Kaffeekasse.aufladen(this.getContext().getSelf()));

        return this;
    }


    private Behavior<Response> onFail(Fail command) {
        getContext().getLog().info("Fail");
        return Behaviors.stopped();
    }


}



