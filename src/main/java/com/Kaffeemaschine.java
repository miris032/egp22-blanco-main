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

    public static final class GetAmount implements Request {
        public ActorRef<Loadbalancer.Response> sender;
        public GetAmount(ActorRef<Loadbalancer.Response> sender) {
            this.sender = sender;
        }
    }

    public static final class GetOneCoffee implements Request {
        public ActorRef<Kaffeetrinkende.Response> sender2;  // sender2: Kaffeetrinkende
        public GetOneCoffee(ActorRef<Kaffeetrinkende.Response> sender2) {
            this.sender2 = sender2;
        }
    }




    public static Behavior<Request> create(int Vorrat) {
        return Behaviors.setup(context -> new Kaffeemaschine(context, Vorrat));
    }


    // Constructor
    private Kaffeemaschine(ActorContext<Request> context, int vorrat) {
        super(context);
        this.Vorrat = vorrat;
    }


    @Override
    public Receive<Request> createReceive() {
        return newReceiveBuilder()
                .onMessage(GetAmount.class, this::onGetAmount)
                .onMessage(GetOneCoffee.class, this::onGetOneCoffee)
                .build();
    }


    private Behavior<Request> onGetAmount(GetAmount request) {
        getContext().getLog().info("Got a get request from {} ({})!", request.sender.path(), Vorrat);
        if (this.Vorrat > 0) {
            this.Vorrat -= 1;
            request.sender.tell(new Loadbalancer.Success());
        } else {
            request.sender.tell(new Loadbalancer.Fail());
        }
        return this;
    }
    
    
    private Behavior<Request> onGetOneCoffee(GetOneCoffee request) {
        getContext().getLog().info("Got a getOneCoffee request from {} ({})!", request.sender2.path(), Vorrat);

        // if GetAmount 检测
        request.sender2.tell(new Kaffeetrinkende.Success());

        return this;
    }
    
}
