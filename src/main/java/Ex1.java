import static java.lang.Thread.sleep;

public class Ex1 {
//Напишите программу, которая каждую секунду отображает на экране данные о времени,
// прошедшем от начала сессии (запуска программы).
//
//Другой ее поток выводит каждые 5 секунд сообщение "Прошло 5 секунд". Предусмотрите возможность ежесекундного
//оповещения потока, воспроизводящего сообщение, потоком, отсчитывающим время.

    static Object monitor = new Object(); // монитор для синхронизации

    public static void main(String[] args) throws InterruptedException {

        Counter counter = new Counter();
        Thread oneSecondAnnunciator = new Thread(new SecondAnnunciator(counter));
        Thread fiveSecondAnnunciator = new Thread(new MinutesAnnunciator(counter));
        oneSecondAnnunciator.start();
        fiveSecondAnnunciator.start();
    }
}
class Counter {
    int firstCounter = 0;   //секундный счетчик
    int secondCounter = 0;  //минутный счетчик

    public void secondAnnunciator() {
        synchronized (Ex1.monitor) {  //блок синхронизации
            System.out.println(firstCounter + ":" + secondCounter);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            firstCounter++;
            if (firstCounter == 60) {
                firstCounter = 0;
                secondCounter++;
            }
            while (firstCounter % 5 == 0) {
                try {
                    System.out.println(firstCounter + ":" + secondCounter);
                    Ex1.monitor.wait(); // освобождает монитор и переводит вызывающий поток в состояние ожидания до тех пор, пока другой поток не вызовет метод notify()
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Ex1.monitor.notify(); //продолжает работу потока, у которого ранее был вызван метод wait()


        }
    }
    public void minutesAnnunciator() {
            synchronized (Ex1.monitor) {  //блок синхронизации
                while (firstCounter % 5 != 0) {
                    try {
                        Ex1.monitor.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                firstCounter++;
                System.out.println("Five seconds past , алярм-алярм ! воруй - убивай - души гусей ");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Ex1.monitor.notify();
            }
        }
    }

class SecondAnnunciator implements Runnable {
   Counter counter;

    public SecondAnnunciator(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        while (true) {
            counter.secondAnnunciator();
        }
    }
}

class MinutesAnnunciator implements Runnable {
    Counter counter;

    public MinutesAnnunciator(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        while (true) {
            counter.minutesAnnunciator();
        }
    }
}