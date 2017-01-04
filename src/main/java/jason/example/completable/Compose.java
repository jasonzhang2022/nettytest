package jason.example.completable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Compose {

	public static void main(String[] args) {

		ExecutorService executor = Executors.newFixedThreadPool(1);

		CompletableFuture<Integer> future1 = new CompletableFuture();
		CompletableFuture<Integer> future2 = new CompletableFuture();
		future2.complete(2);
		CompletableFuture<Integer> future3 = new CompletableFuture();

		
		executor.execute(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			future1.complete(1);
		});

		//convenient method if we only has two future.
		future1.thenAcceptBoth(future2, (int1, int2)->future3.complete(int1+int2*2));
		
		
		CompletableFuture<Integer>[] futures = new CompletableFuture[]{future1, future2, future3};

		/* use allOf if combine more than 2 */
		CompletableFuture<Integer> sum = CompletableFuture.allOf(futures).thenApply((v)->{
			/*
			 * V is not used here. It is void.
			 * We get the intermediate result for each future from context.
			 * f.join() returns immediately since f already finishes
			 */
			return Arrays.stream(futures).mapToInt(f->f.join()).sum();
		});
		
		sum.thenAccept((s)-> { 
			System.out.printf("sum is %d\n", s) ;  
			executor.shutdown();
		});
				
		System.out.printf("main thread %s comes to end\n", Thread.currentThread().getName());
	}

}
