package jason.example.completable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HandleAnyway {

	public static void main(String[] args) {

		ExecutorService executor = Executors.newFixedThreadPool(1);

		CompletableFuture<Integer> future1 = new CompletableFuture();
		
		
		executor.execute(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			future1.completeExceptionally(new RuntimeException("runtime exception"));
		});

		
		future1.thenRun(()->System.out.println("action after successful completion. This should never be printed"));
	
		CompletableFuture<Integer> future2=future1.whenComplete((intValue, exception)->{ 
			System.out.println("This action is executed no matter task complete successfully or abnormally.\n We don't return anything here");});
		
		/* whenComplete doesn't change future */
		assert future1==future2;
		
		//recover from failure
		CompletableFuture<String> recovered=future2.handle((intValu, exception)->{ return "recovered"; });
		recovered.thenAccept((s)->{ 
			System.out.println(s);
			executor.shutdown();
		});
		
		System.out.printf("main thread %s comes to end\n", Thread.currentThread().getName());
	}

}
