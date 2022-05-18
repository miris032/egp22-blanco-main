package com.example;
import akka.actor.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import uebung04.Lagerist;
import uebung04.Lagerverwaltung;


public class Kaffeemaschine extends AbstractBehavior<Kaffeemaschine.SomeMessage> {

    private int Vorrat;
    public interface Request {}
    public static final class make implements Kaffeemaschine.Request {
        public final ActorRef<Loadbalancer.Response> sender;
        public make(ActorRef<Loadbalancer.Response> sender) {
            this.sender = sender;
        }
    }

    public static Behavior<Kaffeemaschine.Request> create(int Vorrat) {
        return Behaviors.setup(context -> new Kaffeemaschine(context, Vorrat));
    }


    //Constructor
    private Kaffeemaschine(ActorContext<Kaffeemaschine.Request> context, int Vorrat) {
        super(context);
        this.Vorrat = Vorrat;
    }


    @Override
    public Receive<Kaffeemaschine.Request> createReceive() {
        return newReceiveBuilder()
                .onMessage(make.class, this::oneMake)
                .build();
    }


    private Behavior<Kaffeemaschine.Request> oneMake(Kaffeemaschine.make request) {
        getContext().getLog().info("Got a get request from {} ({})!", request.sender.path(), Vorrat);
        if (this.Vorrat > 0) {
            this.Vorrat -= 1;
            request.sender.tell(new Loadbalancer.Success());
        } else {
            request.sender.tell(new Loadbalancer.Fail());
        }
        return this;
    }
}
