package Projekt01;
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
        //someActor.tell(new SomeActor.SomeMessage());

        /*Loadbalancer = new Loadbalancer();
        Kaffeekasse = new Kaffeekasse();
        Kaffeemaschine1 = new Kaffeemaschine(10);
        Kaffeemaschine2 = new Kaffeemaschine(10);
        Kaffeemaschine3 = new Kaffeemaschine(10);
        Kaffeetrinkende1 = new Kaffeetrinkende();
        Kaffeetrinkende2 = new Kaffeetrinkende();
        Kaffeetrinkende3 = new Kaffeetrinkende();
        Kaffeetrinkende4 = new Kaffeetrinkende();*/


        return this;
    }
}
