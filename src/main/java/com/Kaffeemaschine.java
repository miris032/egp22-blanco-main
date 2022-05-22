package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Kaffeemaschine extends AbstractBehavior<Kaffeemaschine.km> {


    public interface km {}
    private int index;
    private int Vorrat;

    public static final class GetAmount implements km {
        public ActorRef<Loadbalancer.lb> sender;
        public GetAmount(ActorRef<Loadbalancer.lb> sender) {
            this.sender = sender;
        }
    }

    public static final class GetOneCoffee implements km {
        public ActorRef<Kaffeetrinkende.kt> sender;
        public GetOneCoffee(ActorRef<Kaffeetrinkende.kt> sender) {
            this.sender = sender;
        }
    }




    public static Behavior<km> create(int index, int Vorrat) {
        return Behaviors.setup(context -> new Kaffeemaschine(context, index, Vorrat));
    }


    // Constructor
    private Kaffeemaschine(ActorContext<km> context, int index, int vorrat) {
        super(context);
        this.index = index;
        this.Vorrat = vorrat;
    }


    @Override
    public Receive<km> createReceive() {
        return newReceiveBuilder()
                .onMessage(GetAmount.class, this::onGetAmount)
                .onMessage(GetOneCoffee.class, this::onGetOneCoffee)
                .build();
    }


    /*private Behavior<Request> onGetAmount(GetAmount request) {
        getContext().getLog().info("Got a get request from {} ({})!", request.sender.path(), Vorrat);
        if (this.Vorrat > 0) {
            this.Vorrat -= 1;
            request.sender.tell(new Loadbalancer.Success());
        } else {
            request.sender.tell(new Loadbalancer.Fail());
        }
        return this;
    }*/




    // 询问咖啡存量
    private Behavior<km> onGetAmount(GetAmount request) {

        // 有足够的咖啡
        if (this.Vorrat > 0) {
            // 告诉loadbalancer咖啡数量足够，并返回存量Vorrat
            request.sender.tell(new Loadbalancer.CoffeeEnough(index, Vorrat));
        }
        return this;
    }




    

    // Hat die Voraussetzung erfüllt und holt jetzt eine Kaffe ab
    private Behavior<km> onGetOneCoffee(GetOneCoffee request) {
        getContext().getLog().info("Got a getOneCoffee request from {} ({})!", request.sender.path(), Vorrat);

        // if GetAmount 检测

        this.Vorrat -= 1;
        request.sender.tell(new Kaffeetrinkende.Success());
        return this;
    }
    
}
