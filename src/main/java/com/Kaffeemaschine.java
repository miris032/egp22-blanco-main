package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Kaffeemaschine extends AbstractBehavior<Kaffeemaschine.Request> {


    public interface Request {}
    private int Vorrat;
    public static final class make implements Request {
        public ActorRef<Loadbalancer.Response> sender;
        public make(ActorRef<Loadbalancer.Response> sender) {
            this.sender = sender;
        }
    }




    public static Behavior<Request> create(int Vorrat) {
        return Behaviors.setup(context -> new Kaffeemaschine(context, Vorrat));
    }


    //Constructor
    private Kaffeemaschine(ActorContext<Request> context, int vorrat) {
        super(context);
        this.Vorrat = vorrat;
    }


    @Override
    public Receive<Request> createReceive() {
        return newReceiveBuilder()
                .onMessage(make.class, this::onMake)
                .build();
    }


    private Behavior<Request> onMake(Kaffeemaschine.make request) {
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
