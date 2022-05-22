// Yuyang Peng, 216417
// Zefei Gao, 216783
// Fangshu YU, 208929
// Zihao Li, 214271

package com;
import akka.actor.Actor;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;


public class KaffeeMain extends AbstractBehavior<KaffeeMain.StartMessage> {


    public static class StartMessage {}
    ActorRef<SomeActor.SomeMessage> someActor;
    public static Behavior<StartMessage> create() {
        return Behaviors.setup(KaffeeMain::new);
    }


    private KaffeeMain(ActorContext<StartMessage> context) {
        super(context);
        someActor = context.spawn(SomeActor.create(37), "someActor");
    }


    @Override
    public Receive<StartMessage> createReceive() {
        return newReceiveBuilder().onMessage(StartMessage.class, this::onStartMessage).build();
    }


    private Behavior<StartMessage> onStartMessage(StartMessage command) {

        ActorRef<Kaffeekasse.kk> kaffeekasse = getContext().spawn(Kaffeekasse.create(0), "kaffeekasse");

        ActorRef<Kaffeemaschine.km> kaffeemaschine1 = getContext().spawn(Kaffeemaschine.create(1, 10), "kaffeemaschine1");
        ActorRef<Kaffeemaschine.km> kaffeemaschine2 = getContext().spawn(Kaffeemaschine.create(2, 10), "kaffeemaschine2");
        ActorRef<Kaffeemaschine.km> kaffeemaschine3 = getContext().spawn(Kaffeemaschine.create(3, 10), "kaffeemaschine3");

        ActorRef<Loadbalancer.lb> loadbalancer = getContext().spawn(Loadbalancer.create(kaffeekasse, kaffeemaschine1), "loadbalancer");

        ActorRef<Kaffeetrinkende.kt> kaffeetrinkender1 = getContext().spawn(Kaffeetrinkende.create(kaffeekasse, loadbalancer, kaffeemaschine1), "kaffeetrinkender1");
        ActorRef<Kaffeetrinkende.kt> kaffeetrinkender2 = getContext().spawn(Kaffeetrinkende.create(kaffeekasse, loadbalancer, kaffeemaschine2), "kaffeetrinkender2");
        ActorRef<Kaffeetrinkende.kt> kaffeetrinkender3 = getContext().spawn(Kaffeetrinkende.create(kaffeekasse, loadbalancer, kaffeemaschine3), "kaffeetrinkender3");
        ActorRef<Kaffeetrinkende.kt> kaffeetrinkender4 = getContext().spawn(Kaffeetrinkende.create(kaffeekasse, loadbalancer, kaffeemaschine3), "kaffeetrinkender4");


        return this;
    }
}
