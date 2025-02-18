import com.kfkProdCons.kafka.*;

public class Main{
    public static void main(String[] args){
        kfkProducer.produceOnce();
        kfkStreamProcessor.processStream();
        kfkConsumer.consumeOnce();
    }
}