package jason.example.completable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThenCompose {

	public static void main(String[] args) {

		ExecutorService executor = Executors.newFixedThreadPool(1);

		CompletableFuture<Integer> future1 = new CompletableFuture<>();
		
		
		executor.execute(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			future1.complete(5);
		});

		
		CompletableFuture<String> future2=future1.thenCompose((intValue)->{
			/*
			 * thenApply returns a value which is known immediately. The value is wrapped completableFuture and trigger next stage.
			 * 
			 * There is case we need to return a value which is computed asynchronously, 
			 * In this case, we return a completableFuture representing the value
			 */
			return new CompletableFuture<String>();
		});
		
		executor.execute(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			future2.complete("completed");
		});
		
		future2.thenAccept((s)->{
			System.out.println(s);
			executor.shutdown();
		});
		System.out.printf("main thread %s comes to end\n", Thread.currentThread().getName());
	}

}
