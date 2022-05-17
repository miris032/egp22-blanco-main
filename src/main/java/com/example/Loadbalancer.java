package com.example;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Loadbalancer extends AbstractBehavior<Loadbalancer.Request> {

    private final int someAttribute;

    public interface Request {}
    public interface Response {}
    public static final class Request {}


    public static Behavior<Request> create(int someAttribute) {
      return Behaviors.setup(context -> new Loadbalancer(context, someAttribute));
    }


    private Loadbalancer(ActorContext<Request> context, int someAttribute) {
      super(context);
      this.someAttribute = someAttribute;
    }


    @Override
    public Receive<Request> createReceive() {
      return newReceiveBuilder().onMessage(Request.class, this::onSomeMessage).build();
    }


    private Behavior<Request> onSomeMessage(Request command) {
      getContext().getLog().info("Got a message. My attribute is {}!", someAttribute);
      return this;
    }


    /*private Behavior<Request> onPut(Put request) {

    }*/



}



