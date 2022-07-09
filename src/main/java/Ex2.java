import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;

class Ex2 {
    int n;
    //примитив синхронизации[1] работы процессов и потоков,
// в основе которого лежит счётчик, над которым можно производить
// две атомарные операции: увеличение и уменьшение значения на единицу,
// при этом операция уменьшения для нулевого значения счётчика является
// блокирующейся. Служит для построения более сложных механизмов
// синхронизации и используется для синхронизации параллельно
// работающих задач, для защиты передачи данных через разделяемую память,
// для защиты критических секций, а также для управления доступом к а
// ппаратному обеспечению.
    private final Semaphore Fizz;
    private final Semaphore Buzz;
    private final Semaphore FizzBuzz;
    private final Semaphore Number;

    public Ex2(int n) {
        this.n = n;
//передается количество потоков, которому семафор будет разрешать одновременно использовать заданный ресурс.
        Fizz = new Semaphore(0); //
        Buzz = new Semaphore(0);
        FizzBuzz = new Semaphore(0);
        Number = new Semaphore(1);
    }

    public void fizz(Runnable printFizz) throws InterruptedException {
        for (int i = 3; i <= n ; i+= 3) {
            Fizz.acquire();
//acquire() запрашивает доступ к следующему за вызовом этого метода блоку кода,
//если доступ не разрешен, поток вызвавший этот метод блокируется до тех пор,
//пока семафор не разрешит доступ
            printFizz.run();
//release() освобождает ресурс            
            Number.release();
            if ((i + 3) % 5 == 0) {
                i += 3;
            }
        }
    }

    public void buzz(Runnable printBuzz) throws InterruptedException {
        for (int i = 5; i <= n ; i += 5) {
            Buzz.acquire();
            printBuzz.run();
            Number.release();
            if ((i + 5) % 3 == 0) {
                i += 5;
            }
        }
    }

    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        for (int i = 15; i <= n; i += 15) {
            FizzBuzz.acquire();
            printFizzBuzz.run();
            Number.release();
        }
    }

    public void number(IntConsumer number) throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            Number.acquire();
            if((i % 3 == 0)&& (i % 5 == 0)){
                FizzBuzz.release();
            }
            else if(i % 5 == 0){
                Buzz.release();
            }
            else if(i % 3 == 0){
                Fizz.release();
            }
            else{
                number.accept(i);
                Number.release();
            }
        }
    }
}

class OrderNumber {
    public static void main(String[] args) {
        Ex2 Ex2 = new Ex2(15);
        Runnable printFizz = () -> System.out.print("fizz ");
        Runnable printBuzz = () -> System.out.print("buzz ");
        Runnable printFizzBuzz = () -> System.out.print("fizzbuzz ");
        IntConsumer printNumber = number -> System.out.print(number + " ");

        Thread threadA = new Thread(() -> {
            try {
                Ex2.fizz(printFizz);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        Thread threadB = new Thread(() -> {
            try {
                Ex2.buzz(printBuzz);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        Thread threadC = new Thread(()-> {
            try {
                Ex2.fizzbuzz(printFizzBuzz);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        Thread threadD = new Thread(() -> {
            try {
                Ex2.number(printNumber);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        threadA.start();
        threadB.start();
        threadC.start();
        threadD.start();
    }
}