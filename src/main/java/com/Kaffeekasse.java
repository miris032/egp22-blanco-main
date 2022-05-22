// Yuyang Peng, 216417
// Zefei Gao, 216783
// Fangshu YU, 208929
// Zihao Li, 214271

package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Kaffeekasse extends AbstractBehavior<Kaffeekasse.kk> {


    public interface kk {}
    private int Guthaben;

    public static final class Recharge implements kk {
        public ActorRef<Kaffeetrinkende.kt> sender;
        public Recharge(ActorRef<Kaffeetrinkende.kt> sender) {
            this.sender = sender;
        }
    }

    public static final class Pay implements kk {
        public ActorRef<Loadbalancer.lb> sender;
        public Pay(ActorRef<Loadbalancer.lb> sender) {
            this.sender = sender;
        }
    }




    public static Behavior<kk> create(int Guthaben) {
        return Behaviors.setup(context -> new Kaffeekasse(context, Guthaben));
    }


    // Constructor
    private Kaffeekasse(ActorContext<kk> context, int Guthaben) {
        super(context);
        this.Guthaben = Guthaben;

        //loadbalancer.tell(new Kaffeekasse.Pay(this.getContext().getSelf()));

    }


    @Override
    public Receive<kk> createReceive() {
        return newReceiveBuilder()
                .onMessage(Recharge.class, this::onRecharge)
                .onMessage(Pay.class, this::onPay)
                .build();
    }


    // Fall 1: Guthaben aufladen
    private Behavior<kk> onRecharge(Recharge request) {
        getContext().getLog().info("recharge 1 Euro! for {} ({})", request.sender.path(), Guthaben);
        this.Guthaben += 1;
        request.sender.tell(new Kaffeetrinkende.Success());
        return this;
    }


    // Fall 2 & 3: bezahlen für eine Kaffee
    private Behavior<kk> onPay(Pay request) {
        getContext().getLog().info("Got a pay request! from {} ({})", request.sender.path(), Guthaben);

        // Fall 2: Hat genug Geld
        if (this.Guthaben > 0) {
            this.Guthaben -= 1;
            request.sender.tell(new Loadbalancer.MoneyEnough());
        }

        // Fall 3: Kein genug Geld
        else {
            request.sender.tell(new Loadbalancer.MoneyNotEnough());
        }
        return this;
    }

}
